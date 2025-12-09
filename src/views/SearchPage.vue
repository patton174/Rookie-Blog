<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import ArticleCard from '../components/ArticleCard.vue';
import IconSearch from '../components/icons/IconSearch.vue';

const { t } = useI18n();
const route = useRoute();
const router = useRouter();

const searchQuery = ref('');
const activeFilter = ref('all');
const filters = ['all', 'article', 'tag', 'author'];

const isLoading = ref(false);

// Pagination
const currentPage = ref(1);
const itemsPerPage = 10;

const searchHistory = ref<string[]>([]);
const hotSearches = ref(['Spring Boot 3', 'Kubernetes', 'Microservices', 'AI Tools', 'Vue 3', 'React', 'Docker']);

// Mock results
const generateMockResults = () => {
  const results = [];
  const topics = ['Vue 3', 'React', 'Angular', 'Spring Boot', 'Java', 'Python', 'Docker', 'Kubernetes', 'AWS', 'Azure'];
  
  for (let i = 1; i <= 35; i++) {
    const topic = topics[i % topics.length];
    results.push({
      id: i,
      title: `${topic} Guide Part ${Math.floor(i / 10) + 1} - Advanced Techniques`,
      summary: `This is a detailed summary for article ${i} about ${topic}. Learn how to master ${topic} with practical examples and best practices.`,
      date: `2025-${10 + (i % 2)}-${(i % 28) + 1}`,
      tags: [topic, 'Tech', 'Programming'],
      image: '',
      views: Math.floor(Math.random() * 5000) + 100
    });
  }
  return results;
};

const mockResults = generateMockResults();

// Computed properties
const filteredResults = computed(() => {
  if (!searchQuery.value) return [];
  
  const query = searchQuery.value.toLowerCase();
  let results = mockResults.filter(item => {
    // Text Search
    const matchTitle = item.title.toLowerCase().includes(query);
    const matchSummary = item.summary.toLowerCase().includes(query);
    const matchTags = item.tags.some(tag => tag.toLowerCase().includes(query));
    
    if (activeFilter.value === 'article') return matchTitle || matchSummary;
    if (activeFilter.value === 'tag') return matchTags;
    // if (activeFilter.value === 'author') return matchAuthor; // Add author if needed
    
    return matchTitle || matchSummary || matchTags;
  });

  return results;
});

const totalPages = computed(() => Math.ceil(filteredResults.value.length / itemsPerPage));

const paginatedResults = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return filteredResults.value.slice(start, end);
});

// Methods
const performSearch = async () => {
  if (!searchQuery.value) return;

  isLoading.value = true;
  
  // Update URL without reloading
  if (route.query.q !== searchQuery.value) {
    router.push({ query: { ...route.query, q: searchQuery.value } });
  }

  // Save to history
  addToHistory(searchQuery.value);
  
  // Reset pagination
  currentPage.value = 1;

  // Simulate API delay
  setTimeout(() => {
    isLoading.value = false;
  }, 800);
};

const addToHistory = (query: string) => {
  const cleanQuery = query.trim();
  if (!cleanQuery) return;

  const index = searchHistory.value.indexOf(cleanQuery);
  if (index > -1) {
    searchHistory.value.splice(index, 1);
  }
  
  searchHistory.value.unshift(cleanQuery);
  if (searchHistory.value.length > 10) {
    searchHistory.value.pop();
  }
  
  localStorage.setItem('searchHistory', JSON.stringify(searchHistory.value));
};

const loadHistory = () => {
  const history = localStorage.getItem('searchHistory');
  if (history) {
    try {
      searchHistory.value = JSON.parse(history);
    } catch (e) {
      console.error('Failed to parse search history', e);
    }
  }
};

const clearHistory = () => {
  searchHistory.value = [];
  localStorage.removeItem('searchHistory');
};

const selectTag = (tag: string) => {
  searchQuery.value = tag;
  performSearch();
};

const changePage = (page: number) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
};

// Watchers and Lifecycle
watch(() => route.query.q, (newQ) => {
  if (newQ && typeof newQ === 'string') {
    searchQuery.value = newQ;
    if (!isLoading.value) {
        isLoading.value = true;
        setTimeout(() => isLoading.value = false, 500);
    }
  } else {
    searchQuery.value = '';
  }
}, { immediate: true });

onMounted(() => {
  loadHistory();
});
</script>

<template>
  <div class="search-page">
    <div class="search-container">
      
      <div class="search-layout">
        
        <!-- Left Sidebar: Filters & Hot Topics -->
        <aside class="search-sidebar">
          
          <!-- Filters Section -->
          <div class="sidebar-widget">
            <h3 class="widget-title">
              {{ t('common.filter', 'Filters') }}
            </h3>
            <div class="vertical-filters">
              <button 
                v-for="filter in filters" 
                :key="filter" 
                class="filter-item"
                :class="{ active: activeFilter === filter }"
                @click="activeFilter = filter"
              >
                <span class="filter-name">{{ filter.charAt(0).toUpperCase() + filter.slice(1) }}</span>
                <span class="filter-indicator" v-if="activeFilter === filter"></span>
              </button>
            </div>
          </div>

          <!-- Hot Searches Section -->
          <div class="sidebar-widget">
            <h3 class="widget-title">
              {{ t('common.hotSearches', 'Trending') }}
            </h3>
            <div class="tags-cloud compact">
              <button 
                v-for="item in hotSearches" 
                :key="item" 
                class="tag hot-tag"
                @click="selectTag(item)"
              >
                {{ item }}
              </button>
            </div>
          </div>

          <!-- History Sidebar (Mini) - Removed as per new layout request -->
          <!--
          <div class="sidebar-widget" v-if="searchHistory.length > 0">
             <div class="widget-header">
                <h3 class="widget-title">
                  <span class="icon">🕒</span> {{ t('common.history', 'History') }}
                </h3>
                <button @click="clearHistory" class="clear-btn-mini" :title="t('common.clear', 'Clear')">×</button>
             </div>
             <div class="history-list-mini">
                <button 
                  v-for="item in searchHistory.slice(0, 5)" 
                  :key="item" 
                  class="history-item-mini"
                  @click="selectTag(item)"
                >
                  {{ item }}
                </button>
             </div>
          </div>
          -->

        </aside>

        <!-- Main Content: Search Bar & Results -->
        <main class="search-main">
          
          <!-- Sticky Search Header -->
          <div class="main-search-header">
             <div class="search-box">
                <IconSearch class="search-icon" :size="20" />
                <input 
                  v-model="searchQuery" 
                  @keydown.enter="performSearch"
                  type="text" 
                  :placeholder="t('common.searchPlaceholder', 'Type to search...')" 
                  class="search-input"
                  autofocus
                />
                <button v-if="searchQuery" class="clear-query-btn" @click="searchQuery = ''">×</button>
                <button class="search-btn" @click="performSearch">
                  {{ t('common.search', 'Search') }}
                </button>
             </div>
             
             <!-- Inline History (Below Search Box) -->
             <div class="inline-history" v-if="!searchQuery && searchHistory.length > 0">
                <span class="history-label">{{ t('common.history', 'History') }}:</span>
                <div class="history-chips">
                  <button 
                    v-for="item in searchHistory.slice(0, 6)" 
                    :key="item" 
                    class="history-chip"
                    @click="selectTag(item)"
                  >
                    {{ item }}
                  </button>
                  <button @click="clearHistory" class="clear-history-text" :title="t('common.clear', 'Clear')">
                    {{ t('common.clear', 'Clear') }}
                  </button>
                </div>
             </div>
          </div>

          <!-- Content Area -->
          <transition name="fade" mode="out-in">
             
             <!-- Loading State -->
             <div v-if="isLoading" class="state-container">
                <div class="spinner"></div>
                <p>Searching...</p>
             </div>

             <!-- Results State -->
             <div v-else-if="searchQuery && paginatedResults.length > 0" class="results-container">
                <div class="results-meta">
                   <span class="count">Found {{ filteredResults.length }} results for "{{ searchQuery }}"</span>
                </div>
                
                <div class="results-grid">
                  <ArticleCard 
                    v-for="result in paginatedResults" 
                    :key="result.id" 
                    v-bind="result" 
                  />
                </div>

                <!-- Pagination -->
                <div class="pagination" v-if="totalPages > 1">
                  <button class="page-btn" :disabled="currentPage === 1" @click="changePage(currentPage - 1)">&lt;</button>
                  <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
                  <button class="page-btn" :disabled="currentPage === totalPages" @click="changePage(currentPage + 1)">&gt;</button>
                </div>
             </div>

             <!-- Empty/No Results State -->
             <div v-else class="state-container empty-state">
                <div v-if="searchQuery" class="no-results">
                   <IconSearch :size="48" class="icon-muted" />
                   <h3>No results found</h3>
                   <p>Try different keywords or filters.</p>
                </div>
                <div v-else class="start-search">
                   <h3>Ready to explore?</h3>
                   <p>Select a topic from the sidebar or type to search.</p>
                </div>
             </div>

          </transition>
        </main>

      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.search-page {
  padding-top: 80px; // Reduced padding
  padding-bottom: $spacing-xxl;
  min-height: 100vh;
  background: var(--color-bg-primary);
}

.search-container {
  max-width: 1400px; // Wide layout
  margin: 0 auto;
  padding: 0 $spacing-lg;

  @media (max-width: $breakpoint-mobile) {
    padding: 0 $spacing-md;
  }
}

.search-layout {
  display: grid;
  grid-template-columns: 260px 1fr; // Sidebar | Content
  gap: $spacing-xl;
  align-items: start;

  @media (max-width: $breakpoint-desktop) {
    grid-template-columns: 1fr;
    gap: $spacing-lg;
  }
}

/* --- Sidebar Styles --- */
.search-sidebar {
  position: sticky;
  top: 90px;
  display: flex;
  flex-direction: column;
  gap: $spacing-lg;

  @media (max-width: $breakpoint-desktop) {
    position: static;
    display: none; // Hide on mobile for now, or move to bottom/drawer (Simplification)
    // Or make it horizontal scroll
  }
}

.sidebar-widget {
    background: var(--color-bg-secondary); // Card style
    border: 1px solid var(--color-border);
    border-radius: 16px;
    padding: 1.25rem;
    
    // High contrast for dark mode
    :global(.dark) &,
    :global([data-theme="dark"]) & {
      background: #111111 !important; // Absolute black/dark gray
      border: 1px solid #444444 !important; // Visible border
      box-shadow: 0 4px 12px rgba(0,0,0,0.8) !important;
    }
    
    .widget-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: $spacing-md;
    }
  
    .widget-title {
      font-size: 0.95rem;
      font-weight: 700;
      color: var(--color-text-tertiary);
      text-transform: uppercase;
      letter-spacing: 0.5px;
      margin-bottom: $spacing-md;
      display: flex;
      align-items: center;
      gap: 8px;
  
      // Enhance title contrast in dark mode
      :global(.dark) &,
      :global([data-theme="dark"]) & {
        color: #ffffff !important; // Pure white
        text-shadow: 0 1px 2px rgba(0,0,0,1) !important;
      }
  
      .icon {
        font-size: 1.1em;
      }
    }
  }
  
  /* Vertical Filters */
  .vertical-filters {
    display: flex;
    flex-direction: column;
    gap: 4px;
  
    .filter-item {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 10px 14px;
      border-radius: 8px;
      background: transparent;
      border: 1px solid transparent; // Reserve space for border
      color: var(--color-text-secondary);
      font-weight: 500;
      cursor: pointer;
      transition: all 0.2s ease;
      text-align: left;
      font-size: 0.95rem;
  
      // Enhance text contrast
      :global(.dark) &,
      :global([data-theme="dark"]) & {
        color: #e0e0e0 !important; // High visibility gray
      }
  
      &:hover {
        background: rgba(0,0,0,0.03);
        color: var(--color-text-primary);
        
        :global(.dark) &,
        :global([data-theme="dark"]) & {
          background: #333333 !important;
          color: #ffffff !important;
          border-color: #777777 !important; // Distinct border
          box-shadow: 0 2px 8px rgba(0,0,0,0.5) !important;
        }
      }
  
      &.active {
        background: rgba($color-accent-primary-rgb, 0.1);
        color: var(--color-accent-primary);
        font-weight: 600;
        
        :global(.dark) &,
        :global([data-theme="dark"]) & {
          background: rgba($color-accent-primary-rgb, 0.3) !important;
          color: #ffffff !important;
          border: 1px solid rgba($color-accent-primary-rgb, 0.8) !important;
          box-shadow: 0 0 15px rgba($color-accent-primary-rgb, 0.5) !important;
        }
  
        .filter-indicator {
          width: 6px;
          height: 6px;
          border-radius: 50%;
          background: currentColor;
          box-shadow: 0 0 8px currentColor;
        }
      }
    }
  }
  
  /* Compact Tags */
  .tags-cloud.compact {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  
    .tag {
      background: var(--color-bg-primary);
      border: 1px solid var(--color-border);
      color: var(--color-text-secondary);
      padding: 4px 10px;
      border-radius: 6px;
      font-size: 0.85rem;
      cursor: pointer;
      transition: all 0.2s;
  
      :global(.dark) &,
      :global([data-theme="dark"]) & {
        background: #222222 !important;
        border-color: #666666 !important;
        color: #eeeeee !important;
      }
  
      &:hover {
        border-color: var(--color-accent-primary);
        color: var(--color-accent-primary);
        transform: translateY(-1px);
        
        :global(.dark) &,
        :global([data-theme="dark"]) & {
          background: #444444 !important;
          color: #ffffff !important;
          border-color: var(--color-accent-primary) !important;
          box-shadow: 0 2px 8px rgba(0,0,0,0.6) !important;
        }
      }
    }
  }

/* Mini History */
.clear-btn-mini {
  background: none;
  border: none;
  color: var(--color-text-tertiary);
  font-size: 1.2rem;
  line-height: 1;
  cursor: pointer;
  padding: 0 4px;
  
  &:hover {
    color: var(--color-status-error);
  }
}

.history-list-mini {
  display: flex;
  flex-direction: column;
  gap: 2px;
  
  .history-item-mini {
    text-align: left;
    background: none;
    border: none;
    padding: 6px 0;
    color: var(--color-text-secondary);
    font-size: 0.9rem;
    cursor: pointer;
    transition: color 0.2s;
    
    &:hover {
      color: var(--color-accent-primary);
      text-decoration: underline;
    }
  }
}

/* --- Main Content Styles --- */
.search-main {
  min-width: 0;
}

.main-search-header {
  margin-bottom: $spacing-xl;
  position: sticky;
  top: 80px; // Below navbar
  z-index: 20;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 12px;
  background: var(--color-bg-secondary);
  border: 1px solid var(--color-border);
  padding: 8px 8px 8px 20px;
  border-radius: 12px; // Slightly squarer than pill
  box-shadow: $shadow-sm;
  transition: all 0.3s ease;

  // Search Box Contrast
  :global(.dark) &,
  :global([data-theme="dark"]) & {
    background: #000000 !important; // Pitch black
    border: 1px solid #666666 !important; // High contrast border
    box-shadow: 0 4px 20px rgba(0,0,0,1) !important;
  }

  &:focus-within {
    border-color: var(--color-accent-primary);
    box-shadow: 0 4px 20px rgba($color-accent-primary-rgb, 0.15);
    transform: translateY(-1px);
    
    :global(.dark) &,
    :global([data-theme="dark"]) & {
      background: #000000 !important;
      border-color: var(--color-accent-primary) !important;
      box-shadow: 0 0 0 1px var(--color-accent-primary), 0 0 20px rgba($color-accent-primary-rgb, 0.5) !important;
    }
  }

  .search-icon {
    color: var(--color-text-tertiary);
    :global(.dark) &,
    :global([data-theme="dark"]) & { 
      color: #cccccc !important; // Very light gray icon
    }
  }

  .search-input {
    flex: 1;
    background: transparent;
    border: none;
    color: var(--color-text-primary);
    font-size: 1.1rem;
    outline: none;
    box-shadow: none; /* Ensure no shadow */
    font-weight: 500;
    
    :global(.dark) &,
    :global([data-theme="dark"]) & { 
      color: #ffffff !important; // Pure white text
    }
    
    &::placeholder {
      color: var(--color-text-tertiary);
      font-weight: 400;
      :global(.dark) &,
      :global([data-theme="dark"]) & { 
        color: #999999 !important; // High contrast placeholder
        opacity: 1 !important;
      }
    }
  }

  .clear-query-btn {
    background: none;
    border: none;
    color: var(--color-text-tertiary);
    font-size: 1.2rem;
    cursor: pointer;
    padding: 0 8px;
    
    &:hover {
      color: var(--color-text-secondary);
      :global(.dark) &,
      :global([data-theme="dark"]) & { 
        color: #ffffff !important; 
      }
    }
  }

  .search-btn {
    background: $color-accent-primary;
    color: #fff;
    border: none;
    padding: 10px 24px;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s;
    
    &:hover {
      filter: brightness(1.1);
      box-shadow: 0 4px 12px rgba($color-accent-primary-rgb, 0.3);
    }
    
    &:active {
      transform: translateY(1px);
    }
  }
}

/* Inline History Styles */
.inline-history {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 12px;
  padding-left: 12px;
  animation: slideDown 0.3s ease;

  .history-label {
    font-size: 0.85rem;
    color: var(--color-text-tertiary);
    font-weight: 500;
    
    :global(.dark) &,
    :global([data-theme="dark"]) & { 
      color: #cccccc !important; // Light gray label
    }
  }

  .history-chips {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    align-items: center;
  }

  .history-chip {
    background: transparent;
    border: 1px solid transparent;
    color: var(--color-text-secondary);
    font-size: 0.85rem;
    padding: 2px 8px;
    border-radius: 4px;
    cursor: pointer;
    transition: all 0.2s;

    :global(.dark) &,
    :global([data-theme="dark"]) & { 
      color: #eeeeee !important; // Nearly white
    }

    &:hover {
      background: rgba(0,0,0,0.05);
      color: var(--color-text-primary);
      
      :global(.dark) &,
      :global([data-theme="dark"]) & {
        background: #333333 !important;
        color: #ffffff !important;
        border: 1px solid #777777 !important;
      }
    }
  }

  .clear-history-text {
    background: none;
    border: none;
    padding: 4px;
    cursor: pointer;
    font-size: 0.9rem;
    opacity: 0.6;
    transition: opacity 0.2s;
    margin-left: 4px;
    color: inherit; // Inherit color

    :global(.dark) &,
    :global([data-theme="dark"]) & { 
      color: #cccccc !important; // Visible trash icon
      opacity: 0.8 !important;
    }

    &:hover {
      opacity: 1;
      :global(.dark) &,
      :global([data-theme="dark"]) & { 
        color: #ff6b6b !important; // Red on hover
        opacity: 1 !important;
      }
    }
  }
}

@keyframes slideDown {
  from { opacity: 0; transform: translateY(-5px); }
  to { opacity: 1; transform: translateY(0); }
}

/* Results Area */
.results-meta {
  margin-bottom: $spacing-lg;
  .count {
    color: var(--color-text-secondary);
    font-size: 0.9rem;
    font-family: $font-family-code;
  }
}

.results-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: $spacing-lg;
  margin-bottom: $spacing-xl;
}

.state-container {
  padding: 4rem 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: var(--color-text-secondary);
  
  .spinner {
    width: 40px;
    height: 40px;
    border: 3px solid rgba($color-accent-primary-rgb, 0.1);
    border-radius: 50%;
    border-top-color: var(--color-accent-primary);
    animation: spin 1s infinite linear;
    margin-bottom: $spacing-md;
  }

  &.empty-state {
    opacity: 0.7;
    
    .icon-muted {
      margin-bottom: $spacing-md;
      opacity: 0.3;
    }

    h3 {
      font-size: 1.25rem;
      color: var(--color-text-primary);
      margin-bottom: $spacing-sm;
    }
  }
}

/* Pagination */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: $spacing-md;
  padding-top: $spacing-lg;
  border-top: 1px solid var(--color-border);
  
  .page-btn {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid var(--color-border);
    background: var(--color-bg-secondary);
    border-radius: 8px;
    color: var(--color-text-primary);
    cursor: pointer;
    transition: all 0.2s;
    
    &:hover:not(:disabled) {
      border-color: var(--color-accent-primary);
      color: var(--color-accent-primary);
    }
    
    &:disabled {
      opacity: 0.3;
      cursor: not-allowed;
    }
  }
  
  .page-info {
    font-family: $font-family-code;
    font-size: 0.9rem;
    color: var(--color-text-secondary);
  }
}

@keyframes spin { to { transform: rotate(360deg); } }
.fade-enter-active, .fade-leave-active { transition: opacity 0.2s ease; }
.fade-enter-from, .fade-leave-to { opacity: 0; }
</style>

<style lang="scss">
/* 
  Global Overrides for Search Page Dark Mode 
  Using non-scoped styles to ensure 100% application and specificity dominance 
*/
:root[class~="dark"] .search-page,
:root[data-theme="dark"] .search-page {
  
  /* Sidebar Widgets - Make them visible against black background */
  .sidebar-widget {
    background-color: #1e1e1e !important;
    border: 1px solid #333333 !important;
    box-shadow: 0 4px 12px rgba(0,0,0,0.5) !important;
  }

  /* Widget Titles - Pure White */
  .widget-title {
    color: #ffffff !important;
    text-shadow: 0 1px 2px rgba(0,0,0,0.8) !important;
    
    .icon {
      filter: drop-shadow(0 0 2px rgba(255,255,255,0.3));
    }
  }

  /* Filter Items - High Contrast */
  .filter-item {
    color: #e0e0e0 !important;
    
    &:hover {
      background-color: #333333 !important;
      color: #ffffff !important;
      border-color: #666666 !important;
    }

    &.active {
      background-color: rgba(99, 102, 241, 0.25) !important;
      color: #ffffff !important;
      border-color: #6366f1 !important;
      box-shadow: 0 0 10px rgba(99, 102, 241, 0.4) !important;
    }
  }

  /* Tags - Visible Pills */
  .tag {
    background-color: #2a2a2a !important;
    border-color: #555555 !important;
    color: #eeeeee !important;

    &:hover {
      background-color: #444444 !important;
      border-color: #818cf8 !important; /* Indigo-400 */
      color: #ffffff !important;
    }
  }

  /* Search Box - Distinct from page bg */
  .search-box {
    background-color: #1e1e1e !important;
    border: 1px solid #444444 !important;
    box-shadow: 0 4px 20px rgba(0,0,0,0.6) !important;

    &:focus-within {
      background-color: #000000 !important;
      border-color: #818cf8 !important; /* Restore border for search box */
      box-shadow: 0 0 0 1px #818cf8, 0 0 20px rgba(129, 140, 248, 0.3) !important; /* Restore glow */
    }

    .search-input {
      color: #ffffff !important;
      border: none !important; /* Ensure input has no border */
      outline: none !important; /* Ensure input has no outline */
      box-shadow: none !important; /* Ensure input has no shadow */
      
      &::placeholder {
        color: #aaaaaa !important;
        opacity: 1 !important;
      }
    }

    .search-icon {
      color: #cccccc !important;
    }

    .clear-query-btn {
      color: #ffffff !important;
      &:hover { color: #ff6b6b !important; }
    }
  }

  /* History Items */
  .inline-history {
    .history-label {
      color: #cccccc !important;
      font-weight: 600 !important;
    }

    .history-chip {
      color: #dddddd !important;
      
      &:hover {
        background-color: #333333 !important;
        color: #ffffff !important;
        border-color: #666666 !important;
      }
    }

    .clear-history-text {
      color: #aaaaaa !important;
      opacity: 0.8 !important;
      
      &:hover {
        color: #ff6b6b !important;
        opacity: 1 !important;
      }
    }
  }

  /* Empty State / Illustration */
  .state-container {
    color: #cccccc !important;
    
    .icon-muted {
      color: #818cf8 !important; /* Premium Indigo */
      opacity: 0.8 !important;
      filter: drop-shadow(0 0 15px rgba(129, 140, 248, 0.4)); /* Premium Glow */
      transform: scale(1.2);
    }

    h3 {
      color: #ffffff !important;
      font-weight: 700 !important;
      letter-spacing: 0.5px;
    }

    p {
      color: #aaaaaa !important;
    }
  }
  
  /* Pagination */
  .pagination {
    border-top-color: #333333 !important;
    
    .page-btn {
      background-color: #1e1e1e !important;
      border-color: #444444 !important;
      color: #eeeeee !important;
      
      &:hover:not(:disabled) {
        border-color: #818cf8 !important;
        color: #818cf8 !important;
      }
      
      &:disabled {
        opacity: 0.3 !important;
      }
    }
    
    .page-info {
      color: #aaaaaa !important;
    }
  }
}
</style>
