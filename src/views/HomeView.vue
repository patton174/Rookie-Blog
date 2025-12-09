<script setup lang="ts">
import { onMounted, ref, computed, watch, nextTick } from 'vue';
import { useI18n } from 'vue-i18n';
import HeroSection from '../components/HeroSection.vue';
import ArticleCard from '../components/ArticleCard.vue';
import Sidebar from '../components/Sidebar.vue';
import { useAppStore } from '../store/app';
import { getPublishedArticles } from '../api/article';

const { t } = useI18n();
const { isLoading, stopLoading } = useAppStore();
const isFetching = ref(true);

const showContent = computed(() => !isLoading.value && !isFetching.value);

const handleTitleReady = () => {
  // Title is ready (DOM rendered), now we can stop the global loader
  // We use a small delay to ensure the DOM paint is fully committed
  setTimeout(() => {
    stopLoading();
  }, 50);
};

const featuredArticle = ref({
  id: '1',
  title: t('home.loadingTitle'),
  summary: t('home.loadingSummary'),
  date: '2023-10-15',
  tags: ['Vue', 'Frontend'],
  image: ''
});

const articles = ref<any[]>([]);

const observeElements = () => {
  // Disconnect previous observer if exists? 
  // In Vue setup, we can just create a new one or clean up in onUnmounted.
  // But here we are just fixing the re-entry issue.
  
  const observer = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible');
      }
    });
  }, { threshold: 0.1, rootMargin: "0px 0px -50px 0px" });

  // Delay slightly to ensure DOM is ready (especially when coming back from another route)
  setTimeout(() => {
    const elements = document.querySelectorAll('.fade-in-up');
    if (elements.length > 0) {
      elements.forEach((el) => {
        observer.observe(el);
        // If element is already in viewport (e.g. top of page), manually trigger or ensure observer catches it
        // IntersectionObserver should catch it, but sometimes initial state needs help?
        // Actually, the issue might be that elements are v-show="!isLoading"
        // If isLoading is true initially, elements are display:none, so no intersection.
        // We need to observe AFTER loading is done.
      });
    }
  }, 100);
};

onMounted(async () => {
  // Start API call
  try {
    const res = await getPublishedArticles();
    if (res.isSuccess && res.data) {
      const fetchedArticles = res.data;
      
      // Handle featured article
      const featured = fetchedArticles.find(a => a.isTop === 1) || fetchedArticles[0];
      
      if (featured) {
        featuredArticle.value = {
          id: featured.slug || featured.id, 
          title: featured.title,
          summary: featured.summary,
          date: featured.publishAt || featured.createdAt,
          tags: [], 
          image: featured.coverUrl
        };
      }

      // Map remaining articles
      articles.value = fetchedArticles
        .filter(a => a.id !== featured?.id)
        .map(a => ({
          id: a.slug || a.id,
          title: a.title,
          summary: a.summary,
          date: a.publishAt || a.createdAt,
          tags: [], 
          image: a.coverUrl
        }));
    }
  } catch (error) {
    console.error('Failed to fetch articles:', error);
  } finally {
    isFetching.value = false;
  }
});

watch(showContent, (val) => {
  if (val) {
    nextTick(() => {
      observeElements();
    });
  }
});
</script>

<template>
  <div>
    <!-- Hero Section is always rendered but lightweight -->
    <HeroSection @title-ready="handleTitleReady" />
    
    <!-- Main Content -->
    <div class="container content-layout">
      <div class="main-content">
        <!-- Featured Section -->
        <section class="featured-section fade-in-up">
          <h2 class="section-title">
            <span class="text-gradient">{{ t('home.featured') }}</span>
          </h2>
          <ArticleCard v-if="showContent" v-bind="featuredArticle" class="featured-card" />
          <div v-else class="skeleton-card featured-skeleton"></div>
        </section>

        <!-- Latest Articles -->
        <section class="articles-section">
          <h2 class="section-title fade-in-up">{{ t('common.latestArticles') }}</h2>
          <div class="articles-grid" v-if="showContent">
            <div v-for="article in articles" :key="article.id" class="fade-in-up">
              <ArticleCard v-bind="article" />
            </div>
          </div>
          <!-- Skeleton Grid -->
          <div class="articles-grid" v-else>
             <div v-for="i in 6" :key="i" class="skeleton-card"></div>
          </div>
        </section>
      </div>
      
      <aside class="sidebar-section fade-in-up">
        <Sidebar />
      </aside>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

// Skeleton Styles
.skeleton-card {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 16px;
  width: 100%;
  height: 480px; // Match ArticleCard min-height
  animation: pulse 1.5s infinite ease-in-out;
}

.featured-skeleton {
  height: 480px; // Match ArticleCard min-height
}

@keyframes pulse {
  0% { opacity: 0.6; }
  50% { opacity: 0.8; }
  100% { opacity: 0.6; }
}

.content-layout {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: $spacing-xxl;
  margin-top: $spacing-xxl;
  position: relative;

  @media (max-width: $breakpoint-desktop) {
    grid-template-columns: 1fr;
    gap: $spacing-xl;
  }
}

.section-title {
  font-size: 2.5rem;
  margin-bottom: $spacing-lg;
  font-weight: 800;
  letter-spacing: -1px;
  
  @media (max-width: $breakpoint-mobile) {
    font-size: 1.8rem;
  }

  span {
    font-weight: 300;
  }
}

.featured-section {
  margin-bottom: $spacing-xxl;
}

.articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: $spacing-lg;

  @media (max-width: $breakpoint-mobile) {
    grid-template-columns: 1fr;
  }
}

.sidebar-section {
  position: sticky;
  top: 100px;
  height: fit-content;

  @media (max-width: $breakpoint-desktop) {
    position: static;
    order: 1;
  }
}
</style>