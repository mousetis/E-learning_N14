import { useLocalSearchParams, useRouter } from '@/navigation/routerHook';
import { ScrollView, StyleSheet, View } from 'react-native';

import { CourseStats } from '@/components/course/course-stats';
import { FileRow } from '@/components/course/file-row';
import { ProjectCard } from '@/components/course/project-card';
import { UploadCard } from '@/components/course/upload-card';
import { CourseHero } from '@/components/ui/course-hero';
import { Screen } from '@/components/ui/screen';
import { ScreenHeader } from '@/components/ui/screen-header';
import { SectionHeader } from '@/components/ui/section-header';
import type { SegmentItem } from '@/components/ui/segmented-tabs';
import { Spacing } from '@/constants/theme';
import { course, courseFiles, courseProjects } from '@/data/course-detail';
import {
  courseCurriculumHref,
  coursePlayerHref,
  courseQaHref,
} from '@/lib/routes';

export default function CoursePlayerScreen() {
  const { id } = useLocalSearchParams<{ id?: string }>();
  const courseId = typeof id === 'string' && id.length > 0 ? id : course.id;
  const router = useRouter();

  const tabs: SegmentItem[] = [
    { key: 'lessons', label: 'Bài học', href: courseCurriculumHref(courseId) },
    { key: 'projects', label: 'Dự án', href: coursePlayerHref(courseId) },
    { key: 'qa', label: 'Hỏi đáp', href: courseQaHref(courseId) },
  ];

  return (
    <Screen>
      <ScreenHeader title={course.shortTitle} />
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <CourseHero
          activeTab="projects"
          category={course.category}
          compact
          footer={<CourseStats likes={course.stats.likes} shares={course.stats.shares} />}
          headline={course.headline}
          tabs={tabs}
          title={course.title}
          tone="ink"
        />

        <SectionHeader title="Tải dự án của bạn" />
        <UploadCard />

        <SectionHeader actionLabel="Xem thêm" title="12 dự án của học viên" />
        <View style={styles.grid}>
          {courseProjects.map((project) => (
            <ProjectCard key={project.id} project={project} style={styles.gridItem} />
          ))}
        </View>

        <SectionHeader title={`Tài liệu (${courseFiles.length})`} />
        <View style={styles.files}>
          {courseFiles.map((file) => (
            <FileRow key={file.id} file={file} />
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
  grid: {
    flexDirection: 'row',
    gap: Spacing.three,
  },
  gridItem: {
    flex: 1,
  },
  files: {
    gap: Spacing.two,
  },
});
