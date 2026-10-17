import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import AccountsView from '../AccountsView';
import { Account, AccountDetails } from '../../types';

describe('AccountsView', () => {
  const sampleAccounts: Account[] = [
    { id: 1, username: 'admin', email: 'admin@example.com', firstName: 'Admin', lastName: 'User' },
    { id: 2, username: 'regular', email: 'reg@example.com', firstName: 'Reg', lastName: 'User' },
  ];

  it('should render account list and hide delete button for current user', () => {
    render(
      <AccountsView
        accounts={sampleAccounts}
        currentUsername="admin"
        onBack={vi.fn()}
        onDeleteAccount={vi.fn()}
        onFetchAccountDetails={vi.fn()}
        onCreateAdmin={vi.fn()}
      />
    );

    expect(screen.getByText('admin')).toBeInTheDocument();
    expect(screen.getByText('regular')).toBeInTheDocument();

    expect(screen.queryByRole('button', { name: /delete/i })).toBeInTheDocument();
  });

  it('should fetch and display account profile details when clicking Details', async () => {
    const userAction = userEvent.setup();
    const sampleDetails: AccountDetails = {
      account: sampleAccounts[0],
      created: '2026-10-17T09:00:00Z',
      roles: ['ADMINISTRATOR'],
    };
    const handleFetchDetails = vi.fn().mockResolvedValue(sampleDetails);

    render(
      <AccountsView
        accounts={sampleAccounts}
        currentUsername="admin"
        onBack={vi.fn()}
        onDeleteAccount={vi.fn()}
        onFetchAccountDetails={handleFetchDetails}
        onCreateAdmin={vi.fn()}
      />
    );

    const detailsBtn = screen.getByRole('button', { name: /details for admin/i });
    await userAction.click(detailsBtn);

    expect(handleFetchDetails).toHaveBeenCalledWith(1);
    expect(await screen.findByText('Account Profile')).toBeInTheDocument();
    expect(screen.getByText('ASSIGNED ROLES')).toBeInTheDocument();
  });

  it('should call onCreateModerator when submitting modal with moderator role', async () => {
    const userAction = userEvent.setup();
    const handleCreateModerator = vi.fn().mockResolvedValue(undefined);

    render(
      <AccountsView
        accounts={sampleAccounts}
        currentUsername="admin"
        onBack={vi.fn()}
        onDeleteAccount={vi.fn()}
        onFetchAccountDetails={vi.fn()}
        onCreateAdmin={vi.fn()}
        onCreateModerator={handleCreateModerator}
      />
    );

    await userAction.click(screen.getByRole('button', { name: /\+ provision account/i }));

    await userAction.selectOptions(screen.getByLabelText(/role/i), 'MODERATOR');
    await userAction.type(screen.getByLabelText(/^username$/i), 'moduser');
    await userAction.type(screen.getByLabelText(/^email$/i), 'mod@example.com');
    await userAction.type(screen.getByLabelText(/^first name$/i), 'Mod');
    await userAction.type(screen.getByLabelText(/^last name$/i), 'User');
    await userAction.type(screen.getByLabelText(/^password$/i), 'secretPass123!');

    await userAction.click(screen.getByRole('button', { name: /provision moderator/i }));

    expect(handleCreateModerator).toHaveBeenCalledWith({
      username: 'moduser',
      email: 'mod@example.com',
      firstName: 'Mod',
      lastName: 'User',
      password: 'secretPass123!',
    });
  });

  it('should call onUpdateAccountRole when changing role and clicking Apply', async () => {
    const userAction = userEvent.setup();
    const sampleDetails: AccountDetails = {
      account: sampleAccounts[1],
      created: '2026-10-17T09:00:00Z',
      roles: ['USER'],
    };
    const updatedDetails: AccountDetails = {
      ...sampleDetails,
      roles: ['MODERATOR'],
    };
    const handleFetchDetails = vi.fn().mockResolvedValue(sampleDetails);
    const handleUpdateRole = vi.fn().mockResolvedValue(updatedDetails);

    render(
      <AccountsView
        accounts={sampleAccounts}
        currentUsername="admin"
        onBack={vi.fn()}
        onDeleteAccount={vi.fn()}
        onFetchAccountDetails={handleFetchDetails}
        onCreateAdmin={vi.fn()}
        onUpdateAccountRole={handleUpdateRole}
      />
    );

    await userAction.click(screen.getByRole('button', { name: /details for regular/i }));
    expect(await screen.findByText('Account Profile')).toBeInTheDocument();

    const roleSelect = screen.getByLabelText(/change account role/i);
    await userAction.selectOptions(roleSelect, 'MODERATOR');

    await userAction.click(screen.getByRole('button', { name: /apply/i }));

    expect(handleUpdateRole).toHaveBeenCalledWith(2, 'MODERATOR');
  });
});
