<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const isVisible = ref(false);

onMounted(() => {
  const consent = localStorage.getItem('cookieConsent');
  if (!consent) {
    setTimeout(() => {
      isVisible.value = true;
    }, 1000); // Delay showing it slightly
  }
});

const acceptCookies = () => {
  localStorage.setItem('cookieConsent', 'true');
  isVisible.value = false;
};
</script>

<template>
  <transition name="slide-up">
    <div v-if="isVisible" class="cookie-card glass-panel">
      <div class="icon-wrapper">
        <!-- Shield Icon for Privacy -->
        <svg width="32" height="32" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="shield-icon">
          <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M12 8v4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <path d="M12 16h.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
      
      <div class="content">
            <div class="text-group">
              <h3 class="title">{{ t('cookie.title') }}</h3>
              <p class="text">{{ t('cookie.text') }}</p>
            </div>
            <div class="actions">
              <router-link to="/privacy" class="policy-link">{{ t('cookie.policy') }}</router-link>
              <button class="accept-btn" @click="acceptCookies">
                {{ t('cookie.accept') }}
              </button>
            </div>
          </div>
    </div>
  </transition>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.cookie-card {
  position: fixed;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  width: 95%;
  max-width: none; /* Limit width on very large screens for better readability */
  z-index: 9999;
  
  padding: 24px;
  border-radius: 16px;
  
  // Dark Glassmorphism for Professional Look
  background: rgba(20, 20, 25, 0.85);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 
    0 20px 40px -10px rgba(0, 0, 0, 0.5),
    0 0 0 1px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
    
  display: flex;
  gap: 24px;
  align-items: flex-start; /* Align top since we are stacking */
  justify-content: flex-start;
  
  :global(.light) & {
    background: rgba(255, 255, 255, 0.9);
    border-color: rgba(0, 0, 0, 0.05);
    box-shadow: 
      0 20px 40px -10px rgba(0, 0, 0, 0.1),
      0 0 0 1px rgba(0, 0, 0, 0.05);
  }

  @media (max-width: $breakpoint-mobile) {
    bottom: 16px;
    width: calc(100% - 32px); // 16px padding on each side
    max-width: none;
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
}

.icon-wrapper {
  flex-shrink: 0;
  width: 48px;
  height: 48px;
  border-radius: 12px;
  background: linear-gradient(135deg, rgba($color-accent-primary-rgb, 0.2), rgba($color-accent-secondary-rgb, 0.1));
  display: flex;
  align-items: center;
  justify-content: center;
  color: $color-accent-primary;
  border: 1px solid rgba($color-accent-primary-rgb, 0.2);
  
  .shield-icon {
    filter: drop-shadow(0 0 8px rgba($color-accent-primary-rgb, 0.4));
  }
}

.content {
  flex: 1;
  display: flex;
  flex-direction: column; /* Changed to column to stack text and actions */
  gap: 16px;
  justify-content: center;
}

.text-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.title {
  font-size: 1.1rem;
  font-weight: 700;
  color: #fff;
  margin: 0;
  letter-spacing: 0.5px;
  
  :global(.light) & {
    color: $color-text-primary;
  }
}

.text {
  font-size: 0.9rem;
  line-height: 1.5;
  color: rgba(255, 255, 255, 0.7);
  margin: 0;
  max-width: 100%; /* Allow full width */
  
  :global(.light) & {
    color: $color-text-secondary;
  }
}

.actions {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
  justify-content: flex-end; /* Align actions to the right */
  width: 100%;
}

.policy-link {
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.5);
  text-decoration: none;
  border-bottom: 1px solid transparent;
  transition: all 0.3s;
  
  :global(.light) & {
    color: $color-text-secondary;
  }

  &:hover {
    color: $color-accent-primary;
    border-color: $color-accent-primary;
  }
}

.accept-btn {
  background: linear-gradient(135deg, $color-accent-primary, $color-accent-secondary);
  color: #fff;
  border: none;
  padding: 8px 20px;
  border-radius: 8px;
  font-weight: 600;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba($color-accent-primary-rgb, 0.3);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(to bottom, rgba(255,255,255,0.2), transparent);
    opacity: 0;
    transition: opacity 0.3s;
  }

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 20px rgba($color-accent-primary-rgb, 0.4);
    
    &::before {
      opacity: 1;
    }
  }

  &:active {
    transform: translateY(0);
    box-shadow: 0 2px 8px rgba($color-accent-primary, 0.3);
  }
}

// Transition
.slide-up-enter-active,
.slide-up-leave-active {
  transition: all 0.6s cubic-bezier(0.16, 1, 0.3, 1);
}

.slide-up-enter-from,
.slide-up-leave-to {
  opacity: 0;
  transform: translate(-50%, 40px) scale(0.95);
}
</style>
