<script setup lang="ts">
import { ref, computed, onUnmounted, watchEffect, nextTick } from 'vue';
import { useI18n } from 'vue-i18n';
import ArticleCard from '../components/ArticleCard.vue';
import AvatarGenerator from '../components/AvatarGenerator.vue';
import { useUserStore } from '../store/user';
import { requestEmailVerification } from '../api/auth';

const { t } = useI18n();
const { user: currentUser } = useUserStore();

const activeTab = ref('favorites');
const tabs = computed(() => [
  { id: 'favorites', label: t('profile.favorites') },
  { id: 'history', label: t('profile.history') },
  { id: 'settings', label: t('profile.settings') }
]);

const user = computed(() => {
  if (currentUser.value) {
    return {
      name: currentUser.value.username,
      bio: t('profile.bio'), // Use localized bio
      email: currentUser.value.email,
      emailVerified: currentUser.value.emailVerified,
      avatarUrl: currentUser.value.avatarUrl,
      stats: {
        articles: 12,
        likes: 340,
        following: 45
      }
    };
  }
  return {
    name: t('profile.guest'),
    bio: '',
    email: '',
    emailVerified: false,
    avatarUrl: '',
    stats: { articles: 0, likes: 0, following: 0 }
  };
});

// Mock data for favorites/history
const mockArticles = [
  {
    id: 1,
    title: 'Mastering Vue 3 Composition API',
    summary: 'Deep dive into the new Composition API features.',
    date: '2023-10-15',
    tags: ['Vue', 'Frontend'],
    image: ''
  },
  {
    id: 3,
    title: 'Building Microservices with Kubernetes',
    summary: 'Learn how to deploy, scale, and manage your microservices.',
    date: '2023-11-20',
    tags: ['DevOps', 'K8s'],
    image: ''
  }
];

// --- Email Verification Logic ---
const emailStatus = ref<'unverified' | 'sending' | 'sent' | 'verified'>('unverified');
const emailTimer = ref(0);
let timerInterval: number | null = null;

// Update email status based on user data
watchEffect(() => {
  if (user.value.emailVerified) {
    emailStatus.value = 'verified';
  } else if (emailStatus.value !== 'sending' && emailStatus.value !== 'sent') {
    emailStatus.value = 'unverified';
  }
});

const startTimer = () => {
  emailTimer.value = 60;
  if (timerInterval) clearInterval(timerInterval);
  timerInterval = window.setInterval(() => {
    if (emailTimer.value > 0) {
      emailTimer.value--;
    } else {
      if (timerInterval) clearInterval(timerInterval);
    }
  }, 1000);
};

const sendVerificationEmail = async () => {
  if (emailStatus.value === 'sending' || emailStatus.value === 'verified' || emailTimer.value > 0) return;
  
  emailStatus.value = 'sending';
  try {
    const res = await requestEmailVerification();
    if (res.isSuccess) {
      emailStatus.value = 'sent';
      startTimer();
    } else {
      emailStatus.value = 'unverified';
      alert(res.errMsg || 'Failed to send verification email');
    }
  } catch (error) {
    console.error(error);
    emailStatus.value = 'unverified';
    alert('An error occurred');
  }
};

// --- Password Change Logic ---
const passwordForm = ref({
  current: '',
  new: '',
  confirm: ''
});

const showPassword = ref({
  current: false,
  new: false,
  confirm: false
});

const passwordErrors = ref({
  current: '',
  new: '',
  confirm: ''
});

const isPasswordValid = computed(() => {
  const p = passwordForm.value.new;
  const hasUpperCase = /[A-Z]/.test(p);
  const hasLowerCase = /[a-z]/.test(p);
  const hasNumber = /[0-9]/.test(p);
  const isLongEnough = p.length >= 8;
  return hasUpperCase && hasLowerCase && hasNumber && isLongEnough;
});

const passwordMatch = computed(() => {
  return passwordForm.value.new === passwordForm.value.confirm && passwordForm.value.new !== '';
});

const updatePassword = async () => {
  // Reset errors
  passwordErrors.value = { current: '', new: '', confirm: '' };

  // Validate
  if (!passwordForm.value.current) {
    passwordErrors.value.current = 'Current password is required';
    return;
  }
  if (!isPasswordValid.value) {
    passwordErrors.value.new = 'Password must be 8+ chars, include uppercase, lowercase, and number';
    return;
  }
  if (!passwordMatch.value) {
    passwordErrors.value.confirm = 'Passwords do not match';
    return;
  }

  // Simulate API
  setTimeout(() => {
    alert('Password updated successfully!');
    passwordForm.value = { current: '', new: '', confirm: '' };
  }, 1500);
};

const tabsRef = ref<HTMLElement | null>(null);

const handleEditProfile = async () => {
  activeTab.value = 'settings';
  await nextTick();
  if (tabsRef.value) {
    tabsRef.value.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }
};

onUnmounted(() => {
  if (timerInterval) clearInterval(timerInterval);
});
</script>

<template>
  <div class="container profile-page">
    <header class="profile-header glass-panel">
      <div class="profile-info">
        <div class="avatar-wrapper">
          <img v-if="user.avatarUrl" :src="user.avatarUrl" :alt="user.name" class="profile-avatar-img" />
          <AvatarGenerator v-else :username="user.name" :size="100" />
        </div>
        <div class="user-details">
          <h1 class="username">{{ user.name }}</h1>
          <p class="bio">{{ user.bio }}</p>
          <div class="stats-row">
            <div class="stat-item">
              <span class="count">{{ user.stats.articles }}</span>
              <span class="label">{{ t('profile.articles') }}</span>
            </div>
            <div class="stat-item">
              <span class="count">{{ user.stats.likes }}</span>
              <span class="label">{{ t('profile.likes') }}</span>
            </div>
            <div class="stat-item">
              <span class="count">{{ user.stats.following }}</span>
              <span class="label">{{ t('profile.following') }}</span>
            </div>
          </div>
        </div>
      </div>
      <button class="edit-btn" @click="handleEditProfile">{{ t('profile.editProfile') }}</button>
    </header>

    <div class="profile-content">
      <nav class="profile-tabs" ref="tabsRef">
        <button 
          v-for="tab in tabs" 
          :key="tab.id"
          class="tab-btn"
          :class="{ active: activeTab === tab.id }"
          @click="activeTab = tab.id"
        >
          {{ tab.label }}
        </button>
      </nav>

      <div class="tab-content" v-if="activeTab === 'favorites'">
        <div class="articles-grid" v-if="mockArticles.length">
          <ArticleCard v-for="article in mockArticles" :key="article.id" v-bind="article" />
        </div>
        <div v-else class="empty-state">
          <p>{{ t('profile.noFavorites') }}</p>
        </div>
      </div>

      <div class="tab-content" v-if="activeTab === 'history'">
        <div class="history-list" v-if="mockArticles.length">
          <div v-for="article in mockArticles" :key="article.id" class="history-item glass-panel">
            <div class="history-info">
              <span class="history-date">{{ t('profile.viewedOn') }} {{ article.date }}</span>
              <h3>{{ article.title }}</h3>
            </div>
            <button class="view-btn">{{ t('profile.readAgain') }}</button>
          </div>
        </div>
        <div v-else class="empty-state">
          <p>{{ t('profile.noHistory') }}</p>
        </div>
      </div>

      <div class="tab-content settings-panel glass-panel" v-if="activeTab === 'settings'">
        <!-- Account Security Section -->
        <section class="setting-section">
          <h3>{{ t('profile.accountSecurity') }}</h3>
          
          <!-- Email Verification -->
          <div class="security-card">
            <div class="card-header">
              <span class="label">{{ t('profile.emailAddress') }}</span>
              <span 
                class="status-badge" 
                :class="emailStatus === 'verified' ? 'verified' : 'unverified'"
              >
                {{ emailStatus === 'verified' ? t('profile.verified') : t('profile.unverified') }}
              </span>
            </div>
            <div class="card-body">
              <div class="current-email">{{ user.email }}</div>
              
              <div class="action-row" v-if="emailStatus !== 'verified'">
                <button 
                  class="btn-primary" 
                  @click="sendVerificationEmail" 
                  :disabled="emailStatus === 'sending' || emailTimer > 0"
                >
                  {{ emailStatus === 'sending' ? t('profile.sending') : (emailTimer > 0 ? t('profile.resendIn', { seconds: emailTimer }) : t('profile.sendVerification')) }}
                </button>
                <p class="helper-text" v-if="emailStatus === 'sent'">
                  {{ t('profile.verificationSent') }}
                </p>
              </div>
            </div>
          </div>

          <!-- Password Change -->
          <div class="security-card">
            <div class="card-header">
              <span class="label">{{ t('profile.changePassword') }}</span>
            </div>
            <div class="card-body form-stack">
              <!-- Current Password -->
              <div class="form-group span-full">
                <label>{{ t('profile.currentPassword') }}</label>
                <div class="input-wrapper">
                  <input 
                    :type="showPassword.current ? 'text' : 'password'" 
                    v-model="passwordForm.current"
                    :placeholder="t('profile.enterCurrentPassword')"
                  />
                  <button class="toggle-visibility" @click="showPassword.current = !showPassword.current">
                    {{ showPassword.current ? t('profile.hide') : t('profile.show') }}
                  </button>
                </div>
                <span class="error-msg" v-if="passwordErrors.current">{{ passwordErrors.current }}</span>
              </div>

              <!-- New Password -->
              <div class="form-group">
                <label>{{ t('profile.newPassword') }}</label>
                <div class="input-wrapper">
                  <input 
                    :type="showPassword.new ? 'text' : 'password'" 
                    v-model="passwordForm.new"
                    :placeholder="t('profile.enterNewPassword')"
                  />
                  <button class="toggle-visibility" @click="showPassword.new = !showPassword.new">
                    {{ showPassword.new ? t('profile.hide') : t('profile.show') }}
                  </button>
                </div>
                <span class="error-msg" v-if="passwordErrors.new">{{ passwordErrors.new }}</span>
                <ul class="password-requirements">
                  <li :class="{ met: /[A-Z]/.test(passwordForm.new) }">{{ t('profile.uppercase') }}</li>
                  <li :class="{ met: /[a-z]/.test(passwordForm.new) }">{{ t('profile.lowercase') }}</li>
                  <li :class="{ met: /[0-9]/.test(passwordForm.new) }">{{ t('profile.number') }}</li>
                  <li :class="{ met: passwordForm.new.length >= 8 }">{{ t('profile.minChars') }}</li>
                </ul>
              </div>

              <!-- Confirm Password -->
              <div class="form-group">
                <label>{{ t('profile.confirmNewPassword') }}</label>
                <div class="input-wrapper">
                  <input 
                    :type="showPassword.confirm ? 'text' : 'password'" 
                    v-model="passwordForm.confirm"
                    :placeholder="t('profile.confirmNewPasswordPlaceholder')"
                  />
                  <button class="toggle-visibility" @click="showPassword.confirm = !showPassword.confirm">
                    {{ showPassword.confirm ? t('profile.hide') : t('profile.show') }}
                  </button>
                </div>
                <span class="error-msg" v-if="passwordErrors.confirm">{{ passwordErrors.confirm }}</span>
              </div>

              <button class="btn-primary full-width" @click="updatePassword">{{ t('profile.updatePassword') }}</button>
            </div>
          </div>
        </section>

        <div class="setting-group">
          <h3>{{ t('profile.appearance') }}</h3>
          <label class="toggle-row">
            <span>{{ t('profile.darkMode') }}</span>
            <input type="checkbox" checked />
          </label>
          <label class="toggle-row">
            <span>{{ t('profile.reducedMotion') }}</span>
            <input type="checkbox" />
          </label>
        </div>
        
        <div class="setting-group">
          <h3>{{ t('profile.notifications') }}</h3>
          <label class="toggle-row">
            <span>{{ t('profile.emailDigest') }}</span>
            <input type="checkbox" checked />
          </label>
          <label class="toggle-row">
            <span>{{ t('profile.newComments') }}</span>
            <input type="checkbox" checked />
          </label>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.profile-page {
  padding-top: 120px;
  padding-bottom: $spacing-xxl;
  max-width: 900px;
  margin: 0 auto;
}

.glass-panel {
  background: $color-card-bg;
  backdrop-filter: blur(10px);
  border: 1px solid $color-card-border;
  border-radius: 16px;
  box-shadow: $shadow-card;
}

.profile-header {
  padding: $spacing-xl;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: $spacing-xl;

  .profile-info {
    display: flex;
    gap: $spacing-xl;
    align-items: center;

    .avatar-wrapper {
      width: 100px;
      height: 100px;
      min-width: 100px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      
      .profile-avatar-img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 50%;
        border: 2px solid $color-accent-primary;
      }
    }

    .user-details {
      .username {
        font-size: 2rem;
        margin-bottom: $spacing-xs;
        color: $color-text-primary;
      }

      .bio {
        color: $color-text-secondary;
        margin-bottom: $spacing-md;
      }

      .stats-row {
        display: flex;
        gap: $spacing-lg;

        // Mobile Adaptation for Stats
        @media (max-width: $breakpoint-tablet) {
          width: 100%;
          justify-content: space-evenly; // Use space-evenly to avoid edge clipping
          gap: 0; 
          padding: $spacing-md $spacing-sm; // Reduced side padding from $spacing-lg to $spacing-sm
          background: rgba(var(--color-accent-primary-rgb), 0.05);
          border-radius: 12px;
          border: 1px solid $color-border;
        }

        .stat-item {
          display: flex;
          flex-direction: column;
          
          // Mobile center align
          @media (max-width: $breakpoint-tablet) {
             align-items: center;
             min-width: auto; // Remove fixed min-width to allow natural shrinking if needed
             flex: 1; // Allow items to take equal space
          }
          
          .count {
            font-weight: bold;
            font-size: 1.2rem;
            color: $color-text-primary;
          }
          
          .label {
            font-size: 0.85rem;
            color: $color-text-secondary;
          }
        }
      }
    }
  }

  .edit-btn {
    background: transparent;
    border: 1px solid $color-border;
    color: $color-text-primary;
    padding: 8px 16px;
    border-radius: 20px;
    cursor: pointer;
    transition: all 0.3s;
    white-space: nowrap;

    &:hover {
      border-color: $color-accent-primary;
      background: $color-bg-secondary;
      color: $color-accent-primary;
    }
  }
}

.profile-tabs {
  display: flex;
  gap: $spacing-lg;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  margin-bottom: $spacing-xl;
  overflow-x: auto; /* Allow scrolling on mobile */
  scrollbar-width: none; /* Firefox */
  
  &::-webkit-scrollbar {
    display: none; /* Chrome/Safari */
  }

  .tab-btn {
    background: none;
    border: none;
    color: $color-text-secondary;
    padding: $spacing-md 0;
    font-size: 1.1rem;
    cursor: pointer;
    position: relative;
    transition: color 0.3s;
    white-space: nowrap;

    &.active {
      color: $color-accent-primary;
      font-weight: bold;

      &::after {
        content: '';
        position: absolute;
        bottom: -1px;
        left: 0;
        width: 100%;
        height: 2px;
        background: $color-accent-primary;
      }
    }

    &:hover {
      color: $color-text-primary;
    }
  }
}

.articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: $spacing-lg;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-md;

  .history-item {
    padding: $spacing-md;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .history-info {
      flex: 1;
      margin-right: $spacing-md;
    }

    .history-date {
      font-size: 0.85rem;
      color: $color-text-secondary;
      display: block;
      margin-bottom: 4px;
    }

    h3 {
      font-size: 1.1rem;
      margin: 0;
    }

    .view-btn {
      background: none;
      border: 1px solid rgba(255, 255, 255, 0.2);
      color: $color-text-primary;
      padding: 4px 12px;
      border-radius: 12px;
      cursor: pointer;
      white-space: nowrap;
      min-height: 32px;

      &:hover {
        background: rgba(255, 255, 255, 0.1);
      }
    }
  }
}

.settings-panel {
  padding: $spacing-xl;
  /* max-width removed for desktop adaptation */

  .setting-section {
    margin-bottom: $spacing-xxl;
    
    h3 {
      margin-bottom: $spacing-lg;
      color: $color-accent-secondary;
      border-bottom: 1px solid rgba(255, 255, 255, 0.05);
      padding-bottom: $spacing-sm;
    }
  }

  .security-card {
    background: rgba(255, 255, 255, 0.03);
    border-radius: 12px;
    padding: $spacing-lg;
    margin-bottom: $spacing-lg;
    border: 1px solid rgba(255, 255, 255, 0.05);

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: $spacing-md;

      .label {
        font-weight: 600;
        color: $color-text-primary;
      }

      .status-badge {
        font-size: 0.8rem;
        padding: 2px 8px;
        border-radius: 10px;
        background: rgba(255, 255, 255, 0.1);

        &.verified {
          background: rgba(16, 185, 129, 0.2);
          color: #34d399;
        }
        &.unverified {
          background: rgba(245, 158, 11, 0.2);
          color: #fbbf24;
        }
      }
    }

    .card-body {
      .current-email {
        color: $color-text-secondary;
        margin-bottom: $spacing-md;
      }

      .action-row {
        margin-top: $spacing-sm;
      }
    }
  }

  .form-stack {
    display: flex;
    flex-direction: column;
    gap: $spacing-lg;
  }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 8px;

    label {
      font-size: 0.9rem;
      color: $color-text-secondary;
    }

    .input-wrapper {
      position: relative;
      display: flex;
      align-items: center;

      input {
        width: 100%;
        background: rgba(0, 0, 0, 0.2);
        border: 1px solid rgba(255, 255, 255, 0.1);
        border-radius: 8px;
        padding: 10px 12px;
        color: $color-text-primary;
        padding-right: 60px; // Space for toggle button
        transition: border-color 0.3s;

        &:focus {
          outline: none;
          border-color: $color-accent-primary;
        }
      }

      .toggle-visibility {
        position: absolute;
        right: 8px;
        background: none;
        border: none;
        color: $color-text-secondary;
        font-size: 0.8rem;
        cursor: pointer;
        padding: 4px;
        
        &:hover {
          color: $color-text-primary;
        }
      }
    }

    .error-msg {
      font-size: 0.8rem;
      color: #ef4444;
    }
  }

  .password-requirements {
    list-style: none;
    padding: 0;
    margin-top: 4px;
    display: flex;
    flex-wrap: wrap;
    gap: 8px;

    li {
      font-size: 0.75rem;
      color: $color-text-secondary;
      padding: 2px 6px;
      border-radius: 4px;
      background: rgba(255, 255, 255, 0.05);

      &.met {
        color: #34d399;
        background: rgba(16, 185, 129, 0.1);
      }
    }
  }

  .btn-primary {
    background: $color-accent-primary;
    color: #000;
    border: none;
    padding: 10px 20px;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
    transition: opacity 0.3s;

    &:hover {
      opacity: 0.9;
    }

    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }

    &.full-width {
      width: 100%;
      margin-top: $spacing-sm;
    }
  }

  .btn-link {
    background: none;
    border: none;
    color: $color-accent-primary;
    cursor: pointer;
    font-size: 0.9rem;
    text-decoration: underline;
    padding: 0;

    &:disabled {
      color: $color-text-secondary;
      text-decoration: none;
      cursor: default;
    }
  }

  .verification-form {
    margin-top: $spacing-md;
    padding-top: $spacing-md;
    border-top: 1px solid rgba(255, 255, 255, 0.05);

    .hint {
      font-size: 0.9rem;
      color: $color-text-secondary;
      margin-bottom: $spacing-sm;
    }

    .input-group {
      display: flex;
      gap: $spacing-sm;
      margin-bottom: $spacing-sm;

      .code-input {
        flex: 1;
        background: rgba(0, 0, 0, 0.2);
        border: 1px solid rgba(255, 255, 255, 0.1);
        border-radius: 8px;
        padding: 10px;
        color: $color-text-primary;
        text-align: center;
        letter-spacing: 4px;
        font-family: monospace;

        &:focus {
          outline: none;
          border-color: $color-accent-primary;
        }
      }
    }

    .resend-row {
      text-align: right;
    }
  }

  .setting-group {
    margin-bottom: $spacing-xl;

    h3 {
      margin-bottom: $spacing-md;
      color: $color-accent-secondary;
    }

    .toggle-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: $spacing-sm 0;
      cursor: pointer;
      
      input[type="checkbox"] {
        accent-color: $color-accent-primary;
        width: 20px;
        height: 20px;
      }
    }
  }
}

.empty-state {
  text-align: center;
  padding: $spacing-xl;
  color: $color-text-secondary;
  background: rgba(255, 255, 255, 0.02);
  border-radius: 8px;
}

/* Desktop Adaptation */
@media (min-width: 769px) {
  .profile-tabs {
    overflow-x: visible;
  }

  .form-stack {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: $spacing-lg;

    .span-full,
    .full-width {
      grid-column: 1 / -1;
    }
  }

  .verification-form {
    max-width: 400px;
  }
}

/* Mobile Responsiveness */
@media (max-width: 768px) {
  .profile-page {
    padding-top: 100px;
    padding-left: $spacing-md;
    padding-right: $spacing-md;
  }

  .profile-header {
    flex-direction: column;
    align-items: center;
    text-align: center;
    gap: $spacing-lg;

    .profile-info {
      flex-direction: column;
      width: 100%;
      gap: $spacing-lg;

      .user-details {
        width: 100%;
        
        .stats-row {
          justify-content: space-around;
          margin-top: $spacing-md;
          background: rgba(255, 255, 255, 0.03);
          padding: $spacing-md;
          border-radius: 12px;
        }
      }
    }

    .edit-btn {
      width: 100%;
      padding: 12px;
    }
  }

  .history-list {
    .history-item {
      flex-direction: column;
      align-items: flex-start;
      gap: $spacing-md;

      .history-info {
        margin-right: 0;
      }

      .view-btn {
        width: 100%;
        text-align: center;
        padding: 10px;
      }
    }
  }

  .settings-panel {
    padding: $spacing-md;
    
    .security-card {
      padding: $spacing-md;
    }

    .btn-primary {
      padding: 12px; // Larger touch target
    }
  }
}
</style>