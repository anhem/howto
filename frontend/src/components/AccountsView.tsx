import { useState, FormEvent } from 'react';
import { Account, AccountDetails, CreateAccountRequest } from '../types';

interface AccountsViewProps {
  accounts: Account[];
  currentUsername: string;
  onBack: () => void;
  onDeleteAccount: (accountId: number) => Promise<void>;
  onFetchAccountDetails: (accountId: number) => Promise<AccountDetails>;
  onCreateAdmin: (request: CreateAccountRequest) => Promise<void>;
  onCreateModerator?: (request: CreateAccountRequest) => Promise<void>;
  onUpdateAccountRole?: (accountId: number, role: string) => Promise<AccountDetails>;
}

export default function AccountsView({
  accounts,
  currentUsername,
  onBack,
  onDeleteAccount,
  onFetchAccountDetails,
  onCreateAdmin,
  onCreateModerator,
  onUpdateAccountRole,
}: AccountsViewProps) {
  const [selectedDetails, setSelectedDetails] = useState<AccountDetails | null>(null);
  const [showAdminModal, setShowAdminModal] = useState(false);
  const [role, setRole] = useState<'ADMINISTRATOR' | 'MODERATOR'>('ADMINISTRATOR');
  const [selectedRoleToAssign, setSelectedRoleToAssign] = useState<string>('USER');
  const [updatingRole, setUpdatingRole] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [password, setPassword] = useState('');

  const handleDetailsClick = async (accountId: number) => {
    setError('');
    try {
      const details = await onFetchAccountDetails(accountId);
      setSelectedDetails(details);
      const currentRole = details.roles[0]?.replace('ROLE_', '') || 'USER';
      setSelectedRoleToAssign(currentRole);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to fetch details');
    }
  };

  const handleRoleChange = async () => {
    if (!selectedDetails || !onUpdateAccountRole) return;
    setError('');
    setUpdatingRole(true);
    try {
      const updated = await onUpdateAccountRole(selectedDetails.account.id, selectedRoleToAssign);
      setSelectedDetails(updated);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to update role');
    } finally {
      setUpdatingRole(false);
    }
  };

  const handleDelete = async (account: Account) => {
    if (!window.confirm(`Are you sure you want to delete account "${account.username}"?`)) {
      return;
    }
    setError('');
    try {
      await onDeleteAccount(account.id);
      if (selectedDetails?.account.id === account.id) {
        setSelectedDetails(null);
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to delete account');
    }
  };

  const handleAdminSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!username.trim() || !email.trim() || !password.trim() || !firstName.trim() || !lastName.trim()) {
      setError('Please fill in all fields');
      return;
    }
    setError('');
    setLoading(true);
    try {
      const payload = {
        username: username.trim(),
        email: email.trim(),
        firstName: firstName.trim(),
        lastName: lastName.trim(),
        password,
      };
      if (role === 'MODERATOR' && onCreateModerator) {
        await onCreateModerator(payload);
      } else {
        await onCreateAdmin(payload);
      }
      setShowAdminModal(false);
      setUsername('');
      setEmail('');
      setFirstName('');
      setLastName('');
      setPassword('');
      setRole('ADMINISTRATOR');
    } catch (err) {
      setError(err instanceof Error ? err.message : `Failed to create ${role.toLowerCase()}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <nav className="breadcrumbs" aria-label="Breadcrumb">
        <button
          type="button"
          id="breadcrumb-forum-home"
          className="breadcrumb-link"
          onClick={onBack}
          style={{ background: 'none', border: 'none', font: 'inherit', padding: 0, cursor: 'pointer' }}
          aria-label="Back to Forum"
        >
          Forum Home
        </button>
        <span className="breadcrumb-separator" aria-hidden="true">/</span>
        <span style={{ color: 'var(--text-main)', fontWeight: 600 }}>Account Administration</span>
      </nav>

      <div className="section-header">
        <div>
          <h2 className="section-title">User Accounts ({accounts.length})</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginTop: 4 }}>
            Manage registered accounts, roles, and administrative privileges.
          </p>
        </div>

        <button
          id="create-admin-btn"
          className="btn btn-primary btn-sm"
          onClick={() => setShowAdminModal(true)}
        >
          + Provision Account
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

      <div style={{ display: 'grid', gridTemplateColumns: selectedDetails ? '1fr 340px' : '1fr', gap: 20 }}>
        <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left', fontSize: '0.9rem' }}>
            <thead>
              <tr style={{ background: 'rgba(255, 255, 255, 0.03)', borderBottom: '1px solid var(--border-color)' }}>
                <th style={{ padding: '12px 16px' }}>ID</th>
                <th style={{ padding: '12px 16px' }}>Username</th>
                <th style={{ padding: '12px 16px' }}>Email</th>
                <th style={{ padding: '12px 16px' }}>Name</th>
                <th style={{ padding: '12px 16px', textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {accounts.map((acc) => (
                <tr
                  key={acc.id}
                  id={`account-row-${acc.id}`}
                  style={{ borderBottom: '1px solid var(--border-color)' }}
                >
                  <td style={{ padding: '12px 16px', color: 'var(--text-dim)' }}>#{acc.id}</td>
                  <td style={{ padding: '12px 16px', fontWeight: 600 }}>{acc.username}</td>
                  <td style={{ padding: '12px 16px', color: 'var(--text-muted)' }}>{acc.email}</td>
                  <td style={{ padding: '12px 16px' }}>{acc.firstName} {acc.lastName}</td>
                  <td style={{ padding: '12px 16px', textAlign: 'right' }}>
                    <button
                      id={`account-details-btn-${acc.id}`}
                      className="btn btn-secondary btn-sm"
                      onClick={() => handleDetailsClick(acc.id)}
                      style={{ marginRight: 8 }}
                      aria-label={`Details for ${acc.username}`}
                    >
                      Details
                    </button>
                    {acc.username !== currentUsername && (
                      <button
                        id={`account-delete-btn-${acc.id}`}
                        className="btn btn-outline-danger btn-sm"
                        onClick={() => handleDelete(acc)}
                        aria-label={`Delete ${acc.username}`}
                      >
                        Delete
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {selectedDetails && (
          <aside
            id="account-details-panel"
            className="card"
            style={{ position: 'sticky', top: 20, alignSelf: 'start' }}
          >
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
              <h3 style={{ fontSize: '1.1rem', fontWeight: 700 }}>Account Profile</h3>
              <button
                className="modal-close"
                onClick={() => setSelectedDetails(null)}
                aria-label="Close details"
              >
                ×
              </button>
            </div>
            <div style={{ marginBottom: 12 }}>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-dim)' }}>USERNAME</div>
              <div style={{ fontWeight: 600, fontSize: '1.05rem' }}>{selectedDetails.account.username}</div>
            </div>
            <div style={{ marginBottom: 12 }}>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-dim)' }}>EMAIL</div>
              <div>{selectedDetails.account.email}</div>
            </div>
            <div style={{ marginBottom: 14 }}>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-dim)', marginBottom: 4 }}>ASSIGNED ROLES</div>
              <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap', marginTop: 4, marginBottom: 10 }}>
                {selectedDetails.roles.map((r) => (
                  <span key={r} className={`role-pill role-${r.toLowerCase()}`}>
                    {r}
                  </span>
                ))}
              </div>
              {onUpdateAccountRole && (
                <div style={{ display: 'flex', gap: 6, alignItems: 'center' }}>
                  <select
                    id="account-role-select"
                    aria-label="Change account role"
                    value={selectedRoleToAssign}
                    onChange={(e) => setSelectedRoleToAssign(e.target.value)}
                    style={{
                      flex: 1,
                      padding: '5px 8px',
                      fontSize: '0.8rem',
                      background: 'var(--bg-primary)',
                      color: 'var(--text-main)',
                      border: '1px solid var(--border-color)',
                      borderRadius: 'var(--radius-sm)',
                    }}
                  >
                    <option value="USER">USER</option>
                    <option value="MODERATOR">MODERATOR</option>
                    <option value="ADMINISTRATOR">ADMINISTRATOR</option>
                  </select>
                  <button
                    id="update-role-btn"
                    className="btn btn-secondary btn-sm"
                    onClick={handleRoleChange}
                    disabled={updatingRole}
                  >
                    {updatingRole ? 'Saving...' : 'Apply'}
                  </button>
                </div>
              )}
            </div>
            <div style={{ marginBottom: 12 }}>
              <div style={{ fontSize: '0.8rem', color: 'var(--text-dim)' }}>CREATED AT</div>
              <div style={{ fontSize: '0.85rem' }}>{new Date(selectedDetails.created).toLocaleString()}</div>
            </div>
          </aside>
        )}
      </div>

      {showAdminModal && (
        <div
          className="modal-overlay"
          id="admin-modal"
          onClick={() => setShowAdminModal(false)}
          role="dialog"
          aria-modal="true"
          aria-labelledby="admin-modal-title"
        >
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title" id="admin-modal-title">
                Provision {role === 'MODERATOR' ? 'Moderator' : 'Administrator'}
              </h2>
              <button
                className="modal-close"
                onClick={() => setShowAdminModal(false)}
                aria-label="Close"
              >
                ×
              </button>
            </div>

            <form onSubmit={handleAdminSubmit}>
              <div className="form-group">
                <label className="form-label" htmlFor="admin-role-select">Role</label>
                <select
                  id="admin-role-select"
                  value={role}
                  onChange={(e) => setRole(e.target.value as 'ADMINISTRATOR' | 'MODERATOR')}
                >
                  <option value="ADMINISTRATOR">Administrator</option>
                  <option value="MODERATOR">Moderator</option>
                </select>
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="admin-username-input">Username</label>
                <input
                  id="admin-username-input"
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="Username"
                  autoFocus
                />
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="admin-email-input">Email</label>
                <input
                  id="admin-email-input"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="email@example.com"
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                <div className="form-group">
                  <label className="form-label" htmlFor="admin-firstname-input">First Name</label>
                  <input
                    id="admin-firstname-input"
                    type="text"
                    value={firstName}
                    onChange={(e) => setFirstName(e.target.value)}
                    placeholder="First name"
                  />
                </div>
                <div className="form-group">
                  <label className="form-label" htmlFor="admin-lastname-input">Last Name</label>
                  <input
                    id="admin-lastname-input"
                    type="text"
                    value={lastName}
                    onChange={(e) => setLastName(e.target.value)}
                    placeholder="Last name"
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="admin-password-input">Password</label>
                <input
                  id="admin-password-input"
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="Strong password"
                />
              </div>

              <div className="form-actions">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setShowAdminModal(false)}
                >
                  Cancel
                </button>
                <button
                  id="admin-submit-btn"
                  type="submit"
                  className="btn btn-primary"
                  disabled={loading}
                >
                  {loading ? 'Creating...' : `Provision ${role === 'MODERATOR' ? 'Moderator' : 'Administrator'}`}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
