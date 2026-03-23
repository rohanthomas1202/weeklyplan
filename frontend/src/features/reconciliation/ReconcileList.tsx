import { ReconcileCard } from './ReconcileCard';
import type { WeeklyCommitDto, PlanningWeekStatus } from '../../api/types';
import './ReconcileList.css';

interface ReconcileListProps {
  commits: WeeklyCommitDto[];
  weekId: number;
  weekStatus: PlanningWeekStatus;
  onCommitReconciled: () => void;
}

export function ReconcileList({ commits, weekId, weekStatus, onCommitReconciled }: ReconcileListProps) {
  const reconciledCount = commits.filter((c) => c.reconciliation != null).length;
  const total = commits.length;
  const progressPercent = total > 0 ? Math.round((reconciledCount / total) * 100) : 0;

  return (
    <div className="reconcile-list">
      <div className="reconcile-list__progress">
        <div className="reconcile-list__progress-text">
          <span className="reconcile-list__progress-count">
            {reconciledCount === total && total > 0
              ? `All ${total} commits reconciled`
              : `${reconciledCount} of ${total} commits reconciled`}
          </span>
          <span className={`reconcile-list__progress-pct${reconciledCount === total && total > 0 ? ' reconcile-list__progress-pct--complete' : ''}`}>
            {progressPercent}%
          </span>
        </div>
        <div className="reconcile-list__progress-bar">
          <div
            className={`reconcile-list__progress-fill${reconciledCount === total && total > 0 ? ' reconcile-list__progress-fill--complete' : ''}`}
            style={{ width: `${progressPercent}%` }}
          />
        </div>
        {total > 0 && reconciledCount < total && (
          <span className="reconcile-list__progress-remaining">
            {total - reconciledCount} remaining
          </span>
        )}
      </div>

      <div className="reconcile-list__items">
        {commits.map((commit) => (
          <ReconcileCard
            key={commit.id}
            commit={commit}
            weekId={weekId}
            weekStatus={weekStatus}
            onReconciled={onCommitReconciled}
          />
        ))}
      </div>

      {commits.length === 0 && (
        <div className="reconcile-list__empty">No commits to reconcile.</div>
      )}
    </div>
  );
}
