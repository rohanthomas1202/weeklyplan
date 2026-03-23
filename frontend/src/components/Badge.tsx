import './Badge.css';

interface BadgeProps {
  label: string;
  variant?: string;
}

const chessColors: Record<string, string> = {
  KING: 'badge--chess-king',
  QUEEN: 'badge--chess-queen',
  ROOK: 'badge--chess-rook',
  BISHOP: 'badge--chess-bishop',
  KNIGHT: 'badge--chess-knight',
  PAWN: 'badge--chess-pawn',
};

const statusColors: Record<string, string> = {
  DRAFT: 'badge--status-draft',
  LOCKED: 'badge--status-locked',
  RECONCILING: 'badge--status-reconciling',
  RECONCILED: 'badge--status-reconciled',
};

function resolveVariantClass(variant?: string): string {
  if (!variant) return 'badge--default';
  if (chessColors[variant]) return chessColors[variant];
  if (statusColors[variant]) return statusColors[variant];
  return 'badge--default';
}

export function Badge({ label, variant }: BadgeProps) {
  return (
    <span className={`badge ${resolveVariantClass(variant)}`}>
      {label}
    </span>
  );
}
