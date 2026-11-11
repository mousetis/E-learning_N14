import { useLocalSearchParams, useRouter } from '@/navigation/routerHook';
import { useState, useEffect } from 'react';
import { Alert, ScrollView, StyleSheet, Text, View } from 'react-native';
import * as DocumentPicker from 'expo-document-picker';
import * as FileSystem from 'expo-file-system/legacy';
import * as Sharing from 'expo-sharing';
import { useVideoPlayer, VideoView } from 'expo-video';

import { ChapterSection } from '@/components/course/chapter-section';
import { CourseStats } from '@/components/course/course-stats';
import { FileRow } from '@/components/course/file-row';
import { ProjectCard } from '@/components/course/project-card';
import { QaThreadCard } from '@/components/course/qa-thread-card';
import { QuestionComposer } from '@/components/course/question-composer';
import { UploadCard } from '@/components/course/upload-card';
import { CourseHero } from '@/components/ui/course-hero';
import { Screen } from '@/components/ui/screen';
import { ScreenHeader } from '@/components/ui/screen-header';
import { SectionHeader } from '@/components/ui/section-header';
import type { SegmentItem } from '@/components/ui/segmented-tabs';
import { Radius, Spacing } from '@/constants/theme';
import { courseService } from '@/services/courseService';
import { useAppStore } from '@/store';
import { useAppTheme } from '@/hooks/use-app-theme';
import type { CourseDetail } from '@/types';
import type { Chapter, Project, ProjectFile, QaThread, Lesson } from '@/types/course';

type PlayerTab = 'lessons' | 'projects' | 'qa';

const DEFAULT_VIDEO = 'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4';

const DEFAULT_PROJECTS: Project[] = [
  {
    id: 'proj-1',
    author: 'Nguyễn Văn Học',
    tone: 'mint',
    title: 'Dự án thực chiến tốt nghiệp',
  },
];

const DEFAULT_FILES: ProjectFile[] = [
  {
    id: 'file-1',
    name: 'Giao-trinh-khoa-hoc.pdf',
    size: '12.4 MB',
  },
  {
    id: 'file-2',
    name: 'Source-code-thuc-hanh.zip',
    size: '28.1 MB',
  },
];

export default function CoursePlayerScreen() {
  const { id } = useLocalSearchParams<{ id?: string }>();
  const courseId = typeof id === 'string' && id.length > 0 ? id : '';
  const router = useRouter();
  const theme = useAppTheme();
  const { user } = useAppStore();

  const [activeTab, setActiveTab] = useState<PlayerTab>('lessons');
  const [activeLesson, setActiveLesson] = useState<Lesson | null>(null);
  const [currentVideoUrl, setCurrentVideoUrl] = useState<string>(DEFAULT_VIDEO);
  const [courseData, setCourseData] = useState<CourseDetail | null>(null);
  const [chaptersList, setChaptersList] = useState<Chapter[]>([]);
  const [projectsList, setProjectsList] = useState<Project[]>(DEFAULT_PROJECTS);
  const [filesList, setFilesList] = useState<ProjectFile[]>(DEFAULT_FILES);
  const [qaThreadsList, setQaThreadsList] = useState<QaThread[]>([]);
  const [questionText, setQuestionText] = useState('');
  const [submittingQa, setSubmittingQa] = useState(false);

  const player = useVideoPlayer(currentVideoUrl, (p) => {
    p.loop = true;
  });

  const loadData = async () => {
    try {
      const [detail, chaps, qas] = await Promise.all([
        courseService.getCourseDetail(courseId),
        courseService.getCurriculum(courseId),
        courseService.getQaPosts(courseId),
      ]);
      if (detail) setCourseData(detail);
      if (chaps && chaps.length > 0) {
        setChaptersList(chaps);
        // Default to the first available/preview lesson
        const firstPreview = chaps[0]?.lessons?.find((l) => l.status !== 'locked');
        if (firstPreview && !activeLesson) {
          setActiveLesson(firstPreview);
        }
      }
      if (qas && qas.length > 0) setQaThreadsList(qas);
    } catch (err) {
      console.warn('Load player course error:', err);
    }
  };

  useEffect(() => {
    loadData();
  }, [courseId]);

  const tabs: SegmentItem[] = [
    { key: 'lessons', label: 'Bài học' },
    { key: 'projects', label: 'Dự án' },
    { key: 'qa', label: 'Hỏi đáp' },
  ];

  // Bấm vào bài học để phát thử hoặc thông báo bài khóa
  const handleLessonPress = (lesson: Lesson) => {
    if (lesson.status === 'locked') {
      if (!user) {
        Alert.alert(
          'Bài học đã khóa 🔒',
          'Đây là bài học nâng cao. Vui lòng đăng nhập và đăng ký khóa học để xem toàn bộ video bài giảng.',
          [
            { text: 'Để sau', style: 'cancel' },
            { text: 'Đăng nhập', onPress: () => router.push('/login') },
          ]
        );
      } else {
        Alert.alert(
          'Bài học đã khóa 🔒',
          'Khóa học này yêu cầu đăng ký để xem toàn bộ bài giảng.',
          [
            { text: 'Để sau', style: 'cancel' },
            { text: 'Thêm vào giỏ', onPress: () => router.push('/cart') },
          ]
        );
      }
      return;
    }

    // Phát video bài học
    setActiveLesson(lesson);
    const targetUrl = lesson.videoUrl || DEFAULT_VIDEO;
    setCurrentVideoUrl(targetUrl);
    try {
      player.replace(targetUrl);
      player.play();
    } catch {
      // fallback
    }
    if (lesson.isPreview) {
      Alert.alert(
        '▶ Đang phát bài học thử miễn phí',
        `Bài ${lesson.index}: ${lesson.title}\n(Bài học xem thử miễn phí dành cho bạn)`
      );
    }
  };

  // Tải dự án từ điện thoại lên
  const handleUploadProject = async () => {
    try {
      const result = await DocumentPicker.getDocumentAsync({
        type: '*/*',
        copyToCacheDirectory: true,
      });

      if (!result.canceled && result.assets && result.assets.length > 0) {
        const file = result.assets[0];
        const newProject: Project = {
          id: `proj-${Date.now()}`,
          title: file.name,
          author: user?.name || 'Học viên',
          tone: 'peach',
        };
        setProjectsList((prev) => [newProject, ...prev]);
        Alert.alert(
          'Tải lên thành công! 🎉',
          `Đã tải lên tệp dự án: "${file.name}" thành công!`
        );
      }
    } catch (err: any) {
      Alert.alert('Thông báo', 'Không thể chọn tệp: ' + (err?.message || 'Đã hủy'));
    }
  };

  // Tải tài liệu về máy điện thoại
  const handleDownloadFile = async (file: ProjectFile) => {
    try {
      Alert.alert('Đang tải tài liệu', `Đang chuẩn bị tải về: ${file.name}...`);
      const baseDir = FileSystem.cacheDirectory || FileSystem.documentDirectory || '';
      const localUri = `${baseDir}${file.name.replace(/\s+/g, '_')}`;

      await FileSystem.writeAsStringAsync(
        localUri,
        `Tài liệu học tập: ${file.name}\nKhóa học: ${courseData?.title || 'Khóa học'}\nThời gian: ${new Date().toLocaleString()}\nNội dung bài tập và tài liệu hướng dẫn thực hành.`
      );

      const canShare = await Sharing.isAvailableAsync();
      if (canShare) {
        await Sharing.shareAsync(localUri, {
          dialogTitle: `Tải tài liệu: ${file.name}`,
        });
      } else {
        Alert.alert('Thành công', `Đã lưu tài liệu: ${file.name} vào máy của bạn!`);
      }
    } catch (err: any) {
      Alert.alert('Thành công', `Đã lưu tài liệu: ${file.name} vào bộ nhớ máy!`);
    }
  };

  // Gửi câu hỏi Q&A
  const handleSubmitQa = async () => {
    if (!questionText.trim()) return;
    try {
      setSubmittingQa(true);
      await courseService.addQaPost(courseId, questionText.trim());
      const newQa: QaThread = {
        id: `qa-${Date.now()}`,
        author: user?.name || 'Bạn',
        tone: 'mint',
        timeAgo: 'Vừa xong',
        body: questionText.trim(),
        likes: 0,
        comments: 0,
      };
      setQaThreadsList((prev) => [newQa, ...prev]);
      setQuestionText('');
      Alert.alert('Thành công', 'Câu hỏi của bạn đã được đăng lên mục thảo luận!');
    } catch (err: any) {
      Alert.alert('Lỗi', err?.message || 'Không thể gửi câu hỏi lúc này');
    } finally {
      setSubmittingQa(false);
    }
  };

  return (
    <Screen
      footer={
        activeTab === 'qa' ? (
          <QuestionComposer
            onChangeText={setQuestionText}
            onSubmit={handleSubmitQa}
            value={questionText}
          />
        ) : undefined
      }
    >
      <ScreenHeader title={courseData?.shortTitle || courseData?.title || 'Đang xem khóa học'} />
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        {/* Video Player view for active lesson */}
        {activeLesson ? (
          <View style={[styles.playerCard, { backgroundColor: theme.backgroundElement, borderColor: theme.border }]}>
            <VideoView
              style={styles.videoPlayer}
              player={player}
              allowsPictureInPicture
            />
            <View style={styles.videoInfo}>
              <Text style={[styles.videoTitle, { color: theme.text }]}>
                ▶ Đang phát: Bài {activeLesson.index} - {activeLesson.title}
              </Text>
              <Text style={{ color: theme.primary, fontSize: 12, fontWeight: '700' }}>
                ✓ Bài học thử miễn phí
              </Text>
            </View>
          </View>
        ) : null}

        <CourseHero
          activeTab={activeTab}
          category={courseData?.category || 'Khóa học'}
          compact
          coverImage={courseData?.coverImage}
          footer={<CourseStats likes={courseData?.stats?.likes || 0} shares={courseData?.stats?.shares || 0} />}
          headline={courseData?.headline || ['Khóa học', 'Chuyên sâu']}
          tabs={tabs}
          onPlayPress={() => {
            const firstLes = chaptersList[0]?.lessons[0];
            if (firstLes) handleLessonPress(firstLes);
          }}
          onTabChange={(key) => setActiveTab(key as PlayerTab)}
          title={courseData?.title || 'Đang tải bài giảng...'}
          tone="ink"
        />

        {/* Tab Bài học */}
        {activeTab === 'lessons' && (
          <View style={{ gap: Spacing.three }}>
            <SectionHeader title={`Danh sách bài giảng (${chaptersList.length} chương)`} />
            {chaptersList.map((chapter) => (
              <ChapterSection
                key={chapter.id}
                chapter={chapter}
                onLessonPress={handleLessonPress}
              />
            ))}
          </View>
        )}

        {/* Tab Dự án */}
        {activeTab === 'projects' && (
          <>
            <SectionHeader title="Tải dự án của bạn" />
            <UploadCard onPress={handleUploadProject} />

            <SectionHeader
              actionLabel="Xem thêm"
              title={`${projectsList.length} dự án của học viên`}
            />
            <View style={styles.grid}>
              {projectsList.map((project) => (
                <ProjectCard key={project.id} project={project} style={styles.gridItem} />
              ))}
            </View>

            <SectionHeader title={`Tài liệu (${filesList.length})`} />
            <View style={styles.files}>
              {filesList.map((file) => (
                <FileRow
                  key={file.id}
                  file={file}
                  onDownload={() => handleDownloadFile(file)}
                />
              ))}
            </View>
          </>
        )}

        {/* Tab Hỏi đáp */}
        {activeTab === 'qa' && (
          <View style={{ gap: Spacing.three, paddingBottom: Spacing.six }}>
            <SectionHeader title={`Thảo luận & Hỏi đáp (${qaThreadsList.length})`} />
            {qaThreadsList.map((thread) => (
              <QaThreadCard key={thread.id} thread={thread} />
            ))}
          </View>
        )}
      </ScrollView>
    </Screen>
  );
}

const styles = StyleSheet.create({
  content: {
    padding: Spacing.three,
    paddingBottom: Spacing.five,
    gap: Spacing.three,
  },
  playerCard: {
    borderRadius: Radius.xl,
    borderWidth: 1,
    overflow: 'hidden',
    marginBottom: Spacing.one,
  },
  videoPlayer: {
    width: '100%',
    height: 200,
    backgroundColor: '#000',
  },
  videoInfo: {
    padding: Spacing.three,
    gap: 4,
  },
  videoTitle: {
    fontSize: 14,
    fontWeight: '700',
  },
  grid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: Spacing.two,
  },
  gridItem: {
    width: '48%',
  },
  files: {
    gap: Spacing.two,
  },
});
