import type { ReactNode } from 'react';
import './Card.css';

interface CardProps {
  children: ReactNode;
  className?: string;
  onClick?: () => void;
}

export function Card({ children, className, onClick }: CardProps) {
  return (
    <div
      className={`card${className ? ` ${className}` : ''}${onClick ? ' card--clickable' : ''}`}
      onClick={onClick}
    >
      {children}
    </div>
  );
}
