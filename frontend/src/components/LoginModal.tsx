import { useState, FormEvent } from 'react';
import { CreateAccountRequest } from '../types';

interface LoginModalProps {
  isOpen: boolean;
  onClose: () => void;
  onLogin: (username: string, password: string) => Promise<void>;
  onQuickLogin: (username: string, password: string) => Promise<void>;
  onRegister: (account: CreateAccountRequest) => Promise<void>;
}

export default function LoginModal({
  isOpen,
  onClose,
  onLogin,
  onQuickLogin,
  onRegister,
}: LoginModalProps) {
  const [mode, setMode] = useState<'login' | 'signup'>('login');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  if (!isOpen) {
    return null;
  }

  const handleLoginSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!username.trim() || !password.trim()) {
      setError('Please fill in both fields');
      return;
    }
    setError('');
    setLoading(true);
    try {
      await onLogin(username.trim(), password);
      onClose();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Authentication failed');
    } finally {
      setLoading(false);
    }
  };

  const handleSignupSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!username.trim() || !email.trim() || !password.trim() || !firstName.trim() || !lastName.trim()) {
      setError('Please fill in all fields');
      return;
    }
    if (username.trim().length < 6) {
      setError('Username must be at least 6 characters');
      return;
    }
    setError('');
    setLoading(true);
    try {
      await onRegister({
        username: username.trim(),
        email: email.trim(),
        firstName: firstName.trim(),
        lastName: lastName.trim(),
        password,
      });
      onClose();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Registration failed');
    } finally {
      setLoading(false);
    }
  };

  const handleQuick = async (uname: string, pwd: string) => {
    setError('');
    setLoading(true);
    try {
      await onQuickLogin(uname, pwd);
      onClose();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Quick login failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      className="modal-overlay"
      id="login-modal"
      onClick={onClose}
      role="dialog"
      aria-modal="true"
      aria-labelledby="login-modal-title"
    >
      <div className="modal-content" onClick={(e) => e.stopPropagation()} style={{ maxWidth: 440 }}>
        <div className="modal-header">
          <div role="tablist" aria-label="Authentication modes" style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
            <button
              type="button"
              role="tab"
              id="tab-signin-btn"
              aria-selected={mode === 'login'}
              onClick={() => { setMode('login'); setError(''); }}
              style={{
                background: 'none',
                border: 'none',
                fontSize: '1.15rem',
                fontWeight: 700,
                color: mode === 'login' ? 'var(--text-main)' : 'var(--text-dim)',
                cursor: 'pointer',
                paddingBottom: 4,
                borderBottom: mode === 'login' ? '2px solid var(--primary)' : '2px solid transparent',
              }}
            >
              Sign In
            </button>
            <button
              type="button"
              role="tab"
              id="tab-signup-btn"
              aria-selected={mode === 'signup'}
              onClick={() => { setMode('signup'); setError(''); }}
              style={{
                background: 'none',
                border: 'none',
                fontSize: '1.15rem',
                fontWeight: 700,
                color: mode === 'signup' ? 'var(--text-main)' : 'var(--text-dim)',
                cursor: 'pointer',
                paddingBottom: 4,
                borderBottom: mode === 'signup' ? '2px solid var(--primary)' : '2px solid transparent',
              }}
            >
              Sign Up
            </button>
          </div>
          <button
            id="login-modal-close"
            className="modal-close"
            onClick={onClose}
            aria-label="Close"
          >
            ×
          </button>
        </div>

        {error && (
          <div
            role="alert"
            style={{
              padding: '10px 14px',
              borderRadius: 8,
              background: 'rgba(244, 63, 94, 0.15)',
              color: '#fda4af',
              marginBottom: 16,
              fontSize: '0.875rem',
            }}
          >
            {error}
          </div>
        )}

        {mode === 'login' ? (
          <>
            <form onSubmit={handleLoginSubmit}>
              <div className="form-group">
                <label className="form-label" htmlFor="login-username-input">Username</label>
                <input
                  id="login-username-input"
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="e.g. admin or user"
                  autoFocus
                />
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="login-password-input">Password</label>
                <input
                  id="login-password-input"
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••••••"
                />
              </div>

              <div className="form-actions">
                <button type="button" className="btn btn-secondary" onClick={onClose}>
                  Cancel
                </button>
                <button
                  id="login-submit-btn"
                  type="submit"
                  className="btn btn-primary"
                  disabled={loading}
                >
                  {loading ? 'Signing in...' : 'Sign In'}
                </button>
              </div>
            </form>

            <div style={{ marginTop: 24, paddingTop: 16, borderTop: '1px solid var(--border-color)' }}>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-dim)', marginBottom: 10, fontWeight: 600 }}>
                QUICK LOGIN FOR TESTING:
              </div>
              <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
                <button
                  id="quick-login-admin"
                  className="quick-chip"
                  onClick={() => handleQuick('admin', 'superSecret1!')}
                >
                  Admin
                </button>
                <button
                  id="quick-login-moderator"
                  className="quick-chip"
                  onClick={() => handleQuick('moderator', 'superSecret2!')}
                >
                  Moderator
                </button>
                <button
                  id="quick-login-user"
                  className="quick-chip"
                  onClick={() => handleQuick('user', 'superSecret3!')}
                >
                  User
                </button>
              </div>
            </div>
          </>
        ) : (
          <form onSubmit={handleSignupSubmit} id="signup-form">
            <div className="form-group">
              <label className="form-label" htmlFor="signup-username-input">Username (min 6 characters)</label>
              <input
                id="signup-username-input"
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Unique username"
                autoFocus
              />
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="signup-email-input">Email</label>
              <input
                id="signup-email-input"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="user@example.com"
              />
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
              <div className="form-group">
                <label className="form-label" htmlFor="signup-firstname-input">First Name</label>
                <input
                  id="signup-firstname-input"
                  type="text"
                  value={firstName}
                  onChange={(e) => setFirstName(e.target.value)}
                  placeholder="First name"
                />
              </div>
              <div className="form-group">
                <label className="form-label" htmlFor="signup-lastname-input">Last Name</label>
                <input
                  id="signup-lastname-input"
                  type="text"
                  value={lastName}
                  onChange={(e) => setLastName(e.target.value)}
                  placeholder="Last name"
                />
              </div>
            </div>

            <div className="form-group">
              <label className="form-label" htmlFor="signup-password-input">Password</label>
              <input
                id="signup-password-input"
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Create a strong password"
              />
            </div>

            <div className="form-actions">
              <button type="button" className="btn btn-secondary" onClick={() => setMode('login')}>
                Back to Login
              </button>
              <button
                id="signup-submit-btn"
                type="submit"
                className="btn btn-primary"
                disabled={loading}
              >
                {loading ? 'Creating...' : 'Create Account'}
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}
