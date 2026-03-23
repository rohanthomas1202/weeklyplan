import type { WeeklyCommitDto } from '../../api/types';
import './WeekSummaryBar.css';

interface WeekSummaryBarProps {
  commits: WeeklyCommitDto[];
}

export function WeekSummaryBar({ commits }: WeekSummaryBarProps) {
  const totalCount = commits.length;

  // Chess category breakdown
  const categoryCount: Record<string, number> = {};
  const categoryDisplay: Record<string, string> = {};
  for (const commit of commits) {
    const code = commit.chessCategoryCode;
    categoryCount[code] = (categoryCount[code] ?? 0) + 1;
    categoryDisplay[code] = commit.chessCategoryDisplayName;
  }
  const categoryBreakdown = Object.entries(categoryCount)
    .sort(([a], [b]) => a.localeCompare(b))
    .map(([code, count]) => `${count} ${categoryDisplay[code]}`)
    .join(' · ');

  // Rally cry coverage
  const rallyCryIds = new Set(commits.map((c) => c.rallyCryId));
  const rallyCryCoverage = rallyCryIds.size;

  return (
    <div className="week-summary-bar">
      <div className="week-summary-bar__stat">
        <span className="week-summary-bar__label">Commits:</span>
        <span className="week-summary-bar__value">{totalCount}</span>
      </div>

      {categoryBreakdown && (
        <>
          <div className="week-summary-bar__divider" />
          <div className="week-summary-bar__stat">
            <span className="week-summary-bar__label">Categories:</span>
            <span className="week-summary-bar__value">{categoryBreakdown}</span>
          </div>
        </>
      )}

      {totalCount > 0 && (
        <>
          <div className="week-summary-bar__divider" />
          <div className="week-summary-bar__stat">
            <span className="week-summary-bar__label">Rally Cries covered:</span>
            <span className="week-summary-bar__value">{rallyCryCoverage}</span>
          </div>
        </>
      )}
    </div>
  );
}
