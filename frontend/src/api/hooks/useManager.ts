import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../client';
import type { TeamWeekStatusDto, PlanningWeekDto } from '../types';

export function useTeamWeeks() {
  return useQuery({
    queryKey: ['manager', 'team', 'weeks'],
    queryFn: () => apiClient<TeamWeekStatusDto[]>('/manager/team/weeks'),
  });
}

export function useTeamMemberWeek(userId: number, weekId: number) {
  return useQuery({
    queryKey: ['manager', 'users', userId, 'weeks', weekId],
    queryFn: () => apiClient<PlanningWeekDto>(`/manager/users/${userId}/weeks/${weekId}`),
    enabled: userId > 0 && weekId > 0,
  });
}
