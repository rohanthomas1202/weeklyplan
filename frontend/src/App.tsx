import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ThemeProvider } from './context/ThemeContext';
import { UserProvider } from './context/UserContext';
import { AppShell } from './layout/AppShell';
import { ToastContainer } from './components/Toast';
import { PlanningPage } from './features/planning/PlanningPage';
import { ReconciliationPage } from './features/reconciliation/ReconciliationPage';
import { ManagerDashboardPage } from './features/manager/ManagerDashboardPage';
import { MemberWeekView } from './features/manager/MemberWeekView';
import './App.css';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: { staleTime: 30_000 },
  },
});

export function App() {
  return (
    <ThemeProvider>
    <QueryClientProvider client={queryClient}>
      <UserProvider>
        <BrowserRouter>
          <Routes>
            <Route element={<AppShell />}>
              <Route index element={<Navigate to="/my-week" replace />} />
              <Route path="/my-week" element={<PlanningPage />} />
              <Route path="/my-week/reconcile" element={<ReconciliationPage />} />
              <Route path="/manager/team" element={<ManagerDashboardPage />} />
              <Route path="/manager/team/:userId/:weekId" element={<MemberWeekView />} />
            </Route>
          </Routes>
          <ToastContainer />
        </BrowserRouter>
      </UserProvider>
    </QueryClientProvider>
    </ThemeProvider>
  );
}
