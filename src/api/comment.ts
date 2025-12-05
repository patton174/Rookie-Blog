import request from './request';

export interface ResultObject {
  errCode: number;
  errMsg: string;
  isSuccess: boolean;
  data: any;
}

export interface CommentReply {
  id: number;
  articleId: string;
  commentId: number;
  replyToReplyId: number;
  userId: string;
  replyToUserId: string;
  content: string;
  replyAt: string;
  username: string;
  avatar: string;
  replyToUsername: string;
  isShow: boolean;
}

export interface Comment {
  id: number;
  articleId: string;
  userId: string;
  content: string;
  floor: number;
  isPinned: number;
  hasReply: number;
  replyCount?: number;
  commentAt: string;
  username: string;
  avatar: string;
  isShow: boolean;
  replies?: CommentReply[] | null;
}

export interface ResultListComment {
  errCode: number;
  errMsg: string;
  isSuccess: boolean;
  data: {
    items: Comment[];
  };
}

export interface ResultListCommentReply {
  errCode: number;
  errMsg: string;
  isSuccess: boolean;
  data: CommentReply[];
}

export interface ResultNumber {
  errCode: number;
  errMsg: string;
  isSuccess: boolean;
  data: number;
}

// Query Comments List
export const getComments = (articleId: string) => {
  return request.get<any, ResultListComment>(`/api/article/comment/${articleId}/comments`);
};

// Get Comment Count
export const getCommentCount = (articleId: string) => {
  return request.get<any, ResultNumber>(`/api/article/comment/${articleId}/comment-count`);
};

// Query Replies List (if needed, usually embedded)
export const getReplies = (commentId: number) => {
  return request.get<any, ResultListCommentReply>(`/api/article/comment/${commentId}/replies`);
};

// Add Comment
export const addComment = (articleId: string, content: string) => {
  return request.post<any, ResultObject>(`/api/article/comment/add`, {
    articleId,
    content
  });
};

// Reply Comment
export const replyComment = (commentId: number, content: string, articleId: string, replyToReplyId?: number, replyToUserId?: string) => {
  const data: any = { commentId, content, articleId };
  if (replyToReplyId) data.replyToReplyId = replyToReplyId;
  if (replyToUserId) data.replyToUserId = replyToUserId;
  return request.post<any, ResultObject>(`/api/article/comment/reply`, data);
};

// React to Comment
export const reactToComment = (type: string, commentId?: number, replyId?: number) => {
  const params: any = { type };
  if (commentId) params.commentId = commentId;
  if (replyId) params.replyId = replyId;
  return request.post<any, ResultObject>(`/api/article/comment/reaction`, null, {
    params
  });
};
