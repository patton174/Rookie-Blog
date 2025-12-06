<script setup lang="ts">
import { ref } from 'vue';

interface Article {
  id: string;
  title: string;
  views: number;
}

const recommendedArticles = ref<Article[]>([
  { id: '1', title: 'Understanding Event Loop in JavaScript', views: 1204 },
  { id: '2', title: 'CSS Grid Layout: A Complete Guide', views: 980 },
  { id: '3', title: 'Reactive Programming with RxJS', views: 856 },
  { id: '4', title: 'Building Scalable APIs with Node.js', views: 742 },
  { id: '5', title: 'Mastering TypeScript Generics', views: 650 },
]);
</script>

<template>
  <div class="recommended-card">
    <div class="card-header">
      <h3 class="card-title">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
        <span>推荐阅读</span>
      </h3>
    </div>
    
    <div class="card-content">
      <div class="article-list">
        <a v-for="item in recommendedArticles" :key="item.id" href="#" class="article-item">
          <span class="article-title">{{ item.title }}</span>
          <span class="article-views">{{ item.views }} 阅读</span>
        </a>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.recommended-card {
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

.article-list {
  display: flex;
  flex-direction: column;
}

.article-item {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
  color: $color-text-secondary;
  text-decoration: none;
  font-size: 0.9rem;
  position: relative;
  transition: all 0.3s ease;
  padding: 0.8rem 1.2rem;
  
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
    
    .article-views {
      color: $color-accent-primary;
    }
  }
  
  .article-title {
    line-height: 1.4;
    font-weight: 500;
  }
  
  .article-views {
    font-size: 0.75rem;
    color: rgba($color-text-secondary, 0.7);
    transition: color 0.3s ease;
  }
}
</style>