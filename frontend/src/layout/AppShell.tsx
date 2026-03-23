import { Outlet } from 'react-router-dom';
import { NavBar } from './NavBar';
import './AppShell.css';

export function AppShell() {
  return (
    <div className="app-shell">
      <NavBar />
      <main className="app-shell-main">
        <Outlet />
      </main>
    </div>
  );
}
