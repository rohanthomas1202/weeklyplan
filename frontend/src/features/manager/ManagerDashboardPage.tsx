import { useUserContext } from '../../context/UserContext';
import { TeamStatusGrid } from './TeamStatusGrid';
import './ManagerDashboardPage.css';

export function ManagerDashboardPage() {
  const { currentUser } = useUserContext();

  if (!currentUser) {
    return (
      <div className="manager-dashboard">
        <div className="manager-dashboard__denied">
          Select a user to continue.
        </div>
      </div>
    );
  }

  if (currentUser.role !== 'MANAGER') {
    return (
      <div className="manager-dashboard">
        <div className="manager-dashboard__denied">
          Access denied — manager role required.
        </div>
      </div>
    );
  }

  return (
    <div className="manager-dashboard">
      <div className="manager-dashboard__header">
        <h1 className="manager-dashboard__title">Team Dashboard</h1>
      </div>
      <TeamStatusGrid />
    </div>
  );
}
