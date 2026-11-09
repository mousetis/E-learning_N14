import type { Href } from 'expo-router';

export const courseOverviewHref = (id: string): Href => ({
  pathname: '/course/[id]',
  params: { id },
});

export const courseCurriculumHref = (id: string): Href => ({
  pathname: '/course/[id]/curriculum',
  params: { id },
});

export const courseReviewsHref = (id: string): Href => ({
  pathname: '/course/[id]/reviews',
  params: { id },
});

export const coursePlayerHref = (id: string): Href => ({
  pathname: '/course/[id]/player',
  params: { id },
});

export const courseQaHref = (id: string): Href => ({
  pathname: '/course/[id]/qa',
  params: { id },
});

export const instructorHref = (id: string): Href => ({
  pathname: '/instructor/[id]',
  params: { id },
});
