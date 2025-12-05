import { ref, onMounted, onUnmounted } from 'vue';

const getSystemTheme = () => {
  if (typeof window === 'undefined') return 'light';
  return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
};

const theme = ref<'light' | 'dark'>(getSystemTheme());

// Apply theme immediately upon import to avoid flicker
if (typeof window !== 'undefined') {
  const initialTheme = getSystemTheme();
  const root = document.documentElement;
  if (initialTheme === 'dark') {
    root.classList.add('dark');
    root.setAttribute('data-theme', 'dark');
  } else {
    root.classList.remove('dark');
    root.setAttribute('data-theme', 'light');
  }
}

const updateMetaThemeColor = (newTheme: 'light' | 'dark') => {
  const themeColor = newTheme === 'dark' ? '#0f172a' : '#ffffff';
  const metaThemeColor = document.querySelector('meta[name="theme-color"]');
  if (metaThemeColor) {
    metaThemeColor.setAttribute('content', themeColor);
  } else {
    const meta = document.createElement('meta');
    meta.name = 'theme-color';
    meta.content = themeColor;
    document.head.appendChild(meta);
  }
};

export function useTheme() {
  const applyTheme = (newTheme: 'light' | 'dark') => {
    const root = document.documentElement;
    if (newTheme === 'dark') {
      root.classList.add('dark');
      root.setAttribute('data-theme', 'dark');
    } else {
      root.classList.remove('dark');
      root.setAttribute('data-theme', 'light');
    }
    updateMetaThemeColor(newTheme);
    theme.value = newTheme;
  };

  const initTheme = () => {
    applyTheme(getSystemTheme());
  };

  // Apply immediately upon import if possible to avoid flicker
  // (But safe to call initTheme in App.vue)
  
  // System preference listener
  const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
  const handleSystemChange = (e: MediaQueryListEvent) => {
    applyTheme(e.matches ? 'dark' : 'light');
  };

  onMounted(() => {
    // Initialize and start listening
    initTheme();
    mediaQuery.addEventListener('change', handleSystemChange);
  });

  onUnmounted(() => {
    mediaQuery.removeEventListener('change', handleSystemChange);
  });

  return {
    theme,
    // toggleTheme removed as requested
    initTheme
  };
}
