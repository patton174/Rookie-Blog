import { reactive, computed } from 'vue';
import { getUserInfo, logout } from '../api/auth';

interface UserState {
  userInfo: UserInfo | null;
  loading: boolean;
}

export interface UserInfo {
  id: string;
  username: string;
  email: string;
  status: number;
  bio?: string;
  avatarUrl: string;
  emailVerified: boolean;
  ipAddress: string;
  lastLogin: string;
}

const state = reactive<UserState>({
  userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'),
  loading: false,
});

export const useUserStore = () => {
  const isLoggedIn = computed(() => !!state.userInfo);
  const user = computed(() => state.userInfo);

  const fetchUserInfo = async () => {
    try {
      state.loading = true;
      const res = await getUserInfo();
      if (res.isSuccess && res.data) {
        state.userInfo = res.data;
        localStorage.setItem('userInfo', JSON.stringify(res.data));
      }
    } catch (error: any) {
      // Ignore 401 errors as they just mean user is not logged in
      if (error.response && error.response.status === 401) {
        state.userInfo = null;
        localStorage.removeItem('userInfo');
        return;
      }
      console.error('Failed to fetch user info:', error);
      // Don't clear user info on other errors (like network error) to keep UI stable
      // state.userInfo = null; 
    } finally {
      state.loading = false;
    }
  };

  const clearUser = () => {
    state.userInfo = null;
    localStorage.removeItem('userInfo');
  };

  const logoutUser = async () => {
    try {
      await logout();
    } catch (error) {
      console.error('Logout failed:', error);
    } finally {
      clearUser();
    }
  };

  return {
    state,
    isLoggedIn,
    user,
    fetchUserInfo,
    clearUser,
    logoutUser,
  };
};
