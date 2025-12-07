import request from './request';

export interface LoginDto {
  username: string;
  password: string;
  ipAddress: string;
}

export interface RegisterDto {
  username?: string;
  password?: string;
  confirmPassword?: string;
  email?: string;
  ipAddress?: string;
}

export interface UpdatePasswordDto {
  oldPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface SaTokenInfo {
  tokenName: string;
  tokenValue: string;
  isLogin: boolean;
  loginId: any;
  loginType: string;
  tokenTimeout: number;
  sessionTimeout: number;
  tokenSessionTimeout: number;
  tokenActiveTimeout: number;
  loginDeviceType: string;
  tag: string;
}

export interface ResultSaTokenInfo {
  errCode: number;
  errMsg: string;
  isSuccess: boolean;
  data: SaTokenInfo;
}

export interface ResultObject {
  errCode: number;
  errMsg: string;
  isSuccess: boolean;
  data: any;
}

export const login = (data: LoginDto) => {
  return request.post<any, ResultSaTokenInfo>('/api/user/auth/login', data);
};

export const register = (data: RegisterDto) => {
  return request.post<any, ResultObject>('/api/user/auth/register', data);
};

export const updatePassword = (data: UpdatePasswordDto) => {
  return request.post<any, ResultObject>('/api/user/auth/password/update', data);
};

export interface UserDto {
  id: string;
  username: string;
  email: string;
  status: number;
  avatarUrl: string;
  emailVerified: boolean;
  ipAddress: string;
  lastLogin: string;
}

export interface ResultUserDto {
  errCode: number;
  errMsg: string;
  isSuccess: boolean;
  data: UserDto;
}

export const getUserInfo = () => {
  return request.get<any, ResultUserDto>('/api/user/profile/info');
};

export const logout = () => {
  return request.post<any, ResultObject>('/api/user/auth/logout');
};

export interface ResultBoolean {
  errCode: number;
  errMsg: string;
  isSuccess: boolean;
  data: boolean;
}

// 1.5 Check email verification status
export const getEmailVerificationStatus = () => {
  return request.get<any, ResultBoolean>('/api/user/email/verification/status');
};

// 1.6 Request email verification (send email)
export const requestEmailVerification = () => {
  return request.post<any, ResultObject>('/api/user/email/verification/request');
};

// 1.7 Confirm email verification
export const confirmEmailVerification = (token: string) => {
  return request.post<any, ResultObject>(`/api/user/email/verification/confirm?token=${token}`);
};

// Get Username by ID
export const getUsernameById = (userId: string) => {
  return request.get<any, { data: string }>(`/api/user/profile/public/username/${userId}`);
};
