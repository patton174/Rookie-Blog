<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import HotTagIcon from './icons/HotTagIcon.vue';

const { t } = useI18n();

interface Tag {
  name: string;
  count: number;
  category: 'backend' | 'frontend' | 'devops' | 'cloud' | 'database' | 'architecture';
}

// Mock data - normally fetched from API
const tags: Tag[] = [
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
].slice(0, 5); // Limit to 5 tags as requested
</script>

<template>
  <div class="tags-card">
    <div class="card-header">
      <h3 class="card-title">
        <HotTagIcon class="icon" />
        <span>热门标签</span>
      </h3>
    </div>
    
    <div class="card-content">
      <div class="tags-list">
        <a v-for="tag in tags" :key="tag.name" href="#" class="tag-item">
          <span class="tag-name">{{ tag.name }}</span>
          <span class="tag-count">{{ tag.count }}</span>
        </a>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.tags-card {
  display: flex;
  flex-direction: column;
  background: $color-bg-secondary; 
  border: 1px solid rgba($color-border, 0.5);
  border-radius: 12px;
  box-shadow: $shadow-sm;
  overflow: hidden;
  flex-shrink: 0;
}

.card-header {
  padding: 0.8rem 1rem;
  border-bottom: 1px solid rgba($color-border, 0.3);
  background: rgba($color-bg-secondary, 0.5);
  flex-shrink: 0;

  .card-title {
    font-size: 1rem;
    font-weight: 700;
    margin: 0;
    color: $color-text-primary;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    letter-spacing: 0.05em;
    
    .icon {
      width: 18px;
      height: 18px;
      color: $color-accent-primary;
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
