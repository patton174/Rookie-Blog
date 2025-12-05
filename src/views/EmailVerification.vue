<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { confirmEmailVerification } from '../api/auth';
import { useUserStore } from '../store/user';

const route = useRoute();
const router = useRouter();
const { t } = useI18n();
const { fetchUserInfo } = useUserStore();

const status = ref<'verifying' | 'success' | 'error'>('verifying');
const message = ref(t('verify.verifying'));

onMounted(async () => {
  const token = route.query.token as string;
  
  if (!token) {
    status.value = 'error';
    message.value = t('verify.invalidToken');
    return;
  }

  try {
    const res = await confirmEmailVerification(token);
    if (res.isSuccess) {
      status.value = 'success';
      message.value = t('verify.success');
      // Refresh user info to update status in store
      await fetchUserInfo();
      
      // Redirect after a delay
      setTimeout(() => {
        router.push('/profile');
      }, 3000);
    } else {
      status.value = 'error';
      message.value = res.errMsg || t('verify.failed');
    }
  } catch (error) {
    console.error(error);
    status.value = 'error';
    message.value = t('verify.error');
  }
});
</script>

<template>
  <div class="verification-page container">
    <div class="glass-panel content-card">
      <div class="status-icon" :class="status">
        <svg v-if="status === 'verifying'" class="spinner" viewBox="0 0 50 50">
          <circle cx="25" cy="25" r="20" fill="none" stroke-width="4"></circle>
        </svg>
        
        <svg v-else-if="status === 'success'" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
          <polyline points="22 4 12 14.01 9 11.01"></polyline>
        </svg>
        
        <svg v-else xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="12" y1="8" x2="12" y2="12"></line>
          <line x1="12" y1="16" x2="12.01" y2="16"></line>
        </svg>
      </div>
      
      <h1 class="title">{{ status === 'verifying' ? 'Verifying...' : (status === 'success' ? 'Success!' : 'Verification Failed') }}</h1>
      <p class="message">{{ message }}</p>
      
      <button v-if="status !== 'verifying'" class="btn-primary" @click="router.push('/')">
        Go Home
      </button>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.verification-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 80vh;
  padding-top: 100px;
}

.content-card {
  padding: $spacing-xxl;
  text-align: center;
  max-width: 500px;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-lg;
}

.status-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.05);
  margin-bottom: $spacing-md;
  
  svg {
    width: 40px;
    height: 40px;
  }
  
  &.verifying {
    color: $color-accent-primary;
    
    .spinner {
      animation: rotate 2s linear infinite;
      
      circle {
        stroke: currentColor;
        stroke-dasharray: 1, 150;
        stroke-dashoffset: 0;
        stroke-linecap: round;
        animation: dash 1.5s ease-in-out infinite;
      }
    }
  }
  
  &.success {
    color: #10b981; // Green
    background: rgba(16, 185, 129, 0.1);
  }
  
  &.error {
    color: #ef4444; // Red
    background: rgba(239, 68, 68, 0.1);
  }
}

.title {
  font-size: 2rem;
  margin: 0;
}

.message {
  color: $color-text-secondary;
  margin-bottom: $spacing-lg;
  line-height: 1.6;
}

.btn-primary {
  background: $color-accent-primary;
  color: #000;
  border: none;
  padding: 12px 32px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.3s;
  font-size: 1rem;

  &:hover {
    opacity: 0.9;
  }
}

@keyframes rotate {
  100% {
    transform: rotate(360deg);
  }
}

@keyframes dash {
  0% {
    stroke-dasharray: 1, 150;
    stroke-dashoffset: 0;
  }
  50% {
    stroke-dasharray: 90, 150;
    stroke-dashoffset: -35;
  }
  100% {
    stroke-dasharray: 90, 150;
    stroke-dashoffset: -124;
  }
}
</style>
