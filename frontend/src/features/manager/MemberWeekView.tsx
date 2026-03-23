import { useParams, useNavigate } from 'react-router-dom';
import { useTeamMemberWeek } from '../../api/hooks/useManager';
import { Badge } from '../../components/Badge';
import { Button } from '../../components/Button';
import type { WeeklyCommitDto, CommitDisposition } from '../../api/types';
import './MemberWeekView.css';

const DISPOSITION_LABELS: Record<CommitDisposition, string> = {
  COMPLETED: 'Completed',
  PARTIALLY_COMPLETED: 'Partially Completed',
  NOT_COMPLETED: 'Not Completed',
  CARRIED_FORWARD: 'Carried Forward',
  DROPPED: 'Dropped',
};

const DISPOSITION_CSS_SUFFIX: Record<CommitDisposition, string> = {
  COMPLETED: 'completed',
  PARTIALLY_COMPLETED: 'partially-completed',
  NOT_COMPLETED: 'not-completed',
  CARRIED_FORWARD: 'carried-forward',
  DROPPED: 'dropped',
};

function MemberCommitCard({ commit }: { commit: WeeklyCommitDto }) {
  const recon = commit.reconciliation;

  return (
    <div className={`member-commit-card${recon ? ` member-commit-card--${DISPOSITION_CSS_SUFFIX[recon.disposition]}` : ''}`}>
      <div className="member-commit-card__planned">
        <div className="member-commit-card__top">
          <Badge label={commit.chessCategoryDisplayName} variant={commit.chessCategoryCode} />
          <span className="member-commit-card__rank">#{commit.priorityRank}</span>
          {commit.stretch && <span className="member-commit-card__stretch">Stretch</span>}
        </div>
        <div className="member-commit-card__title">{commit.title}</div>
        <div className="member-commit-card__rcdo">
          <span className="member-commit-card__rcdo-icon">🎯</span>
          <span className="member-commit-card__rcdo-chip" title={commit.rallyCryTitle}>
            {commit.rallyCryTitle}
          </span>
          <span className="member-commit-card__rcdo-arrow">→</span>
          <span className="member-commit-card__rcdo-chip" title={commit.definingObjectiveTitle}>
            {commit.definingObjectiveTitle}
          </span>
          <span className="member-commit-card__rcdo-arrow">→</span>
          <span className="member-commit-card__rcdo-chip" title={commit.outcomeTitle}>
            {commit.outcomeTitle}
          </span>
        </div>
      </div>

      {recon && (
        <div className="member-commit-card__actual">
          <div className="member-commit-card__actual-header">Actual</div>
          <div className="member-commit-card__actual-row">
            <span className={`member-commit-card__disposition member-commit-card__disposition--${DISPOSITION_CSS_SUFFIX[recon.disposition]}`}>
              {DISPOSITION_LABELS[recon.disposition]}
            </span>
            {recon.percentComplete != null && (
              <span className="member-commit-card__pct">{recon.percentComplete}%</span>
            )}
          </div>
          {recon.actualResult && (
            <p className="member-commit-card__actual-text">{recon.actualResult}</p>
          )}
          {recon.blockerNotes && (
            <div className="member-commit-card__blocker">
              <span className="member-commit-card__blocker-label">Blocker:</span> {recon.blockerNotes}
            </div>
          )}
          {recon.carryForward && (
            <span className="member-commit-card__carry-tag">Carry Forward</span>
          )}
        </div>
      )}
    </div>
  );
}

export function MemberWeekView() {
  const { userId, weekId } = useParams<{ userId: string; weekId: string }>();
  const navigate = useNavigate();

  const parsedUserId = userId ? parseInt(userId, 10) : 0;
  const parsedWeekId = weekId ? parseInt(weekId, 10) : 0;

  const { data: week, isLoading, error } = useTeamMemberWeek(parsedUserId, parsedWeekId);

  if (isLoading) {
    return (
      <div className="member-week-view">
        <div className="member-week-view__loading">
          <div className="member-week-view__spinner" />
          <span>Loading…</span>
        </div>
      </div>
    );
  }

  if (error || !week) {
    return (
      <div className="member-week-view">
        <div className="member-week-view__error">
          {error?.message ?? 'Failed to load member week.'}
        </div>
      </div>
    );
  }

  return (
    <div className="member-week-view">
      <div className="member-week-view__back">
        <button
          className="member-week-view__back-btn"
          onClick={() => void navigate('/manager/team')}
        >
          ← Back to Team
        </button>
      </div>

      <div className="member-week-view__header">
        <h1 className="member-week-view__title">
          Week of {week.weekStartDate}
        </h1>
        <Badge label={week.status} variant={week.status} />
      </div>

      {week.commits.length === 0 ? (
        <div className="member-week-view__empty">No commits for this week.</div>
      ) : (
        <div className="member-week-view__commits">
          {week.commits.map((commit) => (
            <MemberCommitCard key={commit.id} commit={commit} />
          ))}
        </div>
      )}

      {(week.blockersSummary || week.managerNotes) && (
        <div className="member-week-view__summary">
          <h2 className="member-week-view__summary-title">Week Summary</h2>
          {week.blockersSummary && (
            <div className="member-week-view__summary-field">
              <span className="member-week-view__summary-label">Blockers</span>
              <p className="member-week-view__summary-text">{week.blockersSummary}</p>
            </div>
          )}
          {week.managerNotes && (
            <div className="member-week-view__summary-field">
              <span className="member-week-view__summary-label">Manager Notes</span>
              <p className="member-week-view__summary-text">{week.managerNotes}</p>
            </div>
          )}
        </div>
      )}

      <div className="member-week-view__footer">
        <Button variant="secondary" onClick={() => void navigate('/manager/team')}>
          Back to Team
        </Button>
      </div>
    </div>
  );
}
