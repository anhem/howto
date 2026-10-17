import { useState, FormEvent } from 'react';
import {
  Post,
  Category,
  Reply,
  CreateReplyRequest,
  UpdatePostRequest,
  UpdateReplyRequest,
} from '../types';

interface UserState {
  username: string;
  role: string;
  token: string;
}

interface PostDetailProps {
  post: Post;
  category: Category | null;
  replies: Reply[];
  user: UserState | null;
  onBackToCategory: () => void;
  onBackToCategories: () => void;
  onCreateReply: (request: CreateReplyRequest) => Promise<void>;
  onUpdatePost?: (postId: number, request: UpdatePostRequest) => Promise<void>;
  onDeletePost?: (postId: number) => Promise<void>;
  onUpdateReply?: (replyId: number, request: UpdateReplyRequest) => Promise<void>;
  onDeleteReply?: (replyId: number) => Promise<void>;
  onOpenLogin: () => void;
}

export default function PostDetail({
  post,
  category,
  replies,
  user,
  onBackToCategory,
  onBackToCategories,
  onCreateReply,
  onUpdatePost,
  onDeletePost,
  onUpdateReply,
  onDeleteReply,
  onOpenLogin,
}: PostDetailProps) {
  const [content, setContent] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const [isEditingPost, setIsEditingPost] = useState(false);
  const [editPostTitle, setEditPostTitle] = useState(post.title);
  const [editPostBody, setEditPostBody] = useState(post.body);

  const [editingReplyId, setEditingReplyId] = useState<number | null>(null);
  const [editReplyBody, setEditReplyBody] = useState('');

  const canModeratePost = Boolean(
    user &&
      (user.username === post.username ||
        user.role === 'MODERATOR' ||
        user.role === 'ADMINISTRATOR')
  );

  const canModerateReply = (reply: Reply) =>
    Boolean(
      user &&
        (user.username === reply.username ||
          user.role === 'MODERATOR' ||
          user.role === 'ADMINISTRATOR')
    );

  const handleReplySubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!content.trim()) {
      setError('Reply content cannot be empty');
      return;
    }
    setError('');
    setLoading(true);
    try {
      await onCreateReply({
        postId: post.postId,
        body: content.trim(),
      });
      setContent('');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to submit reply');
    } finally {
      setLoading(false);
    }
  };

  const handlePostEditSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!editPostTitle.trim() || !editPostBody.trim()) {
      setError('Title and content are required');
      return;
    }
    if (onUpdatePost) {
      setError('');
      setLoading(true);
      try {
        await onUpdatePost(post.postId, {
          title: editPostTitle.trim(),
          body: editPostBody.trim(),
        });
        setIsEditingPost(false);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to update post');
      } finally {
        setLoading(false);
      }
    }
  };

  const handlePostDelete = async () => {
    if (!window.confirm('Are you sure you want to delete this discussion post?')) {
      return;
    }
    if (onDeletePost) {
      try {
        await onDeletePost(post.postId);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to delete post');
      }
    }
  };

  const handleReplyEditSubmit = async (replyId: number) => {
    if (!editReplyBody.trim()) {
      setError('Reply body cannot be empty');
      return;
    }
    if (onUpdateReply) {
      setError('');
      try {
        await onUpdateReply(replyId, { body: editReplyBody.trim() });
        setEditingReplyId(null);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to update reply');
      }
    }
  };

  const handleReplyDelete = async (replyId: number) => {
    if (!window.confirm('Are you sure you want to delete this reply?')) {
      return;
    }
    if (onDeleteReply) {
      try {
        await onDeleteReply(replyId);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to delete reply');
      }
    }
  };

  return (
    <div>
      <nav className="breadcrumbs" aria-label="Breadcrumb">
        <button
          type="button"
          id="breadcrumb-home"
          className="breadcrumb-link"
          onClick={onBackToCategories}
          style={{ background: 'none', border: 'none', font: 'inherit', padding: 0, cursor: 'pointer' }}
          aria-label="Back to Categories"
        >
          Categories
        </button>
        <span className="breadcrumb-separator" aria-hidden="true">/</span>
        <button
          type="button"
          id="breadcrumb-category"
          className="breadcrumb-link"
          onClick={onBackToCategory}
          style={{ background: 'none', border: 'none', font: 'inherit', padding: 0, cursor: 'pointer' }}
          aria-label={`Back to ${category ? category.name : 'Category'}`}
        >
          {category ? category.name : 'Category'}
        </button>
        <span className="breadcrumb-separator" aria-hidden="true">/</span>
        <span style={{ color: 'var(--text-main)', fontWeight: 600 }}>Post Discussion</span>
      </nav>

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

      <article className="post-detail-container" id="post-detail-container">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: 12 }}>
          <h1 className="post-title-main" id="post-detail-title" style={{ margin: 0 }}>{post.title}</h1>
          {canModeratePost && (
            <div style={{ display: 'flex', gap: 8 }}>
              {onUpdatePost && (
                <button
                  id="edit-post-btn"
                  className="btn btn-secondary btn-sm"
                  onClick={() => {
                    setEditPostTitle(post.title);
                    setEditPostBody(post.body);
                    setIsEditingPost(true);
                  }}
                  aria-label="Edit post"
                >
                  Edit
                </button>
              )}
              {onDeletePost && (
                <button
                  id="delete-post-btn"
                  className="btn btn-outline-danger btn-sm"
                  onClick={handlePostDelete}
                  aria-label="Delete post"
                >
                  Delete
                </button>
              )}
            </div>
          )}
        </div>

        <div className="post-meta-bar">
          <span>Author: <strong>{post.username || 'User'}</strong></span>
          <span>Post ID: #{post.postId}</span>
          {post.updated && <span style={{ color: 'var(--text-dim)' }}>(edited)</span>}
        </div>
        <div className="post-body-text" id="post-detail-body">
          {post.body}
        </div>
      </article>

      {isEditingPost && (
        <div
          className="modal-overlay"
          id="edit-post-modal"
          onClick={() => setIsEditingPost(false)}
          role="dialog"
          aria-modal="true"
          aria-labelledby="edit-post-modal-title"
        >
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title" id="edit-post-modal-title">Edit Discussion Post</h2>
              <button
                className="modal-close"
                onClick={() => setIsEditingPost(false)}
                aria-label="Close"
              >
                ×
              </button>
            </div>

            <form onSubmit={handlePostEditSubmit}>
              <div className="form-group">
                <label className="form-label" htmlFor="edit-post-title-input">Title</label>
                <input
                  id="edit-post-title-input"
                  type="text"
                  value={editPostTitle}
                  onChange={(e) => setEditPostTitle(e.target.value)}
                  autoFocus
                />
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="edit-post-body-input">Content</label>
                <textarea
                  id="edit-post-body-input"
                  rows={5}
                  value={editPostBody}
                  onChange={(e) => setEditPostBody(e.target.value)}
                />
              </div>

              <div className="form-actions">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setIsEditingPost(false)}
                >
                  Cancel
                </button>
                <button
                  id="edit-post-submit-btn"
                  type="submit"
                  className="btn btn-primary"
                  disabled={loading}
                >
                  {loading ? 'Saving...' : 'Save Changes'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      <section className="replies-section" aria-label="Discussion replies">
        <h2 className="section-title" style={{ marginBottom: 16 }}>
          Replies ({replies.length})
        </h2>

        <div id="replies-container" role="list" aria-label="Replies list">
          {replies.length === 0 ? (
            <div className="empty-state" style={{ background: 'var(--bg-card)', borderRadius: 12 }}>
              <div className="empty-state-title">No replies yet</div>
              <p>Be the first to share your thoughts below.</p>
            </div>
          ) : (
            replies.map((reply) => {
              const canModThisReply = canModerateReply(reply);
              const isEditingThisReply = editingReplyId === reply.replyId;

              return (
                <div
                  key={reply.replyId}
                  id={`reply-card-${reply.replyId}`}
                  className="reply-card"
                  role="listitem"
                >
                  <div className="reply-meta">
                    <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
                      <strong style={{ color: 'var(--primary)' }}>{reply.username || 'User'}</strong>
                      <span>#{reply.replyId}</span>
                    </div>

                    {canModThisReply && !isEditingThisReply && (
                      <div style={{ display: 'flex', gap: 6 }}>
                        {onUpdateReply && (
                          <button
                            id={`edit-reply-btn-${reply.replyId}`}
                            className="btn btn-secondary btn-sm"
                            onClick={() => {
                              setEditingReplyId(reply.replyId);
                              setEditReplyBody(reply.body);
                            }}
                            style={{ padding: '2px 8px', fontSize: '0.75rem' }}
                            aria-label={`Edit reply ${reply.replyId}`}
                          >
                            Edit
                          </button>
                        )}
                        {onDeleteReply && (
                          <button
                            id={`delete-reply-btn-${reply.replyId}`}
                            className="btn btn-outline-danger btn-sm"
                            onClick={() => handleReplyDelete(reply.replyId)}
                            style={{ padding: '2px 8px', fontSize: '0.75rem' }}
                            aria-label={`Delete reply ${reply.replyId}`}
                          >
                            Delete
                          </button>
                        )}
                      </div>
                    )}
                  </div>

                  {isEditingThisReply ? (
                    <div style={{ marginTop: 8 }}>
                      <textarea
                        id={`edit-reply-input-${reply.replyId}`}
                        rows={3}
                        value={editReplyBody}
                        onChange={(e) => setEditReplyBody(e.target.value)}
                        style={{ width: '100%', marginBottom: 8 }}
                      />
                      <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
                        <button
                          type="button"
                          className="btn btn-secondary btn-sm"
                          onClick={() => setEditingReplyId(null)}
                        >
                          Cancel
                        </button>
                        <button
                          id={`save-reply-btn-${reply.replyId}`}
                          type="button"
                          className="btn btn-primary btn-sm"
                          onClick={() => handleReplyEditSubmit(reply.replyId)}
                        >
                          Save
                        </button>
                      </div>
                    </div>
                  ) : (
                    <div className="reply-body" id={`reply-content-${reply.replyId}`}>
                      {reply.body}
                    </div>
                  )}
                </div>
              );
            })
          )}
        </div>

        <div style={{ marginTop: 24, background: 'var(--bg-card)', padding: 24, borderRadius: 12, border: '1px solid var(--border-color)' }}>
          {user ? (
            <form onSubmit={handleReplySubmit} aria-label="Add reply form">
              <h3 style={{ fontSize: '1.05rem', fontWeight: 600, marginBottom: 12 }}>Add a Reply</h3>
              <div className="form-group">
                <label className="form-label" htmlFor="reply-content-input">Your Reply</label>
                <textarea
                  id="reply-content-input"
                  rows={3}
                  value={content}
                  onChange={(e) => setContent(e.target.value)}
                  placeholder="Write your response..."
                />
              </div>
              <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                <button
                  id="reply-submit-btn"
                  type="submit"
                  className="btn btn-primary btn-sm"
                  disabled={loading}
                >
                  {loading ? 'Submitting...' : 'Post Reply'}
                </button>
              </div>
            </form>
          ) : (
            <div style={{ textAlign: 'center', padding: '12px 0' }}>
              <p style={{ color: 'var(--text-muted)', marginBottom: 12 }}>Sign in to reply to this post</p>
              <button id="reply-signin-btn" className="btn btn-secondary btn-sm" onClick={onOpenLogin}>
                Sign In to Reply
              </button>
            </div>
          )}
        </div>
      </section>
    </div>
  );
}
