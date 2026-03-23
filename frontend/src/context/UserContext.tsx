import { createContext, useContext, useState, type ReactNode } from 'react';
import { useQueryClient } from '@tanstack/react-query';
import { setCurrentUserId } from '../api/client';
import type { UserDto } from '../api/types';

interface UserContextType {
  currentUser: UserDto | null;
  setUser: (user: UserDto) => void;
}

const UserCtx = createContext<UserContextType | null>(null);

export function UserProvider({ children }: { children: ReactNode }) {
  const queryClient = useQueryClient();
  const [currentUser, setCurrentUser] = useState<UserDto | null>(() => {
    const stored = localStorage.getItem('wp_current_user');
    if (stored) {
      const user = JSON.parse(stored) as UserDto;
      setCurrentUserId(user.id);
      return user;
    }
    return null;
  });

  function setUser(user: UserDto) {
    setCurrentUser(user);
    setCurrentUserId(user.id);
    localStorage.setItem('wp_current_user', JSON.stringify(user));
    queryClient.invalidateQueries({ queryKey: ['weeks'] });
    queryClient.invalidateQueries({ queryKey: ['manager'] });
  }

  return (
    <UserCtx.Provider value={{ currentUser, setUser }}>
      {children}
    </UserCtx.Provider>
  );
}

export function useUserContext(): UserContextType {
  const ctx = useContext(UserCtx);
  if (!ctx) throw new Error('useUserContext must be used within UserProvider');
  return ctx;
}
