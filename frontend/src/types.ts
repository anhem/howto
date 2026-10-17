export interface Category {
  categoryId: number;
  name: string;
  description?: string;
}

export interface Post {
  postId: number;
  categoryId: number;
  username: string;
  title: string;
  body: string;
  created: string;
  updated?: string;
}

export interface Reply {
  replyId: number;
  postId: number;
  username: string;
  body: string;
  created: string;
  updated?: string;
}

export interface Account {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
}

export interface AccountDetails {
  account: Account;
  created: string;
  updated?: string;
  roles: string[];
}

export interface AccountsResponse {
  accountCount: number;
  accounts: Account[];
}

export interface UpdateAccountRoleRequest {
  role: string;
}

export interface CreatePostRequest {
  categoryId: number;
  title: string;
  body: string;
}

export interface UpdatePostRequest {
  title: string;
  body: string;
}

export interface CreateReplyRequest {
  postId: number;
  body: string;
}

export interface UpdateReplyRequest {
  body: string;
}

export interface UpsertCategoryRequest {
  name: string;
  description?: string;
}

export interface CreateAccountRequest {
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  password: string;
}
