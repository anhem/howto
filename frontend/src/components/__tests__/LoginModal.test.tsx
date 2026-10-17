import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import LoginModal from '../LoginModal';

describe('LoginModal', () => {
  it('should not render anything when isOpen is false', () => {
    const { container } = render(
      <LoginModal
        isOpen={false}
        onClose={vi.fn()}
        onLogin={vi.fn()}
        onQuickLogin={vi.fn()}
        onRegister={vi.fn()}
      />
    );

    expect(container).toBeEmptyDOMElement();
  });

  it('should display error when submitting with empty fields', async () => {
    const userAction = userEvent.setup();
    const handleLogin = vi.fn();

    render(
      <LoginModal
        isOpen={true}
        onClose={vi.fn()}
        onLogin={handleLogin}
        onQuickLogin={vi.fn()}
        onRegister={vi.fn()}
      />
    );

    await userAction.click(screen.getByRole('button', { name: 'Sign In' }));

    expect(screen.getByRole('alert')).toHaveTextContent(/please fill in both fields/i);
    expect(handleLogin).not.toHaveBeenCalled();
  });

  it('should call onLogin with entered credentials when form is valid', async () => {
    const userAction = userEvent.setup();
    const handleLogin = vi.fn().mockResolvedValue(undefined);
    const handleClose = vi.fn();

    render(
      <LoginModal
        isOpen={true}
        onClose={handleClose}
        onLogin={handleLogin}
        onQuickLogin={vi.fn()}
        onRegister={vi.fn()}
      />
    );

    await userAction.type(screen.getByLabelText(/^username$/i), 'admin');
    await userAction.type(screen.getByLabelText(/^password$/i), 'secret');
    await userAction.click(screen.getByRole('button', { name: 'Sign In' }));

    expect(handleLogin).toHaveBeenCalledWith('admin', 'secret');
    expect(handleClose).toHaveBeenCalledTimes(1);
  });

  it('should trigger quick login when quick chip is clicked', async () => {
    const userAction = userEvent.setup();
    const handleQuickLogin = vi.fn().mockResolvedValue(undefined);
    const handleClose = vi.fn();

    render(
      <LoginModal
        isOpen={true}
        onClose={handleClose}
        onLogin={vi.fn()}
        onQuickLogin={handleQuickLogin}
        onRegister={vi.fn()}
      />
    );

    await userAction.click(screen.getByRole('button', { name: /admin/i }));

    expect(handleQuickLogin).toHaveBeenCalledWith('admin', 'superSecret1!');
    expect(handleClose).toHaveBeenCalledTimes(1);
  });

  it('should switch to sign up tab and register new user', async () => {
    const userAction = userEvent.setup();
    const handleRegister = vi.fn().mockResolvedValue(undefined);
    const handleClose = vi.fn();

    render(
      <LoginModal
        isOpen={true}
        onClose={handleClose}
        onLogin={vi.fn()}
        onQuickLogin={vi.fn()}
        onRegister={handleRegister}
      />
    );

    await userAction.click(screen.getByRole('tab', { name: 'Sign Up' }));

    await userAction.type(screen.getByLabelText(/username \(min 6 characters\)/i), 'johnsmith');
    await userAction.type(screen.getByLabelText(/email/i), 'john@example.com');
    await userAction.type(screen.getByLabelText(/first name/i), 'John');
    await userAction.type(screen.getByLabelText(/last name/i), 'Smith');
    await userAction.type(screen.getByLabelText(/password/i), 'strongPassword123');

    await userAction.click(screen.getByRole('button', { name: /create account/i }));

    expect(handleRegister).toHaveBeenCalledWith({
      username: 'johnsmith',
      email: 'john@example.com',
      firstName: 'John',
      lastName: 'Smith',
      password: 'strongPassword123',
    });
    expect(handleClose).toHaveBeenCalledTimes(1);
  });
});
