import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import Navbar from '../Navbar';

describe('Navbar', () => {
  it('should render sign in button when user is unauthenticated', () => {
    render(
      <Navbar
        user={null}
        onOpenLogin={vi.fn()}
        onLogout={vi.fn()}
        onHomeClick={vi.fn()}
        onAccountsClick={vi.fn()}
      />
    );

    expect(screen.getByRole('button', { name: /sign in/i })).toBeInTheDocument();
    expect(screen.getByText(/not logged in/i)).toBeInTheDocument();
  });

  it('should display username and logout button when user is authenticated', () => {
    const user = { username: 'testuser', role: 'USER', token: 'jwt' };

    render(
      <Navbar
        user={user}
        onOpenLogin={vi.fn()}
        onLogout={vi.fn()}
        onHomeClick={vi.fn()}
        onAccountsClick={vi.fn()}
      />
    );

    expect(screen.getByText('testuser')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /log out/i })).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: /accounts/i })).not.toBeInTheDocument();
  });

  it('should display accounts button when user is administrator', async () => {
    const userAction = userEvent.setup();
    const handleAccountsClick = vi.fn();
    const adminUser = { username: 'admin', role: 'ADMINISTRATOR', token: 'jwt' };

    render(
      <Navbar
        user={adminUser}
        onOpenLogin={vi.fn()}
        onLogout={vi.fn()}
        onHomeClick={vi.fn()}
        onAccountsClick={handleAccountsClick}
      />
    );

    const accountsBtn = screen.getByRole('button', { name: /user account management/i });
    expect(accountsBtn).toBeInTheDocument();

    await userAction.click(accountsBtn);
    expect(handleAccountsClick).toHaveBeenCalledTimes(1);
  });

  it('should call onOpenLogin when sign in button is clicked', async () => {
    const userAction = userEvent.setup();
    const handleOpenLogin = vi.fn();

    render(
      <Navbar
        user={null}
        onOpenLogin={handleOpenLogin}
        onLogout={vi.fn()}
        onHomeClick={vi.fn()}
        onAccountsClick={vi.fn()}
      />
    );

    await userAction.click(screen.getByRole('button', { name: /sign in/i }));

    expect(handleOpenLogin).toHaveBeenCalledTimes(1);
  });

  it('should call onLogout when logout button is clicked', async () => {
    const userAction = userEvent.setup();
    const handleLogout = vi.fn();
    const user = { username: 'admin', role: 'ADMINISTRATOR', token: 'jwt' };

    render(
      <Navbar
        user={user}
        onOpenLogin={vi.fn()}
        onLogout={handleLogout}
        onHomeClick={vi.fn()}
        onAccountsClick={vi.fn()}
      />
    );

    await userAction.click(screen.getByRole('button', { name: /log out/i }));

    expect(handleLogout).toHaveBeenCalledTimes(1);
  });
});
