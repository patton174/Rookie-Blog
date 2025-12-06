<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue';
import type { ArticleChapter } from '../api/article';

const props = defineProps<{
  chapters: ArticleChapter[];
  isLoading?: boolean;
}>();

const emit = defineEmits<{
  (e: 'click-chapter', chapter: ArticleChapter): void
}>();

const activeId = ref<string>('');
const observer = ref<IntersectionObserver | null>(null);

const handleChapterClick = (chapter: ArticleChapter) => {
  activeId.value = chapter.id;
  
  let element = document.getElementById(chapter.id);
  
  if (!element) {
    const slug = chapter.title.toLowerCase().replace(/\s+/g, '-').replace(/[^\w\u4e00-\u9fa5-]/g, '');
    element = document.getElementById(slug);
  }
  
  if (!element) {
     const headings = document.querySelectorAll('h1, h2, h3, h4, h5, h6');
     for (const h of headings) {
       if (h.textContent?.trim() === chapter.title.trim()) {
         element = h as HTMLElement;
         break;
       }
     }
  }

  if (element) {
    const top = element.getBoundingClientRect().top + window.scrollY - 100; 
    window.scrollTo({ top, behavior: 'smooth' });
  }
  
  emit('click-chapter', chapter);
};

const initObserver = () => {
  if (observer.value) observer.value.disconnect();
  
  observer.value = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        activeId.value = entry.target.id;
      }
    });
  }, {
    rootMargin: '-100px 0px -80% 0px'
  });

  const headings = document.querySelectorAll('h1, h2, h3, h4, h5, h6');
  headings.forEach(h => observer.value?.observe(h));
};

onMounted(() => {
  nextTick(() => {
    setTimeout(initObserver, 1000);
  });
});

watch(() => props.chapters, () => {
  nextTick(() => {
    setTimeout(initObserver, 500);
  });
});

onUnmounted(() => {
  if (observer.value) observer.value.disconnect();
});
</script>

<template>
  <div class="article-toc">
    <div class="toc-header">
      <h3 class="toc-title">目录</h3>
    </div>
    
    <div class="toc-content">
      <div v-if="isLoading" class="toc-loading">
        <div class="skeleton-line"></div>
        <div class="skeleton-line short"></div>
        <div class="skeleton-line"></div>
      </div>
      
      <div v-else-if="chapters.length === 0" class="toc-empty">
        暂无目录
      </div>
      
      <nav v-else class="toc-list">
        <ul>
          <li 
            v-for="chapter in chapters" 
            :key="chapter.id" 
            class="toc-item"
            :class="{ 
              'is-sub': chapter.parentId !== '0' && chapter.parentId !== null 
            }"
          >
            <a 
              href="#" 
              @click.prevent="handleChapterClick(chapter)" 
              class="toc-link"
              :class="{ 'active': activeId === chapter.id }"
            >
              {{ chapter.title }}
            </a>
          </li>
        </ul>
      </nav>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.article-toc {
  display: flex;
  flex-direction: column;
  // max-height: 60vh; // Removed max-height so TOC can expand, scrollbar is now on the sticky-wrapper
  background: $color-bg-secondary; 
  border: 1px solid rgba($color-border, 0.5);
  border-radius: 12px;
  backdrop-filter: blur(12px);
  box-shadow: $shadow-sm;
  overflow: hidden; 
}

.toc-header {
  padding: 0.8rem 1rem;
  border-bottom: 1px solid rgba($color-border, 0.3);
  background: $color-bg-secondary; // Use solid color to avoid Sass/CSS var issues
  border-top-left-radius: 12px;
  border-top-right-radius: 12px;
  
  .toc-title {
    font-size: 1rem;
    font-weight: 700;
    margin: 0;
    color: $color-text-primary;
    display: flex;
    align-items: center;
    letter-spacing: 0.05em;
    
    &::before {
      content: '';
      display: block;
      width: 4px;
      height: 16px;
      background: $color-accent-primary;
      margin-right: 8px;
      border-radius: 2px;
    }
  }
}

.toc-content {
  flex: 1;
  padding: 0.5rem 0;
}

.toc-list {
  ul {
    list-style: none;
    padding: 0;
    margin: 0;
  }
}

.toc-item {
  position: relative;
  margin: 0;
  
  &.is-sub .toc-link {
    padding-left: 2rem;
    font-size: 0.85rem;
    
    &::before {
      content: '';
      position: absolute;
      left: 1rem;
      top: 50%;
      width: 4px;
      height: 4px;
      border-radius: 50%;
      background: rgba($color-text-secondary, 0.3);
      transform: translateY(-50%);
    }
  }
}

.toc-link {
  display: block;
  padding: 0.5rem 1rem;
  color: $color-text-secondary;
  text-decoration: none;
  font-size: 0.9rem;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  position: relative;
  border-left: 2px solid transparent;
  
  // Hover Animation: Left-to-right underline
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
  }
  
  // Active State
  &.active {
    color: $color-accent-primary;
    font-weight: 600;
    background: rgba($color-accent-primary, 0.05);
    border-left-color: $color-accent-primary;
  }
}

.toc-loading {
  padding: 1rem;
  
  .skeleton-line {
    height: 1rem;
    background: rgba($color-text-secondary, 0.1);
    margin-bottom: 0.8rem;
    border-radius: 4px;
    animation: pulse 1.5s infinite;
    
    &.short {
      width: 70%;
    }
  }
}

.toc-empty {
  padding: 1.5rem;
  text-align: center;
  color: $color-text-secondary;
  font-size: 0.9rem;
}

@keyframes pulse {
  0% { opacity: 0.6; }
  50% { opacity: 0.3; }
  100% { opacity: 0.6; }
}
</style>
