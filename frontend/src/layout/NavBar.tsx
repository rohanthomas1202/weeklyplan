import { NavLink } from 'react-router-dom';
import { useUserContext } from '../context/UserContext';
import { useTheme } from '../context/ThemeContext';
import { UserSwitcher } from './UserSwitcher';
import './NavBar.css';

export function NavBar() {
  const { currentUser } = useUserContext();
  const { theme, toggleTheme } = useTheme();

  return (
    <nav className="navbar">
      <div className="navbar-left">
        <span className="navbar-logo">
          <span className="navbar-logo-icon">W</span>
          WeeklyPlan
        </span>
        <div className="navbar-links">
          <NavLink
            to="/my-week"
            className={({ isActive }) => `navbar-link${isActive ? ' navbar-link--active' : ''}`}
          >
            My Week
          </NavLink>
          {currentUser?.role === 'MANAGER' && (
            <NavLink
              to="/manager/team"
              className={({ isActive }) => `navbar-link${isActive ? ' navbar-link--active' : ''}`}
            >
              Team
            </NavLink>
          )}
        </div>
      </div>
      <div className="navbar-right">
        <button
          className="navbar-theme-toggle"
          onClick={toggleTheme}
          aria-label={theme === 'light' ? 'Switch to dark mode' : 'Switch to light mode'}
        >
          {theme === 'light' ? '☾' : '☀'}
        </button>
        <UserSwitcher />
      </div>
    </nav>
  );
}
