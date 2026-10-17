import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import CategoryList from '../CategoryList';
import { Category } from '../../types';

describe('CategoryList', () => {
  const sampleCategories: Category[] = [
    { categoryId: 1, name: 'General', description: 'General discussions' },
    { categoryId: 2, name: 'Tech', description: 'Tech discussions' },
  ];

  it('should render empty state message when no categories exist', () => {
    render(
      <CategoryList
        categories={[]}
        user={null}
        onSelectCategory={vi.fn()}
        onCreateCategory={vi.fn()}
        onOpenLogin={vi.fn()}
        onQuickLogin={vi.fn()}
      />
    );

    expect(screen.getByText(/no categories yet/i)).toBeInTheDocument();
  });

  it('should render category cards with names and descriptions', () => {
    render(
      <CategoryList
        categories={sampleCategories}
        user={null}
        onSelectCategory={vi.fn()}
        onCreateCategory={vi.fn()}
        onOpenLogin={vi.fn()}
        onQuickLogin={vi.fn()}
      />
    );

    expect(screen.getByText('General')).toBeInTheDocument();
    expect(screen.getByText('Tech')).toBeInTheDocument();
    expect(screen.getByText('General discussions')).toBeInTheDocument();
  });

  it('should call onSelectCategory when a category card is clicked', async () => {
    const userAction = userEvent.setup();
    const handleSelectCategory = vi.fn();

    render(
      <CategoryList
        categories={sampleCategories}
        user={null}
        onSelectCategory={handleSelectCategory}
        onCreateCategory={vi.fn()}
        onOpenLogin={vi.fn()}
        onQuickLogin={vi.fn()}
      />
    );

    await userAction.click(screen.getByRole('listitem', { name: /category general/i }));

    expect(handleSelectCategory).toHaveBeenCalledWith(sampleCategories[0]);
  });

  it('should show create category button only when user has permission', () => {
    const { rerender } = render(
      <CategoryList
        categories={sampleCategories}
        user={{ username: 'standardUser', role: 'USER', token: 'jwt' }}
        onSelectCategory={vi.fn()}
        onCreateCategory={vi.fn()}
        onOpenLogin={vi.fn()}
        onQuickLogin={vi.fn()}
      />
    );

    expect(screen.queryByRole('button', { name: /create new category/i })).not.toBeInTheDocument();

    rerender(
      <CategoryList
        categories={sampleCategories}
        user={{ username: 'admin', role: 'ADMINISTRATOR', token: 'jwt' }}
        onSelectCategory={vi.fn()}
        onCreateCategory={vi.fn()}
        onOpenLogin={vi.fn()}
        onQuickLogin={vi.fn()}
      />
    );

    expect(screen.getByRole('button', { name: /create new category/i })).toBeInTheDocument();
  });
});
