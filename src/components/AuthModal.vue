<script setup lang="ts">
import { ref, watch, computed, onUnmounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { login, register } from '../api/auth';
import { useUserStore } from '../store/user';
import AnimatedLogo from './AnimatedLogo.vue';
import AnimatedTextLogo from './AnimatedTextLogo.vue';

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
const loginMethod = ref<'password' | 'wechat'>('password');
const wechatStep = ref<'scan' | 'set-password'>('scan');
const username = ref('');
const email = ref('');
const password = ref('');
const confirmPassword = ref('');
const isLoading = ref(false);
const error = ref('');
const success = ref(false);

// WeChat Mock Timer
let scanTimer: any = null;

// Watch for initialMode changes when modal opens
watch(() => props.initialMode, (newVal) => {
  mode.value = newVal;
});

// Reset state when modal closes
watch(() => props.isOpen, (newVal) => {
  if (!newVal) {
    setTimeout(() => {
      resetState();
    }, 300);
  } else {
    // If opening in login mode and wechat selected, restart scan simulation
    if (mode.value === 'login' && loginMethod.value === 'wechat') {
      startScanSimulation();
    }
  }
});

const resetState = () => {
  username.value = '';
  email.value = '';
  password.value = '';
  confirmPassword.value = '';
  error.value = '';
  success.value = false;
  isLoading.value = false;
  loginMethod.value = 'password';
  wechatStep.value = 'scan';
  stopScanSimulation();
};

const title = computed(() => {
  if (success.value) return mode.value === 'login' ? 'Success' : 'Welcome';
  if (loginMethod.value === 'wechat') return 'WeChat Login';
  if (mode.value === 'register') return 'Join Rookie Blog';
  return 'Rookie Blog';
});

const subtitle = computed(() => {
  if (success.value) return '';
  if (loginMethod.value === 'wechat') return 'Scan with WeChat to login or register';
  if (mode.value === 'register') return 'Share knowledge, Connect the world';
  return 'Start your technical journey';
});

const buttonText = computed(() => {
  if (isLoading.value) return t('auth.processing');
  if (mode.value === 'login' && loginMethod.value === 'wechat' && wechatStep.value === 'set-password') return 'Complete Setup';
  return mode.value === 'login' ? t('auth.signIn') : t('auth.signUp');
});

const toggleMode = () => {
  mode.value = mode.value === 'login' ? 'register' : 'login';
  error.value = '';
  loginMethod.value = 'password'; // Reset to password on mode switch
};

const switchToWeChat = () => {
  loginMethod.value = 'wechat';
  mode.value = 'login'; // WeChat handles both login/register flow usually
  error.value = '';
  wechatStep.value = 'scan';
  startScanSimulation();
};

const switchToPassword = () => {
  loginMethod.value = 'password';
  error.value = '';
  stopScanSimulation();
};

const startScanSimulation = () => {
  stopScanSimulation();
  // Simulate a scan after 3 seconds
  scanTimer = setTimeout(() => {
    if (props.isOpen && loginMethod.value === 'wechat' && wechatStep.value === 'scan') {
      wechatStep.value = 'set-password';
    }
  }, 3000);
};

const stopScanSimulation = () => {
  if (scanTimer) {
    clearTimeout(scanTimer);
    scanTimer = null;
  }
};

const handleSubmit = async () => {
  error.value = '';

  // WeChat Set Password Flow
  if (loginMethod.value === 'wechat' && wechatStep.value === 'set-password') {
     if (!password.value || !confirmPassword.value) {
      error.value = t('auth.fillAll');
      return;
    }
    if (password.value !== confirmPassword.value) {
      error.value = t('auth.passwordsDoNotMatch');
      return;
    }
    
    // Simulate API call for binding/registering
    isLoading.value = true;
    setTimeout(() => {
        isLoading.value = false;
        success.value = true;
        setTimeout(() => {
            emit('success');
            emit('close');
            // Mock login
            // In real app, we would get token here
            fetchUserInfo(); 
        }, 1500);
    }, 1500);
    return;
  }

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

onUnmounted(() => {
  stopScanSimulation();
});
</script>

<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="isOpen" class="modal-backdrop">
        <div class="modal-container glass-panel" @click.stop>
          
          <!-- Left Side: Dynamic Logo (No Text) -->
          <div class="modal-left">
            <AnimatedLogo />
          </div>

          <!-- Right Side: Auth Form -->
          <div class="modal-right">
            <!-- Close Button -->
            <button class="modal-close" @click="handleClose" aria-label="Close modal">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <line x1="18" y1="6" x2="6" y2="18"></line>
                <line x1="6" y1="6" x2="18" y2="18"></line>
              </svg>
            </button>
            
            <!-- Back Button (Stable Position) -->
            <button v-if="loginMethod === 'wechat'" class="back-btn-corner" @click="switchToPassword" aria-label="Back to login">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M19 12H5M12 19l-7-7 7-7"/>
              </svg>
            </button>

            <div v-if="success" class="success-state">
              <div class="success-icon">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M20 6L9 17l-5-5"/>
                </svg>
              </div>
              <div class="success-content">
                <h3>{{ title }}</h3>
                <p v-if="mode === 'login'">{{ t('auth.welcomeBackExcl') }}</p>
                <p v-else>{{ t('auth.accountCreated') }}</p>
              </div>
            </div>

            <!-- Main Content Area with Transition -->
            <Transition name="fade-slide" mode="out-in">
              
              <!-- State 1: WeChat Login -->
              <div v-if="loginMethod === 'wechat'" key="wechat" class="auth-view wechat-view">
                <div class="modal-header">
                  <div class="logo-wrapper">
                     <AnimatedTextLogo :width="200" :height="34" :delay="300" />
                  </div>
                  <p class="modal-subtitle">{{ subtitle }}</p>
                </div>

                <div v-if="wechatStep === 'scan'" class="qr-container">
                  <div class="qr-placeholder">
                    <svg viewBox="0 0 24 24" width="120" height="120" stroke="currentColor" fill="none">
                      <path stroke="none" d="M0 0h24v24H0z" fill="none"/>
                      <path d="M4 4h6v6h-6z" />
                      <path d="M14 4h6v6h-6z" />
                      <path d="M4 14h6v6h-6z" />
                      <path d="M14 14h6v6h-6z" />
                      <path d="M7 7m-3 0a3 3 0 1 0 6 0a3 3 0 1 0 -6 0" />
                      <path d="M17 7m-3 0a3 3 0 1 0 6 0a3 3 0 1 0 -6 0" />
                      <path d="M7 17m-3 0a3 3 0 1 0 6 0a3 3 0 1 0 -6 0" />
                      <path d="M17 17m-3 0a3 3 0 1 0 6 0a3 3 0 1 0 -6 0" />
                    </svg>
                    <div class="scan-line"></div>
                  </div>
                  <p class="scan-tip">Use WeChat to scan</p>
                </div>

                <div v-else-if="wechatStep === 'set-password'" class="set-password-form">
                  <form @submit.prevent="handleSubmit" class="auth-form">
                    <p class="info-text">Scan successful! Set a password to continue.</p>
                     <!-- Password Field -->
                    <div class="form-group">
                      <label>{{ t('auth.password') }}</label>
                      <input 
                        type="password" 
                        v-model="password" 
                        :placeholder="t('auth.passwordPlaceholder')" 
                        :class="{ 'has-error': !!error }"
                        required
                      />
                    </div>

                    <!-- Confirm Password Field -->
                    <div class="form-group">
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
                    </button>
                  </form>
                </div>

                <!-- Removed bottom back button as it is now in corner -->
              </div>

              <!-- State 2: Standard Login/Register -->
              <div v-else key="standard" class="auth-view standard-view">
                <div class="modal-header">
                  <div class="logo-wrapper">
                     <AnimatedTextLogo :width="200" :height="34" :delay="300" />
                  </div>
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
                  
                  <!-- Forgot Password (Login Only) -->
                   <div class="form-actions" v-if="mode === 'login'">
                      <a href="#" class="forgot-password">{{ t('auth.forgotPassword') }}</a>
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
                  <template v-if="mode === 'login'">
                    <!-- WeChat Prompt -->
                    <button class="wechat-prompt-btn" @click="switchToWeChat">
                      <!-- Nuclear Option: Solid Green Circle with White Icon -->
                      <div class="wechat-icon-container">
                        <svg viewBox="0 0 24 24" width="20" height="20" class="wechat-icon">
                          <path d="M19.05,4.91H5.08c-2.31,0-4.18,1.87-4.18,4.18v9.84c0,2.31,1.87,4.18,4.18,4.18h13.97c2.31,0,4.18-1.87,4.18-4.18V9.09C23.23,6.78,21.36,4.91,19.05,4.91z M8.8,9.78c0.55,0,1,0.45,1,1s-0.45,1-1,1s-1-0.45-1-1S8.25,9.78,8.8,9.78z M12.8,9.78c0.55,0,1,0.45,1,1s-0.45,1-1,1s-1-0.45-1-1S12.25,9.78,12.8,9.78z M10.8,16.5c-2.67,0-4.83-1.64-4.83-3.66s2.17-3.66,4.83-3.66c2.67,0,4.83,1.64,4.83,3.66S13.47,16.5,10.8,16.5z M18.47,14.5c0.55,0,1,0.45,1,1s-0.45,1-1,1s-1-0.45-1-1S17.92,14.5,18.47,14.5z M20.47,10.5c0.55,0,1,0.45,1,1s-0.45,1-1,1s-1-0.45-1-1S19.92,10.5,20.47,10.5z" opacity="0" />
                          <path d="M8 5C4.686 5 2 7.239 2 10c0 1.564.86 2.955 2.19 3.895-.1.6-.335 1.485-.385 1.685-.05.2-.1.39.15.535.24.14.52.025 1.52-.64 1.04.59 2.22.925 3.48.925.14 0 .28-.005.42-.015.27 3.36 3.035 6.015 6.355 6.015 1.08 0 2.1-.28 3-.765.87.555 1.11.655 1.32.535.22-.125.17-.29.13-.465-.04-.19-.27-.975-.35-1.475 1.23-1.02 2.04-2.54 2.04-4.25 0-3.006-2.694-5.415-5.905-5.415-3.216 0-5.905 2.409-5.905 5.415 0 .155.01.3.03.455-.05-.01-.1-.01-.15-.01-3.811 0-6.905-2.771-6.905-6.245S5.689 5 8 5zm-1.5 3c.552 0 1 .448 1 1s-.448 1-1 1-1-.448-1-1 .448-1 1-1zm3 0c.552 0 1 .448 1 1s-.448 1-1 1-1-.448-1-1 .448-1 1-1zm5.5 6c.552 0 1 .448 1 1s-.448 1-1 1-1-.448-1-1 .448-1 1-1zm3 0c.552 0 1 .448 1 1s-.448 1-1 1-1-.448-1-1 .448-1 1-1z" />
                        </svg>
                      </div>
                      <strong>WeChat Login</strong>
                    </button>
                    
                    <div class="divider">or</div>

                    <div class="register-prompt">
                      <span>Don't have an account?</span>
                      <a href="#" class="register-link" @click.prevent="toggleMode">Register now</a>
                    </div>
                  </template>
                  
                  <template v-else>
                     <p>
                      {{ t('auth.hasAccount') }} 
                      <a href="#" @click.prevent="toggleMode">{{ t('auth.signIn') }}</a>
                    </p>
                  </template>
                </div>
              </div>

            </Transition>
          </div>
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
  background: rgba(0, 0, 0, 0.6); // More neutral for light/dark
  backdrop-filter: blur(8px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
  padding: 20px;
}

.modal-container {
  display: flex;
  width: 100%;
  max-width: 720px;
  height: 480px;
  background: var(--color-card-bg);
  border: 1px solid var(--color-border);
  border-radius: 16px;
  box-shadow: var(--shadow-lg);
  position: relative;
  overflow: hidden;
}

.modal-left {
  flex: 0 0 38%;
  // Use theme gradient or subtle background
  background: linear-gradient(135deg, rgba(var(--color-accent-primary-rgb), 0.05) 0%, rgba(var(--color-accent-secondary-rgb), 0.05) 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
  border-right: 1px solid var(--color-border);
}

.modal-right {
  flex: 1;
  padding: 30px 32px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  position: relative;
  z-index: 1;
  // Use theme background
  background: var(--color-bg-secondary);
}

.modal-close {
  position: absolute;
  top: 16px;
  right: 16px;
  background: transparent;
  border: none;
  color: var(--color-text-secondary);
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  z-index: 20; // Ensure on top

  svg {
    width: 18px;
    height: 18px;
  }

  &:hover {
    background: var(--color-bg-primary);
    color: var(--color-text-primary);
  }
}

.auth-view {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.modal-header {
  text-align: center;
  margin-bottom: 20px;
}

.modal-title {
  font-size: 22px;
  font-weight: 800;
  margin-bottom: 4px;
  font-family: 'Inter', sans-serif;
  letter-spacing: -0.5px;
  color: var(--color-text-primary);
}

.text-gradient {
  background: linear-gradient(135deg, var(--color-text-primary) 0%, var(--color-text-secondary) 100%);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.modal-subtitle {
  color: var(--color-text-secondary);
  font-size: 13px;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
  text-align: left;

  label {
    font-size: 12px;
    color: var(--color-text-secondary);
    font-weight: 600;
    margin-left: 2px;
  }

  input {
    width: 100%;
    padding: 10px 14px;
    background: var(--color-input-bg);
    border: 1px solid var(--color-input-border);
    border-radius: 10px;
    color: var(--color-text-primary);
    font-size: 13px;
    transition: all 0.2s ease;
    
    &::placeholder {
      color: var(--color-input-placeholder);
    }

    &:hover {
      border-color: var(--color-text-secondary);
    }

    &:focus {
      outline: none;
      border-color: var(--color-accent-primary);
      box-shadow: 0 0 0 3px rgba(var(--color-accent-primary-rgb), 0.1);
      background: var(--color-bg-primary);
    }

    &.has-error {
      border-color: var(--color-accent-tertiary);
    }
  }
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: -4px;
}

.forgot-password {
  font-size: 13px;
  color: var(--color-text-secondary);
  transition: color 0.2s;
  
  &:hover {
    color: var(--color-accent-primary);
    text-decoration: none;
  }
}

.submit-btn {
  margin-top: 4px;
  padding: 10px;
  background: var(--color-accent-primary);
  color: #fff; // Always white text on primary button
  border: none;
  border-radius: 12px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(var(--color-accent-primary-rgb), 0.2);

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 16px rgba(var(--color-accent-primary-rgb), 0.3);
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

.modal-footer {
  margin-top: 16px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  
  p {
    color: var(--color-text-secondary);
    font-size: 14px;
    
    a {
      color: var(--color-accent-primary);
      font-weight: 600;
      &:hover { text-decoration: underline; }
    }
  }
}

.wechat-prompt-btn {
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  padding: 10px 16px;
  border-radius: 12px;
  width: 100%;
  color: var(--color-text-primary);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-size: 14px;
  box-shadow: var(--shadow-sm);

  .wechat-icon-container {
    width: 28px;
    height: 28px;
    background: #07c160;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;
  }

  .wechat-icon {
    fill: #ffffff;
    filter: none;
  }

  strong {
    color: var(--color-text-primary);
    font-weight: 600;
  }

  &:hover {
    background: var(--color-bg-secondary);
    border-color: #07c160;
    transform: translateY(-1px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  }

  &:active {
    transform: translateY(1px);
  }
}

.logo-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.register-prompt {
  display: flex;
  gap: 6px;
  font-size: 13px;
  color: var(--color-text-secondary);
  align-items: center;
  justify-content: center;
}

.register-link {
  color: var(--color-accent-primary);
  font-weight: 600;
  text-decoration: none;
  
  &:hover {
    text-decoration: underline;
  }
}

.back-btn-corner {
  position: absolute;
  top: 16px;
  left: 16px;
  background: transparent;
  border: none;
  color: var(--color-text-secondary);
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  z-index: 20;

  svg {
    width: 20px;
    height: 20px;
  }

  &:hover {
    background: var(--color-bg-primary);
    color: var(--color-text-primary);
  }
}

// WeChat View Styles
.wechat-view {
  align-items: center;
  text-align: center;
}

.qr-container {
  margin: 16px 0 24px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;

  .qr-placeholder {
    width: 150px;
    height: 150px;
    background: #fff;
    border-radius: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #333;
    position: relative;
    overflow: hidden;
    box-shadow: var(--shadow-sm);
    border: 4px solid #fff;
    
    .scan-line {
      position: absolute;
      width: 100%;
      height: 2px;
      background: #07c160;
      box-shadow: 0 0 8px #07c160;
      top: 0;
      animation: scan 2.5s ease-in-out infinite;
    }
  }
  
  .scan-tip {
    color: var(--color-text-secondary);
    font-size: 13px;
    font-weight: 500;
  }
}

.set-password-form {
  width: 100%;
}

.info-text {
  font-size: 14px;
  color: var(--color-text-primary);
  margin-bottom: 16px;
  text-align: center;
  background: rgba(var(--color-accent-primary-rgb), 0.1);
  padding: 10px;
  border-radius: 8px;
}

.back-btn {
  background: none;
  border: none;
  color: var(--color-text-secondary);
  font-size: 14px;
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 8px;
  transition: all 0.2s;

  &:hover {
    background: var(--color-bg-primary);
    color: var(--color-text-primary);
  }
}

// Transitions
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateX(20px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

// Keyframes
@keyframes scan {
  0% { top: 10%; opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% { top: 90%; opacity: 0; }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

// Responsive
@media (max-width: 768px) {
  .modal-container {
    flex-direction: column;
    height: auto;
    max-height: 90vh;
    overflow-y: auto;
  }

  .modal-left {
    display: none;
  }
  
  .modal-right {
    padding: 32px 24px;
  }
}

// Success State
.success-state {
  text-align: center;
  padding: 30px 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;

  .success-icon {
    width: 64px;
    height: 64px;
    background: rgba(7, 193, 96, 0.1);
    color: #07c160;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 2px solid #07c160;
    animation: popIn 0.5s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
    
    svg {
      width: 32px;
      height: 32px;
      stroke-dasharray: 50;
      stroke-dashoffset: 50;
      animation: drawCheck 0.5s ease-out 0.3s forwards;
    }
  }
  
  .success-content {
    opacity: 0;
    transform: translateY(10px);
    animation: fadeUp 0.5s ease-out 0.4s forwards;
    
    h3 {
      font-size: 24px;
      color: var(--color-text-primary);
      font-weight: 700;
      margin-bottom: 4px;
    }
    
    p {
      color: var(--color-text-secondary);
      font-size: 14px;
    }
  }
}

@keyframes drawCheck {
  to { stroke-dashoffset: 0; }
}

@keyframes fadeUp {
  to { opacity: 1; transform: translateY(0); }
}
</style>
