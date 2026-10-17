import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import PostDetail from '../PostDetail';
import { Category, Post, Reply } from '../../types';

describe('PostDetail', () => {
  const category: Category = { categoryId: 10, name: 'Spring Boot' };
  const post: Post = {
    postId: 101,
    categoryId: 10,
    username: 'alice',
    title: 'Understanding JPA',
    body: 'How does JPA work with Hibernate?',
    created: '2026-10-17T09:00:00Z',
  };
  const replies: Reply[] = [
    {
      replyId: 201,
      postId: 101,
      username: 'bob',
      body: 'JPA is the specification, Hibernate is the provider.',
      created: '2026-10-17T09:10:00Z',
    },
  ];

  it('should render post details and existing replies', () => {
    render(
      <PostDetail
        post={post}
        category={category}
        replies={replies}
        user={null}
        onBackToCategory={vi.fn()}
        onBackToCategories={vi.fn()}
        onCreateReply={vi.fn()}
        onOpenLogin={vi.fn()}
      />
    );

    expect(screen.getByRole('heading', { level: 1, name: 'Understanding JPA' })).toBeInTheDocument();
    expect(screen.getByText('How does JPA work with Hibernate?')).toBeInTheDocument();
    expect(screen.getByText('JPA is the specification, Hibernate is the provider.')).toBeInTheDocument();
  });

  it('should submit reply when user enters text and clicks post reply', async () => {
    const userAction = userEvent.setup();
    const handleCreateReply = vi.fn().mockResolvedValue(undefined);

    render(
      <PostDetail
        post={post}
        category={category}
        replies={replies}
        user={{ username: 'carol', role: 'USER', token: 'jwt' }}
        onBackToCategory={vi.fn()}
        onBackToCategories={vi.fn()}
        onCreateReply={handleCreateReply}
        onOpenLogin={vi.fn()}
      />
    );

    await userAction.type(screen.getByLabelText(/your reply/i), 'Great explanation!');
    await userAction.click(screen.getByRole('button', { name: /post reply/i }));

    expect(handleCreateReply).toHaveBeenCalledWith({
      postId: 101,
      body: 'Great explanation!',
    });
  });
});
