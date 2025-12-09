<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import TechIcon from './icons/TechIcon.vue';
import HotTagIcon from './icons/HotTagIcon.vue';
import { useUserStore } from '../store/user';

const { t } = useI18n();
const { isLoggedIn, user } = useUserStore();

interface Tag {
  name: string;
  count: number;
  category: 'backend' | 'frontend' | 'devops' | 'cloud' | 'database' | 'architecture';
}

const tags: Tag[] = ([
  { name: 'Java', count: 128, category: 'backend' },
  { name: 'Spring Boot', count: 85, category: 'backend' },
  { name: 'Vue.js', count: 64, category: 'frontend' },
  { name: 'TypeScript', count: 42, category: 'frontend' },
  { name: 'Docker', count: 36, category: 'devops' },
  { name: 'Kubernetes', count: 24, category: 'devops' },
  { name: 'Microservices', count: 30, category: 'architecture' },
  { name: 'AWS', count: 18, category: 'cloud' },
  { name: 'Redis', count: 28, category: 'database' },
  { name: 'MySQL', count: 32, category: 'database' }
] as const).slice(0, 5) as unknown as Tag[];
</script>

<template>
  <aside class="sidebar">
    <div class="sidebar__widget author-card glass-panel" v-if="isLoggedIn && user">
      <div class="author-card__avatar">
        <img v-if="user.avatarUrl" :src="user.avatarUrl" :alt="user.username" class="user-avatar-img" />
        <div v-else class="avatar-placeholder">{{ user.username.charAt(0).toUpperCase() }}</div>
      </div>
      <h3 class="author-card__name text-gradient">{{ user.username }}</h3>
      
      <!-- Optimized Email Display -->
      <div class="author-card__contact">
        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="contact-icon"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path><polyline points="22,6 12,13 2,6"></polyline></svg>
        <span class="contact-text">{{ user.email }}</span>
      </div>

      <div class="author-card__info">
        <!-- Bio / Self Introduction -->
        <p class="bio-text">
          {{ user.bio || t('sidebar.defaultBio') }}
        </p>
      </div>
    </div>

    <div class="sidebar__widget author-card glass-panel" v-else>
      <div class="author-card__avatar">
        <div class="avatar-placeholder">RC</div>
      </div>
      <h3 class="author-card__name text-gradient">{{ t('sidebar.author') }}</h3>
      <div class="author-card__info">
        <p class="bio-text">
          {{ t('sidebar.bio') }}
        </p>
      </div>
      <div class="author-card__socials">
        <a href="#" class="social-link">GH</a>
        <a href="#" class="social-link">TW</a>
        <a href="#" class="social-link">LI</a>
      </div>
    </div>

  <div class="sidebar__widget tags-card glass-panel">
    <h3 class="sidebar__title">
      <HotTagIcon class="sidebar-icon" />
      {{ t('sidebar.hotTags') }}
    </h3>
    <div class="card-content">
      <div class="tags-list">
        <a v-for="tag in tags" :key="tag.name" href="#" class="tag-item">
          <div class="tag-info">
            <TechIcon :name="tag.name" :size="16" class="tag-icon" />
            <span class="tag-name">{{ tag.name }}</span>
          </div>
          <span class="tag-count">{{ tag.count }}</span>
        </a>
      </div>
    </div>
  </div>
</aside>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.sidebar {
  &__widget {
    padding: 5px; // Reduced from $spacing-xl (24px) to 5px for tighter layout
    margin-bottom: $spacing-xl;
    position: relative;
    overflow: hidden;
    border-radius: 20px;
    background: var(--color-card-bg);
    border: 1px solid var(--color-card-border);
    transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);

    &:hover {
      transform: translateY(-2px);
      border-color: rgba($color-accent-primary, 0.1);
      box-shadow: var(--color-card-hover-shadow);
      background: var(--color-card-bg);
    }
  }

  &__title {
    font-size: 1.15rem;
    margin: 10px 0 15px 5px;
    padding-left: 0; // Removed padding-left as we use flex gap now
    border-left: none; // Removed border-left for icon style
    color: $color-text-primary;
    font-weight: 600;
    letter-spacing: 0.5px;
    opacity: 0.95;
    line-height: 1.2;
    display: flex;
    align-items: center;
    gap: 8px;

    .sidebar-icon {
      width: 24px;
      height: 24px;
    }
  }
}

.author-card {
  text-align: center;
  padding: 10px 5px; // Extra internal breathing room

  &__avatar {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    border: 2px solid var(--color-border); // Slightly thicker border
    padding: 4px;
    margin: 5px auto $spacing-md; // Reduced margins
    background: var(--color-bg-secondary);
    
    .avatar-placeholder {
      width: 100%;
      height: 100%;
      background: linear-gradient(135deg, $color-bg-secondary, $color-bg-primary);
      color: $color-text-secondary;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.5rem;
      font-weight: 600;
      border-radius: 50%;
      border: 1px solid var(--color-border);
      transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
    }

    &:hover .avatar-placeholder {
      color: $color-accent-primary;
      border-color: rgba($color-accent-primary, 0.3);
      box-shadow: 0 0 20px rgba($color-accent-primary, 0.15);
      transform: scale(1.05);
    }
  }

  &__name {
    font-size: 1.4rem;
    font-weight: 700;
    margin-bottom: 5px; // Tightened
    letter-spacing: -0.5px;
  }

  &__contact {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: 6px 14px;
    margin-bottom: 15px;
    background: rgba(var(--color-bg-secondary-rgb), 0.05); // Fallback or variable usage
    background: var(--color-bg-secondary);
    border-radius: 50px; // Pill shape
    border: 1px solid transparent;
    transition: all 0.3s ease;
    max-width: 100%;
    
    .contact-icon {
      color: $color-text-secondary;
      opacity: 0.7;
      transition: all 0.3s ease;
      flex-shrink: 0;
    }
    
    .contact-text {
      font-size: 0.85rem;
      color: $color-text-secondary;
      font-weight: 500;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    
    &:hover {
      background: var(--color-card-bg);
      border-color: rgba($color-accent-primary, 0.3);
      box-shadow: 0 2px 8px rgba(0,0,0,0.05);
      transform: translateY(-1px);
      
      .contact-icon {
        color: $color-accent-primary;
        opacity: 1;
      }
      
      .contact-text {
        color: $color-text-primary;
      }
    }
  }

  &__socials {
    display: flex;
    justify-content: center;
    gap: $spacing-md;
  }

  .social-link {
    width: 42px;
    height: 42px;
    border-radius: 12px;
    background: var(--color-bg-secondary);
    color: $color-text-secondary;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid var(--color-border);
    font-size: 0.85rem;
    font-weight: 600;
    transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);

    &:hover {
      color: #fff;
      background: rgba($color-accent-primary, 0.8); // Changed for visibility
      border-color: rgba($color-accent-primary, 0.3);
      transform: translateY(-2px) scale(1.05);
      box-shadow: 0 5px 15px rgba($color-accent-primary, 0.1);
    }
  }

  .user-avatar-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 50%;
    border: 1px solid var(--color-border);
  }

  .author-card__info {
    margin-top: 0;
    margin-bottom: 20px;
    padding: 0 15px;
    
    .bio-text {
      font-size: 0.9rem;
      line-height: 1.6;
      color: $color-text-secondary;
      font-style: italic;
      opacity: 0.85;
      position: relative;
      
      &::before {
        content: '"';
        font-size: 1.4rem;
        color: var(--color-accent-primary);
        opacity: 0.5;
        margin-right: 4px;
        vertical-align: -3px;
      }
      
      &::after {
        content: '"';
        font-size: 1.4rem;
        color: var(--color-accent-primary);
        opacity: 0.5;
        margin-left: 4px;
        vertical-align: -6px;
        line-height: 0;
      }
    }
  }
}

.card-content {
  padding: 0.5rem 0;
}

.tags-list {
  display: flex;
  flex-direction: column;
}

.tag-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tag-icon {
  opacity: 0.8;
  transition: opacity 0.3s;
  flex-shrink: 0;
}

.tag-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 1rem;
  text-decoration: none;
  color: $color-text-secondary;
  position: relative;
  transition: all 0.3s ease;
  font-size: 0.9rem;
  
  // Hover Animation
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 1px;
    background-color: $color-accent-primary;
    transform: scaleX(0);
    transform-origin: right;
    transition: transform 0.4s cubic-bezier(0.22, 1, 0.36, 1);
  }
  
  &:hover {
    color: $color-text-primary;
    background: rgba($color-text-primary, 0.02);
    
    &::after {
      transform: scaleX(1);
      transform-origin: left;
    }
    
    .tag-count {
      background: rgba($color-accent-primary, 0.1);
      color: $color-accent-primary;
    }

    .tag-icon {
      opacity: 1;
      transform: scale(1.1);
    }
  }
  
  .tag-count {
    font-size: 0.75rem;
    background: rgba($color-text-secondary, 0.1);
    padding: 2px 8px;
    border-radius: 10px;
    transition: all 0.3s ease;
  }
}
</style>
