<script setup lang="ts">
import { computed } from 'vue';
import IconUser from './icons/IconUser.vue';

const props = defineProps<{
  author?: {
    name: string;
    avatar?: string;
    bio?: string;
    articleCount?: number;
    likeCount?: number;
  }
}>();

const authorName = computed(() => props.author?.name || 'Anonymous');
const authorBio = computed(() => props.author?.bio || 'Full Stack Developer | Tech Enthusiast');
const stats = computed(() => [
  { label: 'Articles', value: props.author?.articleCount || 42 },
  { label: 'Likes', value: props.author?.likeCount || 1024 },
  { label: 'Followers', value: 89 }
]);
</script>

<template>
  <div class="user-card glass-panel">
    <div class="user-header">
      <div class="avatar-ring">
        <img v-if="author?.avatar" :src="author.avatar" :alt="authorName" class="avatar" />
        <div v-else class="avatar-placeholder">
          <IconUser :size="40" />
        </div>
      </div>
      <div class="user-info">
        <h3 class="username">{{ authorName }}</h3>
        <p class="bio">{{ authorBio }}</p>
      </div>
    </div>
    
    <div class="user-stats">
      <div v-for="stat in stats" :key="stat.label" class="stat-item">
        <span class="stat-value">{{ stat.value }}</span>
        <span class="stat-label">{{ stat.label }}</span>
      </div>
    </div>
    
    <button class="follow-btn">Follow</button>
  </div>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.user-card {
  padding: $spacing-lg;
  margin-bottom: $spacing-lg;
  display: flex;
  flex-direction: column;
  gap: $spacing-md;
}

.user-header {
  display: flex;
  align-items: center;
  gap: $spacing-md;
}

.avatar-ring {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  padding: 2px;
  background: linear-gradient(135deg, $color-accent-primary, $color-accent-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  
  .avatar, .avatar-placeholder {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    background: $color-bg-primary;
    border: 2px solid $color-bg-secondary;
    object-fit: cover;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.user-info {
  flex: 1;
  overflow: hidden;
  
  .username {
    font-size: 1.1rem;
    font-weight: 700;
    color: $color-text-primary;
    margin-bottom: 4px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  
  .bio {
    font-size: 0.85rem;
    color: $color-text-secondary;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }
}

.user-stats {
  display: flex;
  justify-content: space-around;
  padding: $spacing-sm 0;
  border-top: 1px solid rgba(255, 255, 255, 0.05);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  
  .stat-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2px;
    
    .stat-value {
      font-size: 1.1rem;
      font-weight: 700;
      color: $color-text-primary;
    }
    
    .stat-label {
      font-size: 0.75rem;
      color: $color-text-secondary;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
  }
}

.follow-btn {
  width: 100%;
  padding: $spacing-sm;
  border-radius: 8px;
  background: $color-accent-primary;
  color: white;
  font-weight: 600;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    background: $color-accent-primary; // Using same color but with opacity or just relying on transform
    filter: brightness(0.9); // Use filter for darkening CSS variable
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba($color-accent-primary, 0.3);
  }
}
</style>
