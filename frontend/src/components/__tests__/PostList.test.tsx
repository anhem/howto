import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import PostList from '../PostList';
import { Category, Post } from '../../types';

describe('PostList', () => {
  const category: Category = { categoryId: 10, name: 'Spring Boot', description: 'Framework' };
  const samplePosts: Post[] = [
    {
      postId: 101,
      categoryId: 10,
      username: 'johndoe',
      title: 'Spring Security Config',
      body: 'Detailed guide on security configuration.',
      created: '2026-10-17T09:00:00Z',
    },
  ];

  it('should render category name and posts', () => {
    render(
      <PostList
        category={category}
        posts={samplePosts}
        user={null}
        onBack={vi.fn()}
        onSelectPost={vi.fn()}
        onCreatePost={vi.fn()}
      />
    );

    expect(screen.getByRole('heading', { level: 2, name: /spring boot/i })).toBeInTheDocument();
    expect(screen.getByText('Spring Security Config')).toBeInTheDocument();
  });

  it('should navigate back to categories when breadcrumb link is clicked', async () => {
    const userAction = userEvent.setup();
    const handleBack = vi.fn();

    render(
      <PostList
        category={category}
        posts={samplePosts}
        user={null}
        onBack={handleBack}
        onSelectPost={vi.fn()}
        onCreatePost={vi.fn()}
      />
    );

    await userAction.click(screen.getByRole('button', { name: /back to categories/i }));

    expect(handleBack).toHaveBeenCalledTimes(1);
  });

  it('should trigger onSelectPost when post card is clicked', async () => {
    const userAction = userEvent.setup();
    const handleSelectPost = vi.fn();

    render(
      <PostList
        category={category}
        posts={samplePosts}
        user={null}
        onBack={vi.fn()}
        onSelectPost={handleSelectPost}
        onCreatePost={vi.fn()}
      />
    );

    await userAction.click(screen.getByRole('listitem', { name: /post spring security config/i }));

    expect(handleSelectPost).toHaveBeenCalledWith(samplePosts[0]);
  });
});
