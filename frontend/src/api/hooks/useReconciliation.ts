import { useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from '../client';
import type { PlanningWeekDto, WeeklyCommitDto, ReconcileCommitRequest } from '../types';

export function useStartReconciliation() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (weekId: number) =>
      apiClient<PlanningWeekDto>(`/weeks/${weekId}/start-reconciliation`, {
        method: 'POST',
      }),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['weeks'] });
    },
  });
}

export function useReconcileCommit(weekId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ commitId, data }: { commitId: number; data: ReconcileCommitRequest }) =>
      apiClient<WeeklyCommitDto>(`/weeks/${weekId}/commits/${commitId}/reconcile`, {
        method: 'PUT',
        body: JSON.stringify(data),
      }),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['weeks'] });
    },
  });
}

export function useUpdateWeekSummary(weekId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: { blockersSummary?: string; managerNotes?: string }) =>
      apiClient<PlanningWeekDto>(`/weeks/${weekId}/summary`, {
        method: 'PUT',
        body: JSON.stringify(data),
      }),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['weeks'] });
    },
  });
}

export function useReconcileWeek() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (weekId: number) =>
      apiClient<PlanningWeekDto>(`/weeks/${weekId}/reconcile`, {
        method: 'POST',
      }),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['weeks'] });
    },
  });
}

export function useCarryForward() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (weekId: number) =>
      apiClient<PlanningWeekDto>(`/weeks/${weekId}/carry-forward`, {
        method: 'POST',
      }),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['weeks'] });
    },
  });
}
