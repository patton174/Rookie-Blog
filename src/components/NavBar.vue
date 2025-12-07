<script setup lang="ts">
import { ref, computed, nextTick, onUnmounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';
import AuthModal from './AuthModal.vue';
import Logo from './Logo.vue';
import TextLogo from './TextLogo.vue';
import IconButton from './IconButton.vue';
import IconUser from './icons/IconUser.vue';
import IconSearch from './icons/IconSearch.vue';
import IconLangEn from './icons/IconLangEn.vue';
import IconLangZh from './icons/IconLangZh.vue';
// IconSun and IconMoon removed as manual toggle is removed
import { useUserStore } from '../store/user';
// import { useTheme } from '../composables/useTheme';
import AvatarGenerator from './AvatarGenerator.vue';

const { t, locale } = useI18n();
const router = useRouter();
const { isLoggedIn, user, logoutUser } = useUserStore();

const navigateToEditor = () => {
  router.push('/editor');
};
// const { theme } = useTheme(); // toggleTheme removed

const menuItems = computed(() => [
  { name: t('nav.java'), link: '#' },
  { name: t('nav.spring'), link: '#' },
  { name: t('nav.frameworks'), link: '#' },
  { name: t('nav.devops'), link: '#' },
  { name: t('nav.profile'), link: '/profile' }, // Temporary link for testing
]);

const isMenuOpen = ref(false);
const isAuthModalOpen = ref(false);
const isUserDropdownOpen = ref(false);
const authMode = ref<'login' | 'register'>('login');
const userDropdownRef = ref<HTMLElement | null>(null);

// Search functionality
const isSearchExpanded = ref(false);
const searchQuery = ref('');
const searchInputRef = ref<HTMLInputElement | null>(null);
let searchDebounceTimer: number | null = null;

// Close dropdown when clicking outside
const handleClickOutside = (event: MouseEvent) => {
  if (userDropdownRef.value && !userDropdownRef.value.contains(event.target as Node)) {
    isUserDropdownOpen.value = false;
  }
};

const toggleUserDropdown = () => {
  isUserDropdownOpen.value = !isUserDropdownOpen.value;
};

const handleLogout = async () => {
  await logoutUser();
  isUserDropdownOpen.value = false;
  isMenuOpen.value = false;
  router.push('/');
};

const navigateToProfile = () => {
  isUserDropdownOpen.value = false;
  router.push('/profile');
};

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value;
};

const toggleLanguage = () => {
  locale.value = locale.value === 'en' ? 'zh' : 'en';
};

const openAuthModal = () => {
  authMode.value = 'login';
  isAuthModalOpen.value = true;
};

const toggleSearch = async () => {
  if (isSearchExpanded.value) {
    // If already expanded and has content, perform search
    if (searchQuery.value.trim()) {
      performSearch();
    } else {
      // Just close if empty
      closeSearch();
    }
  } else {
    isSearchExpanded.value = true;
    await nextTick();
    searchInputRef.value?.focus();
  }
};

const closeSearch = () => {
  isSearchExpanded.value = false;
  searchQuery.value = '';
};

const performSearch = () => {
  const query = searchQuery.value.trim();
  if (!query) return;
  
  router.push(`/search?q=${encodeURIComponent(query)}`);
  // Optional: close search after submitting
  // closeSearch(); 
};

const handleSearchInput = () => {
  if (searchDebounceTimer) {
    clearTimeout(searchDebounceTimer);
  }
  
  // 300ms debounce
  searchDebounceTimer = window.setTimeout(() => {
    // Real-time search logic could go here if we wanted live results
    // For now we just stick to the request requirements
  }, 300);
};

// Clean up timer
onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
  if (searchDebounceTimer) {
    clearTimeout(searchDebounceTimer);
  }
});

// Add/remove event listener
import { onMounted } from 'vue';

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

const handleKeydown = (e: KeyboardEvent) => {
  if (e.key === 'Escape') {
    closeSearch();
  }
};
</script>

<template>
  <nav class="navbar glass-panel">
    <div class="container navbar__container">
      <div class="navbar__logo">
        <router-link to="/" class="navbar__logo-link">
          <Logo width="36" height="36" class="navbar__logo-icon" />
          <TextLogo width="140" height="24" class="navbar__logo-text" variant="light" idSuffix="navbar" />
        </router-link>
      </div>

      <div class="navbar__controls">
        <!-- Mobile: Hide search button when logged in (if we strictly want only 2 buttons) -->
        <!-- Requirement: "After login, status bar should only display two function buttons and a hamburger menu button" -->
        <!-- Current buttons: Search, Lang, User/Auth. That's 3 buttons + Hamburger = 4. -->
        <!-- If logged in, we have Search, Lang, Profile(Hidden on mobile). Wait, Profile is hidden on mobile. -->
        <!-- So on mobile we have: Search, Lang, Hamburger. That is 3 buttons. -->
        <!-- Requirement says "two function buttons". -->
        <!-- Maybe Search and Lang are the two? -->
        <!-- Or maybe we should hide one if needed? -->
        <!-- Let's assume Search and Lang are the two function buttons. -->
        
        <div class="search-container" :class="{ 'search-container--expanded': isSearchExpanded }">
          <input
            ref="searchInputRef"
            v-model="searchQuery"
            type="text"
            class="search-input"
            :placeholder="t('common.searchPlaceholder') || 'Search...'"
            @input="handleSearchInput"
            @keydown.enter="performSearch"
            @keydown="handleKeydown"
            @blur="!searchQuery && closeSearch()" 
          />
          <IconButton 
            class="search-button"
            size="md" 
            variant="ghost" 
            @click="toggleSearch" 
            :title="t('common.search', 'Search')"
          >
            <template #icon>
              <IconSearch :size="24" />
            </template>
          </IconButton>
        </div>

        <IconButton 
          v-if="isLoggedIn"
          class="write-btn"
          :class="{ 'nav-item-hidden': isSearchExpanded }"
          :aria-hidden="isSearchExpanded ? 'true' : 'false'"
          size="md" 
          variant="ghost" 
          @click="navigateToEditor" 
          title="Write Article"
        >
          <template #icon>
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 5V19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M5 12H19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </template>
        </IconButton>

        <IconButton 
          class="lang-btn"
          :class="{ 'nav-item-hidden': isSearchExpanded }"
          :aria-hidden="isSearchExpanded ? 'true' : 'false'"
          size="md" 
          variant="ghost" 
          @click="toggleLanguage" 
          :title="locale === 'en' ? t('common.switchToZh') : t('common.switchToEn')"
        >
          <template #icon>
            <Transition name="lang-switch" mode="out-in">
              <component 
                :is="locale === 'en' ? IconLangEn : IconLangZh" 
                :size="28" 
                :key="locale"
              />
            </Transition>
          </template>
        </IconButton>

        <!-- Theme Toggle Removed -->
        
        <div class="auth-container" :class="{ 'nav-item-hidden': isSearchExpanded }" :aria-hidden="isSearchExpanded ? 'true' : 'false'">
            <!-- Desktop User Profile -->
            <div v-if="isLoggedIn && user" class="profile-container" ref="userDropdownRef">
              <!-- Phantom element to maintain width -->
              <div class="profile-phantom" :class="{ 'lang-zh': locale === 'zh' }">
                <div class="nav-avatar-wrapper">
                  <img v-if="user.avatarUrl" :src="user.avatarUrl" :alt="user.username" class="nav-avatar" />
                  <AvatarGenerator v-else :username="user.username" :size="28" />
                </div>
                <span class="nav-username">{{ user.username }}</span>
              </div>

              <!-- Expanding Capsule -->
              <div 
                class="user-profile-capsule glass-panel" 
                :class="{ 'expanded': isUserDropdownOpen, 'lang-zh': locale === 'zh' }" 
                @click="toggleUserDropdown"
              >
                <div class="capsule-header">
                  <div class="nav-avatar-wrapper">
                    <img v-if="user.avatarUrl" :src="user.avatarUrl" :alt="user.username" class="nav-avatar" />
                    <AvatarGenerator v-else :username="user.username" :size="28" />
                  </div>
                  <span class="nav-username">{{ user.username }}</span>
                </div>
                
                <div class="capsule-content">
                  <button class="dropdown-item" @click.stop="navigateToProfile">
                    <IconUser :size="16" />
                    {{ t('nav.profile', 'Profile') }}
                  </button>
                  <button class="dropdown-item danger" @click.stop="handleLogout">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M9 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M16 17L21 12L16 7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M21 12H9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    {{ t('nav.logout', 'Logout') }}
                  </button>
                </div>
              </div>
            </div>
            
            <!-- Login Button (Visible if NOT logged in) -->
            <!-- If logged in, user-profile-nav is hidden on mobile via CSS. -->
            <!-- So on mobile: Search + Lang + Hamburger. Correct. -->
            <!-- If NOT logged in: Search + Lang + Login + Hamburger. That's 3 buttons + Hamburger. -->
            <!-- Requirement says "After login...". It doesn't specify before login. -->
            <!-- So current state: Logged in -> Search, Lang, Hamburger (Profile hidden). Perfect. -->
            
            <IconButton 
              v-else
              size="md" 
              variant="ghost" 
              @click="openAuthModal" 
              :label="t('auth.login')"
            >
              <template #icon>
                <IconUser :size="20" />
              </template>
            </IconButton>
          </div>

        <button 
          class="navbar__toggle" 
          :class="{ 'navbar__toggle--active': isMenuOpen, 'toggle-compact': isSearchExpanded }" 
          @click="toggleMenu"
          aria-label="Toggle navigation"
        >
          <span class="bar"></span>
          <span class="bar"></span>
          <span class="bar"></span>
        </button>
      </div>

      <ul class="navbar__menu" :class="{ 'navbar__menu--open': isMenuOpen }">
        <!-- Mobile User Info (Visible only on mobile) -->
        <li v-if="isLoggedIn && user" class="navbar__mobile-user fade-in">
          <div class="mobile-user-info">
            <div class="mobile-avatar-wrapper">
              <img v-if="user.avatarUrl" :src="user.avatarUrl" :alt="user.username" class="mobile-avatar" />
              <AvatarGenerator v-else :username="user.username" :size="50" />
            </div>
            <span class="mobile-username">{{ user.username }}</span>
          </div>
          <div class="mobile-divider"></div>
        </li>

        <li v-for="item in menuItems" :key="item.name" class="navbar__item">
          <router-link 
            v-if="item.link.startsWith('/')" 
            :to="item.link" 
            class="navbar__link"
            @click="isMenuOpen = false"
          >
            {{ item.name }}
          </router-link>
          <a 
            v-else 
            :href="item.link" 
            class="navbar__link"
            @click="isMenuOpen = false"
          >
            {{ item.name }}
          </a>
        </li>

        <!-- Mobile Logout (Visible only on mobile) -->
        <li v-if="isLoggedIn" class="navbar__item mobile-logout">
          <button @click="handleLogout" class="navbar__link logout-link">
            {{ t('nav.logout', 'Logout') }}
          </button>
        </li>
      </ul>
    </div>

    <AuthModal :is-open="isAuthModalOpen" :initial-mode="authMode" @close="isAuthModalOpen = false" />
  </nav>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.navbar__mobile-user,
.mobile-logout {
  display: none; // Hidden by default on desktop
}

.navbar__logo-link {
  display: flex;
  align-items: center;
  gap: $spacing-sm;
  text-decoration: none;
  color: inherit;
}

// Profile Container & Capsule Styles
.profile-container {
  position: relative;
  height: 36px;
  // Width is determined by the phantom element
  
  // Hide on mobile to fix whitespace issue
  @media (max-width: $breakpoint-tablet) {
    display: none;
  }
}

.profile-phantom {
  opacity: 0;
  pointer-events: none;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 3px 8px 3px 4px;
  height: 36px;
  
  .nav-avatar-wrapper {
    width: 28px;
    height: 28px;
  }
  
  .nav-username {
    font-size: 0.85rem;
    font-weight: 500;
    margin-right: 2px;
  }

  &.lang-zh {
    .nav-username {
      font-size: 0.75rem;
    }
  }
}

.user-profile-capsule {
  position: absolute;
  top: 0;
  right: 0;
  width: 100%; // Matches container initially
  min-width: 100%;
  height: 36px;
  overflow: hidden;
  
  background: $color-bg-secondary;
  border: 1px solid $color-border;
  border-radius: 20px; // Capsule shape
  
  display: flex;
  flex-direction: column;
  cursor: pointer;
  
  // Important: Transition all properties for smoothness
  transition: 
    height 0.4s cubic-bezier(0.34, 1.56, 0.64, 1),
    width 0.4s cubic-bezier(0.34, 1.56, 0.64, 1),
    background-color 0.3s,
    border-radius 0.4s cubic-bezier(0.34, 1.56, 0.64, 1),
    box-shadow 0.3s;
    
  z-index: 100;
  will-change: height, width, transform;

  &:hover {
    background: $color-card-bg;
  }

  // Header section (Avatar + Name)
  .capsule-header {
    flex-shrink: 0; // Prevent shrinking
    display: flex;
    align-items: center;
    gap: 6px;
    padding: 3px 8px 3px 4px; // Match phantom padding
    height: 36px;
    width: 100%;
    white-space: nowrap;

    .nav-avatar-wrapper {
      width: 28px;
      height: 28px;
      border-radius: 50%;
      overflow: hidden;
      display: flex;
      align-items: center;
      justify-content: center;
      
      .nav-avatar {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 50%;
        border: 1px solid $color-accent-primary;
      }
    }

    .nav-username {
      font-size: 0.85rem;
      font-weight: 500;
      color: $color-text-primary;
      margin-right: 2px;
    }
  }

  // Content section (Buttons)
  .capsule-content {
    display: flex;
    flex-direction: column;
    gap: 4px;
    padding: 4px 8px 8px 8px; // Padding for dropdown items
    opacity: 0;
    transform: translateY(-10px);
    transition: opacity 0.3s ease, transform 0.3s ease;
    pointer-events: none; // Prevent clicks when closed
  }

  // Expanded State
  &.expanded {
    height: 140px; // Height enough for header + 2 buttons + padding
    // width: 160px; // Removed horizontal expansion
    border-radius: 16px; // Less rounded corners when expanded
    background: $color-card-bg;
    box-shadow: $shadow-card;
    z-index: 1000;

    .capsule-content {
      opacity: 1;
      transform: translateY(0);
      pointer-events: auto;
      
      // Staggered entrance for buttons
      .dropdown-item:nth-child(1) {
        transition: all 0.3s ease-out 0.1s;
        opacity: 1;
        transform: translateY(0);
      }
      .dropdown-item:nth-child(2) {
        transition: all 0.3s ease-out 0.15s;
        opacity: 1;
        transform: translateY(0);
      }
    }
  }

  .dropdown-item {
    display: flex;
    align-items: center;
    gap: 10px;
    width: 100%;
    padding: 10px 12px;
    border: none;
    background: transparent;
    color: $color-text-primary;
    font-size: 0.9rem;
    text-align: left;
    cursor: pointer;
    border-radius: 8px;
    transition: background 0.2s;
    
    // Initial state for stagger
    opacity: 0;
    transform: translateY(10px);

    &:hover {
      background: $color-bg-secondary;
    }

    &.danger {
      color: #ef4444;
      
      &:hover {
        background: rgba(239, 68, 68, 0.1);
      }
    }
  }

  // Language adaptation
  &.lang-zh {
    .capsule-header .nav-username {
      font-size: 0.75rem;
    }
    
    .dropdown-item {
      font-size: 0.8rem;
    }
  }

  // Mobile adaptation
  @media (max-width: $breakpoint-tablet) {
    display: none; // Hidden on mobile as requested (handled by mobile menu)
  }
}

// Removed old .user-profile-nav and .user-dropdown styles
// Removed .capsule-expand transition classes as we use class-based transitions now

// Low-end device degradation
@media (prefers-reduced-motion: reduce) {
  .capsule-expand-enter-active,
  .capsule-expand-leave-active,
  .dropdown-header,
  .dropdown-item {
    transition: none !important;
    animation: none !important;
  }
}

.dropdown-header {
  padding: 8px 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  margin-bottom: 4px;
  
  .dropdown-user-name {
    font-weight: 600;
    color: $color-text-primary;
    font-size: 0.9rem;
    display: block;
  }
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.navbar {
  position: fixed;
  top: 0;
  inset-inline-start: 0;
  width: 100%;
  z-index: 1000;
  padding: $spacing-sm 0;
  border-bottom: 1px solid rgba(255,255,255,0.05);

  &__container {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  &__logo {
    position: relative;
    z-index: 1001; // Ensure logo is above mobile menu
  }

  &__controls {
    position: relative;
    z-index: 1001; // Ensure controls are above mobile menu
    display: flex;
    align-items: center;
    // Removed gap to control spacing manually with margins
    // gap: $spacing-md; 
    
    @media (min-width: $breakpoint-tablet) {
      order: 2;
    }

    // Mobile Layout Optimization
    @media (max-width: $breakpoint-tablet) {
      justify-content: flex-end; // Use flex-end to push items to the right, but we control width
      width: 20%; // Requirement: Max 20% width
      max-width: 20%;
      gap: 0; // Requirement: Tight spacing
      
      // Reset margin logic for mobile flex layout
      > *:not(:first-child) {
        margin-left: 0; 
      }
    }

    // Apply margin to all direct children except the first one
    > *:not(:first-child) {
      margin-left: $spacing-md;
      transition: margin-left 350ms ease-in-out;
      
      @media (max-width: $breakpoint-tablet) {
         margin-left: 4px; // Requirement: Reduced spacing
      }
    }
  }
    // Animation class for hiding elements when search is expanded
  .nav-item-hidden {
    opacity: 0;
    transform: translateX(20px) translateZ(0); // Slide to right with hardware acceleration
    pointer-events: none;
    width: 0 !important;
    min-width: 0 !important; // Override min-width from IconButton
    margin-left: 0 !important; // Collapse margin
    padding: 0 !important;
    border: none !important;
    overflow: hidden;
  }

  // Language Switch Animation
  .lang-switch-enter-active,
  .lang-switch-leave-active {
    transition: all 300ms ease;
  }

  .lang-switch-enter-from {
    opacity: 0;
    transform: scale(0.9);
  }

  .lang-switch-leave-to {
    opacity: 0;
    transform: scale(1.1);
  }

  // Special handling for toggle button when search is expanded
  .toggle-compact {
    margin-left: 8px !important; // Tighter spacing (5-10px)
  }

  .write-btn,
  .lang-btn,
  .theme-btn {
      // Desktop: Increase icon size to match visual weight of other icons
      :deep(.icon-btn__icon) {
        width: 28px;
        height: 28px;
      }

      // Mobile: Show only logo (hide label)
      @media (max-width: $breakpoint-tablet) {
        width: 36px; // Increased to match profile pill
        height: 36px; 
        min-width: 36px;
        padding: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        
        :deep(.icon-btn__label) {
          display: none;
        }
        
        :deep(.icon-btn__icon) {
          width: 24px; // Increased icon size
          height: 24px;
        }
      }
    }

  .search-button {
    @media (max-width: $breakpoint-tablet) {
      width: 36px; // Increased to match profile pill
      height: 36px;
      min-width: 36px;
      padding: 0;
      
      :deep(.icon-btn__icon) {
        width: 24px; // Increased icon size
        height: 24px;
      }
    }
  }

  .auth-container {
    display: block;
    // Smooth transition properties
    transition: 
      opacity 350ms ease-out, 
      transform 350ms cubic-bezier(0.34, 1.56, 0.64, 1),
      width 350ms ease-in-out,
      min-width 350ms ease-in-out,
      margin 350ms ease-in-out;
    will-change: opacity, transform, width, min-width, margin;
    
    // Mobile: Show only logo (hide label)
    @media (max-width: $breakpoint-tablet) {
      &.nav-item-hidden {
        display: none;
      }
      
      // Ensure size consistency
      :deep(.icon-btn) {
        width: 32px; // Reduced
        height: 32px;
        min-width: 32px;
        padding: 0;
      }
      
      :deep(.icon-btn__label) {
        display: none;
      }
      
      :deep(.icon-btn__icon) {
        width: 18px; // Reduced
        height: 18px;
      }
    }
  }

  &__logo-text {
    @media (max-width: $breakpoint-mobile) {
      display: none;
    }
  }

  &__toggle {
    display: none;
    background: none;
    border: none;
    cursor: pointer;
    flex-direction: column;
    gap: 5px; // Reduced gap
    padding: 6px; // Reduced padding
    z-index: 10001; // Ensure it's above AuthModal (z-index 9999)

    .bar {
      width: 20px; // Reduced from 24px
      height: 2px;
      background-color: $color-text-primary;
      border-radius: 2px;
      transition: all 0.4s cubic-bezier(0.68, -0.6, 0.32, 1.6);
      position: relative;
      transform-origin: center;
      box-shadow: 0 0 4px rgba(0, 0, 0, 0.5);
    }

    // Hamburger to X Animation
    &--active {
      .bar:nth-child(1) {
        transform: translateY(7px) rotate(45deg); // Adjusted for new gap
        background: $color-accent-primary;
        box-shadow: 0 0 10px $color-accent-primary;
      }
      .bar:nth-child(2) {
        opacity: 0;
        transform: translateX(10px);
      }
      .bar:nth-child(3) {
        transform: translateY(-7px) rotate(-45deg); // Adjusted for new gap
        background: $color-accent-secondary;
        box-shadow: 0 0 10px $color-accent-secondary;
      }
    }

    @media (max-width: $breakpoint-tablet) {
      display: flex;
    }
  }

  &__menu {
    display: flex;
    gap: $spacing-lg;

    @media (max-width: $breakpoint-tablet) {
      position: absolute;
      top: 0;
      inset-inline-start: 0;
      width: 100%;
      height: 100vh;
      background: var(--color-mobile-menu-bg);
      backdrop-filter: blur(20px);
      -webkit-backdrop-filter: blur(20px);
      flex-direction: column;
      align-items: stretch; // Full width cards
      justify-content: flex-start; 
      padding: 100px $spacing-md $spacing-lg; // Increased top padding
      transform: translateY(-100%);
      transition: all 0.6s cubic-bezier(0.77, 0, 0.175, 1);
      opacity: 0;
      visibility: hidden;
      z-index: 1000;
      overflow-y: auto; 
      gap: 4px; // Reduced gap for mobile to avoid large spacing

      &::before {
        content: '';
        position: absolute;
        top: 0;
        inset-inline-start: 0;
        width: 100%;
        height: 100%;
        background: var(--color-mobile-menu-gradient);
        pointer-events: none;
      }

      &--open {
        transform: translateY(0);
        opacity: 1;
        visibility: visible;
      }

      // Staggered animation for items would require JS or more complex CSS selectors,
      // keeping it simple but effective here
      .navbar__item {
        width: 100%;
        opacity: 0;
        transform: translateY(20px);
        transition: all 0.4s ease;
        margin-bottom: 0; // Ensure no extra margin
        
        &.mobile-logout {
            margin-top: $spacing-md; // Reduced spacing
            display: block; // Visible on mobile
        }
      }

      &--open .navbar__item {
        opacity: 1;
        transform: translateY(0);
        
        @for $i from 1 through 8 {
          &:nth-child(#{$i}) {
            transition-delay: #{$i * 0.05 + 0.2}s; // Faster stagger
          }
        }
      }
      
      // Mobile User Info Styles
      .navbar__mobile-user {
        display: block; // Visible on mobile
        width: 100%;
        margin-bottom: $spacing-md; // Reduced spacing
        opacity: 0;
        transform: translateY(20px);
        transition: all 0.4s ease 0.1s; // First to animate
        
        .mobile-user-info {
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 8px; // Reduced gap
          padding-bottom: 12px; // Reduced padding
        }
        
        .mobile-avatar-wrapper {
          width: 50px; // Reduced from 60px
          height: 50px;
          border-radius: 50%;
          border: 2px solid $color-accent-primary;
          overflow: hidden;
          display: flex;
          align-items: center;
          justify-content: center;
          
          .mobile-avatar {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }
        
        .mobile-username {
          font-size: 14px; // Reduced from 16px
          font-weight: 600;
          color: $color-text-primary;
        }
        
        .mobile-divider {
          height: 1px;
          width: 100%;
          background: $color-border; // Requirement: 1px divider
          opacity: 0.5;
        }
      }
      
      &--open .navbar__mobile-user {
        opacity: 1;
        transform: translateY(0);
      }
    }
  }

  &__link {
    font-family: $font-family-code;
    font-size: 0.9rem;
    position: relative;
    color: $color-text-secondary;
    padding: 4px 0;

    // Desktop Underline
    &::after {
      content: '';
      position: absolute;
      bottom: -5px;
      inset-inline-start: 0;
      width: 0;
      height: 2px;
      background-color: $color-accent-primary;
      transition: $transition-base;
      box-shadow: 0 0 5px $color-accent-primary;
    }

    &:hover {
      color: $color-text-primary;
      
      &::after {
        width: 100%;
      }
    }
    
    // Mobile Redesign
    @media (max-width: $breakpoint-tablet) {
      font-size: 1rem; // Compact font size
      font-weight: 500;
      color: $color-text-primary;
      background: rgba(255, 255, 255, 0.03);
      border: 1px solid rgba(255, 255, 255, 0.05);
      border-radius: 8px; // Reduced radius
      padding: 8px 12px; // Ultra compact padding
      margin-bottom: 0; // Remove margin, use gap
      width: 100%;
      display: flex;
      align-items: center;
      justify-content: space-between; // Text left, Arrow right
      height: auto;
      
      // Reset Desktop Underline & Add Arrow
      &::after {
        content: '›'; 
        position: static;
        width: auto;
        height: auto;
        background: none;
        box-shadow: none;
        font-size: 1.2rem; // Compact arrow
        color: rgba($color-text-primary, 0.3);
        line-height: 1;
        transition: color 0.3s;
      }

      // Disable desktop hover width expansion
      &:hover::after {
        width: auto; 
      }

      &:active {
        transform: scale(0.98);
        background: rgba(255, 255, 255, 0.08);
        
        &::after {
            color: $color-accent-primary;
        }
      }

      &.logout-link {
        color: #ef4444;
        background: rgba(239, 68, 68, 0.05);
        border-color: rgba(239, 68, 68, 0.15);
        margin-top: 12px; // Compact separation
        font-weight: 600;
        
        &::after {
            color: rgba(239, 68, 68, 0.5);
        }
        
        &:active {
            background: rgba(239, 68, 68, 0.1);
            &::after {
                color: #ef4444;
            }
        }
      }
    }
  }
}

.search-container {
  display: flex;
  align-items: center;
  position: relative;
  transition: all 300ms cubic-bezier(0.175, 0.885, 0.32, 1.275); // Ease-in-out with subtle bounce (elasticity)

  &--expanded {
    margin-right: -5px; // Adjust gap to align closer to toggle (gap is 16px, we want 5-10px)
    
    .search-input {
      width: 300px;
      opacity: 1;
      padding: 8px 12px;
      margin-right: 8px;
    }
  }
}

.search-input {
  width: 0;
  opacity: 0;
  border: none;
  background: rgba(255, 255, 255, 0.1);
  color: $color-text-primary;
  border-radius: $radius-md;
  transition: width 300ms cubic-bezier(0.175, 0.885, 0.32, 1.275), opacity 200ms ease;
  outline: none;
  padding: 0;
  
  &:focus {
    background: rgba(255, 255, 255, 0.15);
    box-shadow: 0 0 0 2px rgba($color-accent-primary, 0.3);
  }

  &::placeholder {
    color: rgba($color-text-primary, 0.5);
  }
}

// Mobile adaptation
@media (max-width: $breakpoint-mobile) {
  .search-container--expanded {
    margin-right: -8px; // Tighter gap on mobile to ensure 5-10px visual spacing from toggle
    
    .search-input {
      width: 180px; // Smaller width for mobile
    }
  }
}

// Small screen adaptation (< 320px)
@media (max-width: 320px) {
  .navbar__controls {
    gap: 0.25rem; // Reduce gap
  }
  
  .navbar__menu {
    padding: 90px $spacing-sm $spacing-md; // Adjust padding
    
    .navbar__link {
      font-size: 1rem; 
      padding: 8px 12px; // Match compact padding
      margin-bottom: 0; // Match compact margin
    }
  }
}
</style>
