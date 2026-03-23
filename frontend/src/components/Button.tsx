import type { ReactNode } from 'react';
import './Button.css';

interface ButtonProps {
  variant?: 'primary' | 'secondary';
  onClick?: () => void;
  disabled?: boolean;
  children: ReactNode;
  type?: 'button' | 'submit' | 'reset';
}

export function Button({
  variant = 'primary',
  onClick,
  disabled,
  children,
  type = 'button',
}: ButtonProps) {
  return (
    <button
      className={`btn btn--${variant}`}
      onClick={onClick}
      disabled={disabled}
      type={type}
    >
      {children}
    </button>
  );
}
