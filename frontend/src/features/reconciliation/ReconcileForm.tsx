import { useState, useEffect } from 'react';
import { useReconcileCommit } from '../../api/hooks/useReconciliation';
import { useToast } from '../../components/Toast';
import { Button } from '../../components/Button';
import type { WeeklyCommitDto, CommitDisposition } from '../../api/types';
import './ReconcileForm.css';

const DISPOSITION_OPTIONS: { value: CommitDisposition; label: string }[] = [
  { value: 'COMPLETED', label: 'Completed' },
  { value: 'PARTIALLY_COMPLETED', label: 'Partially Completed' },
  { value: 'NOT_COMPLETED', label: 'Not Completed' },
  { value: 'CARRIED_FORWARD', label: 'Carried Forward' },
  { value: 'DROPPED', label: 'Dropped' },
];

interface ReconcileFormProps {
  commit: WeeklyCommitDto;
  weekId: number;
  onSaved: () => void;
}

export function ReconcileForm({ commit, weekId, onSaved }: ReconcileFormProps) {
  const existing = commit.reconciliation;
  const { showToast } = useToast();
  const reconcileCommit = useReconcileCommit(weekId);

  const [disposition, setDisposition] = useState<CommitDisposition | ''>(
    existing?.disposition ?? ''
  );
  const [percentComplete, setPercentComplete] = useState<string>(
    existing?.percentComplete != null ? String(existing.percentComplete) : ''
  );
  const [actualResult, setActualResult] = useState(existing?.actualResult ?? '');
  const [blockerNotes, setBlockerNotes] = useState(existing?.blockerNotes ?? '');
  const [carryForward, setCarryForward] = useState(existing?.carryForward ?? false);

  // Sync carryForward when disposition changes
  useEffect(() => {
    if (disposition === 'CARRIED_FORWARD') {
      setCarryForward(true);
    } else if (disposition === 'COMPLETED' || disposition === 'DROPPED') {
      setCarryForward(false);
    }
  }, [disposition]);

  const isCarryForwardAuto = disposition === 'CARRIED_FORWARD';
  const isCarryForwardDisabled =
    disposition === 'COMPLETED' || disposition === 'DROPPED' || isCarryForwardAuto;

  function handleSave() {
    if (!disposition) {
      showToast('Please select a disposition.', 'error');
      return;
    }

    reconcileCommit.mutate(
      {
        commitId: commit.id,
        data: {
          disposition,
          actualResult: actualResult || undefined,
          percentComplete: percentComplete !== '' ? Number(percentComplete) : undefined,
          blockerNotes: blockerNotes || undefined,
          carryForward,
        },
      },
      {
        onSuccess: () => {
          showToast('Reconciliation saved.', 'success');
          onSaved();
        },
        onError: (err) => {
          showToast(err.message ?? 'Failed to save reconciliation.', 'error');
        },
      }
    );
  }

  return (
    <div className="reconcile-form">
      <div className="reconcile-form__field">
        <label className="reconcile-form__label" htmlFor={`disposition-${commit.id}`}>
          Disposition
        </label>
        <select
          id={`disposition-${commit.id}`}
          className="reconcile-form__select"
          value={disposition}
          onChange={(e) => setDisposition(e.target.value as CommitDisposition)}
        >
          <option value="">— Select —</option>
          {DISPOSITION_OPTIONS.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
      </div>

      <div className="reconcile-form__field">
        <label className="reconcile-form__label" htmlFor={`percent-${commit.id}`}>
          Percent Complete
        </label>
        <input
          id={`percent-${commit.id}`}
          type="number"
          min={0}
          max={100}
          className="reconcile-form__input"
          value={percentComplete}
          onChange={(e) => setPercentComplete(e.target.value)}
          placeholder="0–100"
        />
      </div>

      <div className="reconcile-form__field">
        <label className="reconcile-form__label" htmlFor={`actual-${commit.id}`}>
          Actual Result
        </label>
        <textarea
          id={`actual-${commit.id}`}
          className="reconcile-form__textarea"
          value={actualResult}
          onChange={(e) => setActualResult(e.target.value)}
          placeholder="What actually happened?"
          rows={3}
        />
      </div>

      <div className="reconcile-form__field">
        <label className="reconcile-form__label" htmlFor={`blocker-${commit.id}`}>
          Blocker Notes
        </label>
        <textarea
          id={`blocker-${commit.id}`}
          className="reconcile-form__textarea"
          value={blockerNotes}
          onChange={(e) => setBlockerNotes(e.target.value)}
          placeholder="What blocked you on this commit?"
          rows={2}
        />
      </div>

      <div className="reconcile-form__field reconcile-form__field--checkbox">
        <input
          id={`carry-${commit.id}`}
          type="checkbox"
          className="reconcile-form__checkbox"
          checked={carryForward}
          disabled={isCarryForwardDisabled}
          onChange={(e) => setCarryForward(e.target.checked)}
        />
        <label className="reconcile-form__label reconcile-form__label--inline" htmlFor={`carry-${commit.id}`}>
          Carry Forward to next week
        </label>
      </div>

      <div className="reconcile-form__actions">
        <Button
          onClick={handleSave}
          disabled={reconcileCommit.isPending || !disposition}
        >
          {reconcileCommit.isPending ? 'Saving…' : 'Save'}
        </Button>
      </div>
    </div>
  );
}
