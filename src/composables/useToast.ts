import { ref } from 'vue';

export interface Toast {
  id: number;
  message: string;
  type: 'success' | 'error' | 'info' | 'warning';
  duration?: number;
}

const toasts = ref<Toast[]>([]);
let nextId = 0;

export function useToast() {
  const addToast = (message: string, type: Toast['type'] = 'info', duration = 3000) => {
    const id = nextId++;
    toasts.value.push({ id, message, type, duration });

    if (duration > 0) {
      setTimeout(() => {
        removeToast(id);
      }, duration);
    }
  };

  const removeToast = (id: number) => {
    const index = toasts.value.findIndex(t => t.id === id);
    if (index !== -1) {
      toasts.value.splice(index, 1);
    }
  };

  return {
    toasts,
    addToast,
    removeToast
  };
}
