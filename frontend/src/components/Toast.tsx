import { useState, useCallback, useEffect } from 'react';
import './Toast.css';

interface ToastState {
  id: number;
  message: string;
  type: 'error' | 'success' | 'info';
}

let toastIdCounter = 0;
let globalShowToast: ((message: string, type?: ToastState['type']) => void) | null = null;

export function useToast() {
  const showToast = useCallback(
    (message: string, type: ToastState['type'] = 'info') => {
      if (globalShowToast) {
        globalShowToast(message, type);
      }
    },
    [],
  );

  return { showToast };
}

export function ToastContainer() {
  const [toasts, setToasts] = useState<ToastState[]>([]);

  const addToast = useCallback((message: string, type: ToastState['type'] = 'info') => {
    const id = ++toastIdCounter;
    setToasts((prev) => [...prev, { id, message, type }]);
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, 5000);
  }, []);

  useEffect(() => {
    globalShowToast = addToast;
    return () => {
      globalShowToast = null;
    };
  }, [addToast]);

  function dismiss(id: number) {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }

  if (toasts.length === 0) return null;

  return (
    <div className="toast-container">
      {toasts.map((toast) => (
        <div key={toast.id} className={`toast toast--${toast.type}`}>
          <span className="toast-message">{toast.message}</span>
          <button className="toast-dismiss" onClick={() => dismiss(toast.id)} aria-label="Dismiss">
            &#x2715;
          </button>
        </div>
      ))}
    </div>
  );
}
