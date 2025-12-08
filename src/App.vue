<script setup lang="ts">
import { watch, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';
import NavBar from './components/NavBar.vue';
import Footer from './components/Footer.vue';
import ScrollProgress from './components/ScrollProgress.vue';
import InteractiveBackground from './components/InteractiveBackground.vue';
import SvgSprite from './components/SvgSprite.vue';
import JavaHelloLoader from './components/JavaHelloLoader.vue';
import Toast from './components/Toast.vue';
import CookieNotice from './components/CookieNotice.vue';
import { useUserStore } from './store/user';
import { useAppStore } from './store/app';
import { useTheme } from './composables/useTheme';

const { locale } = useI18n();
const { fetchUserInfo } = useUserStore();
const { isLoading, startLoading, stopLoading } = useAppStore();
const { initTheme } = useTheme();

onMounted(async () => {
  // Initialize theme immediately
  initTheme();

  // Initial load
  startLoading();

  // Fetch user info
  await fetchUserInfo();

  // Keep loader for a minimum time to show animation
  stopLoading(1500);
});

watch(locale, (newLocale) => {
  localStorage.setItem('locale', newLocale);
  document.documentElement.dir = newLocale === 'ar' ? 'rtl' : 'ltr';
  document.documentElement.lang = newLocale;
}, { immediate: true });
</script>

<template>
  <div class="app-layout">
    <SvgSprite />
    <Toast />
    <CookieNotice />
    <transition name="fade">
      <JavaHelloLoader v-if="isLoading" />
    </transition>
    <InteractiveBackground />
    <ScrollProgress />
    <NavBar />

    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>

    <Footer />
  </div>
</template>

<style lang="scss" scoped>
@use './styles/variables' as *;

.content-layout {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: $spacing-xxl;
  margin-top: $spacing-xxl;
  position: relative;

  @media (max-width: $breakpoint-desktop) {
    grid-template-columns: 1fr;
    gap: $spacing-xl;
  }
}

.section-title {
  font-size: 2.5rem;
  margin-bottom: $spacing-lg;
  font-weight: 800;
  letter-spacing: -1px;

  span {
    font-weight: 300;
  }
}

.featured-section {
  margin-bottom: $spacing-xxl;
}

.articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: $spacing-lg;
}

.sidebar-section {
  position: sticky;
  top: 100px;
  height: fit-content;

  @media (max-width: $breakpoint-desktop) {
    position: static;
    order: 1;
  }
}

:deep(.md-editor-preview) {
  .md-editor-code {
    .md-editor-code-head {
      .md-editor-code-flag {
        span {
          margin-top: unset !important;
        }
      }
    }
  }
}
</style>
