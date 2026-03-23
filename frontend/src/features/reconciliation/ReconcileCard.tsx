import { Badge } from '../../components/Badge';
import { ReconcileForm } from './ReconcileForm';
import type { WeeklyCommitDto, PlanningWeekStatus, CommitDisposition } from '../../api/types';
import './ReconcileCard.css';

const DISPOSITION_LABELS: Record<CommitDisposition, string> = {
  COMPLETED: 'Completed',
  PARTIALLY_COMPLETED: 'Partially Completed',
  NOT_COMPLETED: 'Not Completed',
  CARRIED_FORWARD: 'Carried Forward',
  DROPPED: 'Dropped',
};

const DISPOSITION_CSS_CLASS: Record<CommitDisposition, string> = {
  COMPLETED: 'reconcile-card--completed',
  PARTIALLY_COMPLETED: 'reconcile-card--partial',
  NOT_COMPLETED: 'reconcile-card--not-completed',
  CARRIED_FORWARD: 'reconcile-card--carried-forward',
  DROPPED: 'reconcile-card--dropped',
};

interface ReconcileCardProps {
  commit: WeeklyCommitDto;
  weekId: number;
  weekStatus: PlanningWeekStatus;
  onReconciled: () => void;
}

export function ReconcileCard({ commit, weekId, weekStatus, onReconciled }: ReconcileCardProps) {
  const recon = commit.reconciliation;
  const isReconciled = recon != null;
  const dispositionClass = recon ? DISPOSITION_CSS_CLASS[recon.disposition] : 'reconcile-card--unreconciled';

  return (
    <div className={`reconcile-card ${dispositionClass}`}>
      {/* Left panel — planned */}
      <div className="reconcile-card__planned">
        <div className="reconcile-card__planned-top">
          <Badge label={commit.chessCategoryDisplayName} variant={commit.chessCategoryCode} />
          <span className="reconcile-card__rank">#{commit.priorityRank}</span>
          {isReconciled && (
            <span className="reconcile-card__checkmark" aria-label="Reconciled">✓</span>
          )}
        </div>
        <div className="reconcile-card__title">{commit.title}</div>
        {commit.stretch && <span className="reconcile-card__stretch">Stretch</span>}
        <div className="reconcile-card__rcdo">
          <span className="reconcile-card__rcdo-icon">🎯</span>
          <span className="reconcile-card__rcdo-chip" title={commit.rallyCryTitle}>
            {commit.rallyCryTitle}
          </span>
          <span className="reconcile-card__rcdo-arrow">→</span>
          <span className="reconcile-card__rcdo-chip" title={commit.definingObjectiveTitle}>
            {commit.definingObjectiveTitle}
          </span>
          <span className="reconcile-card__rcdo-arrow">→</span>
          <span className="reconcile-card__rcdo-chip" title={commit.outcomeTitle}>
            {commit.outcomeTitle}
          </span>
        </div>
      </div>

      {/* Right panel — actual */}
      <div className="reconcile-card__actual">
        {weekStatus === 'RECONCILING' && (
          <ReconcileForm commit={commit} weekId={weekId} onSaved={onReconciled} />
        )}

        {weekStatus === 'RECONCILED' && recon && (
          <div className="reconcile-card__readonly">
            <div className="reconcile-card__readonly-row">
              <span className="reconcile-card__readonly-label">Disposition</span>
              <span className={`reconcile-card__disposition-badge reconcile-card__disposition-badge--${recon.disposition.toLowerCase().replace(/_/g, '-')}`}>
                {DISPOSITION_LABELS[recon.disposition]}
              </span>
            </div>
            {recon.percentComplete != null && (
              <div className="reconcile-card__readonly-row">
                <span className="reconcile-card__readonly-label">% Complete</span>
                <span className="reconcile-card__readonly-value">{recon.percentComplete}%</span>
              </div>
            )}
            {recon.actualResult && (
              <div className="reconcile-card__readonly-row reconcile-card__readonly-row--block">
                <span className="reconcile-card__readonly-label">Actual Result</span>
                <span className="reconcile-card__readonly-value">{recon.actualResult}</span>
              </div>
            )}
            {recon.blockerNotes && (
              <div className="reconcile-card__readonly-row reconcile-card__readonly-row--block">
                <span className="reconcile-card__readonly-label">Blocker Notes</span>
                <span className="reconcile-card__readonly-value">{recon.blockerNotes}</span>
              </div>
            )}
            {recon.carryForward && (
              <div className="reconcile-card__readonly-row">
                <span className="reconcile-card__carry-tag">Carry Forward</span>
              </div>
            )}
          </div>
        )}

        {weekStatus === 'RECONCILED' && !recon && (
          <div className="reconcile-card__no-recon">No reconciliation data</div>
        )}
      </div>
    </div>
  );
}
