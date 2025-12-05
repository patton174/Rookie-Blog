import { reactive, computed } from 'vue';

interface AppState {
  loading: boolean;
}

const state = reactive<AppState>({
  loading: false,
});

let loadingTimer: any = null;

export const useAppStore = () => {
  const isLoading = computed(() => state.loading);

  const startLoading = () => {
    if (loadingTimer) clearTimeout(loadingTimer);
    state.loading = true;
  };

  const stopLoading = (delay: number = 0) => {
    if (loadingTimer) clearTimeout(loadingTimer);
    if (delay > 0) {
      loadingTimer = setTimeout(() => {
        state.loading = false;
      }, delay);
    } else {
      state.loading = false;
    }
  };

  return {
    state,
    isLoading,
    startLoading,
    stopLoading,
  };
};
