import { useQuery } from '@tanstack/react-query';
import { apiClient } from '../client';
import type { StrategyTreeNode, ChessCategoryDto } from '../types';

export function useStrategyTree() {
  return useQuery({
    queryKey: ['strategy', 'tree'],
    queryFn: () => apiClient<StrategyTreeNode[]>('/strategy/tree'),
  });
}

export function useChessCategories() {
  return useQuery({
    queryKey: ['chess-categories'],
    queryFn: () => apiClient<ChessCategoryDto[]>('/chess-categories'),
  });
}
