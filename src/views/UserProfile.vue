<script setup lang="ts">
import { ref, computed, onUnmounted, watchEffect, nextTick } from 'vue';
import { useI18n } from 'vue-i18n';
import ArticleCard from '../components/ArticleCard.vue';
import AvatarGenerator from '../components/AvatarGenerator.vue';
import { useUserStore } from '../store/user';
import { requestEmailVerification, updateUserInfo, type UpdateProfileDto } from '../api/auth';
import { useTheme } from '../composables/useTheme';

const { t } = useI18n();
const { user: currentUser, fetchUserInfo } = useUserStore();
const { theme, themePreference, setThemePreference } = useTheme();

const profileForm = ref<UpdateProfileDto>({
  username: '',
  bio: '',
  avatarUrl: ''
});

const isProfileUpdating = ref(false);

// Sync form with user data
watchEffect(() => {
  if (currentUser.value) {
    profileForm.value = {
      username: currentUser.value.username,
      bio: currentUser.value.bio || '',
      avatarUrl: currentUser.value.avatarUrl
    };
  }
});

const handleUpdateProfile = async () => {
  if (!currentUser.value) return;
  
  isProfileUpdating.value = true;
  try {
    const res = await updateUserInfo(profileForm.value);
    if (res.isSuccess) {
      await fetchUserInfo();
      alert(t('profile.updateSuccess') || 'Profile updated successfully');
    } else {
      alert(res.errMsg || 'Failed to update profile');
    }
  } catch (error) {
    console.error(error);
    alert('An error occurred');
  } finally {
    isProfileUpdating.value = false;
  }
};

const activeTab = ref('favorites');
const transitionName = ref('fade-slide');

const tabs = computed(() => [
  { id: 'favorites', label: t('profile.favorites') },
  { id: 'history', label: t('profile.history') },
  { id: 'settings', label: t('profile.settings') }
]);

const tabOrder = ['favorites', 'history', 'settings'];

const activeSettingTab = ref('profile');
const settingTabs = computed(() => [
  { id: 'profile', label: t('profile.profileInfo'), icon: 'M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z' },
  { id: 'security', label: t('profile.accountSecurity'), icon: 'M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z' },
  { id: 'appearance', label: t('profile.appearance'), icon: 'M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z' },
  { id: 'notifications', label: t('profile.notifications'), icon: 'M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9M13.73 21a2 2 0 0 1-3.46 0' }
]);

// Tab Indicator Logic
const tabRefs = ref<HTMLElement[]>([]);
const tabIndicatorStyle = ref({
  left: '0px',
  width: '0px'
});

const updateTabIndicator = async () => {
  await nextTick();
  const currentIndex = tabOrder.indexOf(activeTab.value);
  const activeBtn = tabRefs.value[currentIndex];
  
  if (activeBtn) {
    tabIndicatorStyle.value = {
      left: `${activeBtn.offsetLeft}px`,
      width: `${activeBtn.offsetWidth}px`
    };
  }
};

watchEffect(() => {
  if (activeTab.value) {
    updateTabIndicator();
  }
});

const changeTab = (newTab: string) => {
  const oldIndex = tabOrder.indexOf(activeTab.value);
  const newIndex = tabOrder.indexOf(newTab);
  transitionName.value = newIndex > oldIndex ? 'slide-left' : 'slide-right';
  activeTab.value = newTab;
};

// Heatmap Data (Static generation to prevent re-render flickering)
const generateHeatmapData = () => {
  const weeks = 52;
  const days = 7;
  const data = [];
  
  for (let w = 0; w < weeks; w++) {
    const weekData = [];
    for (let d = 0; d < days; d++) {
      // Simulate some activity pattern
      const isWeekend = d === 0 || d === 6;
      const baseChance = isWeekend ? 0.3 : 0.7;
      const hasActivity = Math.random() < baseChance;
      const intensity = hasActivity ? Math.random() : 0.1;
      
      // Add date for tooltip (mock)
      const date = new Date();
      date.setDate(date.getDate() - ((weeks - w) * 7) + d);
      
      weekData.push({
        intensity,
        date: date.toISOString().split('T')[0],
        count: hasActivity ? Math.floor(Math.random() * 10) + 1 : 0
      });
    }
    data.push(weekData);
  }
  return data;
};

const heatmapData = ref(generateHeatmapData());

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
    summary: 'Deep dive into the new Composition API features, script setup, and composables for better code organization.',
    date: '2023-10-15',
    tags: ['Vue', 'Frontend', 'JavaScript'],
    image: 'https://images.unsplash.com/photo-1555066931-4365d14bab8c?q=80&w=1080&auto=format&fit=crop'
  },
  {
    id: 3,
    title: 'Building Microservices with Kubernetes',
    summary: 'Learn how to deploy, scale, and manage your microservices using Docker and Kubernetes clusters.',
    date: '2023-11-20',
    tags: ['DevOps', 'K8s', 'Docker'],
    image: 'https://images.unsplash.com/photo-1667372393119-c85c020799a3?q=80&w=1080&auto=format&fit=crop'
  },
  {
    id: 4,
    title: 'Spring Boot 3.0 Migration Guide',
    summary: 'A comprehensive guide to upgrading your Spring Boot applications to version 3.0 and Java 17.',
    date: '2023-12-05',
    tags: ['Java', 'Spring Boot', 'Backend'],
    image: 'https://images.unsplash.com/photo-1537432376769-00f5c2f4c8d2?q=80&w=1080&auto=format&fit=crop'
  },
  {
    id: 5,
    title: 'Optimizing Web Performance',
    summary: 'Techniques for improving LCP, FID, and CLS scores to boost your website ranking and user experience.',
    date: '2024-01-10',
    tags: ['Performance', 'Web', 'SEO'],
    image: 'https://images.unsplash.com/photo-1461749280684-dccba630e2f6?q=80&w=1080&auto=format&fit=crop'
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
    <div class="profile-layout">
      <!-- Left Sidebar: User Profile Card -->
      <aside class="profile-sidebar">
        <div class="profile-card glass-panel">
          <div class="avatar-wrapper">
            <img v-if="user.avatarUrl" :src="user.avatarUrl" :alt="user.name" class="profile-avatar-img" />
            <AvatarGenerator v-else :username="user.name" :size="120" />
            <div class="status-indicator" :class="{ online: true }"></div>
          </div>
          
          <div class="user-identity">
            <h1 class="username">
              {{ user.name }}
              <span class="verified-badge" v-if="user.emailVerified" title="Verified User">
                <svg viewBox="0 0 24 24" class="icon"><path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z" fill="currentColor"></path></svg>
              </span>
            </h1>
            <p class="role-badge">Rookie Developer</p>
          </div>

          <p class="bio">{{ user.bio }}</p>

          <div class="stats-row">
            <div class="stat-item">
              <span class="count">{{ user.stats.articles }}</span>
              <span class="label">{{ t('profile.articles') }}</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="count">{{ user.stats.likes }}</span>
              <span class="label">{{ t('profile.likes') }}</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="count">{{ user.stats.following }}</span>
              <span class="label">{{ t('profile.following') }}</span>
            </div>
          </div>

          <div class="info-list">
            <div class="info-item">
              <svg viewBox="0 0 24 24" class="icon"><path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5a2.5 2.5 0 0 1 0-5 2.5 2.5 0 0 1 0 5z" fill="currentColor"/></svg>
              <span>Shanghai, China</span>
            </div>
            <div class="info-item">
              <svg viewBox="0 0 24 24" class="icon"><path d="M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z" fill="currentColor"/></svg>
              <span>{{ user.email }}</span>
            </div>
            <div class="info-item">
              <svg viewBox="0 0 24 24" class="icon"><path d="M19 3h-1V1h-2v2H8V1H6v2H5c-1.11 0-1.99.9-1.99 2L3 19a2 2 0 0 0 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V8h14v11zM7 10h5v5H7z" fill="currentColor"/></svg>
              <span>Joined 2023-10</span>
            </div>
          </div>

          <div class="skills-section">
            <h3>Skills</h3>
            <div class="tags">
              <span class="tag">Vue.js</span>
              <span class="tag">TypeScript</span>
              <span class="tag">Spring Boot</span>
              <span class="tag">Docker</span>
            </div>
          </div>

          <button class="edit-btn" @click="handleEditProfile">{{ t('profile.editProfile') }}</button>
        </div>
      </aside>

      <!-- Right Main Content -->
      <main class="profile-main">
        <!-- Contribution Graph / Activity (Mockup) -->
        <div class="activity-card glass-panel">
          <h3>{{ t('profile.activity', 'Activity') }}</h3>
          <div class="heatmap-container">
            <div class="heatmap-months">
              <span>Jan</span><span>Feb</span><span>Mar</span><span>Apr</span><span>May</span><span>Jun</span>
              <span>Jul</span><span>Aug</span><span>Sep</span><span>Oct</span><span>Nov</span><span>Dec</span>
            </div>
            <div class="heatmap-body">
              <div class="heatmap-days">
                <span>Mon</span>
                <span>Wed</span>
                <span>Fri</span>
              </div>
              <div class="heatmap-grid" :key="theme">
                <div v-for="(week, i) in heatmapData" :key="i" class="heatmap-col">
                  <div 
                    v-for="(day, j) in week" 
                    :key="j" 
                    class="heatmap-cell" 
                    :style="{ backgroundColor: `rgba(var(--color-accent-primary-rgb), ${day.intensity})` }"
                    :title="`${day.count} contributions on ${day.date}`"
                  ></div>
                </div>
              </div>
            </div>
            <div class="heatmap-legend">
              <span>Less</span>
              <div class="legend-scale">
                <div class="cell" style="background-color: rgba(var(--color-accent-primary-rgb), 0.1)"></div>
                <div class="cell" style="background-color: rgba(var(--color-accent-primary-rgb), 0.4)"></div>
                <div class="cell" style="background-color: rgba(var(--color-accent-primary-rgb), 0.7)"></div>
                <div class="cell" style="background-color: rgba(var(--color-accent-primary-rgb), 1)"></div>
              </div>
              <span>More</span>
            </div>
          </div>
        </div>

        <nav class="profile-tabs glass-panel">
          <div class="tab-indicator" :style="tabIndicatorStyle"></div>
          <button 
            v-for="(tab, index) in tabs" 
            :key="tab.id"
            ref="tabRefs"
            class="tab-btn"
            :class="{ active: activeTab === tab.id }"
            @click="changeTab(tab.id)"
          >
            {{ tab.label }}
          </button>
        </nav>

        <Transition :name="transitionName" mode="out-in">
          <div class="tab-content" v-if="activeTab === 'favorites'" key="favorites">
            <div class="articles-grid" v-if="mockArticles.length">
              <ArticleCard v-for="article in mockArticles" :key="article.id" v-bind="article" />
            </div>
            <div v-else class="empty-state glass-panel">
              <p>{{ t('profile.noFavorites') }}</p>
            </div>
          </div>

          <div class="tab-content" v-else-if="activeTab === 'history'" key="history">
            <div class="history-list" v-if="mockArticles.length">
              <div v-for="article in mockArticles" :key="article.id" class="history-item glass-panel">
                <div class="history-info">
                  <span class="history-date">{{ t('profile.viewedOn') }} {{ article.date }}</span>
                  <h3>{{ article.title }}</h3>
                </div>
                <button class="view-btn">{{ t('profile.readAgain') }}</button>
              </div>
            </div>
            <div v-else class="empty-state glass-panel">
              <p>{{ t('profile.noHistory') }}</p>
            </div>
          </div>

          <div class="tab-content settings-panel glass-panel" v-else-if="activeTab === 'settings'" key="settings" ref="tabsRef">
            <div class="settings-layout">
              <!-- Sidebar -->
              <aside class="settings-sidebar">
                <button 
                  v-for="tab in settingTabs" 
                  :key="tab.id"
                  class="setting-tab-btn"
                  :class="{ active: activeSettingTab === tab.id }"
                  @click="activeSettingTab = tab.id"
                >
                  <svg viewBox="0 0 24 24" class="tab-icon" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path :d="tab.icon" />
                  </svg>
                  <span>{{ tab.label }}</span>
                </button>
              </aside>

              <!-- Content -->
              <div class="settings-content">
                <Transition name="fade" mode="out-in">
                  <!-- Profile Info -->
                  <div v-if="activeSettingTab === 'profile'" key="profile" class="setting-view">
                    <h3>{{ t('profile.profileInfo') }}</h3>
                    <div class="form-grid">
                      <!-- Avatar URL -->
                      <div class="form-group avatar-group">
                        <label>{{ t('profile.avatarUrl') }}</label>
                        <div class="input-with-preview">
                          <div class="input-wrapper">
                            <input 
                              type="text" 
                              v-model="profileForm.avatarUrl"
                              :placeholder="t('profile.enterAvatarUrl')"
                            />
                          </div>
                          <div class="avatar-preview-small" v-if="profileForm.avatarUrl">
                            <img :src="profileForm.avatarUrl" alt="Preview" @error="(e) => (e.target as HTMLImageElement).style.display = 'none'" />
                          </div>
                        </div>
                      </div>

                      <!-- Username -->
                      <div class="form-group username-group">
                        <label>{{ t('profile.username') }}</label>
                        <div class="input-wrapper">
                          <input 
                            type="text" 
                            v-model="profileForm.username"
                            :placeholder="t('profile.enterUsername')"
                          />
                        </div>
                      </div>

                      <!-- Bio -->
                      <div class="form-group bio-group">
                        <label>{{ t('profile.bio') }}</label>
                        <div class="input-wrapper">
                          <textarea 
                            v-model="profileForm.bio"
                            :placeholder="t('profile.enterBio')"
                            class="bio-input"
                            rows="2"
                          ></textarea>
                        </div>
                      </div>

                      <div class="form-actions">
                        <button 
                          class="btn-primary" 
                          @click="handleUpdateProfile" 
                          :disabled="isProfileUpdating"
                        >
                          {{ isProfileUpdating ? t('common.saving', 'Saving...') : t('common.saveChanges', 'Save Changes') }}
                        </button>
                      </div>
                    </div>
                  </div>

                  <!-- Security -->
                  <div v-else-if="activeSettingTab === 'security'" key="security" class="setting-view">
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
                  </div>

                  <!-- Appearance -->
                  <div v-else-if="activeSettingTab === 'appearance'" key="appearance" class="setting-view">
                    <h3>{{ t('profile.appearance') }}</h3>
                    <div class="theme-selector">
                      <label class="theme-option">
                        <input 
                          type="radio" 
                          name="theme" 
                          value="light" 
                          :checked="themePreference === 'light'"
                          @change="setThemePreference('light')"
                        />
                        <span class="theme-label">
                          <svg class="theme-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <circle cx="12" cy="12" r="5"></circle>
                            <path d="M12 1v2M12 21v2M4.22 4.22l1.42 1.42M18.36 18.36l1.42 1.42M1 12h2M21 12h2M4.22 19.78l1.42-1.42M18.36 5.64l1.42-1.42"></path>
                          </svg>
                          {{ t('profile.themeLight') }}
                        </span>
                      </label>
                      
                      <label class="theme-option">
                        <input 
                          type="radio" 
                          name="theme" 
                          value="dark" 
                          :checked="themePreference === 'dark'"
                          @change="setThemePreference('dark')"
                        />
                        <span class="theme-label">
                          <svg class="theme-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>
                          </svg>
                          {{ t('profile.themeDark') }}
                        </span>
                      </label>
                      
                      <label class="theme-option">
                        <input 
                          type="radio" 
                          name="theme" 
                          value="auto" 
                          :checked="themePreference === 'auto'"
                          @change="setThemePreference('auto')"
                        />
                        <span class="theme-label">
                          <svg class="theme-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <rect x="2" y="3" width="20" height="14" rx="2" ry="2"></rect>
                            <line x1="8" y1="21" x2="16" y2="21"></line>
                            <line x1="12" y1="17" x2="12" y2="21"></line>
                          </svg>
                          {{ t('profile.themeSystem') }}
                        </span>
                      </label>
                    </div>
                    <label class="toggle-row">
                      <span>{{ t('profile.reducedMotion') }}</span>
                      <input type="checkbox" />
                    </label>
                  </div>

                  <!-- Notifications -->
                  <div v-else-if="activeSettingTab === 'notifications'" key="notifications" class="setting-view">
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
                </Transition>
              </div>
            </div>
          </div>
        </Transition>
      </main>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.profile-page {
  padding-top: 100px;
  padding-bottom: $spacing-xxl;
  max-width: 1400px;
  margin: 0 auto;
  padding-left: $spacing-lg;
  padding-right: $spacing-lg;
}

.profile-layout {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 2rem;
  
  @media (max-width: 1024px) {
    grid-template-columns: 1fr;
  }
}

.glass-panel {
  background: $color-card-bg;
  backdrop-filter: blur($backdrop-blur);
  -webkit-backdrop-filter: blur($backdrop-blur); /* Safari support */
  border: 1px solid $color-card-border;
  border-radius: 16px;
  box-shadow: $shadow-card;
  /* Fix for rendering artifacts during theme switch */
  transform: translate3d(0, 0, 0);
  will-change: transform, background-color;
}

/* Sidebar Styles */
.profile-sidebar {
  .profile-card {
    padding: 2rem;
    display: flex;
    flex-direction: column;
    align-items: center;
    text-align: center;
    position: sticky;
    top: 100px;
  }

  .avatar-wrapper {
    position: relative;
    width: 120px;
    height: 120px;
    margin-bottom: 1.5rem;

    .profile-avatar-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      border-radius: 50%;
      border: 4px solid $color-bg-secondary;
      box-shadow: 0 4px 12px rgba(0,0,0,0.1);
    }

    .status-indicator {
      position: absolute;
      bottom: 8px;
      right: 8px;
      width: 16px;
      height: 16px;
      border-radius: 50%;
      background: #ccc;
      border: 2px solid $color-bg-secondary;

      &.online {
        background: #10b981;
        box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.2);
      }
    }
  }

  .user-identity {
    margin-bottom: 1rem;
    
    .username {
      font-size: 1.5rem;
      font-weight: 700;
      color: $color-text-primary;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 6px;
      margin-bottom: 4px;

      .verified-badge {
        color: #3b82f6;
        width: 20px;
        height: 20px;
      }
    }

    .role-badge {
      display: inline-block;
      padding: 4px 12px;
      background: rgba($color-accent-primary-rgb, 0.1);
      color: $color-accent-primary;
      border-radius: 20px;
      font-size: 0.8rem;
      font-weight: 600;
    }
  }

  .bio {
    color: $color-text-secondary;
    font-size: 0.95rem;
    line-height: 1.6;
    margin-bottom: 1.5rem;
    max-width: 90%;
  }

  .stats-row {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    margin-bottom: 1.5rem;
    padding: 1rem 0;
    border-top: 1px solid rgba(255,255,255,0.05);
    border-bottom: 1px solid rgba(255,255,255,0.05);

    .stat-item {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 4px;

      .count {
        font-weight: 700;
        font-size: 1.1rem;
        color: $color-text-primary;
      }

      .label {
        font-size: 0.75rem;
        color: $color-text-secondary;
        text-transform: uppercase;
        letter-spacing: 0.5px;
      }
    }

    .stat-divider {
      width: 1px;
      height: 24px;
      background: rgba(255,255,255,0.1);
    }
  }

  .info-list {
    width: 100%;
    display: flex;
    flex-direction: column;
    gap: 12px;
    margin-bottom: 1.5rem;

    .info-item {
      display: flex;
      align-items: center;
      gap: 10px;
      color: $color-text-secondary;
      font-size: 0.9rem;

      .icon {
        width: 18px;
        height: 18px;
        opacity: 0.7;
      }
    }
  }

  .skills-section {
    width: 100%;
    text-align: left;
    margin-bottom: 1.5rem;

    h3 {
      font-size: 0.9rem;
      text-transform: uppercase;
      color: $color-text-secondary;
      margin-bottom: 10px;
      font-weight: 600;
    }

    .tags {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;

      .tag {
        padding: 4px 10px;
        background: $color-bg-secondary;
        border: 1px solid $color-border;
        border-radius: 6px;
        font-size: 0.8rem;
        color: $color-text-primary;
      }
    }
  }

  .edit-btn {
    width: 100%;
    padding: 10px;
    background: transparent;
    border: 1px solid $color-accent-primary;
    color: $color-accent-primary;
    border-radius: 10px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      background: $color-accent-primary;
      color: white;
    }
  }
}

/* Main Content Styles */
.profile-main {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.activity-card {
  padding: 1.5rem;
  
  h3 {
    margin-bottom: 1rem;
    font-size: 1.1rem;
    color: $color-text-primary;
  }
}

/* Activity Heatmap */
.heatmap-container {
  width: 100%;
  overflow-x: auto;
  padding-bottom: 10px;

  .heatmap-months {
    display: flex;
    justify-content: space-between;
    margin-bottom: 8px;
    padding-left: 30px; /* Space for day labels */
    font-size: 0.75rem;
    color: $color-text-secondary;
  }

  .heatmap-body {
    display: flex;
    gap: 8px;
  }

  .heatmap-days {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding-top: 2px;
    padding-bottom: 2px;
    font-size: 0.7rem;
    color: $color-text-secondary;
    height: 94px; /* Align with grid height (7 cells * 10px + 6 gaps * 4px) */
  }

  .heatmap-grid {
    display: flex;
    gap: 4px;
    
    .heatmap-col {
      display: flex;
      flex-direction: column;
      gap: 4px;
    }

    .heatmap-cell {
      width: 10px;
      height: 10px;
      /* Background set inline for dynamic opacity */
      border-radius: 2px;
      transition: background-color 0.3s, transform 0.2s;
      position: relative; /* Ensure z-index works */
      z-index: 1;
      
      &:hover {
        transform: scale(1.4);
        z-index: 10;
        box-shadow: 0 0 4px rgba(0,0,0,0.2);
        /* Ensure hover state is visible */
        opacity: 1 !important; 
      }
    }
  }

  .heatmap-legend {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 8px;
    margin-top: 12px;
    font-size: 0.8rem;
    color: $color-text-secondary;

    .legend-scale {
      display: flex;
      gap: 4px;
      
      .cell {
        width: 10px;
        height: 10px;
        /* Background set inline */
        border-radius: 2px;
      }
    }
  }
}

.profile-tabs {
  display: flex;
  gap: 2rem;
  padding: 0 1.5rem;
  overflow-x: auto;
  position: relative; /* For indicator positioning */
  
  .tab-indicator {
    position: absolute;
    bottom: 0;
    height: 3px;
    background: $color-accent-primary;
    border-radius: 3px 3px 0 0;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    pointer-events: none;
  }

  .tab-btn {
    padding: 1rem 0;
    background: none;
    border: none;
    color: $color-text-secondary;
    font-size: 1rem;
    font-weight: 500;
    cursor: pointer;
    position: relative;
    transition: color 0.3s;
    white-space: nowrap;

    &.active {
      color: $color-accent-primary;
      /* Remove old pseudo-element */
    }

    &:hover {
      color: $color-text-primary;
    }
  }
}

.articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.empty-state {
  padding: 3rem;
  text-align: center;
  color: $color-text-secondary;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* History List - Refined */
.history-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;

  .history-item {
    padding: 1rem 1.5rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
    transition: transform 0.2s;

    &:hover {
      transform: translateX(4px);
    }

    .history-info {
      h3 {
        font-size: 1rem;
        color: $color-text-primary;
        margin: 0 0 4px 0;
      }
      .history-date {
        font-size: 0.8rem;
        color: $color-text-secondary;
      }
    }
    
    .view-btn {
      padding: 6px 12px;
      border: 1px solid $color-border;
      background: transparent;
      color: $color-text-secondary;
      border-radius: 6px;
      font-size: 0.8rem;
      cursor: pointer;
      transition: all 0.2s;
      
      &:hover {
        border-color: $color-accent-primary;
        color: $color-accent-primary;
      }
    }
  }
}

/* Settings Panel Styles */
.settings-panel {
  padding: 0;
  overflow: hidden;
  min-height: 500px;

  .settings-layout {
    display: grid;
    grid-template-columns: 240px 1fr;
    min-height: 500px;
    
    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }
  }

  .settings-sidebar {
    background: rgba(0, 0, 0, 0.02);
    border-right: 1px solid rgba(255,255,255,0.05);
    padding: 1.5rem 1rem;
    display: flex;
    flex-direction: column;
    gap: 0.5rem;

    :global(.dark) & {
      background: rgba(255, 255, 255, 0.02);
    }

    @media (max-width: 768px) {
      border-right: none;
      border-bottom: 1px solid rgba(255,255,255,0.05);
      flex-direction: row;
      overflow-x: auto;
      padding: 1rem;
    }
  }

  .setting-tab-btn {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    border: none;
    background: transparent;
    color: $color-text-secondary;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s;
    text-align: left;
    font-weight: 500;
    
    &:hover {
      background: rgba(0, 0, 0, 0.05);
      color: $color-text-primary;
    }

    :global(.dark) &:hover {
      background: rgba(255, 255, 255, 0.05);
    }
    
    &.active {
      background: rgba($color-accent-primary-rgb, 0.1);
      color: $color-accent-primary;
    }
    
    .tab-icon {
      width: 20px;
      height: 20px;
    }

    @media (max-width: 768px) {
      white-space: nowrap;
      flex: 0 0 auto;
    }
  }

  .settings-content {
    padding: 2rem;
    
    h3 {
      margin-top: 0;
      margin-bottom: 1.5rem;
      font-size: 1.5rem;
      color: $color-text-primary;
      border-bottom: 1px solid rgba(255,255,255,0.05);
      padding-bottom: 0.5rem;
    }
  }

  .form-grid {
    display: grid;
    gap: 1.5rem;
    
    @media (min-width: 768px) {
      grid-template-columns: 1fr 1fr;
    }
  }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 8px;

    label {
      font-size: 0.9rem;
      color: $color-text-secondary;
      font-weight: 500;
    }

    &.span-full {
      grid-column: 1 / -1;
    }
  }

  .input-wrapper {
    position: relative;
    display: flex;
    align-items: center;

    input, textarea {
      width: 100%;
      padding: 10px 14px;
      background: $color-bg-secondary;
      border: 1px solid $color-border;
      border-radius: 8px;
      color: $color-text-primary;
      font-size: 0.95rem;
      transition: all 0.3s;

      &:focus {
        outline: none;
        border-color: $color-accent-primary;
        box-shadow: 0 0 0 2px rgba($color-accent-primary-rgb, 0.1);
      }
      
      &::placeholder {
        color: rgba($color-text-secondary-rgb, 0.7);
        opacity: 1;
      }
      
      :global(.dark) &::placeholder {
        color: rgba(255, 255, 255, 0.5);
      }
    }
    
    .bio-input {
      resize: vertical;
      min-height: 80px;
    }
  }
  
  .input-with-preview {
    display: flex;
    gap: 16px;
    align-items: center;
    
    .input-wrapper {
      flex: 1;
    }
    
    .avatar-preview-small {
      width: 42px;
      height: 42px;
      border-radius: 50%;
      overflow: hidden;
      border: 2px solid $color-border;
      flex-shrink: 0;
      
      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }
  }

  .toggle-visibility {
    position: absolute;
    right: 10px;
    background: none;
    border: none;
    color: $color-text-secondary;
    cursor: pointer;
    font-size: 0.8rem;
    
    &:hover {
      color: $color-text-primary;
    }
  }

  .error-msg {
    color: #ef4444;
    font-size: 0.8rem;
  }

  .password-requirements {
    list-style: none;
    padding: 0;
    margin: 8px 0 0;
    display: flex;
    flex-wrap: wrap;
    gap: 10px;

    li {
      font-size: 0.75rem;
      color: $color-text-secondary;
      opacity: 0.7;
      display: flex;
      align-items: center;
      gap: 4px;

      &::before {
        content: '•';
      }

      &.met {
        color: #10b981;
        opacity: 1;
        
        &::before {
          content: '✓';
        }
      }
    }
  }
  
  .form-actions {
    grid-column: 1 / -1;
    display: flex;
    justify-content: flex-end;
    margin-top: 1rem;
  }
}

/* Security Cards */
.security-card {
  background: rgba(255, 255, 255, 0.02);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 1.5rem;
  margin-bottom: 1.5rem;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;

    .label {
      font-weight: 600;
      color: $color-text-primary;
    }

    .status-badge {
      font-size: 0.75rem;
      padding: 2px 8px;
      border-radius: 12px;
      background: #ccc;
      color: #333;
      font-weight: 600;

      &.verified {
        background: rgba(16, 185, 129, 0.2);
        color: #10b981;
      }

      &.unverified {
        background: rgba(239, 68, 68, 0.2);
        color: #ef4444;
      }
    }
  }
  
  .card-body {
    .current-email {
      font-family: monospace;
      background: rgba(0,0,0,0.2);
      padding: 8px 12px;
      border-radius: 6px;
      margin-bottom: 1rem;
      color: $color-text-primary;
    }
    
    .action-row {
      display: flex;
      align-items: center;
      gap: 12px;
      flex-wrap: wrap;
    }
    
    .helper-text {
      font-size: 0.85rem;
      color: $color-text-secondary;
      margin: 0;
    }
  }
  
  .form-stack {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }
}

/* Toggle Rows */
.setting-group {
  margin-bottom: 2rem;
  
  h3 {
    font-size: 1.1rem;
    margin-bottom: 1rem;
    color: $color-text-primary;
  }
}

.toggle-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid rgba(255,255,255,0.05);
  cursor: pointer;
  
  span {
    color: $color-text-secondary;
  }
  
  &:last-child {
    border-bottom: none;
  }
}

.theme-selector {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  padding: 8px 0;
  
  @media (max-width: 600px) {
    flex-direction: column;
    gap: 8px;
  }
}

.theme-option {
  flex: 1;
  position: relative;
  cursor: pointer;

  input {
    position: absolute;
    opacity: 0;
    width: 0;
    height: 0;
    
    &:checked + .theme-label {
      background: rgba($color-accent-primary-rgb, 0.1);
      border-color: $color-accent-primary;
      color: $color-accent-primary;
    }
  }

  .theme-label {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: 12px;
    background: rgba(255, 255, 255, 0.03);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 8px;
    font-size: 0.9rem;
    font-weight: 500;
    color: $color-text-secondary;
    transition: all 0.3s;

    &:hover {
      background: rgba(255, 255, 255, 0.06);
      border-color: rgba(255, 255, 255, 0.2);
    }
  }

  .theme-icon {
    width: 18px;
    height: 18px;
  }
}

/* Buttons */
.btn-primary {
  background: linear-gradient(135deg, $color-accent-primary, $color-accent-secondary);
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba($color-accent-primary-rgb, 0.3);
  }
  
  &:disabled {
    opacity: 0.7;
    cursor: not-allowed;
    transform: none;
  }
  
  &.full-width {
    width: 100%;
  }
}

/* Transitions */
.fade-slide-enter-active,
.fade-slide-leave-active,
.slide-left-enter-active,
.slide-left-leave-active,
.slide-right-enter-active,
.slide-right-leave-active {
  transition: all 0.3s ease-out;
}

.fade-slide-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* Slide Left (Next Tab) */
.slide-left-enter-from {
  opacity: 0;
  transform: translateX(30px);
}
.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-30px);
}

/* Slide Right (Prev Tab) */
.slide-right-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}
.slide-right-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
