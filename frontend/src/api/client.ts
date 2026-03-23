import type { ErrorResponse } from './types';

let currentUserId: number | null = null;

export function setCurrentUserId(id: number) {
  currentUserId = id;
}

export function getCurrentUserId(): number | null {
  return currentUserId;
}

export class ApiError extends Error {
  constructor(
    public status: number,
    public errorResponse: ErrorResponse,
  ) {
    super(errorResponse.message);
  }
}

export async function apiClient<T>(
  path: string,
  options: RequestInit = {},
): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  };

  if (currentUserId !== null) {
    headers['X-User-Id'] = String(currentUserId);
  }

  const response = await fetch(`/api${path}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    const body = await response.json().catch(() => ({
      error: 'UNKNOWN',
      message: response.statusText,
    }));
    throw new ApiError(response.status, body as ErrorResponse);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}
