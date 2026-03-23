import { Link } from 'react-router-dom';
import { Badge } from '../../components/Badge';
import { Button } from '../../components/Button';
import type { PlanningWeekDto } from '../../api/types';
import './WeekHeader.css';

interface WeekHeaderProps {
  week: PlanningWeekDto;
  onAddCommit: () => void;
  onLockWeek: () => void;
}

function formatDateRange(startDate: string, endDate: string): string {
  const start = new Date(startDate + 'T00:00:00');
  const end = new Date(endDate + 'T00:00:00');
  const monthNames = [
    'January', 'February', 'March', 'April', 'May', 'June',
    'July', 'August', 'September', 'October', 'November', 'December',
  ];
  const month = monthNames[start.getMonth()];
  const startDay = start.getDate();
  const endDay = end.getDate();
  const year = start.getFullYear();
  return `Week of ${month} ${startDay} \u2013 ${endDay}, ${year}`;
}

export function WeekHeader({ week, onAddCommit, onLockWeek }: WeekHeaderProps) {
  const dateLabel = formatDateRange(week.weekStartDate, week.weekEndDate);

  return (
    <div className="week-header">
      <div className="week-header__left">
        <h2 className="week-header__title">{dateLabel}</h2>
        <Badge label={week.status} variant={week.status} />
      </div>
      <div className="week-header__actions">
        {week.status === 'DRAFT' && (
          <>
            <Button variant="secondary" onClick={onAddCommit}>
              Add Commit
            </Button>
            <Button variant="primary" onClick={onLockWeek}>
              Lock Week
            </Button>
          </>
        )}
        {week.status === 'LOCKED' && (
          <Link to="/my-week/reconcile" className="week-header__link-btn">
            Start Reconciliation
          </Link>
        )}
        {(week.status === 'RECONCILING' || week.status === 'RECONCILED') && (
          <Link to="/my-week/reconcile" className="week-header__link-btn">
            View Reconciliation
          </Link>
        )}
      </div>
    </div>
  );
}
