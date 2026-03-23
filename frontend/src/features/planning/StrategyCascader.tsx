import { useStrategyTree } from '../../api/hooks/useStrategy';
import './StrategyCascader.css';

interface StrategyCascaderValue {
  rallyCryId: number | null;
  definingObjectiveId: number | null;
  outcomeId: number | null;
}

interface StrategyCascaderProps {
  value: StrategyCascaderValue;
  onChange: (value: StrategyCascaderValue) => void;
}

export function StrategyCascader({ value, onChange }: StrategyCascaderProps) {
  const { data: tree, isLoading } = useStrategyTree();

  const rallyCries = tree ?? [];
  const selectedRallyCry = rallyCries.find((rc) => rc.id === value.rallyCryId) ?? null;
  const definingObjectives = selectedRallyCry?.definingObjectives ?? [];
  const selectedDO = definingObjectives.find((d) => d.id === value.definingObjectiveId) ?? null;
  const outcomes = selectedDO?.outcomes ?? [];

  function handleRallyCryChange(e: React.ChangeEvent<HTMLSelectElement>) {
    const id = e.target.value ? Number(e.target.value) : null;
    onChange({ rallyCryId: id, definingObjectiveId: null, outcomeId: null });
  }

  function handleDOChange(e: React.ChangeEvent<HTMLSelectElement>) {
    const id = e.target.value ? Number(e.target.value) : null;
    onChange({ ...value, definingObjectiveId: id, outcomeId: null });
  }

  function handleOutcomeChange(e: React.ChangeEvent<HTMLSelectElement>) {
    const id = e.target.value ? Number(e.target.value) : null;
    onChange({ ...value, outcomeId: id });
  }

  return (
    <div className="strategy-cascader">
      <div className="strategy-cascader__field">
        <label className="strategy-cascader__label">Rally Cry</label>
        <select
          className="strategy-cascader__select"
          value={value.rallyCryId ?? ''}
          onChange={handleRallyCryChange}
          disabled={isLoading}
        >
          <option value="">Select a Rally Cry…</option>
          {rallyCries.map((rc) => (
            <option key={rc.id} value={rc.id}>
              {rc.title}
            </option>
          ))}
        </select>
      </div>

      <div className="strategy-cascader__field">
        <label className="strategy-cascader__label">Defining Objective</label>
        <select
          className="strategy-cascader__select"
          value={value.definingObjectiveId ?? ''}
          onChange={handleDOChange}
          disabled={!value.rallyCryId || definingObjectives.length === 0}
        >
          <option value="">Select a Defining Objective…</option>
          {definingObjectives.map((d) => (
            <option key={d.id} value={d.id}>
              {d.title}
            </option>
          ))}
        </select>
      </div>

      <div className="strategy-cascader__field">
        <label className="strategy-cascader__label">Outcome</label>
        <select
          className="strategy-cascader__select"
          value={value.outcomeId ?? ''}
          onChange={handleOutcomeChange}
          disabled={!value.definingObjectiveId || outcomes.length === 0}
        >
          <option value="">Select an Outcome…</option>
          {outcomes.map((o) => (
            <option key={o.id} value={o.id}>
              {o.title}
            </option>
          ))}
        </select>
      </div>
    </div>
  );
}
