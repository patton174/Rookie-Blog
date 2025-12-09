<script setup lang="ts">
import { ref, watch, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { login, register } from '../api/auth';
import { useUserStore } from '../store/user';

interface Props {
  isOpen: boolean;
  initialMode?: 'login' | 'register';
}

const props = withDefaults(defineProps<Props>(), {
  isOpen: false,
  initialMode: 'login'
});

const emit = defineEmits(['close', 'success']);
const { t } = useI18n();
const { fetchUserInfo } = useUserStore();

const mode = ref(props.initialMode);
const username = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');
const isLoading = ref(false);
const error = ref('');
const success = ref(false);

// Watch for initialMode changes when modal opens
watch(() => props.initialMode, (newVal) => {
  mode.value = newVal;
});

// Reset state when modal closes
watch(() => props.isOpen, (newVal) => {
  if (!newVal) {
    setTimeout(() => {
      username.value = '';
      email.value = '';
      password.value = '';
      confirmPassword.value = '';
      error.value = '';
      success.value = false;
      isLoading.value = false;
    }, 300);
  }
});

const title = computed(() => mode.value === 'login' ? t('auth.welcomeBack') : t('auth.createAccount'));
const subtitle = computed(() => mode.value === 'login' ? t('auth.enterDetails') : t('auth.joinCommunity'));
const buttonText = computed(() => {
  if (isLoading.value) return t('auth.processing');
  return mode.value === 'login' ? t('auth.signIn') : t('auth.signUp');
});

const toggleMode = () => {
  mode.value = mode.value === 'login' ? 'register' : 'login';
  error.value = '';
};

const handleSubmit = async () => {
  error.value = '';

  // Basic Validation
  if (!username.value || !password.value) {
    error.value = t('auth.fillAll');
    return;
  }

  if (mode.value === 'register') {
    if (!email.value || !confirmPassword.value) {
      error.value = t('auth.fillAll');
      return;
    }
    if (password.value !== confirmPassword.value) {
      error.value = t('auth.passwordsDoNotMatch');
      return;
    }
  }

  isLoading.value = true;

  try {
    if (mode.value === 'login') {
      const res = await login({ 
        username: username.value, 
        password: password.value,
        ipAddress: '127.0.0.1' 
      });
      
      if (res.isSuccess) {
        success.value = true;
        setTimeout(() => {
          emit('success');
          emit('close');
          fetchUserInfo();
        }, 1500);
      } else {
        error.value = res.errMsg || 'Login failed';
      }
    } else {
      if (password.value !== confirmPassword.value) {
        error.value = t('auth.passwordsDoNotMatch');
        isLoading.value = false;
        return;
      }

      const res = await register({
        username: username.value,
        password: password.value,
        confirmPassword: confirmPassword.value,
        email: email.value,
        ipAddress: '127.0.0.1'
      });

      if (res.isSuccess) {
        success.value = true;
        setTimeout(() => {
          // Switch to login mode
          mode.value = 'login';
          success.value = false;
          password.value = '';
          confirmPassword.value = '';
        }, 1500);
      } else {
        error.value = res.errMsg || 'Registration failed';
      }
    }
  } catch (err: any) {
    console.error(err);
    error.value = err.response?.data?.errmsg || err.message || 'An error occurred';
  } finally {
    isLoading.value = false;
  }
};

const handleClose = () => {
  if (!isLoading.value) {
    emit('close');
  }
};
</script>

<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="isOpen" class="modal-backdrop" ><!-- 点击遮罩层外面时不会关闭模态框 -->
      <!-- <div v-if="isOpen" class="modal-backdrop" @click="handleClose"> -->
        <div class="modal-container glass-panel" @click.stop>
          
          <!-- Close Button -->
          <button class="modal-close" @click="handleClose" aria-label="Close modal">×</button>

          <div v-if="success" class="success-state">
            <div class="success-icon">✓</div>
            <h3>{{ mode === 'login' ? t('auth.welcomeBackExcl') : t('auth.accountCreated') }}</h3>
          </div>

        <!-- Auth Form -->
        <template v-else>
          <div class="modal-header">
            <h2 class="modal-title text-gradient">{{ title }}</h2>
            <p class="modal-subtitle">{{ subtitle }}</p>
          </div>

          <form @submit.prevent="handleSubmit" class="auth-form" :class="{ 'shake': !!error }">
            
            <!-- Username Field (Both Modes) -->
            <div class="form-group">
              <label>{{ t('auth.username') }}</label>
              <input 
                type="text" 
                v-model="username" 
                :placeholder="t('auth.usernamePlaceholder')" 
                :class="{ 'has-error': !!error }"
                required
              />
            </div>

            <!-- Email Field (Register Only) -->
            <div class="form-group" v-if="mode === 'register'">
              <label>{{ t('auth.email') }}</label>
              <input 
                type="email" 
                v-model="email" 
                :placeholder="t('auth.emailPlaceholder')" 
                :class="{ 'has-error': !!error }"
                required
              />
            </div>

            <!-- Password Field -->
            <div class="form-group">
              <div class="password-header">
                <label>{{ t('auth.password') }}</label>
                <a href="#" class="forgot-password" v-if="mode === 'login'">{{ t('auth.forgotPassword') }}</a>
              </div>
              <input 
                type="password" 
                v-model="password" 
                :placeholder="t('auth.passwordPlaceholder')" 
                :class="{ 'has-error': !!error }"
                required
              />
            </div>

            <!-- Confirm Password Field (Register Only) -->
            <div class="form-group" v-if="mode === 'register'">
              <label>{{ t('auth.confirmPassword') }}</label>
              <input 
                type="password" 
                v-model="confirmPassword" 
                :placeholder="t('auth.passwordPlaceholder')" 
                :class="{ 'has-error': !!error }"
                required
              />
            </div>

            <span class="error-msg" v-if="error">{{ error }}</span>

            <button 
              type="submit" 
              class="submit-btn" 
              :disabled="isLoading"
              :class="{ 'loading': isLoading }"
            >
              <span v-if="!isLoading">{{ buttonText }}</span>
              <div v-else class="spinner"></div>
              <span class="btn-ripple"></span>
            </button>
          </form>

          <div class="modal-footer">
            <p v-if="mode === 'login'">
              {{ t('auth.noAccount') }} 
              <a href="#" @click.prevent="toggleMode">{{ t('auth.signUp') }}</a>
            </p>
            <p v-else>
              {{ t('auth.hasAccount') }} 
              <a href="#" @click.prevent="toggleMode">{{ t('auth.signIn') }}</a>
            </p>
          </div>
        </template>
      </div>
    </div>
    </Transition>
  </Teleport>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(5, 5, 10, 0.8);
  backdrop-filter: blur(8px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999; /* High z-index to overlay everything including particles */
  overflow-y: auto; /* Allow scrolling if modal is too tall */
  padding: 20px; /* Add padding to prevent edge collision */
}

.modal-container {
  width: 100%;
  max-width: 420px;
  padding: 40px;
  background: $color-card-bg;
  border: 1px solid $color-card-border;
  border-radius: 16px;
  box-shadow: $shadow-card;
  position: relative;
  overflow: hidden;
  max-height: 90vh;
  overflow-y: auto;
  
  // Glass texture overlay
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, rgba(255,255,255,0.03) 0%, transparent 100%);
    pointer-events: none;
    z-index: 0;
  }

  > * {
    position: relative;
    z-index: 1;
  }
}

.modal-close {
  position: absolute;
  top: 20px;
  inset-inline-end: 20px;
  background: transparent;
  border: none;
  color: $color-text-secondary;
  font-size: 24px;
  cursor: pointer;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;

  &:hover {
    background: $color-bg-secondary;
    color: $color-text-primary;
  }
}

.modal-header {
  text-align: center;
  margin-bottom: 32px;
}

.modal-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
  font-family: 'Inter', sans-serif;
}

.text-gradient {
  background: linear-gradient(135deg, $color-text-primary 0%, $color-text-secondary 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.modal-subtitle {
  color: $color-text-secondary;
  font-size: 14px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  text-align: left;

  label {
    font-size: 13px;
    color: $color-text-secondary;
    font-weight: 500;
  }

  input {
    width: 100%;
    padding: 12px 16px;
    background: $color-input-bg;
    border: 1px solid $color-input-border;
    border-radius: 8px;
    color: $color-input-text;
    font-size: 15px;
    transition: all 0.3s ease;
    
    &::placeholder {
      color: $color-input-placeholder;
    }

    &:hover {
      border-color: $color-accent-primary;
      box-shadow: 0 0 0 1px rgba($color-accent-primary, 0.1);
    }

    &:focus {
      outline: none;
      border-color: $color-accent-primary;
      box-shadow: 0 0 0 3px $color-input-focus-ring;
      transform: scale(1.02);
      background: $color-input-bg;
    }

    &.has-error {
      border-color: $color-accent-tertiary;
      
      &:focus {
        box-shadow: 0 0 0 3px rgba($color-accent-tertiary, 0.1);
      }
    }
  }
}

.password-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.forgot-password {
  font-size: 12px;
  color: $color-accent-primary;
  
  &:hover {
    text-decoration: underline;
  }
}

.error-msg {
  color: $color-accent-tertiary;
  font-size: 12px;
  margin-top: 4px;
  display: block;
}

.submit-btn {
  margin-top: 12px;
  padding: 14px;
  background: $color-accent-primary;
  color: $color-bg-primary;
  border: none;
  border-radius: 8px;
  font-weight: 700;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 8px 20px rgba(var(--color-accent-primary-rgb), 0.3);
  }

  &:active {
    transform: translateY(1px);
  }

  &:disabled {
    opacity: 0.7;
    cursor: not-allowed;
    transform: none;
  }
}

.spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.2);
  border-left-color: currentColor;
  border-radius: 50%;
  margin: 0 auto;
  animation: spin 1s linear infinite;
}

.modal-footer {
  margin-top: 24px;
  text-align: center;
  font-size: 14px;
  color: $color-text-secondary;

  a {
    color: $color-accent-primary;
    font-weight: 600;
    
    &:hover {
      text-decoration: underline;
    }
  }
}

// Success State
.success-state {
  text-align: center;
  padding: 40px 0;

  .success-icon {
    width: 64px;
    height: 64px;
    background: rgba(var(--color-accent-primary-rgb), 0.1);
    color: $color-accent-primary;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 32px;
    margin: 0 auto 24px;
    border: 2px solid $color-accent-primary;
    animation: scaleIn 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  }

  h3 {
    font-size: 24px;
    color: $color-text-primary;
  }
}

// Animations
@keyframes spin {
  to { transform: rotate(360deg); }
}

@keyframes scaleIn {
  from { transform: scale(0); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

// Modal Transition
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;

  .modal-container {
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;

  .modal-container {
    opacity: 0;
    transform: scale(0.9) translateY(20px);
  }
}

// Error Shake Animation
.shake {
  animation: shake 0.5s cubic-bezier(.36,.07,.19,.97) both;
}

@keyframes shake {
  10%, 90% { transform: translate3d(-1px, 0, 0); }
  20%, 80% { transform: translate3d(2px, 0, 0); }
  30%, 50%, 70% { transform: translate3d(-4px, 0, 0); }
  40%, 60% { transform: translate3d(4px, 0, 0); }
}

// Ripple Effect for Button
.btn-ripple {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  opacity: 0;
}

.submit-btn:active .btn-ripple {
  width: 300px;
  height: 300px;
  opacity: 0;
  transition: width 0.5s ease-out, height 0.5s ease-out, opacity 0.5s ease-out;
}
</style>
