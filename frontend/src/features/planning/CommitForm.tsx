import { useState } from 'react';
import { Modal } from '../../components/Modal';
import { Button } from '../../components/Button';
import { useToast } from '../../components/Toast';
import { useCreateCommit, useUpdateCommit } from '../../api/hooks/useCommits';
import { StrategyCascader } from './StrategyCascader';
import { ChessCategoryPicker } from './ChessCategoryPicker';
import type { WeeklyCommitDto } from '../../api/types';
import './CommitForm.css';

interface StrategyValue {
  rallyCryId: number | null;
  definingObjectiveId: number | null;
  outcomeId: number | null;
}

interface CommitFormProps {
  weekId: number;
  commit?: WeeklyCommitDto;
  commitCount: number;
  onClose: () => void;
}

function getInitialStrategy(commit?: WeeklyCommitDto): StrategyValue {
  if (commit) {
    return {
      rallyCryId: commit.rallyCryId,
      definingObjectiveId: commit.definingObjectiveId,
      outcomeId: commit.outcomeId,
    };
  }
  return { rallyCryId: null, definingObjectiveId: null, outcomeId: null };
}

export function CommitForm({ weekId, commit, commitCount, onClose }: CommitFormProps) {
  const isEditing = Boolean(commit);
  const { showToast } = useToast();

  const [title, setTitle] = useState(commit?.title ?? '');
  const [description, setDescription] = useState(commit?.description ?? '');
  const [strategy, setStrategy] = useState<StrategyValue>(getInitialStrategy(commit));
  const [chessCategoryCode, setChessCategoryCode] = useState<string | null>(
    commit?.chessCategoryCode ?? null,
  );
  const [stretch, setStretch] = useState(commit?.stretch ?? false);

  const [errors, setErrors] = useState<Record<string, string>>({});

  const createMutation = useCreateCommit(weekId);
  const updateMutation = useUpdateCommit(weekId);
  const isPending = createMutation.isPending || updateMutation.isPending;

  function validate(): boolean {
    const next: Record<string, string> = {};
    if (!title.trim()) next.title = 'Title is required.';
    if (!strategy.rallyCryId) next.rallyCryId = 'Please select a Rally Cry.';
    if (!strategy.definingObjectiveId) next.definingObjectiveId = 'Please select a Defining Objective.';
    if (!strategy.outcomeId) next.outcomeId = 'Please select an Outcome.';
    if (!chessCategoryCode) next.chessCategory = 'Please select a chess category.';
    setErrors(next);
    return Object.keys(next).length === 0;
  }

  function handleSave() {
    if (!validate()) return;

    const payload = {
      title: title.trim(),
      description: description.trim() || undefined,
      rallyCryId: strategy.rallyCryId as number,
      definingObjectiveId: strategy.definingObjectiveId as number,
      outcomeId: strategy.outcomeId as number,
      chessCategoryCode: chessCategoryCode as string,
      stretch,
      priorityRank: isEditing ? (commit?.priorityRank ?? commitCount) : commitCount + 1,
    };

    if (isEditing && commit) {
      updateMutation.mutate(
        { commitId: commit.id, data: payload },
        {
          onSuccess: () => onClose(),
          onError: (err) => showToast(err.message ?? 'Failed to update commit.', 'error'),
        },
      );
    } else {
      createMutation.mutate(payload, {
        onSuccess: () => onClose(),
        onError: (err) => showToast(err.message ?? 'Failed to create commit.', 'error'),
      });
    }
  }

  return (
    <Modal open title={isEditing ? 'Edit Commit' : 'Add Commit'} onClose={onClose}>
      <div className="commit-form">
        {/* Title */}
        <div className="commit-form__field">
          <label className="commit-form__label commit-form__label--required">Title</label>
          <input
            className={`commit-form__input${errors.title ? ' commit-form__input--error' : ''}`}
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="What will you commit to this week?"
            autoFocus
          />
          {errors.title && <span className="commit-form__error">{errors.title}</span>}
        </div>

        {/* Description */}
        <div className="commit-form__field">
          <label className="commit-form__label">Description (optional)</label>
          <textarea
            className="commit-form__textarea"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            placeholder="Add any additional context…"
          />
        </div>

        {/* Strategy Cascader */}
        <div className="commit-form__field">
          <div className="commit-form__section-label">Strategic Alignment *</div>
          <StrategyCascader value={strategy} onChange={setStrategy} />
          {(errors.rallyCryId || errors.definingObjectiveId || errors.outcomeId) && (
            <span className="commit-form__error">
              {errors.rallyCryId ?? errors.definingObjectiveId ?? errors.outcomeId}
            </span>
          )}
        </div>

        {/* Chess Category */}
        <div className="commit-form__field">
          <div className="commit-form__section-label">Chess Category *</div>
          <ChessCategoryPicker value={chessCategoryCode} onChange={setChessCategoryCode} />
          {errors.chessCategory && (
            <span className="commit-form__error">{errors.chessCategory}</span>
          )}
        </div>

        {/* Stretch */}
        <div className="commit-form__checkbox-row">
          <input
            id="stretch-checkbox"
            type="checkbox"
            className="commit-form__checkbox"
            checked={stretch}
            onChange={(e) => setStretch(e.target.checked)}
          />
          <label htmlFor="stretch-checkbox" className="commit-form__checkbox-label">
            This is a stretch goal
          </label>
        </div>

        {/* Actions */}
        <div className="commit-form__actions">
          <Button variant="secondary" onClick={onClose} disabled={isPending}>
            Cancel
          </Button>
          <Button variant="primary" onClick={handleSave} disabled={isPending}>
            {isPending ? 'Saving…' : isEditing ? 'Save Changes' : 'Add Commit'}
          </Button>
        </div>
      </div>
    </Modal>
  );
}
