import { useLocalSearchParams, useRouter } from '@/navigation/routerHook';
import { useState } from 'react';
import { ScrollView, StyleSheet, View } from 'react-native';

import { CourseStats } from '@/components/course/course-stats';
import { QaThreadCard } from '@/components/course/qa-thread-card';
import { QuestionComposer } from '@/components/course/question-composer';
import { CourseHero } from '@/components/ui/course-hero';
import { Screen } from '@/components/ui/screen';
import { ScreenHeader } from '@/components/ui/screen-header';
import type { SegmentItem } from '@/components/ui/segmented-tabs';
import { Spacing } from '@/constants/theme';
import { course, qaThreads } from '@/data/course-detail';
import {
  courseCurriculumHref,
  coursePlayerHref,
  courseQaHref,
} from '@/lib/routes';

export default function CourseQaScreen() {
  const { id } = useLocalSearchParams<{ id?: string }>();
  const courseId = typeof id === 'string' && id.length > 0 ? id : course.id;
  const router = useRouter();
  const [question, setQuestion] = useState('');

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
          onSubmit={() => setQuestion('')}
          value={question}
        />
      }
    >
      <ScreenHeader title={course.shortTitle} />
      <ScrollView contentContainerStyle={styles.content} showsVerticalScrollIndicator={false}>
        <CourseHero
          activeTab="qa"
          category={course.category}
          compact
          footer={<CourseStats likes={course.stats.likes} shares={course.stats.shares} />}
          headline={course.headline}
          tabs={tabs}
          title={course.title}
          tone="ink"
        />

        <View style={styles.threads}>
          {qaThreads.map((thread) => (
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
