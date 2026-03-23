import { useNavigate } from 'react-router-dom';
import { useTeamWeeks } from '../../api/hooks/useManager';
import { Badge } from '../../components/Badge';
import './TeamStatusGrid.css';

function statusBadgeVariant(status: string | null): string {
  if (!status) return 'default';
  return status; // Badge already handles DRAFT, LOCKED, RECONCILING, RECONCILED
}

function statusLabel(status: string | null): string {
  if (!status) return 'Not Started';
  return status.charAt(0) + status.slice(1).toLowerCase().replace(/_/g, ' ');
}

export function TeamStatusGrid() {
  const navigate = useNavigate();
  const { data: members, isLoading, error } = useTeamWeeks();

  if (isLoading) {
    return (
      <div className="team-grid__loading">
        <div className="team-grid__spinner" />
        <span>Loading team…</span>
      </div>
    );
  }

  if (error || !members) {
    return (
      <div className="team-grid__error">
        {error?.message ?? 'Failed to load team data.'}
      </div>
    );
  }

  function handleRowClick(userId: number, weekId: number | null) {
    if (!weekId) return;
    void navigate(`/manager/team/${userId}/${weekId}`);
  }

  return (
    <div className="team-grid">
      <table className="team-grid__table">
        <thead>
          <tr className="team-grid__head-row">
            <th className="team-grid__th">Name</th>
            <th className="team-grid__th">Role</th>
            <th className="team-grid__th">Status</th>
            <th className="team-grid__th team-grid__th--right">Commits</th>
          </tr>
        </thead>
        <tbody>
          {members.map((member) => {
            const clickable = member.weekId != null;
            return (
              <tr
                key={member.userId}
                className={`team-grid__row${clickable ? ' team-grid__row--clickable' : ''}`}
                onClick={() => handleRowClick(member.userId, member.weekId)}
              >
                <td className="team-grid__td team-grid__td--name">{member.userName}</td>
                <td className="team-grid__td">
                  <Badge
                    label={member.userRole}
                    variant={member.userRole === 'MANAGER' ? 'LOCKED' : undefined}
                  />
                </td>
                <td className="team-grid__td">
                  {member.status ? (
                    <Badge
                      label={statusLabel(member.status)}
                      variant={statusBadgeVariant(member.status)}
                    />
                  ) : (
                    <span className="team-grid__not-started">Not Started</span>
                  )}
                </td>
                <td className="team-grid__td team-grid__td--right">
                  <span className="team-grid__commit-count">{member.commitCount}</span>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>

      {members.length === 0 && (
        <div className="team-grid__empty">No team members found.</div>
      )}
    </div>
  );
}
