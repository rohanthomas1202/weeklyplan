import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCurrentWeek } from '../../api/hooks/useWeeks';
import {
  useStartReconciliation,
  useUpdateWeekSummary,
  useReconcileWeek,
  useCarryForward,
} from '../../api/hooks/useReconciliation';
import { useToast } from '../../components/Toast';
import { Button } from '../../components/Button';
import { Modal } from '../../components/Modal';
import { Badge } from '../../components/Badge';
import { ReconcileList } from './ReconcileList';
import { StepIndicator } from './StepIndicator';
import './ReconciliationPage.css';

export function ReconciliationPage() {
  const navigate = useNavigate();
  const { data: week, isLoading, error } = useCurrentWeek();
  const { showToast } = useToast();

  const startReconciliation = useStartReconciliation();
  const updateWeekSummary = useUpdateWeekSummary(week?.id ?? 0);
  const reconcileWeek = useReconcileWeek();
  const carryForward = useCarryForward();

  const [blockersSummary, setBlockersSummary] = useState(week?.blockersSummary ?? '');
  const [managerNotes, setManagerNotes] = useState(week?.managerNotes ?? '');
  const [carryForwardModalOpen, setCarryForwardModalOpen] = useState(false);

  // Sync local state when week data arrives / changes
  const [summaryInit, setSummaryInit] = useState(false);
  if (week && !summaryInit) {
    setBlockersSummary(week.blockersSummary ?? '');
    setManagerNotes(week.managerNotes ?? '');
    setSummaryInit(true);
  }

  if (isLoading) {
    return (
      <div className="reconciliation-page">
        <div className="reconciliation-page__loading">
          <div className="reconciliation-page__spinner" />
          <span>Loading…</span>
        </div>
      </div>
    );
  }

  if (error || !week) {
    return (
      <div className="reconciliation-page">
        <div className="reconciliation-page__error">
          {error?.message ?? 'Failed to load week.'}
        </div>
      </div>
    );
  }

  // Redirect DRAFT to planning
  if (week.status === 'DRAFT') {
    void navigate('/my-week', { replace: true });
    return null;
  }

  const reconciledCommits = week.commits.filter((c) => c.reconciliation != null).length;
  const carryForwardItems = week.commits.filter((c) => c.reconciliation?.carryForward === true);

  function handleStartReconciliation() {
    startReconciliation.mutate(week!.id, {
      onError: (err) => showToast(err.message ?? 'Failed to start reconciliation.', 'error'),
    });
  }

  function handleSummaryBlur() {
    updateWeekSummary.mutate(
      { blockersSummary, managerNotes },
      {
        onError: (err) => showToast(err.message ?? 'Failed to save summary.', 'error'),
      }
    );
  }

  function handleSubmitReconciliation() {
    const allReconciled = week!.commits.every((c) => c.reconciliation != null);
    if (!allReconciled) {
      showToast('Please reconcile all commits before submitting.', 'error');
      return;
    }
    reconcileWeek.mutate(week!.id, {
      onError: (err) => showToast(err.message ?? 'Failed to submit reconciliation.', 'error'),
    });
  }

  function handleCarryForwardClick() {
    setCarryForwardModalOpen(true);
  }

  function handleCarryForwardConfirm() {
    setCarryForwardModalOpen(false);
    carryForward.mutate(week!.id, {
      onSuccess: () => {
        void navigate('/my-week');
      },
      onError: (err) => showToast(err.message ?? 'Failed to carry forward.', 'error'),
    });
  }

  return (
    <div className="reconciliation-page">
      <div className="reconciliation-page__header">
        <h1 className="reconciliation-page__title">Weekly Reconciliation</h1>
        <p className="reconciliation-page__subtitle">
          Week of {week.weekStartDate} — {week.weekEndDate}
        </p>
      </div>

      {/* Step indicator — shown during RECONCILING and RECONCILED */}
      {(week.status === 'RECONCILING' || week.status === 'RECONCILED') && (
        <StepIndicator
          totalCommits={week.commits.length}
          reconciledCommits={reconciledCommits}
          weekStatus={week.status}
        />
      )}

      {/* LOCKED: show Start Reconciliation button */}
      {week.status === 'LOCKED' && (
        <div className="reconciliation-page__locked">
          <p className="reconciliation-page__locked-text">
            Your week is locked. Start the reconciliation process when you are ready.
          </p>
          <Button
            onClick={handleStartReconciliation}
            disabled={startReconciliation.isPending}
          >
            {startReconciliation.isPending ? 'Starting…' : 'Start Reconciliation'}
          </Button>
        </div>
      )}

      {/* RECONCILING: editable list + summary */}
      {week.status === 'RECONCILING' && (
        <>
          <ReconcileList
            commits={week.commits}
            weekId={week.id}
            weekStatus={week.status}
            onCommitReconciled={() => {}}
          />

          <div className="reconciliation-page__summary reconciliation-page__summary--reflect">
            <h2 className="reconciliation-page__summary-title">
              ✏️ Reflect on Your Week
            </h2>

            <div className="reconciliation-page__summary-field">
              <label className="reconciliation-page__summary-label" htmlFor="blockers-summary">
                What blocked you this week?
              </label>
              <textarea
                id="blockers-summary"
                className="reconciliation-page__summary-textarea"
                value={blockersSummary}
                onChange={(e) => setBlockersSummary(e.target.value)}
                onBlur={handleSummaryBlur}
                placeholder="What prevented you from completing your plan?"
                rows={4}
              />
            </div>

            <div className="reconciliation-page__summary-field">
              <label className="reconciliation-page__summary-label" htmlFor="manager-notes">
                What should your manager know?
              </label>
              <textarea
                id="manager-notes"
                className="reconciliation-page__summary-textarea"
                value={managerNotes}
                onChange={(e) => setManagerNotes(e.target.value)}
                onBlur={handleSummaryBlur}
                placeholder="What should your manager know about this week?"
                rows={4}
              />
            </div>
          </div>

          <div className="reconciliation-page__submit">
            <Button
              onClick={handleSubmitReconciliation}
              disabled={reconcileWeek.isPending}
            >
              {reconcileWeek.isPending ? 'Submitting…' : 'Submit Reconciliation'}
            </Button>
          </div>
        </>
      )}

      {/* RECONCILED: read-only list + summary + carry forward */}
      {week.status === 'RECONCILED' && (
        <>
          <ReconcileList
            commits={week.commits}
            weekId={week.id}
            weekStatus={week.status}
            onCommitReconciled={() => {}}
          />

          <div className="reconciliation-page__summary reconciliation-page__summary--reflect reconciliation-page__summary--readonly">
            <h2 className="reconciliation-page__summary-title">
              ✏️ Reflect on Your Week
            </h2>

            {week.blockersSummary ? (
              <div className="reconciliation-page__summary-field">
                <span className="reconciliation-page__summary-label">What blocked you?</span>
                <p className="reconciliation-page__summary-text">{week.blockersSummary}</p>
              </div>
            ) : (
              <div className="reconciliation-page__summary-field">
                <span className="reconciliation-page__summary-label">What blocked you?</span>
                <p className="reconciliation-page__summary-empty">No blockers recorded.</p>
              </div>
            )}

            {week.managerNotes ? (
              <div className="reconciliation-page__summary-field">
                <span className="reconciliation-page__summary-label">Manager notes</span>
                <p className="reconciliation-page__summary-text">{week.managerNotes}</p>
              </div>
            ) : (
              <div className="reconciliation-page__summary-field">
                <span className="reconciliation-page__summary-label">Manager notes</span>
                <p className="reconciliation-page__summary-empty">No manager notes recorded.</p>
              </div>
            )}
          </div>

          <div className="reconciliation-page__submit">
            <Button
              onClick={handleCarryForwardClick}
              disabled={carryForward.isPending}
            >
              {carryForward.isPending ? 'Processing…' : 'Carry Forward'}
            </Button>
          </div>

          {/* Carry Forward Preview Modal */}
          <Modal
            open={carryForwardModalOpen}
            onClose={() => setCarryForwardModalOpen(false)}
            title="Carry Forward to Next Week"
          >
            {carryForwardItems.length > 0 ? (
              <>
                <p className="reconciliation-page__modal-description">
                  These {carryForwardItems.length} item{carryForwardItems.length !== 1 ? 's' : ''} will be added to your next week's draft plan.
                </p>
                <ul className="reconciliation-page__carry-list">
                  {carryForwardItems.map((commit) => (
                    <li key={commit.id} className="reconciliation-page__carry-item">
                      <Badge
                        label={commit.chessCategoryDisplayName}
                        variant={commit.chessCategoryCode}
                      />
                      <span className="reconciliation-page__carry-title">{commit.title}</span>
                    </li>
                  ))}
                </ul>
              </>
            ) : (
              <p className="reconciliation-page__modal-description">
                No items are marked for carry forward. A new empty draft plan will be created for next week.
              </p>
            )}
            <div className="reconciliation-page__modal-actions">
              <Button onClick={handleCarryForwardConfirm} disabled={carryForward.isPending}>
                {carryForward.isPending ? 'Processing…' : 'Confirm'}
              </Button>
              <Button variant="secondary" onClick={() => setCarryForwardModalOpen(false)}>
                Cancel
              </Button>
            </div>
          </Modal>
        </>
      )}
    </div>
  );
}
