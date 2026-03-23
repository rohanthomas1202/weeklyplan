import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../client';
import type { PlanningWeekDto } from '../types';

export function useCurrentWeek() {
  return useQuery({
    queryKey: ['weeks', 'current'],
    queryFn: () => apiClient<PlanningWeekDto>('/weeks/current'),
  });
}

export function useWeek(weekId: number) {
  return useQuery({
    queryKey: ['weeks', weekId],
    queryFn: () => apiClient<PlanningWeekDto>(`/weeks/${weekId}`),
    enabled: weekId > 0,
  });
}
