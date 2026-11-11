import { useLocalSearchParams, useRouter } from '@/navigation/routerHook';
import { useState, useEffect } from 'react';
import { Alert, ScrollView, StyleSheet, View } from 'react-native';

import { CourseStats } from '@/components/course/course-stats';
import { QaThreadCard } from '@/components/course/qa-thread-card';
import { QuestionComposer } from '@/components/course/question-composer';
import { CourseHero } from '@/components/ui/course-hero';
import { Screen } from '@/components/ui/screen';
import { ScreenHeader } from '@/components/ui/screen-header';
import type { SegmentItem } from '@/components/ui/segmented-tabs';
import { Spacing } from '@/constants/theme';
import {
  courseCurriculumHref,
  coursePlayerHref,
  courseQaHref,
} from '@/lib/routes';
import { courseService } from '@/services/courseService';
import type { QaThread, CourseDetail } from '@/types';

export default function CourseQaScreen() {
  const { id } = useLocalSearchParams<{ id?: string }>();
  const courseId = typeof id === 'string' && id.length > 0 ? id : '';
  const router = useRouter();
  const [question, setQuestion] = useState('');
  const [courseData, setCourseData] = useState<CourseDetail | null>(null);
  const [threads, setThreads] = useState<QaThread[]>([]);
  const [submitting, setSubmitting] = useState(false);

  const loadData = async () => {
    try {
      const [detail, qas] = await Promise.all([
        courseService.getCourseDetail(courseId),
        courseService.getQaPosts(courseId),
      ]);
      if (detail) setCourseData(detail);
      if (qas && qas.length > 0) setThreads(qas);
    } catch (err) {
      console.warn('Load QA data error:', err);
    }
  };

  useEffect(() => {
    loadData();
  }, [courseId]);

  const handleSubmitQuestion = async () => {
    if (!question.trim()) return;
    try {
      setSubmitting(true);
      await courseService.addQaPost(courseId, question.trim());
      setQuestion('');
      Alert.alert('Thành công', 'Câu hỏi của bạn đã được gửi tới giảng viên!');
      await loadData();
    } catch (err: any) {
      Alert.alert('Lỗi', err?.message || 'Không thể gửi câu hỏi lúc này. Vui lòng đăng nhập.');
    } finally {
      setSubmitting(false);
    }
  };

  const tabs: SegmentItem[] = [
    { key: 'lessons', label: 'Bài học', href: courseCurriculumHref(courseId) },
    { key: 'projects', label: 'Dự án', href: coursePlayerHref(courseId) },
    { key: 'qa', label: 'Hỏi đáp', href: courseQaHref(courseId) },
  ];

  return (
    <Screen
      footer={
        <QuestionComposer
          onChangeText={setQuestion}
          onSubmit={handleSubmitQuestion}
          value={question}
        />
      }
    >
      <ScreenHeader title={courseData?.shortTitle || courseData?.title || 'Hỏi đáp'} />
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <CourseHero
          activeTab="qa"
          category={courseData?.category || 'Khóa học'}
          compact
          coverImage={courseData?.coverImage}
          footer={<CourseStats likes={courseData?.stats?.likes || 0} shares={courseData?.stats?.shares || 0} />}
          headline={courseData?.headline || ['Khóa học', 'Chuyên sâu']}
          tabs={tabs}
          title={courseData?.title || 'Khóa học'}
          tone="ink"
        />

        <View style={styles.threads}>
          {threads.map((thread) => (
            <QaThreadCard key={thread.id} thread={thread} />
          ))}
        </View>
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
  threads: {
    gap: Spacing.three,
  },
});
