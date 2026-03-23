import './Skeleton.css';

interface SkeletonProps {
  count?: number;
}

export function SkeletonCards({ count = 3 }: SkeletonProps) {
  return (
    <div className="skeleton-list">
      {Array.from({ length: count }, (_, i) => (
        <div key={i} className="skeleton-card">
          <div className="skeleton-line skeleton-line--short" />
          <div className="skeleton-line skeleton-line--long" />
          <div className="skeleton-line skeleton-line--medium" />
        </div>
      ))}
    </div>
  );
}
