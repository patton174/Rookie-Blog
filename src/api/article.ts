import request from './request';

export interface ResultObject {
  errCode: number;
  errMsg: string;
  isSuccess: boolean;
  data: any;
}

export interface Article {
  id: string;
  title: string;
  authorId: string;
  status: string;
  summary: string;
  visibility: string;
  isTop: number;
  slug: string;
  coverUrl: string;
  language: string;
  allowComment: number;
  publishAt: string;
  createdAt: string;
  updateAt: string;
  // Optional fields that might not be in ArticleVo but used in UI (to be verified)
  views?: number;
  likes?: number;
  favorites?: number;
  comments?: number;
  tags?: string[];
  readingTimeMinutes?: number;
}

export interface ArticleChapter {
  id: string;
  articleId: string;
  title: string;
  sort: number;
  parentId: string;
}

export interface ArticleContent {
  articleId: string;
  content?: string; // HTML?
  contentRaw?: string; // Markdown?
  contentMd?: string; // Markdown (New)
  contentHtml?: string; // HTML (New)
}

export interface ResultArticle {
  errCode: number;
  errMsg: string;
  isSuccess: boolean;
  data: Article;
}

export interface ResultListArticle {
  errCode: number;
  errMsg: string;
  isSuccess: boolean;
  data: Article[];
}

export interface ResultListArticleChapter {
  errCode: number;
  errMsg: string;
  isSuccess: boolean;
  data: ArticleChapter[];
}

export interface ResultArticleContent {
  errCode: number;
  errMsg: string;
  isSuccess: boolean;
  data: ArticleContent;
}

export interface ResultBoolean {
  errCode: number;
  errMsg: string;
  isSuccess: boolean;
  data: boolean;
}

export interface ResultNumber {
  errCode: number;
  errMsg: string;
  isSuccess: boolean;
  data: number;
}

// Query Published Articles List
export const getPublishedArticles = () => {
  return request.get<any, ResultListArticle>('/api/article/read/published');
};

// Query Article by Slug
export const getArticleBySlug = (slug: string) => {
  return request.get<any, ResultArticle>(`/api/article/read/by-slug/${slug}`);
};

// Get Article by ID
export const getArticleById = (id: string) => {
  return request.get<any, ResultArticle>(`/api/article/read/${id}`);
};

// Query Article Chapters
export const getArticleChapters = (articleId: string) => {
  return request.get<any, ResultListArticleChapter>(`/api/article/read/${articleId}/chapters`);
};

// Query Article Content
export const getArticleContent = (articleId: string) => {
  return request.get<any, ResultArticleContent>(`/api/article/read/${articleId}/content`);
};

// Record Article View
export const recordView = (articleId: string) => {
  return request.post<any, ResultObject>(`/api/article/interaction/${articleId}/view`);
};

// Like Article
export const likeArticle = (articleId: string) => {
  return request.post<any, ResultObject>(`/api/article/interaction/${articleId}/like`);
};

// Unlike Article
export const unlikeArticle = (articleId: string) => {
  return request.post<any, ResultObject>(`/api/article/interaction/${articleId}/unlike`);
};

// Favorite Article
export const favoriteArticle = (articleId: string) => {
  return request.post<any, ResultObject>(`/api/article/interaction/${articleId}/favorite`);
};

// Unfavorite Article
export const unfavoriteArticle = (articleId: string) => {
  return request.post<any, ResultObject>(`/api/article/interaction/${articleId}/unfavorite`);
};

// Check if User Liked Article
export const checkIsLiked = (articleId: string) => {
  return request.get<any, ResultBoolean>(`/api/article/interaction/${articleId}/liked`);
};

// Check if User Favorited Article
export const checkIsFavorited = (articleId: string) => {
  return request.get<any, ResultBoolean>(`/api/article/interaction/${articleId}/favorited`);
};

// Get Like Count
export const getLikeCount = (articleId: string) => {
  return request.get<any, ResultNumber>(`/api/article/interaction/${articleId}/like-count`);
};

// Get Favorite Count
export const getFavoriteCount = (articleId: string) => {
  return request.get<any, ResultNumber>(`/api/article/interaction/${articleId}/favorite-count`);
};

// Check if User is Owner of Article
export const checkArticleOwnership = (articleId: string) => {
  return request.get<any, ResultBoolean>(`/api/article/${articleId}/check`);
};

// Create/Publish Article
export interface ArticleSaveDto {
  id?: string; // Optional for create, required for update
  title: string;
  summary?: string;
  contentMd: string;   // Markdown content
  contentHtml: string; // HTML content
  coverUrl?: string;
  categoryCode?: string;
  tags?: string[];
  allowComment?: number; // 1 for yes, 0 for no
}

export const publishArticle = (data: ArticleSaveDto) => {
  return request.post<any, ResultObject>('/api/article/publish', data);
};

// Save Draft
export const saveDraft = (data: ArticleSaveDto) => {
  return request.post<any, ResultObject>('/api/article/draft', data);
};

// Delete Article
export const deleteArticle = (id: string) => {
  return request.delete<any, ResultObject>(`/api/article/${id}`);
};


