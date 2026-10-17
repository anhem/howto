import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import Navbar from './components/Navbar';
import LoginModal from './components/LoginModal';
import CategoryList from './components/CategoryList';
import PostList from './components/PostList';
import PostDetail from './components/PostDetail';
import AccountsView from './components/AccountsView';
import NotificationToast, { Toast } from './components/NotificationToast';
import { api } from './api/client';
import {
  Category,
  Post,
  CreatePostRequest,
  UpdatePostRequest,
  CreateReplyRequest,
  UpdateReplyRequest,
  UpsertCategoryRequest,
  CreateAccountRequest,
} from './types';

interface UserState {
  username: string;
  role: string;
  token: string;
}

interface JwtPayload {
  sub: string;
  ROLES: string[] | string;
}

interface LoginResponse {
  message: string;
}

function parseJwt(token: string): { username: string; role: string } | null {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    const parsed: JwtPayload = JSON.parse(jsonPayload);
    const rawRoles = parsed.ROLES;
    const roles = Array.isArray(rawRoles) ? rawRoles : [rawRoles].filter(Boolean);
    let role = 'USER';
    if (roles.some((r) => r.includes('ADMINISTRATOR'))) {
      role = 'ADMINISTRATOR';
    } else if (roles.some((r) => r.includes('MODERATOR'))) {
      role = 'MODERATOR';
    }
    return {
      username: parsed.sub,
      role,
    };
  } catch {
    return null;
  }
}

export default function App() {
  const queryClient = useQueryClient();
  const [token, setToken] = useState<string | null>(() => localStorage.getItem('howto_jwt_token'));
  const [user, setUser] = useState<UserState | null>(() => {
    const savedToken = localStorage.getItem('howto_jwt_token');
    if (!savedToken) return null;
    const parsed = parseJwt(savedToken);
    return parsed ? { ...parsed, token: savedToken } : null;
  });

  const [currentView, setCurrentView] = useState<'forum' | 'accounts'>('forum');
  const [selectedCategory, setSelectedCategory] = useState<Category | null>(null);
  const [selectedPost, setSelectedPost] = useState<Post | null>(null);
  const [isLoginOpen, setIsLoginOpen] = useState(false);
  const [toasts, setToasts] = useState<Toast[]>([]);

  const addToast = (message: string, type: 'success' | 'error' = 'success') => {
    const id = Date.now() + Math.random();
    setToasts((prev) => [...prev, { id, message, type }]);
    setTimeout(() => {
      setToasts((prev) => prev.filter((t) => t.id !== id));
    }, 4000);
  };

  const removeToast = (id: number) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  };

  const { data: categories = [] } = useQuery<Category[]>({
    queryKey: ['categories', token],
    queryFn: () => (token ? api.getCategories(token) : Promise.resolve([])),
    enabled: Boolean(token),
  });

  const { data: posts = [] } = useQuery<Post[]>({
    queryKey: ['posts', selectedCategory?.categoryId, token],
    queryFn: () =>
      token && selectedCategory
        ? api.getPosts(selectedCategory.categoryId, token)
        : Promise.resolve([]),
    enabled: Boolean(token && selectedCategory),
  });

  const { data: replies = [] } = useQuery({
    queryKey: ['replies', selectedPost?.postId, token],
    queryFn: () =>
      token && selectedPost
        ? api.getReplies(selectedPost.postId, token)
        : Promise.resolve([]),
    enabled: Boolean(token && selectedPost),
  });

  const { data: accountsData } = useQuery({
    queryKey: ['accounts', token],
    queryFn: () => (token ? api.getAccounts(token) : Promise.resolve({ accountCount: 0, accounts: [] })),
    enabled: Boolean(token && user?.role === 'ADMINISTRATOR' && currentView === 'accounts'),
  });

  const createCategoryMutation = useMutation({
    mutationFn: (newCategory: UpsertCategoryRequest) => {
      if (!token) throw new Error('Not authenticated');
      return api.createCategory(newCategory, token);
    },
    onSuccess: () => {
      addToast('Category created successfully');
      queryClient.invalidateQueries({ queryKey: ['categories', token] });
    },
    onError: (err: Error) => {
      addToast(err.message || 'Failed to create category', 'error');
    },
  });

  const updateCategoryMutation = useMutation({
    mutationFn: ({ categoryId, data }: { categoryId: number; data: UpsertCategoryRequest }) => {
      if (!token) throw new Error('Not authenticated');
      return api.updateCategory(categoryId, data, token);
    },
    onSuccess: () => {
      addToast('Category updated successfully');
      queryClient.invalidateQueries({ queryKey: ['categories', token] });
    },
    onError: (err: Error) => {
      addToast(err.message || 'Failed to update category', 'error');
    },
  });

  const deleteCategoryMutation = useMutation({
    mutationFn: (categoryId: number) => {
      if (!token) throw new Error('Not authenticated');
      return api.deleteCategory(categoryId, token);
    },
    onSuccess: () => {
      addToast('Category deleted');
      queryClient.invalidateQueries({ queryKey: ['categories', token] });
      if (selectedCategory) {
        setSelectedCategory(null);
      }
    },
    onError: (err: Error) => {
      addToast(err.message || 'Failed to delete category', 'error');
    },
  });

  const createPostMutation = useMutation({
    mutationFn: (newPost: CreatePostRequest) => {
      if (!token) throw new Error('Not authenticated');
      return api.createPost(newPost, token);
    },
    onSuccess: () => {
      addToast('Discussion post published');
      queryClient.invalidateQueries({
        queryKey: ['posts', selectedCategory?.categoryId, token],
      });
    },
    onError: (err: Error) => {
      addToast(err.message || 'Failed to create post', 'error');
    },
  });

  const updatePostMutation = useMutation({
    mutationFn: ({ postId, data }: { postId: number; data: UpdatePostRequest }) => {
      if (!token) throw new Error('Not authenticated');
      return api.updatePost(postId, data, token);
    },
    onSuccess: (updated) => {
      addToast('Post updated');
      setSelectedPost(updated);
      queryClient.invalidateQueries({
        queryKey: ['posts', selectedCategory?.categoryId, token],
      });
    },
    onError: (err: Error) => {
      addToast(err.message || 'Failed to update post', 'error');
    },
  });

  const deletePostMutation = useMutation({
    mutationFn: (postId: number) => {
      if (!token) throw new Error('Not authenticated');
      return api.deletePost(postId, token);
    },
    onSuccess: () => {
      addToast('Post deleted');
      setSelectedPost(null);
      queryClient.invalidateQueries({
        queryKey: ['posts', selectedCategory?.categoryId, token],
      });
    },
    onError: (err: Error) => {
      addToast(err.message || 'Failed to delete post', 'error');
    },
  });

  const createReplyMutation = useMutation({
    mutationFn: (newReply: CreateReplyRequest) => {
      if (!token) throw new Error('Not authenticated');
      return api.createReply(newReply, token);
    },
    onSuccess: () => {
      addToast('Reply submitted');
      queryClient.invalidateQueries({
        queryKey: ['replies', selectedPost?.postId, token],
      });
    },
    onError: (err: Error) => {
      addToast(err.message || 'Failed to submit reply', 'error');
    },
  });

  const updateReplyMutation = useMutation({
    mutationFn: ({ replyId, data }: { replyId: number; data: UpdateReplyRequest }) => {
      if (!token) throw new Error('Not authenticated');
      return api.updateReply(replyId, data, token);
    },
    onSuccess: () => {
      addToast('Reply updated');
      queryClient.invalidateQueries({
        queryKey: ['replies', selectedPost?.postId, token],
      });
    },
    onError: (err: Error) => {
      addToast(err.message || 'Failed to update reply', 'error');
    },
  });

  const deleteReplyMutation = useMutation({
    mutationFn: (replyId: number) => {
      if (!token) throw new Error('Not authenticated');
      return api.deleteReply(replyId, token);
    },
    onSuccess: () => {
      addToast('Reply deleted');
      queryClient.invalidateQueries({
        queryKey: ['replies', selectedPost?.postId, token],
      });
    },
    onError: (err: Error) => {
      addToast(err.message || 'Failed to delete reply', 'error');
    },
  });

  const createAdminMutation = useMutation({
    mutationFn: (newAdmin: CreateAccountRequest) => {
      if (!token) throw new Error('Not authenticated');
      return api.createAdminAccount(newAdmin, token);
    },
    onSuccess: () => {
      addToast('Administrator account created');
      queryClient.invalidateQueries({ queryKey: ['accounts', token] });
    },
    onError: (err: Error) => {
      addToast(err.message || 'Failed to create administrator', 'error');
    },
  });

  const createModeratorMutation = useMutation({
    mutationFn: (newModerator: CreateAccountRequest) => {
      if (!token) throw new Error('Not authenticated');
      return api.createModeratorAccount(newModerator, token);
    },
    onSuccess: () => {
      addToast('Moderator account created');
      queryClient.invalidateQueries({ queryKey: ['accounts', token] });
    },
    onError: (err: Error) => {
      addToast(err.message || 'Failed to create moderator', 'error');
    },
  });

  const updateAccountRoleMutation = useMutation({
    mutationFn: ({ accountId, role }: { accountId: number; role: string }) => {
      if (!token) throw new Error('Not authenticated');
      return api.updateAccountRole(accountId, role, token);
    },
    onSuccess: (updated) => {
      addToast(`Role updated for ${updated.account.username}`);
      queryClient.invalidateQueries({ queryKey: ['accounts', token] });
    },
    onError: (err: Error) => {
      addToast(err.message || 'Failed to update role', 'error');
    },
  });

  const deleteAccountMutation = useMutation({
    mutationFn: (accountId: number) => {
      if (!token) throw new Error('Not authenticated');
      return api.deleteAccount(accountId, token);
    },
    onSuccess: () => {
      addToast('Account removed');
      queryClient.invalidateQueries({ queryKey: ['accounts', token] });
    },
    onError: (err: Error) => {
      addToast(err.message || 'Failed to remove account', 'error');
    },
  });

  const handleLogin = async (username: string, password: string) => {
    const res = (await api.login(username, password)) as unknown as LoginResponse;
    const jwt = res.message;
    localStorage.setItem('howto_jwt_token', jwt);
    setToken(jwt);
    const parsed = parseJwt(jwt);
    if (parsed) {
      setUser({ ...parsed, token: jwt });
      addToast(`Signed in as ${parsed.username}`);
    }
  };

  const handleRegister = async (account: CreateAccountRequest) => {
    const res = (await api.register(account)) as unknown as LoginResponse;
    const jwt = res.message;
    localStorage.setItem('howto_jwt_token', jwt);
    setToken(jwt);
    const parsed = parseJwt(jwt);
    if (parsed) {
      setUser({ ...parsed, token: jwt });
      addToast(`Account created! Welcome, ${parsed.username}`);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('howto_jwt_token');
    setToken(null);
    setUser(null);
    setSelectedCategory(null);
    setSelectedPost(null);
    setCurrentView('forum');
    queryClient.clear();
    addToast('Signed out successfully');
  };

  return (
    <div className="app-container">
      <Navbar
        user={user}
        onOpenLogin={() => setIsLoginOpen(true)}
        onLogout={handleLogout}
        onHomeClick={() => {
          setSelectedPost(null);
          setSelectedCategory(null);
          setCurrentView('forum');
        }}
        onAccountsClick={() => setCurrentView('accounts')}
      />

      <main className="main-content">
        {currentView === 'accounts' && user?.role === 'ADMINISTRATOR' ? (
          <AccountsView
            accounts={accountsData?.accounts || []}
            currentUsername={user.username}
            onBack={() => setCurrentView('forum')}
            onDeleteAccount={(id) => deleteAccountMutation.mutateAsync(id).then(() => {})}
            onFetchAccountDetails={(id) => (token ? api.getAccountDetails(id, token) : Promise.reject())}
            onCreateAdmin={(req) => createAdminMutation.mutateAsync(req).then(() => {})}
            onCreateModerator={(req) => createModeratorMutation.mutateAsync(req).then(() => {})}
            onUpdateAccountRole={(id, role) => updateAccountRoleMutation.mutateAsync({ accountId: id, role })}
          />
        ) : selectedPost ? (
          <PostDetail
            post={selectedPost}
            category={selectedCategory}
            replies={replies}
            user={user}
            onBackToCategory={() => setSelectedPost(null)}
            onBackToCategories={() => {
              setSelectedPost(null);
              setSelectedCategory(null);
            }}
            onCreateReply={(req) => createReplyMutation.mutateAsync(req).then(() => {})}
            onUpdatePost={(id, req) => updatePostMutation.mutateAsync({ postId: id, data: req }).then(() => {})}
            onDeletePost={(id) => deletePostMutation.mutateAsync(id).then(() => {})}
            onUpdateReply={(id, req) => updateReplyMutation.mutateAsync({ replyId: id, data: req }).then(() => {})}
            onDeleteReply={(id) => deleteReplyMutation.mutateAsync(id).then(() => {})}
            onOpenLogin={() => setIsLoginOpen(true)}
          />
        ) : selectedCategory ? (
          <PostList
            category={selectedCategory}
            posts={posts}
            user={user}
            onBack={() => setSelectedCategory(null)}
            onSelectPost={(post) => setSelectedPost(post)}
            onCreatePost={(req) => createPostMutation.mutateAsync(req).then(() => {})}
          />
        ) : (
          <CategoryList
            categories={categories}
            user={user}
            onSelectCategory={(cat) => setSelectedCategory(cat)}
            onCreateCategory={(req) => createCategoryMutation.mutateAsync(req).then(() => {})}
            onUpdateCategory={(id, req) => updateCategoryMutation.mutateAsync({ categoryId: id, data: req }).then(() => {})}
            onDeleteCategory={(id) => deleteCategoryMutation.mutateAsync(id).then(() => {})}
            onOpenLogin={() => setIsLoginOpen(true)}
            onQuickLogin={handleLogin}
          />
        )}
      </main>

      <LoginModal
        isOpen={isLoginOpen}
        onClose={() => setIsLoginOpen(false)}
        onLogin={handleLogin}
        onQuickLogin={handleLogin}
        onRegister={handleRegister}
      />

      <NotificationToast toasts={toasts} onDismiss={removeToast} />
    </div>
  );
}
