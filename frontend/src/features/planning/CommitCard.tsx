import { useState, useRef, useEffect } from 'react';
import { Badge } from '../../components/Badge';
import type { WeeklyCommitDto, PlanningWeekStatus } from '../../api/types';
import './CommitCard.css';

interface CommitCardProps {
  commit: WeeklyCommitDto;
  weekStatus: PlanningWeekStatus;
  onEdit: (commit: WeeklyCommitDto) => void;
  onDelete: (commitId: number) => void;
  isDragging?: boolean;
}

export function CommitCard({ commit, weekStatus, onEdit, onDelete, isDragging }: CommitCardProps) {
  const [menuOpen, setMenuOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (!menuOpen) return;
    function handleClickOutside(e: MouseEvent) {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setMenuOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [menuOpen]);

  return (
    <div className={`commit-card${isDragging ? ' commit-card--dragging' : ''}`}>
      <div className="commit-card__rank">{commit.priorityRank}</div>

      <div className="commit-card__body">
        <div className="commit-card__top">
          <Badge label={commit.chessCategoryDisplayName} variant={commit.chessCategoryCode} />
          <span className="commit-card__title">{commit.title}</span>
          {commit.stretch && <span className="commit-card__stretch">Stretch</span>}
        </div>

        <div className="commit-card__rcdo">
          <span className="commit-card__rcdo-icon">🎯</span>
          <span className="commit-card__rcdo-chip" title={commit.rallyCryTitle}>
            {commit.rallyCryTitle}
          </span>
          <span className="commit-card__rcdo-arrow">→</span>
          <span className="commit-card__rcdo-chip" title={commit.definingObjectiveTitle}>
            {commit.definingObjectiveTitle}
          </span>
          <span className="commit-card__rcdo-arrow">→</span>
          <span className="commit-card__rcdo-chip" title={commit.outcomeTitle}>
            {commit.outcomeTitle}
          </span>
        </div>
      </div>

      {weekStatus === 'DRAFT' && (
        <div className="commit-card__menu-wrapper" ref={menuRef}>
          <button
            className="commit-card__menu-btn"
            onClick={() => setMenuOpen((o) => !o)}
            aria-label="Open commit menu"
          >
            ⋮
          </button>
          {menuOpen && (
            <div className="commit-card__menu-dropdown">
              <button
                className="commit-card__menu-item"
                onClick={() => {
                  setMenuOpen(false);
                  onEdit(commit);
                }}
              >
                Edit
              </button>
              <button
                className="commit-card__menu-item commit-card__menu-item--danger"
                onClick={() => {
                  setMenuOpen(false);
                  onDelete(commit.id);
                }}
              >
                Delete
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
