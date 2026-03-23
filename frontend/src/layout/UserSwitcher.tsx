import { useUserContext } from '../context/UserContext';
import { useUsers } from '../api/hooks/useUsers';
import './UserSwitcher.css';

export function UserSwitcher() {
  const { currentUser, setUser } = useUserContext();
  const { data: users } = useUsers();

  function handleChange(e: React.ChangeEvent<HTMLSelectElement>) {
    const userId = Number(e.target.value);
    const user = users?.find((u) => u.id === userId);
    if (user) {
      setUser(user);
    }
  }

  return (
    <div className="user-switcher">
      <span className="user-switcher-label">Acting as:</span>
      <select
        className="user-switcher-select"
        value={currentUser?.id ?? ''}
        onChange={handleChange}
      >
        {!currentUser && <option value="">— select user —</option>}
        {users?.map((u) => (
          <option key={u.id} value={u.id}>
            {u.name} ({u.role})
          </option>
        ))}
      </select>
    </div>
  );
}
