import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../client';
import type { UserDto } from '../types';

export function useUsers() {
  return useQuery({
    queryKey: ['users'],
    queryFn: () => apiClient<UserDto[]>('/users'),
  });
}
