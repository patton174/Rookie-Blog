import request from './request';

// Public User Info by ID (Updated based on Swagger)
export const getUserPublicProfile = (userId: string) => {
  return request.get<any, any>(`/api/user/profile/public/username/${userId}`);
};
