export interface Toast {
  id: number;
  message: string;
  type?: 'success' | 'error';
}

interface NotificationToastProps {
  toasts: Toast[];
  onDismiss: (id: number) => void;
}

export default function NotificationToast({
  toasts,
  onDismiss,
}: NotificationToastProps) {
  if (!toasts || toasts.length === 0) {
    return null;
  }

  return (
    <div
      className="toast-container"
      id="notification-toast"
      aria-live="polite"
      role="region"
      aria-label="Notifications"
    >
      {toasts.map((toast) => (
        <div
          key={toast.id}
          className={`toast toast-${toast.type || 'success'}`}
          onClick={() => onDismiss(toast.id)}
          role="status"
          tabIndex={0}
          onKeyDown={(e) => {
            if (e.key === 'Enter' || e.key === ' ') {
              onDismiss(toast.id);
            }
          }}
        >
          <span aria-hidden="true">{toast.type === 'error' ? '⚠️' : '✅'}</span>
          <span>{toast.message}</span>
        </div>
      ))}
    </div>
  );
}
