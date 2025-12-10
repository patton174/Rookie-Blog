import request from './request';

export interface ResultString {
  errCode: number;
  errMsg: string;
  isSuccess: boolean;
  data: string;
}

// Upload User Avatar
export const uploadAvatar = (file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  return request.post<any, ResultString>('/api/user/profile/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

// Upload Article Cover
export const uploadCover = (file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  return request.post<any, ResultString>('/api/article/cover', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};

// Upload Article Content Image
export const uploadContentImage = (file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  return request.post<any, ResultString>('/api/article/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
};
