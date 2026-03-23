import { createContext, useContext, useState, useEffect, type ReactNode } from 'react';

type Theme = 'light' | 'dark';

interface ThemeContextType {
  theme: Theme;
  toggleTheme: () => void;
}

const ThemeCtx = createContext<ThemeContextType | null>(null);

function getInitialTheme(): Theme {
  const stored = localStorage.getItem('wp_theme');
  if (stored === 'light' || stored === 'dark') return stored;
  if (window.matchMedia('(prefers-color-scheme: dark)').matches) return 'dark';
  return 'light';
}

export function ThemeProvider({ children }: { children: ReactNode }) {
  const [theme, setTheme] = useState<Theme>(getInitialTheme);

  useEffect(() => {
    document.documentElement.dataset.theme = theme;
    localStorage.setItem('wp_theme', theme);
  }, [theme]);

  function toggleTheme() {
    setTheme(prev => prev === 'light' ? 'dark' : 'light');
  }

  return (
    <ThemeCtx.Provider value={{ theme, toggleTheme }}>
      {children}
    </ThemeCtx.Provider>
  );
}

export function useTheme(): ThemeContextType {
  const ctx = useContext(ThemeCtx);
  if (!ctx) throw new Error('useTheme must be used within ThemeProvider');
  return ctx;
}
