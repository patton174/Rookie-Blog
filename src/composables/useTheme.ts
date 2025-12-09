import { ref, computed, watch } from 'vue';

type Theme = 'light' | 'dark';
type ThemePreference = 'light' | 'dark' | 'auto';

const STORAGE_KEY = 'theme-preference';

// Global state to share across components
const themePreference = ref<ThemePreference>('auto');
const systemTheme = ref<Theme>('light');

// Initialize system detection
if (typeof window !== 'undefined') {
  const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
  systemTheme.value = mediaQuery.matches ? 'dark' : 'light';
  
  mediaQuery.addEventListener('change', (e) => {
    systemTheme.value = e.matches ? 'dark' : 'light';
  });

  // Load stored preference
  const stored = localStorage.getItem(STORAGE_KEY) as ThemePreference;
  if (stored && ['light', 'dark', 'auto'].includes(stored)) {
    themePreference.value = stored;
  }
}

// Computed applied theme
const currentTheme = computed(() => {
  if (themePreference.value === 'auto') {
    return systemTheme.value;
  }
  return themePreference.value;
});

// Apply theme side effects
const applyTheme = (newTheme: Theme) => {
  if (typeof window === 'undefined') return;

  const root = document.documentElement;
  if (newTheme === 'dark') {
    root.classList.add('dark');
    root.setAttribute('data-theme', 'dark');
  } else {
    root.classList.remove('dark');
    root.setAttribute('data-theme', 'light');
  }

  // Update meta theme-color
  const themeColor = newTheme === 'dark' ? '#0f172a' : '#ffffff';
  let meta = document.querySelector('meta[name="theme-color"]');
  if (!meta) {
    meta = document.createElement('meta');
    meta.setAttribute('name', 'theme-color');
    document.head.appendChild(meta);
  }
  meta.setAttribute('content', themeColor);
};

// Watch for changes and apply
watch(currentTheme, (newVal) => {
  applyTheme(newVal);
}, { immediate: true });

export function useTheme() {
  const setThemePreference = (pref: ThemePreference) => {
    themePreference.value = pref;
    localStorage.setItem(STORAGE_KEY, pref);
  };

  // Legacy support for initTheme (no-op now as we use reactive watch)
  const initTheme = () => {
    applyTheme(currentTheme.value);
  };

  return {
    theme: currentTheme,
    themePreference,
    setThemePreference,
    initTheme,
    isDark: computed(() => currentTheme.value === 'dark')
  };
}
