import { useState } from 'react';
import {
  DndContext,
  closestCenter,
  type DragEndEvent,
} from '@dnd-kit/core';
import {
  SortableContext,
  verticalListSortingStrategy,
  useSortable,
  arrayMove,
} from '@dnd-kit/sortable';
import { CSS } from '@dnd-kit/utilities';
import { useReorderCommits } from '../../api/hooks/useCommits';
import { CommitCard } from './CommitCard';
import { EmptyState } from '../../components/EmptyState';
import type { WeeklyCommitDto, PlanningWeekStatus } from '../../api/types';
import './CommitList.css';

interface SortableCommitItemProps {
  commit: WeeklyCommitDto;
  weekStatus: PlanningWeekStatus;
  onEdit: (commit: WeeklyCommitDto) => void;
  onDelete: (commitId: number) => void;
  dragEnabled: boolean;
}

function SortableCommitItem({ commit, weekStatus, onEdit, onDelete, dragEnabled }: SortableCommitItemProps) {
  const {
    attributes,
    listeners,
    setNodeRef,
    transform,
    transition,
    isDragging,
  } = useSortable({ id: commit.id, disabled: !dragEnabled });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    cursor: dragEnabled ? (isDragging ? 'grabbing' : 'grab') : undefined,
  };

  return (
    <div ref={setNodeRef} style={style} {...(dragEnabled ? { ...attributes, ...listeners } : {})}>
      <CommitCard
        commit={commit}
        weekStatus={weekStatus}
        onEdit={onEdit}
        onDelete={onDelete}
        isDragging={isDragging}
      />
    </div>
  );
}

interface CommitListProps {
  commits: WeeklyCommitDto[];
  weekId: number;
  weekStatus: PlanningWeekStatus;
  onEditCommit: (commit: WeeklyCommitDto) => void;
  onDeleteCommit: (commitId: number) => void;
  onAddCommit?: () => void;
}

export function CommitList({ commits, weekId, weekStatus, onEditCommit, onDeleteCommit, onAddCommit }: CommitListProps) {
  const [localCommits, setLocalCommits] = useState<WeeklyCommitDto[]>(commits);
  const reorderMutation = useReorderCommits(weekId);
  const dragEnabled = weekStatus === 'DRAFT';

  // Keep local commits in sync when parent changes (e.g. after server refetch)
  if (commits !== localCommits && commits.length !== localCommits.length) {
    setLocalCommits(commits);
  }

  function handleDragEnd(event: DragEndEvent) {
    const { active, over } = event;
    if (!over || active.id === over.id) return;

    const oldIndex = localCommits.findIndex((c) => c.id === active.id);
    const newIndex = localCommits.findIndex((c) => c.id === over.id);
    const reordered = arrayMove(localCommits, oldIndex, newIndex);
    setLocalCommits(reordered);
    reorderMutation.mutate({ commitIds: reordered.map((c) => c.id) });
  }

  // Sync when server data arrives with same length but different order
  const serverIds = commits.map((c) => c.id).join(',');
  const localIds = localCommits.map((c) => c.id).join(',');
  const syncedCommits = serverIds === localIds ? localCommits : commits;

  if (syncedCommits.length === 0) {
    return (
      <EmptyState
        icon="📋"
        title="No commits yet"
        description="Plan your week by adding commits linked to strategy"
        actionLabel="+ Add Your First Commit"
        onAction={onAddCommit}
      />
    );
  }

  return (
    <DndContext collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
      <SortableContext
        items={syncedCommits.map((c) => c.id)}
        strategy={verticalListSortingStrategy}
      >
        <div className="commit-list">
          {syncedCommits.map((commit) => (
            <SortableCommitItem
              key={commit.id}
              commit={commit}
              weekStatus={weekStatus}
              onEdit={onEditCommit}
              onDelete={onDeleteCommit}
              dragEnabled={dragEnabled}
            />
          ))}
        </div>
      </SortableContext>
    </DndContext>
  );
}
