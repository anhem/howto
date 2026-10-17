import { useState, FormEvent, KeyboardEvent, MouseEvent } from 'react';
import { Category, UpsertCategoryRequest } from '../types';

interface UserState {
  username: string;
  role: string;
  token: string;
}

interface CategoryListProps {
  categories: Category[];
  user: UserState | null;
  onSelectCategory: (category: Category) => void;
  onCreateCategory: (request: UpsertCategoryRequest) => Promise<void>;
  onUpdateCategory?: (categoryId: number, request: UpsertCategoryRequest) => Promise<void>;
  onDeleteCategory?: (categoryId: number) => Promise<void>;
  onOpenLogin: () => void;
  onQuickLogin: (username: string, password: string) => Promise<void>;
}

export default function CategoryList({
  categories,
  user,
  onSelectCategory,
  onCreateCategory,
  onUpdateCategory,
  onDeleteCategory,
  onOpenLogin,
  onQuickLogin,
}: CategoryListProps) {
  const [showModal, setShowModal] = useState(false);
  const [editingCategory, setEditingCategory] = useState<Category | null>(null);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const canCreateOrEdit = user && (user.role === 'ADMINISTRATOR' || user.role === 'MODERATOR');
  const canDelete = user && user.role === 'ADMINISTRATOR';

  const openCreateModal = () => {
    setEditingCategory(null);
    setName('');
    setDescription('');
    setError('');
    setShowModal(true);
  };

  const openEditModal = (e: MouseEvent, cat: Category) => {
    e.stopPropagation();
    setEditingCategory(cat);
    setName(cat.name);
    setDescription(cat.description || '');
    setError('');
    setShowModal(true);
  };

  const handleDelete = async (e: MouseEvent, cat: Category) => {
    e.stopPropagation();
    if (!window.confirm(`Are you sure you want to delete category "${cat.name}"?`)) {
      return;
    }
    if (onDeleteCategory) {
      try {
        await onDeleteCategory(cat.categoryId);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to delete category');
      }
    }
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (!name.trim()) {
      setError('Category name is required');
      return;
    }
    setError('');
    setLoading(true);
    try {
      if (editingCategory && onUpdateCategory) {
        await onUpdateCategory(editingCategory.categoryId, {
          name: name.trim(),
          description: description.trim(),
        });
      } else {
        await onCreateCategory({ name: name.trim(), description: description.trim() });
      }
      setName('');
      setDescription('');
      setShowModal(false);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Failed to save category');
    } finally {
      setLoading(false);
    }
  };

  const handleCardKeyDown = (e: KeyboardEvent, cat: Category) => {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      onSelectCategory(cat);
    }
  };

  return (
    <div>
      {!user && (
        <section className="hero-card" aria-label="Welcome banner">
          <h1 className="hero-title">Howto Knowledge & Discussion</h1>
          <p className="hero-subtitle">
            A structured community platform with Spring Boot security and REST services. Please sign in to explore and participate in discussions.
          </p>
          <div className="quick-login-box">
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)', fontWeight: 600 }}>
              Quick test accounts:
            </span>
            <button
              id="hero-login-admin"
              className="quick-chip"
              onClick={() => onQuickLogin('admin', 'superSecret1!')}
            >
              Admin
            </button>
            <button
              id="hero-login-moderator"
              className="quick-chip"
              onClick={() => onQuickLogin('moderator', 'superSecret2!')}
            >
              Moderator
            </button>
            <button
              id="hero-login-user"
              className="quick-chip"
              onClick={() => onQuickLogin('user', 'superSecret3!')}
            >
              User
            </button>
            <button
              id="hero-signin-btn"
              className="btn btn-primary btn-sm"
              onClick={onOpenLogin}
              style={{ marginLeft: 'auto' }}
            >
              Sign In / Sign Up
            </button>
          </div>
        </section>
      )}

      <div className="section-header">
        <h2 className="section-title">Categories</h2>
        {canCreateOrEdit && (
          <button
            id="create-category-btn"
            className="btn btn-primary btn-sm"
            onClick={openCreateModal}
            aria-label="Create new category"
          >
            + New Category
          </button>
        )}
      </div>

      <div className="cards-grid" id="categories-container" role="list" aria-label="Discussion categories">
        {categories.length === 0 ? (
          <div className="empty-state" style={{ gridColumn: '1 / -1' }}>
            <div className="empty-state-title">No categories yet</div>
            <p>
              {canCreateOrEdit
                ? 'Click "+ New Category" above to create your first discussion category.'
                : user
                ? 'Categories will appear here once created by a moderator or administrator.'
                : 'Sign in to view and create discussion categories.'}
            </p>
          </div>
        ) : (
          categories.map((cat) => (
            <div
              key={cat.categoryId}
              id={`category-card-${cat.categoryId}`}
              className="card"
              role="listitem"
              tabIndex={0}
              onClick={() => onSelectCategory(cat)}
              onKeyDown={(e) => handleCardKeyDown(e, cat)}
              aria-label={`Category ${cat.name}`}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: 8 }}>
                <h3 className="card-title" id={`category-title-${cat.categoryId}`} style={{ margin: 0 }}>
                  {cat.name}
                </h3>
                {(canCreateOrEdit || canDelete) && (
                  <div style={{ display: 'flex', gap: 6 }}>
                    {canCreateOrEdit && onUpdateCategory && (
                      <button
                        id={`edit-category-btn-${cat.categoryId}`}
                        className="btn btn-secondary btn-sm"
                        onClick={(e) => openEditModal(e, cat)}
                        style={{ padding: '2px 8px', fontSize: '0.75rem' }}
                        aria-label={`Edit category ${cat.name}`}
                      >
                        Edit
                      </button>
                    )}
                    {canDelete && onDeleteCategory && (
                      <button
                        id={`delete-category-btn-${cat.categoryId}`}
                        className="btn btn-outline-danger btn-sm"
                        onClick={(e) => handleDelete(e, cat)}
                        style={{ padding: '2px 8px', fontSize: '0.75rem' }}
                        aria-label={`Delete category ${cat.name}`}
                      >
                        Delete
                      </button>
                    )}
                  </div>
                )}
              </div>

              <p className="card-desc" style={{ marginTop: 8 }}>{cat.description || 'No description provided.'}</p>
              <div className="card-meta">
                <span>View Discussions →</span>
                <span>ID: #{cat.categoryId}</span>
              </div>
            </div>
          ))
        )}
      </div>

      {showModal && (
        <div
          className="modal-overlay"
          id="category-modal"
          onClick={() => setShowModal(false)}
          role="dialog"
          aria-modal="true"
          aria-labelledby="category-modal-title"
        >
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title" id="category-modal-title">
                {editingCategory ? 'Edit Category' : 'Create Category'}
              </h2>
              <button
                id="category-modal-close"
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
                <label className="form-label" htmlFor="category-name-input">Category Name</label>
                <input
                  id="category-name-input"
                  type="text"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="e.g. Java Architecture"
                  autoFocus
                />
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="category-desc-input">Description</label>
                <textarea
                  id="category-desc-input"
                  rows={3}
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="Brief description of this topic"
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
                  id="category-submit-btn"
                  type="submit"
                  className="btn btn-primary"
                  disabled={loading}
                >
                  {loading
                    ? 'Saving...'
                    : editingCategory
                    ? 'Update Category'
                    : 'Create Category'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
