interface UserState {
  username: string;
  role: string;
  token: string;
}

interface NavbarProps {
  user: UserState | null;
  onOpenLogin: () => void;
  onLogout: () => void;
  onHomeClick: () => void;
  onAccountsClick: () => void;
}

export default function Navbar({
  user,
  onOpenLogin,
  onLogout,
  onHomeClick,
  onAccountsClick,
}: NavbarProps) {
  return (
    <nav className="navbar" aria-label="Main Navigation">
      <div className="navbar-inner">
        <button
          type="button"
          className="brand"
          id="navbar-brand"
          onClick={onHomeClick}
          aria-label="Howto Forum Home"
          style={{ background: 'none', border: 'none', cursor: 'pointer', textAlign: 'left', padding: 0 }}
        >
          <div className="brand-icon" aria-hidden="true">H</div>
          <span className="brand-title">Howto Forum</span>
        </button>

        <div className="nav-actions">
          {user ? (
            <>
              {user.role === 'ADMINISTRATOR' && (
                <button
                  id="navbar-accounts-btn"
                  className="btn btn-secondary btn-sm"
                  onClick={onAccountsClick}
                  aria-label="User Account Management"
                >
                  Accounts
                </button>
              )}
              <div className="user-badge" id="user-badge" aria-label={`Logged in as ${user.username}`}>
                <span id="auth-status" style={{ fontWeight: 600 }}>{user.username}</span>
                <span className={`role-pill role-${user.role.toLowerCase()}`} id="user-role-badge">
                  {user.role}
                </span>
              </div>
              <button
                id="navbar-logout-btn"
                className="btn btn-outline-danger btn-sm"
                onClick={onLogout}
                aria-label="Log out"
              >
                Logout
              </button>
            </>
          ) : (
            <>
              <span id="auth-status" style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
                Not logged in
              </span>
              <button
                id="navbar-login-btn"
                className="btn btn-primary btn-sm"
                onClick={onOpenLogin}
                aria-label="Sign in"
              >
                Sign In
              </button>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
