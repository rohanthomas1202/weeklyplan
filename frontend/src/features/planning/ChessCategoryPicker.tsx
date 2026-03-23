import { useChessCategories } from '../../api/hooks/useStrategy';
import { Badge } from '../../components/Badge';
import './ChessCategoryPicker.css';

interface ChessCategoryPickerProps {
  value: string | null;
  onChange: (code: string) => void;
}

export function ChessCategoryPicker({ value, onChange }: ChessCategoryPickerProps) {
  const { data: categories, isLoading } = useChessCategories();

  if (isLoading) {
    return <div style={{ fontSize: 13, color: '#6b7280' }}>Loading categories…</div>;
  }

  const sorted = (categories ?? []).slice().sort((a, b) => a.sortOrder - b.sortOrder);

  return (
    <div className="chess-category-picker">
      {sorted.map((cat) => {
        const isSelected = value === cat.code;
        const selectedClass = isSelected ? ` chess-category-picker__btn--selected-${cat.code}` : '';
        return (
          <button
            key={cat.code}
            type="button"
            className={`chess-category-picker__btn${selectedClass}`}
            onClick={() => onChange(cat.code)}
          >
            <Badge label={cat.displayName} variant={cat.code} />
            <span className="chess-category-picker__desc">{cat.description}</span>
          </button>
        );
      })}
    </div>
  );
}
