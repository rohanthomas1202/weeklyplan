import { useMutation, useQueryClient } from '@tanstack/react-query';
import { apiClient } from '../client';
import type { WeeklyCommitDto, CreateCommitRequest, ReorderRequest } from '../types';

export function useCreateCommit(weekId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: CreateCommitRequest) =>
      apiClient<WeeklyCommitDto>(`/weeks/${weekId}/commits`, {
        method: 'POST',
        body: JSON.stringify(data),
      }),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['weeks'] });
    },
  });
}

export function useUpdateCommit(weekId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ commitId, data }: { commitId: number; data: Partial<CreateCommitRequest> }) =>
      apiClient<WeeklyCommitDto>(`/weeks/${weekId}/commits/${commitId}`, {
        method: 'PUT',
        body: JSON.stringify(data),
      }),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['weeks'] });
    },
  });
}

export function useDeleteCommit(weekId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (commitId: number) =>
      apiClient<void>(`/weeks/${weekId}/commits/${commitId}`, {
        method: 'DELETE',
      }),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['weeks'] });
    },
  });
}

export function useReorderCommits(weekId: number) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: ReorderRequest) =>
      apiClient<void>(`/weeks/${weekId}/reorder`, {
        method: 'PUT',
        body: JSON.stringify(data),
      }),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['weeks'] });
    },
  });
}

export function useLockWeek() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (weekId: number) =>
      apiClient<void>(`/weeks/${weekId}/lock`, {
        method: 'POST',
      }),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ['weeks'] });
    },
  });
}
