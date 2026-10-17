import { useState, FormEvent, KeyboardEvent } from 'react';
import { Category, Post, CreatePostRequest } from '../types';

interface UserState {
  username: string;
  role: string;
  token: string;
}

interface PostListProps {
  category: Category;
  posts: Post[];
  user: UserState | null;
  onBack: () => void;
  onSelectPost: (post: Post) => void;
  onCreatePost: (request: CreatePostRequest) => Promise<void>;
}

export default function PostList({
  category,
  posts,
  user,
  onBack,
  onSelectPost,
  onCreatePost,
}: PostListProps) {
  const [showModal, setShowModal] = useState(false);
  const [title, setTitle] = useState('');
  const [body, setBody] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!title.trim() || !body.trim()) {
      setError('Please provide both title and content');
      return;
    }
    setError('');
    setLoading(true);
    try {
      await onCreatePost({
        categoryId: category.categoryId,
        title: title.trim(),
        body: body.trim(),
      });
      setTitle('');
      setBody('');
      setShowModal(false);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to create post');
    } finally {
      setLoading(false);
    }
  };

  const handleCardKeyDown = (e: KeyboardEvent, post: Post) => {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      onSelectPost(post);
    }
  };

  return (
    <div>
      <nav className="breadcrumbs" aria-label="Breadcrumb">
        <button
          type="button"
          id="breadcrumb-home"
          className="breadcrumb-link"
          onClick={onBack}
          style={{ background: 'none', border: 'none', font: 'inherit', padding: 0, cursor: 'pointer' }}
          aria-label="Back to Categories"
        >
          Categories
        </button>
        <span className="breadcrumb-separator" aria-hidden="true">/</span>
        <span style={{ color: 'var(--text-main)', fontWeight: 600 }}>{category.name}</span>
      </nav>

      <div className="section-header">
        <div>
          <h2 className="section-title" id="category-page-title">{category.name}</h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginTop: 4 }}>
            {category.description || 'Discussions in this category'}
          </p>
        </div>

        {user && (
          <button
            id="create-post-btn"
            className="btn btn-primary btn-sm"
            onClick={() => setShowModal(true)}
            aria-label="Create new post"
          >
            + New Post
          </button>
        )}
      </div>

      <div className="cards-grid" id="posts-container" role="list" aria-label="Posts in category">
        {posts.length === 0 ? (
          <div className="empty-state" style={{ gridColumn: '1 / -1' }}>
            <div className="empty-state-title">No posts in this category yet</div>
            <p>
              {user
                ? 'Be the first to start a conversation by clicking "+ New Post" above.'
                : 'Sign in to start a discussion.'}
            </p>
          </div>
        ) : (
          posts.map((post) => (
            <div
              key={post.postId}
              id={`post-card-${post.postId}`}
              className="card"
              role="listitem"
              tabIndex={0}
              onClick={() => onSelectPost(post)}
              onKeyDown={(e) => handleCardKeyDown(e, post)}
              aria-label={`Post ${post.title}`}
            >
              <h3 className="card-title" id={`post-title-${post.postId}`}>
                {post.title}
              </h3>
              <p className="card-desc">
                {post.body && post.body.length > 140
                  ? `${post.body.slice(0, 140)}...`
                  : post.body}
              </p>
              <div className="card-meta">
                <span>By {post.username || 'User'}</span>
                <span>View Discussion →</span>
              </div>
            </div>
          ))
        )}
      </div>

      {showModal && (
        <div
          className="modal-overlay"
          id="post-modal"
          onClick={() => setShowModal(false)}
          role="dialog"
          aria-modal="true"
          aria-labelledby="post-modal-title"
        >
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title" id="post-modal-title">New Discussion Post</h2>
              <button
                id="post-modal-close"
                className="modal-close"
                onClick={() => setShowModal(false)}
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

            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label className="form-label" htmlFor="post-title-input">Title</label>
                <input
                  id="post-title-input"
                  type="text"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                  placeholder="Enter a descriptive title"
                  autoFocus
                />
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="post-body-input">Content</label>
                <textarea
                  id="post-body-input"
                  rows={5}
                  value={body}
                  onChange={(e) => setBody(e.target.value)}
                  placeholder="What would you like to discuss?"
                />
              </div>

              <div className="form-actions">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setShowModal(false)}
                >
                  Cancel
                </button>
                <button
                  id="post-submit-btn"
                  type="submit"
                  className="btn btn-primary"
                  disabled={loading}
                >
                  {loading ? 'Posting...' : 'Publish Post'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
