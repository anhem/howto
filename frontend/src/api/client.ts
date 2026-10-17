import {
  Category,
  Post,
  Reply,
  AccountDetails,
  AccountsResponse,
  CreatePostRequest,
  UpdatePostRequest,
  CreateReplyRequest,
  UpdateReplyRequest,
  UpsertCategoryRequest,
  CreateAccountRequest,
} from '../types';

interface AuthResponse {
  message: string;
}

function getAuthHeader(token: string | null): Record<string, string> {
  return token ? { Authorization: `Bearer ${token}` } : {};
}

async function request<T>(
  endpoint: string,
  options: RequestInit = {},
  token: string | null = null
): Promise<T> {
  const headers = {
    'Content-Type': 'application/json',
    ...getAuthHeader(token),
    ...options.headers,
  };

  const response = await fetch(endpoint, {
    ...options,
    headers,
  });

  if (!response.ok) {
    let errorMessage = `HTTP error ${response.status}`;
    try {
      const errorData = await response.json();
      if (errorData.message) {
        errorMessage = errorData.message;
      }
    } catch {
      // Ignore non-JSON error response bodies
    }
    throw new Error(errorMessage);
  }

  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    return response.json();
  }
  return response.text() as unknown as T;
}

export const api = {
  login: async (username: string, password: string): Promise<AuthResponse> => {
    return request<AuthResponse>('/api/auth/authenticate', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    });
  },

  register: async (account: CreateAccountRequest): Promise<AuthResponse> => {
    return request<AuthResponse>('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify(account),
    });
  },

  getCategories: async (token: string): Promise<Category[]> => {
    return request<Category[]>('/api/forum/categories', {}, token);
  },

  createCategory: async (
    category: UpsertCategoryRequest,
    token: string
  ): Promise<Category> => {
    return request<Category>(
      '/api/forum/categories',
      {
        method: 'POST',
        body: JSON.stringify(category),
      },
      token
    );
  },

  updateCategory: async (
    categoryId: number,
    category: UpsertCategoryRequest,
    token: string
  ): Promise<Category> => {
    return request<Category>(
      `/api/forum/categories/${categoryId}`,
      {
        method: 'PUT',
        body: JSON.stringify(category),
      },
      token
    );
  },

  deleteCategory: async (categoryId: number, token: string): Promise<void> => {
    return request<void>(
      `/api/forum/categories/${categoryId}`,
      {
        method: 'DELETE',
      },
      token
    );
  },

  getPosts: async (categoryId: number, token: string): Promise<Post[]> => {
    return request<Post[]>(`/api/forum/categories/${categoryId}/posts`, {}, token);
  },

  getPost: async (postId: number, token: string): Promise<Post> => {
    return request<Post>(`/api/forum/posts/${postId}`, {}, token);
  },

  createPost: async (
    post: CreatePostRequest,
    token: string
  ): Promise<Post> => {
    return request<Post>(
      '/api/forum/posts',
      {
        method: 'POST',
        body: JSON.stringify(post),
      },
      token
    );
  },

  updatePost: async (
    postId: number,
    post: UpdatePostRequest,
    token: string
  ): Promise<Post> => {
    return request<Post>(
      `/api/forum/posts/${postId}`,
      {
        method: 'PUT',
        body: JSON.stringify(post),
      },
      token
    );
  },

  deletePost: async (postId: number, token: string): Promise<void> => {
    return request<void>(
      `/api/forum/posts/${postId}`,
      {
        method: 'DELETE',
      },
      token
    );
  },

  getReplies: async (postId: number, token: string): Promise<Reply[]> => {
    return request<Reply[]>(`/api/forum/posts/${postId}/replies`, {}, token);
  },

  createReply: async (
    reply: CreateReplyRequest,
    token: string
  ): Promise<Reply> => {
    return request<Reply>(
      '/api/forum/replies',
      {
        method: 'POST',
        body: JSON.stringify(reply),
      },
      token
    );
  },

  updateReply: async (
    replyId: number,
    reply: UpdateReplyRequest,
    token: string
  ): Promise<Reply> => {
    return request<Reply>(
      `/api/forum/replies/${replyId}`,
      {
        method: 'PUT',
        body: JSON.stringify(reply),
      },
      token
    );
  },

  deleteReply: async (replyId: number, token: string): Promise<void> => {
    return request<void>(
      `/api/forum/replies/${replyId}`,
      {
        method: 'DELETE',
      },
      token
    );
  },

  getAccounts: async (token: string): Promise<AccountsResponse> => {
    return request<AccountsResponse>('/api/accounts', {}, token);
  },

  getAccountDetails: async (
    accountId: number,
    token: string
  ): Promise<AccountDetails> => {
    return request<AccountDetails>(`/api/accounts/details/${accountId}`, {}, token);
  },

  createAdminAccount: async (
    account: CreateAccountRequest,
    token: string
  ): Promise<AuthResponse> => {
    return request<AuthResponse>(
      '/api/accounts/users/administrator',
      {
        method: 'POST',
        body: JSON.stringify(account),
      },
      token
    );
  },

  createModeratorAccount: async (
    account: CreateAccountRequest,
    token: string
  ): Promise<AuthResponse> => {
    return request<AuthResponse>(
      '/api/accounts/users/moderator',
      {
        method: 'POST',
        body: JSON.stringify(account),
      },
      token
    );
  },

  updateAccountRole: async (
    accountId: number,
    role: string,
    token: string
  ): Promise<AccountDetails> => {
    return request<AccountDetails>(
      `/api/accounts/${accountId}/role`,
      {
        method: 'PUT',
        body: JSON.stringify({ role }),
      },
      token
    );
  },

  deleteAccount: async (accountId: number, token: string): Promise<void> => {
    return request<void>(
      `/api/accounts/${accountId}`,
      {
        method: 'DELETE',
      },
      token
    );
  },

  checkHealth: async (): Promise<unknown> => {
    return request('/actuator/health');
  },
};
