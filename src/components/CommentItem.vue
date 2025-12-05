<script lang="ts">
export interface Comment {
  id: number;
  user: string;
  userId?: string; // Added to store raw user ID
  avatar?: string;
  date: string;
  content: string;
  likes: number;
  dislikes: number;
  userVote: 'like' | 'dislike' | null;
  replies: Comment[];
  replyCount?: number;
}
</script>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import IconUser from './icons/IconUser.vue';

const props = withDefaults(defineProps<{
  comment: Comment;
  depth?: number;
  parentUser?: string;
  suppressReplies?: boolean;
  loadingReplies?: boolean;
}>(), {
  depth: 0,
  parentUser: '',
  suppressReplies: false,
  loadingReplies: false
});

const emit = defineEmits<{
  (e: 'reply', commentId: number, content: string): void;
  (e: 'vote', commentId: number, type: 'like' | 'dislike'): void;
  (e: 'fetch-replies', commentId: number): void;
}>();

const { t, d } = useI18n();

const isReplying = ref(false);
const replyContent = ref('');
const isMobile = ref(false);
const replyInput = ref<HTMLTextAreaElement | null>(null);
const showReplies = ref(false);
const isAnimating = ref(false);
const collapsibleRef = ref<HTMLElement | null>(null);
const isContentHidden = ref(props.comment.userVote === 'dislike');
const displayedRepliesCount = ref(0);

type FlatReply = { comment: Comment; depth: number; parentUser: string };

const flattenReplies = (list: Comment[], parentUser: string, depth: number): FlatReply[] => {
  const out: FlatReply[] = [];
  for (const c of list || []) {
    out.push({ comment: c, depth, parentUser });
    if (c.replies && c.replies.length) {
      out.push(...flattenReplies(c.replies, c.user, depth + 1));
    }
  }
  return out;
};

const flattenedReplies = computed<FlatReply[]>(() => {
  if (!props.comment.replies) return [];
  // 仅顶层评论计算并展示所有层级的回复，子项不再继续有自己的回复区
  if (props.depth === 0 && !props.suppressReplies) {
    return flattenReplies(props.comment.replies, props.comment.user, 1);
  }
  return [];
});

const displayedFlatReplies = computed<FlatReply[]>(() => {
  const all = flattenedReplies.value;
  if (showReplies.value) return all;
  return all.slice(0, displayedRepliesCount.value);
});

const hasMoreReplies = computed(() => {
  if (!props.comment.replies || props.comment.replies.length === 0) {
     return (props.comment.replyCount || 0) > 0;
  }
  return flattenedReplies.value.length > displayedRepliesCount.value;
});

const toggleShowReplies = async () => {
  if (isAnimating.value) return;
  
  if (!showReplies.value && (!props.comment.replies || props.comment.replies.length === 0) && (props.comment.replyCount || 0) > 0) {
      emit('fetch-replies', props.comment.id);
  }

  const el = collapsibleRef.value;
  if (!el) {
    showReplies.value = !showReplies.value;
    return;
  }

  const reduce = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  isAnimating.value = true;
  el.classList.add('is-animating');

  const onEnd = () => {
    el.removeEventListener('transitionend', onEnd);
    el.classList.remove('is-animating');
    isAnimating.value = false;
    if (showReplies.value) {
      el.style.maxHeight = 'none';
      el.style.opacity = '1';
    } else {
      el.style.maxHeight = '';
      el.style.opacity = '';
    }
  };

  if (!showReplies.value) {
    showReplies.value = true;
    await nextTick();
    const h = el.scrollHeight;
    if (reduce) {
      el.style.maxHeight = 'none';
      el.style.opacity = '1';
      onEnd();
      return;
    }
    el.style.maxHeight = '0px';
    el.style.opacity = '0';
    void el.offsetHeight;
    requestAnimationFrame(() => {
      el.style.maxHeight = h + 'px';
      el.style.opacity = '1';
    });
  } else {
    const h = el.scrollHeight;
    if (reduce) {
      showReplies.value = false;
      onEnd();
      return;
    }
    el.style.maxHeight = h + 'px';
    el.style.opacity = '1';
    requestAnimationFrame(() => {
      el.style.maxHeight = '0px';
      el.style.opacity = '0';
    });
    el.addEventListener('transitionend', () => {
      showReplies.value = false;
    }, { once: true });
  }

  el.addEventListener('transitionend', onEnd);
};

// Watch for replies loading to update height if expanded
watch(() => props.comment.replies, async () => {
  if (showReplies.value && collapsibleRef.value) {
    await nextTick();
    // Recalculate height if needed, but since max-height is 'none' when open, it should adjust automatically.
    // However, if we were in loading state, we might need to ensure it stays open correctly.
  }
}, { deep: true });


// Long press handling
let longPressTimer: number | undefined;
const isLongPressMenuOpen = ref(false);
const menuPosition = ref({ x: 0, y: 0 });

// Load hidden state from localStorage
onMounted(() => {
  checkMobile();
  window.addEventListener('resize', checkMobile);
  
  const hiddenComments = JSON.parse(localStorage.getItem('hidden_comments') || '[]');
  if (hiddenComments.includes(props.comment.id)) {
    isContentHidden.value = true;
  }
});

const saveHiddenState = () => {
  const hiddenComments = JSON.parse(localStorage.getItem('hidden_comments') || '[]');
  if (!hiddenComments.includes(props.comment.id)) {
    hiddenComments.push(props.comment.id);
    localStorage.setItem('hidden_comments', JSON.stringify(hiddenComments));
  }
};

const startLongPress = (e: TouchEvent | MouseEvent) => {
  if (isContentHidden.value) return;
  longPressTimer = window.setTimeout(() => {
    isLongPressMenuOpen.value = true;
    const touch = e instanceof TouchEvent ? e.touches[0] : e;
    menuPosition.value = { x: touch.clientX, y: touch.clientY };
  }, 500);
};

const cancelLongPress = () => {
  if (longPressTimer) {
    clearTimeout(longPressTimer);
    longPressTimer = undefined;
  }
};

const handleMenuAction = (action: string) => {
  isLongPressMenuOpen.value = false;
  // Implement actions
  console.log('Action:', action);
};

const checkMobile = () => {
  isMobile.value = window.innerWidth <= 768;
};

onMounted(() => {
  // Removed duplicate checkMobile logic as it's handled above
});

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile);
});

const isLiked = computed(() => props.comment.userVote === 'like');
const isDisliked = computed(() => props.comment.userVote === 'dislike');

const handleVote = (type: 'like' | 'dislike') => {
  if (type === 'dislike' && props.comment.userVote !== 'dislike') {
    // Add fade out effect before hiding
    const el = document.getElementById(`comment-${props.comment.id}`);
    if (el) el.style.opacity = '0';
    
    setTimeout(() => {
      isContentHidden.value = true;
      saveHiddenState();
      showReplies.value = false; // Auto collapse replies
      if (el) el.style.opacity = '1'; // Reset for when it's shown again
    }, 300);
  }
  emit('vote', props.comment.id, type);
};

const toggleReply = () => {
  isReplying.value = !isReplying.value;
  if (isReplying.value) {
    // Focus logic with slight delay for smooth transition
    setTimeout(() => {
      replyInput.value?.focus();
    }, 100);
  }
};

const handleMobileReply = () => {
  if (!isMobile.value) return;
  
  // Add delay as requested (200-300ms)
  // Visual feedback is handled by CSS :active
  setTimeout(() => {
    if (!isReplying.value) {
      isReplying.value = true;
      // Ensure UI stabilizes before focus
      setTimeout(() => {
        replyInput.value?.focus();
        // Scroll into view if needed
        replyInput.value?.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }, 300);
    }
  }, 250);
};

const submitReply = () => {
  if (!replyContent.value.trim()) return;
  emit('reply', props.comment.id, replyContent.value);
  replyContent.value = '';
  isReplying.value = false;
};

// Helper function to safely parse dates
const parseDate = (dateStr: string): Date => {
  if (!dateStr) return new Date();
  // If it's already a standard ISO string or compatible, Date constructor works
  const d = new Date(dateStr);
  if (!isNaN(d.getTime())) return d;
  
  // Fallback: If backend returns 'YYYY-MM-DD HH:mm:ss', replace space with T for standard ISO parsing in some browsers (like Safari)
  // Example: '2025-12-04 22:44:11' -> '2025-12-04T22:44:11'
  const isoStr = dateStr.replace(' ', 'T');
  const d2 = new Date(isoStr);
  if (!isNaN(d2.getTime())) return d2;
  
  return new Date(); // Valid fallback to avoid crash
};

// Recursive handlers
const handleNestedReply = (id: number, content: string) => {
  emit('reply', id, content);
};

const handleNestedVote = (id: number, type: 'like' | 'dislike') => {
  emit('vote', id, type);
};
</script>

<template>
  <div class="comment-item" :class="{ 'is-nested': depth > 0 }" :id="`comment-${comment.id}`">
    <div class="comment-row">
      <!-- Avatar -->
      <div class="avatar-section" @click.stop>
        <div class="avatar">
          <img v-if="comment.avatar" :src="comment.avatar" :alt="comment.user" />
          <IconUser v-else :size="24" />
        </div>
      </div>

      <!-- Content Section -->
      <div class="content-section">
        <div 
          class="comment-content-wrapper"
          @touchstart="startLongPress"
          @touchend="cancelLongPress"
          @mousedown="startLongPress"
          @mouseup="cancelLongPress"
          @mouseleave="cancelLongPress"
          @click="handleMobileReply"
        >
          <div v-if="isContentHidden" class="comment-hidden">
             <span class="hidden-text">{{ t('common.commentHidden', 'Comment hidden') }}</span>
             <button class="show-btn" @click.stop="isContentHidden = false">{{ t('common.show', 'Show') }}</button>
          </div>

          <template v-else>
            <div class="username">
              <router-link class="user-link" :to="{ name: 'profile', query: { user: comment.user } }">{{ comment.user }}</router-link>
              <span v-if="depth >= 2" class="name-arrow" aria-hidden="true">
                <svg class="chevron-right" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" fill="none">
                  <path d="M10 7l5 5-5 5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" />
                </svg>
              </span>
              <router-link v-if="depth >= 2" class="user-link reply-to" :to="{ name: 'profile', query: { user: parentUser } }">{{ parentUser }}</router-link>
            </div>
            <p class="text">{{ comment.content }}</p>
            <div class="footer-meta">
              <span class="date">{{ d(parseDate(comment.date), 'short') }}</span>
              <button class="reply-trigger" @click.stop="toggleReply">{{ t('common.reply', 'Reply') }}</button>
              
              <!-- Inline Actions -->
              <div class="inline-actions">
                <button 
                  class="action-btn like-btn" 
                  :class="{ active: isLiked }" 
                  @click.stop="handleVote('like')"
                >
                  <svg class="icon heart-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z" stroke="currentColor" stroke-width="2" />
                  </svg>
                  <span class="count" v-if="comment.likes > 0">{{ comment.likes }}</span>
                </button>
                
                <button 
                  class="action-btn dislike-btn" 
                  :class="{ active: isDisliked }" 
                  @click.stop="handleVote('dislike')"
                >
                  <svg class="icon dislike-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M17 14V2M9 18.12L10 14H4.17C3.07 14 2.17 13.11 2.17 12v-8c0-1.1.9-2 2-2H20c1.1 0 2 .9 2 2v8c0 1.1-.9 2-2 2h-2.76c-.55 0-1.08.22-1.46.61L12 22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                </button>
              </div>
            </div>
          </template>
        </div>

        <!-- Reply Form -->
        <transition name="reply-expand">
          <div v-if="isReplying && !isContentHidden" class="reply-form-wrapper">
            <div class="reply-form" @click.stop>
              <textarea 
                v-model="replyContent" 
                placeholder="Add a reply..." 
                rows="2"
                ref="replyInput"
              ></textarea>
              <div class="form-actions">
                <button class="cancel-btn" @click="isReplying = false">Cancel</button>
                <button class="submit-btn" @click="submitReply" :disabled="!replyContent.trim()">Post</button>
              </div>
            </div>
          </div>
        </transition>

        <!-- Replies Area -->
        <div v-if="!suppressReplies && hasMoreReplies && !isContentHidden" class="replies-area">
          <!-- Collapsible Replies Container -->
          <div
            :id="`replies-${comment.id}`"
            ref="collapsibleRef"
            class="replies-collapsible"
            :aria-busy="isAnimating ? 'true' : 'false'"
          >
            <div class="replies-list">
              <div v-if="loadingReplies" class="loading-replies" style="padding: 10px; text-align: center; color: var(--color-text-secondary);">
                 Loading replies...
              </div>
              <CommentItem 
                v-else
                v-for="fr in displayedFlatReplies" 
                :key="fr.comment.id" 
                :comment="fr.comment" 
                :depth="fr.depth"
                :parentUser="fr.parentUser"
                :suppressReplies="true"
                @vote="handleNestedVote"
                @reply="handleNestedReply"
              />
            </div>
          </div>
          
          <!-- Expand Button -->
          <div class="expand-action">
            <button
              class="expand-btn"
              @click="toggleShowReplies"
              :aria-expanded="showReplies ? 'true' : 'false'"
              :aria-controls="`replies-${comment.id}`"
              :disabled="isAnimating || loadingReplies"
              :aria-disabled="(isAnimating || loadingReplies) ? 'true' : 'false'"
            >
              <span class="line"></span>
              <span class="text">
                <span v-if="loadingReplies">Loading...</span>
                <span v-else>
                  {{ showReplies ? t('common.collapse') : t('common.viewReplies', { count: comment.replyCount || flattenedReplies.length }) }}
                </span>
              </span>
              <span class="icon" aria-hidden="true">
                <svg v-if="!showReplies" class="chevron-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" fill="none">
                  <path d="M6 9l6 6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <svg v-else class="chevron-icon" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" fill="none">
                  <path d="M6 15l6-6 6 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </span>
            </button>
          </div>
        </div>
      </div>

      <!-- Long Press Menu Overlay -->
      <teleport to="body" v-if="isLongPressMenuOpen">
        <div class="context-menu-overlay" @click="isLongPressMenuOpen = false">
          <div 
            class="context-menu" 
            :style="{ top: `${menuPosition.y}px`, left: `${menuPosition.x}px` }"
            @click.stop
          >
            <button @click="handleMenuAction('copy')">Copy</button>
            <button @click="handleMenuAction('report')" class="danger">Report</button>
          </div>
        </div>
      </teleport>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.comment-item {
  margin-bottom: 16px;
  
  &.is-nested {
    margin-bottom: 12px;
    
    .avatar {
      width: 24px;
      height: 24px;
    }
    
    .username {
      font-size: 0.85rem;
    }
  }
}

.comment-row {
  display: flex;
  gap: 8px; // Reduced gap from 16px to 8px for tighter layout
  position: relative;
}

.avatar-section {
  flex-shrink: 0;
  
  .avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    overflow: hidden;
    
    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }
}

.content-section {
  flex-grow: 1;
  min-width: 0;
  // Removed padding-right to ensure replies-area extends to full width
}

.comment-content-wrapper {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  padding: 2px 0; // Minimal padding: 2px top/bottom, 0 left/right (migrated from mobile)
  margin: 2px 0; // Minimal margin (migrated from mobile)
  transition: all 0.3s ease;
  
  &:hover {
    background: rgba(255, 255, 255, 0.05);
    border-color: rgba(255, 255, 255, 0.1);
  }

  // Mobile active state
  @media (max-width: 768px) {
    cursor: pointer;
    -webkit-tap-highlight-color: transparent;

    &:active {
      background: rgba(255, 255, 255, 0.08);
      transform: scale(0.995);
    }
  }
}
.username {
  display: flex; // Change from inline-flex to flex to better control alignment
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: $color-text-secondary;
  margin-bottom: 4px;
  line-height: 1; // Tighten line-height to match avatar center
  min-height: 24px; // Ensure it has height to align with avatar, using min-height for robustness
  
  .user-link {
    color: $color-text-secondary;
    text-decoration: none;
    
    &:hover,
    &:focus {
      color: $color-text-primary;
      text-decoration: underline;
      outline: none;
    }
  }
  
  .name-arrow {
    display: inline-flex;
    align-items: center;
    line-height: 1;
    
    .chevron-right {
      width: 12px;
      height: 12px;
      display: block;
    }
  }
}

.text {
  font-size: 15px;
  color: $color-text-primary;
  line-height: 1.5;
  margin-bottom: 6px;
  word-break: break-word;
}

.footer-meta {
  display: flex;
  align-items: baseline; // Changed from center to baseline for better text alignment
  gap: 8px; // Requirement: 8px spacing between Date and Reply
  font-size: 12px;
  color: $color-text-secondary;
  position: relative; // For context
  min-height: 24px;
  
  .reply-trigger {
    background: none;
    border: none;
    padding: 0;
    color: $color-text-secondary;
    font-weight: 600;
    cursor: pointer;
    font-size: inherit;
    margin-left: 0; // Reset auto margin, place next to date
    transition: color 0.2s;
    
    &:hover {
      color: $color-text-primary;
    }
  }

  .inline-actions {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-left: auto; // Push to right edge

    .action-btn {
      background: transparent;
      border: none;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 4px;
      padding: 0;
      cursor: pointer;
      color: $color-text-secondary;
      height: 24px;
      min-width: 24px;
      transition: all 0.2s;
      position: relative; // For pseudo-element expansion
      // Ensure vertical alignment with text
      align-self: center; 

      // Expand click area via pseudo-element
      &::after {
        content: '';
        position: absolute;
        top: -8px;
        left: -8px;
        right: -8px;
        bottom: -8px;
      }

      .icon {
        width: 16px;
        height: 16px;
        transition: transform 0.2s;
        flex-shrink: 0; // Prevent icon shrinking
      }

      .count {
        font-size: 12px;
        line-height: 1;
        // Ensure count doesn't shift layout in a way that misaligns icons
      }

      &.like-btn {
        flex-direction: row-reverse; // Move count to left so icon stays fixed on right
        gap: 6px;
      }

      &.active {
        color: #fe2c55;
        
        .heart-icon {
          fill: #fe2c55;
          stroke: none;
          animation: heart-bounce 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
        }
        
        .dislike-icon {
          fill: currentColor;
          stroke: none;
        }
      }

      &.dislike-btn.active {
        color: $color-text-primary;
      }

      &:hover {
        opacity: 0.8;
      }
      
      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }
  }
}

.like-section {
  display: none;
}

@keyframes heart-bounce {
  0% { transform: scale(1); }
  50% { transform: scale(1.3); }
  100% { transform: scale(1); }
}

.replies-area {
  margin-top: 12px;
  // Removed padding and margin to fix indentation alignment
  border-left: 2px solid rgba(255, 255, 255, 0.05);
  contain: layout style;
  
  .replies-collapsible {
    max-height: 0;
    opacity: 0;
    overflow: hidden;
    transition: max-height 360ms ease-in-out, opacity 320ms ease-in-out;
    will-change: max-height, opacity;
    
    &.is-animating {
      pointer-events: none;
    }
  }
  
  .expand-action {
    margin-top: 8px;
    
    .expand-btn {
      background: none;
      border: none;
      display: flex;
      align-items: center; // Center vertically
      color: $color-text-secondary;
      font-weight: 600;
      cursor: pointer;
      line-height: 1;
      
      // Migrated mobile styles to default (desktop)
      font-size: 11px; 
      padding: 0; 
      gap: 4px;
      height: 24px; // Explicit height to contain text and icon comfortably

      .line {
        height: 1px;
        background: currentColor;
        opacity: 0.5;
        margin-top: 0; 
        
        // Migrated mobile style
        width: 12px;
        display: block; // Ensure it behaves as a block for height/width
      }
      
      .text {
        display: flex; // Use flex to ensure vertical centering works perfectly
        align-items: center;
        height: 100%; // Fill container height
        font-size: inherit;
        line-height: 1;
        padding: 0;
        margin: 0;
        // Mobile style migration: ensure text is small
        font-size: 11px;
        position: static; // Remove manual offset
        top: auto;
        
        // Visual fix: Use negative margin to pull text up slightly
         margin-top: -1px; 
       }
      
      .icon {
        display: flex;
        align-items: center;
        line-height: 1;
        height: 100%; // Fill container height
        
        .chevron-icon {
          width: 12px;
          height: 12px;
          display: block;
        }
      }
    }
    
    // Mobile adjustments (mostly redundant now but kept for safety/specific overrides if needed)
    @media (max-width: 768px) {
      margin-top: 4px;
      
      .expand-btn {
        // Styles already synced to default above
      }
    }
  }
}

@media (prefers-reduced-motion: reduce) {
  .replies-collapsible {
    transition: none !important;
  }
}

/* Reply Form Animation */
.reply-expand-enter-active,
.reply-expand-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1); // Smooth easing
  overflow: hidden;
  max-height: 300px; // Approximate max height
  opacity: 1;
  will-change: max-height, opacity, transform;
}

.reply-expand-enter-from,
.reply-expand-leave-to {
  max-height: 0;
  opacity: 0;
  transform: translateY(-10px); // Slight slide down effect
}

.reply-form-wrapper {
  transform-origin: top;
}

.reply-form {
  margin-top: 12px;
  margin-bottom: 12px;
  // Glassmorphism container
  background: rgba(255, 255, 255, 0.03);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);

  :global(.dark) & {
    background: rgba(0, 0, 0, 0.2);
    border-color: rgba(255, 255, 255, 0.05);
  }
  
  textarea {
    width: 100%;
    max-width: 100%; // Ensure constraint
    box-sizing: border-box;
    // Glassy Input
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid rgba(0,0,0,0.05); // Subtle border
    border-radius: 12px; // Softer corners
    padding: 12px;
    color: $color-text-primary;
    font-size: 14px;
    line-height: 1.5;
    resize: none; // Prevent manual resize breaking layout
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    
    :global(.dark) & {
      background: rgba(0, 0, 0, 0.2);
      border-color: rgba(255, 255, 255, 0.05);
    }

    &:focus {
      outline: none;
      background: rgba(255, 255, 255, 0.1);
      border-color: rgba(var(--color-accent-primary-rgb), 0.3);
      box-shadow: 0 4px 12px rgba(0,0,0,0.05);
      transform: translateY(-1px);
    }

    &::placeholder {
      color: rgba($color-text-secondary, 0.6);
    }
  }
  
  .form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px; // Increased gap
    margin-top: 12px;
    
    button {
      padding: 8px 20px; // Larger touch target
      border-radius: 20px; // Match textarea radius
      font-size: 13px;
      font-weight: 600;
      cursor: pointer;
      border: none;
      transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
      position: relative;
      overflow: hidden; // For ripple effect if added later, basically ensures neatness

      &:active {
        transform: scale(0.96); // Tactile feedback
      }
    }
    
    .cancel-btn {
      background: rgba(255, 255, 255, 0.05);
      color: $color-text-secondary;
      border: 1px solid transparent;

      &:hover {
        background: rgba(255, 255, 255, 0.1);
        color: $color-text-primary;
      }
    }
    
    .submit-btn {
      // Premium Dark/Light Button - NO BLUE
      background: #1a1a1a; // Premium Dark Grey for Light Mode
      color: #ffffff;
      border: 1px solid rgba(255,255,255,0.1);
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15); // Elegant shadow
      
      :global(.dark) & {
        background: #ffffff; // White for Dark Mode
        color: #000000;
        box-shadow: 0 4px 20px rgba(255, 255, 255, 0.15); // White glow
      }
      
      &:hover:not(:disabled) {
        transform: translateY(-2px) scale(1.02);
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.25);
        
        :global(.dark) & {
           box-shadow: 0 8px 30px rgba(255, 255, 255, 0.25);
        }
      }

      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
        box-shadow: none;
        background: #888; // Neutral grey
      }
    }
  }

  // Mobile Optimization
  @media (max-width: 768px) {
    textarea {
      font-size: 16px; // Prevent zoom on iOS
      padding: 12px;
    }

    .form-actions {
      button {
        padding: 10px 20px; // Min 44px height approximation
        min-height: 44px; // Ensure touch target size
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }
  }
}

.comment-hidden {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 8px;
  padding: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: $color-text-secondary;
  
  .show-btn {
    background: none;
    border: none;
    color: $color-accent-primary;
    font-weight: 600;
    cursor: pointer;
  }
}

/* Context Menu */
.context-menu-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 9999;
  background: rgba(0, 0, 0, 0.2); // Slight dim
}

.context-menu {
  position: absolute;
  background: #252525; // Dark background
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  padding: 4px;
  display: flex;
  flex-direction: column;
  min-width: 120px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  
  button {
    background: none;
    border: none;
    color: #fff;
    padding: 10px 16px;
    text-align: left;
    font-size: 14px;
    cursor: pointer;
    border-radius: 4px;
    
    &:hover {
      background: rgba(255, 255, 255, 0.1);
    }
    
    &.danger {
      color: #fe2c55;
    }
  }
}

/* RTL Support */
:global([dir="rtl"]) {
  .replies-area {
    border-left: none;
    border-right: 2px solid rgba(255, 255, 255, 0.05);
  }
}

@media (max-width: 768px) {
  .comment-item {
    margin-bottom: 12px; // Reduced spacing on mobile
  }

  .footer-meta {
    .reply-trigger {
      display: none; // Hide reply button on mobile
    }

    .inline-actions {
      margin-top: 4px; // Add a little breathing room for touch
      gap: 2px; // Reduced gap for mobile

      .action-btn {
        min-width: auto; // Reset
        width: auto;
        height: auto;
        padding: 8px; // Reduced padding but enough for touch target
        position: relative;
        
        // Use pseudo-element to guarantee 44x44pt (approx 58px or 44px depending on density, sticking to 44px min)
        &::after {
          content: '';
          position: absolute;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
          min-width: 44px;
          min-height: 44px;
          width: 100%;
          height: 100%;
          display: block; // Re-enable pseudo-element for touch target
        }
      }
    }
  }
}
</style>
