<script setup lang="ts">
import { ref } from 'vue';

interface Series {
  id: string;
  title: string;
  articleCount: number;
}

const seriesList = ref<Series[]>([
  { id: '1', title: 'Java Core', articleCount: 12 },
  { id: '2', title: 'Spring Boot in Action', articleCount: 8 },
  { id: '3', title: 'Microservices Patterns', articleCount: 15 },
  { id: '4', title: 'Vue.js 3 Deep Dive', articleCount: 10 },
]);
</script>

<template>
  <div class="series-card">
    <div class="card-header">
      <h3 class="card-title">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon"><path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20"></path><path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z"></path></svg>
        <span>专栏系列</span>
      </h3>
    </div>
    
    <div class="card-content">
      <div class="series-list">
        <a v-for="item in seriesList" :key="item.id" href="#" class="series-item">
          <span class="series-title">{{ item.title }}</span>
          <span class="series-count">{{ item.articleCount }} 篇</span>
        </a>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.series-card {
  display: flex;
  flex-direction: column;
  background: $color-bg-secondary;
  border: 1px solid rgba($color-border, 0.5);
  border-radius: 12px;
  backdrop-filter: blur(12px);
  box-shadow: $shadow-sm;
  overflow: hidden;
  flex-shrink: 0;
}

.card-header {
  padding: 1rem 1.2rem;
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
      color: $color-accent-primary;
    }
  }
}

.card-content {
  padding: 0.5rem 0;
}

.series-list {
  display: flex;
  flex-direction: column;
}

.series-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.6rem 1.2rem;
  color: $color-text-secondary;
  text-decoration: none;
  font-size: 0.9rem;
  transition: all 0.3s ease;
  position: relative;
  
  // Hover Animation
  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 1px;
    background: linear-gradient(90deg, $color-accent-primary, transparent);
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
    
    .series-count {
      color: $color-accent-primary;
      background: rgba($color-accent-primary, 0.1);
    }
  }
  
  .series-title {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 70%;
  }
  
  .series-count {
    font-size: 0.75rem;
    background: rgba($color-text-secondary, 0.1);
    padding: 2px 8px;
    border-radius: 10px;
    transition: all 0.3s ease;
  }
}
</style>
