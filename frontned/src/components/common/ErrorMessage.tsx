import './ErrorMessage.css';

interface ErrorMessageProps {
  message: string;
  onClose?: () => void;
}

export function ErrorMessage({ message, onClose }: ErrorMessageProps) {
  if (!message) return null;

  return (
    <div className="error-message">
      <span className="error-icon">!</span>
      <span className="error-text">{message}</span>
      {onClose && (
        <button className="error-close" onClick={onClose} aria-label="Close error">
          ×
        </button>
      )}
    </div>
  );
}
