import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCurrentWeek } from '../../api/hooks/useWeeks';
import { useDeleteCommit, useLockWeek } from '../../api/hooks/useCommits';
import { useUserContext } from '../../context/UserContext';
import { useToast } from '../../components/Toast';
import { WeekHeader } from './WeekHeader';
import { CommitList } from './CommitList';
import { WeekSummaryBar } from './WeekSummaryBar';
import { CommitForm } from './CommitForm';
import { SkeletonCards } from '../../components/Skeleton';
import type { WeeklyCommitDto } from '../../api/types';
import './PlanningPage.css';

export function PlanningPage() {
  const navigate = useNavigate();
  const { currentUser } = useUserContext();
  const { data: week, isLoading, error } = useCurrentWeek();
  const { showToast } = useToast();

  const lockWeekMutation = useLockWeek();
  const deleteCommitMutation = useDeleteCommit(week?.id ?? 0);

  const [commitFormOpen, setCommitFormOpen] = useState(false);
  const [editingCommit, setEditingCommit] = useState<WeeklyCommitDto | null>(null);

  if (!currentUser) {
    return (
      <div className="planning-page">
        <div className="planning-page__empty">
          Select a user from the dropdown above to get started.
        </div>
      </div>
    );
  }

  if (isLoading) {
    return (
      <div className="planning-page">
        <SkeletonCards count={3} />
      </div>
    );
  }

  if (error || !week) {
    return (
      <div className="planning-page">
        <div className="planning-page__error">
          {error?.message ?? 'Failed to load your planning week.'}
        </div>
      </div>
    );
  }

  function handleAddCommit() {
    setEditingCommit(null);
    setCommitFormOpen(true);
  }

  function handleEditCommit(commit: WeeklyCommitDto) {
    setEditingCommit(commit);
    setCommitFormOpen(true);
  }

  function handleCloseForm() {
    setCommitFormOpen(false);
    setEditingCommit(null);
  }

  function handleLockWeek() {
    lockWeekMutation.mutate(week!.id, {
      onSuccess: () => {
        showToast('Week locked successfully. Ready to start reconciliation.', 'success');
        setTimeout(() => {
          void navigate('/my-week/reconcile');
        }, 1500);
      },
      onError: (err) => showToast(err.message ?? 'Failed to lock week.', 'error'),
    });
  }

  function handleDeleteCommit(commitId: number) {
    deleteCommitMutation.mutate(commitId, {
      onError: (err) => showToast(err.message ?? 'Failed to delete commit.', 'error'),
    });
  }

  return (
    <div className="planning-page">
      <WeekHeader
        week={week}
        onAddCommit={handleAddCommit}
        onLockWeek={handleLockWeek}
      />

      <CommitList
        commits={week.commits}
        weekId={week.id}
        weekStatus={week.status}
        onEditCommit={handleEditCommit}
        onDeleteCommit={handleDeleteCommit}
        onAddCommit={handleAddCommit}
      />

      <WeekSummaryBar commits={week.commits} />

      {commitFormOpen && (
        <CommitForm
          weekId={week.id}
          commit={editingCommit ?? undefined}
          commitCount={week.commits.length}
          onClose={handleCloseForm}
        />
      )}
    </div>
  );
}
