<script setup lang="ts">
import { ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import Sidebar from '../components/Sidebar.vue';
import IconUser from '../components/icons/IconUser.vue';
import CommentItem, { type Comment as UIComment } from '../components/CommentItem.vue';
import { 
  getArticleBySlug, 
  getArticleContent, 
  recordView, 
  likeArticle, 
  unlikeArticle,
  checkIsLiked,
  checkIsFavorited,
  getLikeCount,
  favoriteArticle,
  unfavoriteArticle,
  type Article
} from '../api/article';
import { 
  getComments, 
  getReplies, // Add this
  getCommentCount,
  addComment, 
  replyComment, 
  reactToComment,
  type Comment as APIComment,
  type CommentReply as APICommentReply 
} from '../api/comment';
import { getUserPublicProfile } from '../api/user';
import { useUserStore } from '../store/user';
// Removed manual marked import as we use MdPreview
import { MdPreview } from 'md-editor-v3';
import 'md-editor-v3/lib/preview.css';
import { useTheme } from '../composables/useTheme';

const props = defineProps<{
  id: string | number
}>();

const { d } = useI18n();
const userStore = useUserStore();
const { isLoggedIn, user } = userStore; // Destructure for reactive access
const { theme } = useTheme();

// Article Data
const article = ref<Article | null>(null);
const authorName = ref(''); // Reactive state for author name
const articleContent = ref(''); // Stores Markdown
const articleHtml = ref(''); // Stores HTML fallback
const isLoading = ref(false);
const isSubmittingComment = ref(false);
const comments = ref<UIComment[]>([]);
const newComment = ref('');

// Helper to convert API Reply to UI Comment
const mapReplyToUI = (r: APICommentReply): UIComment => ({
  id: r.id,
  user: r.username || r.userId || 'User',
  userId: r.userId,
  avatar: r.avatar || undefined,
  date: r.replyAt,
  content: r.content,
  likes: 0,
  dislikes: 0,
  userVote: null,
  replies: [] // Will be populated if needed, though backend sends flat list
});

// Map API comment to UI comment
const mapComment = (c: APIComment): UIComment => {
  const root: UIComment = {
    id: c.id,
    // Prefer username from API response, fallback to userId, then "User"
    user: c.username || c.userId || 'User',
    userId: c.userId, 
    avatar: c.avatar || undefined,
    // Backend uses commentAt as the primary timestamp
    date: c.commentAt,
    content: c.content,
    likes: 0, // Backend doesn't seem to return likes count in this structure yet
    dislikes: 0,
    userVote: null,
    // Recursively map replies if they exist
    replies: [],
    replyCount: c.replyCount || 0 // Map replyCount
  };
  
  // Flag to indicate if we need to fetch replies
  // We don't rely on this flag anymore for auto-fetching, but we use replyCount for the button.
  // if (c.hasReply === 1 && (!c.replies || c.replies.length === 0)) {
  //   (root as any)._hasReplyFlag = true;
  // }

  // Build Tree from flat replies
  if (c.replies && c.replies.length > 0) {
    const replyMap = new Map<number, UIComment>();
    const replies = c.replies.map(mapReplyToUI);
    
    // Index all replies
    replies.forEach(r => replyMap.set(r.id, r));
    
    // Build hierarchy
    c.replies.forEach((rawReply, index) => {
      const uiReply = replies[index];
      if (rawReply.replyToReplyId) {
        const parent = replyMap.get(rawReply.replyToReplyId);
        if (parent) {
          parent.replies.push(uiReply);
        } else {
          root.replies.push(uiReply);
        }
      } else {
        root.replies.push(uiReply);
      }
    });
  }

  return root;
};


// Cache for usernames to avoid duplicate requests
const usernameCache = new Map<string, string>();

const fetchUsername = async (userId: string) => {
  if (!userId || usernameCache.has(userId)) return usernameCache.get(userId);
  
  // Check if userId looks like a UUID (32 chars hex) 
  // OR if it starts with "User-" followed by 32 chars hex (which seems to be the backend default)
  const isRawId = userId.length === 32 && /^[0-9a-fA-F]+$/.test(userId);
  const isDefaultUser = userId.startsWith('User-') && userId.length === 37; // "User-" + 32 chars

  if (isRawId || isDefaultUser) {
      // Extract raw ID if it's in "User-..." format
      const rawId = isDefaultUser ? userId.substring(5) : userId;
      
      try {
        const res = await getUserPublicProfile(rawId);
        // API returns { isSuccess: true, data: { username: "Patton174" } }
        // Or sometimes data IS the username string?
        // Based on user input: "data": { "username": "Patton174" }
        
        if (res.isSuccess && res.data) {
           const name = typeof res.data === 'object' ? res.data.username : res.data;
           if (name) {
              usernameCache.set(userId, name); // Cache using the ORIGINAL userId as key
              return name;
           }
        }
      } catch (e) {
        console.error('Failed to fetch username for', rawId, e);
      }
  }
  return userId;
};

const loadComments = async (articleId: string) => {
  try {
    const res = await getComments(articleId);
    if (res.isSuccess && res.data) {
      // API might return array directly or { items: [] }
      // Based on user feedback: "data": [ { ... }, { ... } ]
      const rawComments = Array.isArray(res.data) ? res.data : (res.data.items || []);
      const mapped = rawComments.map(mapComment);
      
      // Helper to process a list of comments (including replies)
      const processComments = async (list: UIComment[]) => {
        for (const c of list) {
          // If user looks like an ID (32 chars), try to fetch real name
          // Or if it starts with "User-" followed by ID, maybe we want to check? 
          // Actually, the user complained "User-..." is an ID. 
          // But if the username IS "User-...", we can't do much unless we format it.
          // Let's assume the user mainly wants to fix the raw ID case.
          // But for the "User-..." case, let's try to fetch just in case the API returns a better "username" than what was in the comment object.
          
          // Check if user is exactly the ID (raw ID case) or "User-"+ID
          const isRawId = c.user.length === 32 && /^[0-9a-fA-F]+$/.test(c.user);
          const isDefaultUser = c.user.startsWith('User-') && c.user.length === 37;
          
          if (isRawId || isDefaultUser) {
             const name = await fetchUsername(c.user);
             if (name) c.user = name;
          }
          
          // Lazy loading replies is now handled by CommentItem event 'fetch-replies'
          // We just need to ensure if replies are already present, we process them (usernames)

          // Also process replies
          if (c.replies && c.replies.length > 0) {
            await processComments(c.replies);
          }
        }
      };
      
      await processComments(mapped);
      comments.value = mapped;
    }
  } catch (error) {
    console.error('Failed to load comments:', error);
  }
};
interface ArticleLink {
  id: number | string;
  title: string;
}
const prevArticle = ref<ArticleLink | null>(null);
const nextArticle = ref<ArticleLink | null>(null);

// Comments Data
// Removed duplicate declarations
const isLiked = ref(false); // Local state for like button
const isFavorited = ref(false); // Local state for favorite button

// Fetch Article Details
const loadArticle = async (slug: string) => {
  isLoading.value = true;
  try {
    // 1. Get Article Metadata
    console.log('Fetching article by slug:', slug);
    const res = await getArticleBySlug(slug);
    console.log('getArticleBySlug raw response:', res);

    // Normalize response: Handle both wrapped ResultObject and direct Article object
    let articleData: Article | null = null;
    if (res.isSuccess && res.data) {
      articleData = res.data;
    } else if ((res as any).id) {
      // Fallback: response might be the article object directly
      articleData = res as unknown as Article;
    } else if (res.data && res.data.id) {
      // Fallback: response has data but missing isSuccess
      articleData = res.data;
    }

    if (articleData) {
      article.value = articleData;
      console.log('Article data loaded:', articleData);
      
      // Attempt to fetch author name if it looks like an ID
      if (article.value?.authorId) {
           fetchUsername(article.value.authorId).then(name => {
              if (name) {
                 authorName.value = name;
              }
           });
      }

      // Record view and load updated stats
      if (articleData.id) {
        recordView(articleData.id).catch(e => console.warn('Failed to record view', e));
        
        // Load real-time stats
        getLikeCount(articleData.id).then(res => {
           if (res.isSuccess) article.value!.likes = res.data;
        });
        getCommentCount(articleData.id).then(res => {
           if (res.isSuccess) article.value!.comments = res.data;
        });
        
        // Check user interaction status if logged in
        if (isLoggedIn.value) {
            checkIsLiked(articleData.id).then(res => {
                if (res.isSuccess) isLiked.value = res.data;
            });
            checkIsFavorited(articleData.id).then(res => {
                if (res.isSuccess) isFavorited.value = res.data;
            });
        }
      }

      // 2. Get Content
      console.log('Fetching content for article ID:', articleData.id);
      try {
        const contentRes = await getArticleContent(articleData.id);
        console.log('getArticleContent raw response:', contentRes);

        let contentData: any = null;
        if (contentRes.isSuccess && contentRes.data) {
           contentData = contentRes.data;
        } else if ((contentRes as any).content || (contentRes as any).contentRaw || (contentRes as any).contentMd) {
           // Fallback: response might be the content object directly
           contentData = contentRes;
        } else if (contentRes.data && (contentRes.data.content || contentRes.data.contentRaw || contentRes.data.contentMd)) {
           contentData = contentRes.data;
        }

        if (contentData) {
           // Prioritize Markdown content
           if (contentData.contentMd) {
              articleContent.value = contentData.contentMd;
              articleHtml.value = '';
           } else if (contentData.contentRaw) {
              articleContent.value = contentData.contentRaw;
              articleHtml.value = '';
           } 
           // Then HTML content
           else if (contentData.contentHtml) {
              articleHtml.value = contentData.contentHtml;
              articleContent.value = '';
           } else if (contentData.content) {
              articleHtml.value = contentData.content;
              articleContent.value = '';
           } else {
              articleContent.value = article.value?.summary || '# No content available.';
           }
        } else {
           console.warn('Content response format not recognized', contentRes);
           articleContent.value = article.value?.summary || '# No content available.';
        }
      } catch (contentError) {
        console.error('Failed to fetch article content:', contentError);
        articleContent.value = article.value?.summary || '# Failed to load content.';
      }

      // 3. Get Comments
      console.log('Loading comments for article ID:', articleData.id);
      loadComments(articleData.id);
    } else {
      console.error('Article data invalid or not found in response:', res);
      // Show error state or message
    }
  } catch (error) {
    console.error('Failed to load article:', error);
  } finally {
    isLoading.value = false;
  }
};

// loadComments removed because it's duplicate. 
// The correct loadComments is defined above.


watch(() => props.id, (newId) => {
  if (newId) {
    loadArticle(newId.toString());
  }
}, { immediate: true });

const handleFetchReplies = async (commentId: number) => {
  const comment = findCommentById(comments.value, commentId);
  if (!comment) return;

  // Set a temporary loading state if we had one on UIComment, 
  // but UIComment is defined in CommentItem.vue and we don't have a loading prop there yet in the interface.
  // However, we are passing `loadingReplies` prop to CommentItem in template.
  // We need to track loading state for each comment locally in this component since we can't easily modify the comment object structure reactively without casting.
  // Or better, let's add a reactive Set for loading states.
  
  if (loadingRepliesSet.value.has(commentId)) return;
  loadingRepliesSet.value.add(commentId);

  try {
    const replyRes = await getReplies(commentId);
    if (replyRes.isSuccess && replyRes.data && replyRes.data.length > 0) {
      const rawReplies = replyRes.data;
      const replyMap = new Map<number, UIComment>();
      const mappedReplies = rawReplies.map(mapReplyToUI);
      
      mappedReplies.forEach(r => replyMap.set(r.id, r));
      
      // Rebuild tree
      // Note: replies from this API are flat but contain replyToReplyId
      // We need to attach them to the root comment (which is `comment`)
      
      // Clear existing replies just in case
      comment.replies = [];
      
      const rootReplies: UIComment[] = [];

      rawReplies.forEach((rawReply, index) => {
        const uiReply = mappedReplies[index];
        if (rawReply.replyToReplyId) {
          const parent = replyMap.get(rawReply.replyToReplyId);
          if (parent) {
             parent.replies.push(uiReply);
          } else {
             // Fallback if parent not found in this batch (shouldn't happen for valid tree)
             rootReplies.push(uiReply);
          }
        } else {
          rootReplies.push(uiReply);
        }
      });
      
      // Assign root replies to the main comment
      comment.replies = rootReplies;
      
      // Fetch usernames for new replies
      // We need to reuse processComments logic or similar
      // Since processComments is inside loadComments, we should extract it or duplicate simple username fetching
      const fetchUsernamesForList = async (list: UIComment[]) => {
          for (const c of list) {
            const isRawId = c.user.length === 32 && /^[0-9a-fA-F]+$/.test(c.user);
            const isDefaultUser = c.user.startsWith('User-') && c.user.length === 37;
            
            if (isRawId || isDefaultUser) {
               const name = await fetchUsername(c.user);
               if (name) c.user = name;
            }
            if (c.replies.length > 0) {
                await fetchUsernamesForList(c.replies);
            }
          }
      };
      await fetchUsernamesForList(comment.replies);
      
    }
  } catch (e) {
    console.error('Failed to fetch replies for', commentId, e);
  } finally {
    loadingRepliesSet.value.delete(commentId);
  }
};

const loadingRepliesSet = ref(new Set<number>());


const findCommentById = (items: UIComment[], id: number): UIComment | undefined => {
  for (const item of items) {
    if (item.id === id) return item;
    const found = findCommentById(item.replies, id);
    if (found) return found;
  }
  return undefined;
};

const handleVote = async (commentId: number, type: 'like' | 'dislike') => {
  if (!isLoggedIn.value) {
    alert('Please login to vote.');
    return;
  }

  // Optimistic update
  const comment = findCommentById(comments.value, commentId);
  if (!comment) return;

  // Call API
  try {
    await reactToComment(type, commentId);
    // Reload comments to get fresh state? Or update locally?
    // Updating locally is complex with logic. 
    // For now, simple reload or just assume success
    if (comment.userVote === type) {
       comment.userVote = null;
       if (type === 'like') comment.likes--; else comment.dislikes--;
    } else {
       if (comment.userVote) {
         if (comment.userVote === 'like') comment.likes--; else comment.dislikes--;
       }
       comment.userVote = type;
       if (type === 'like') comment.likes++; else comment.dislikes++;
    }
  } catch (error) {
    console.error('Failed to vote:', error);
  }
};

const handleReply = async (commentId: number, content: string) => {
  if (!article.value) return;
  if (!isLoggedIn.value) {
    alert('Please login to reply.');
    return;
  }
  
  // Find the comment we are replying to
  let targetComment: UIComment | undefined;
  let rootCommentId: number | undefined;
  let replyToReplyId: number | undefined;
  
  // Helper to find comment and its root
  const findTarget = (list: UIComment[], rootId: number): boolean => {
    for (const c of list) {
      if (c.id === commentId) {
        targetComment = c;
        rootCommentId = rootId;
        // If the target is NOT the root itself, then it's a reply-to-reply
        if (c.id !== rootId) {
          replyToReplyId = c.id;
        }
        return true;
      }
      if (c.replies && c.replies.length > 0) {
        if (findTarget(c.replies, rootId)) return true;
      }
    }
    return false;
  };

  // Search in root comments
  for (const root of comments.value) {
    if (findTarget([root], root.id)) break;
  }

  if (!targetComment || !rootCommentId) {
    console.error('Target comment not found for id:', commentId);
    return;
  }

  try {
    // replyComment(commentId, content, articleId, replyToReplyId, replyToUserId)
    // commentId param in API is the ROOT comment ID.
    // replyToReplyId is the ID of the reply we are replying to (if any).
    const res = await replyComment(
      rootCommentId, 
      content, 
      article.value.id, 
      replyToReplyId, 
      targetComment.userId
    );
    
    if (res.isSuccess) {
      // Reload comments
      loadComments(article.value.id);
    }
  } catch (error) {
    console.error('Failed to reply:', error);
  }
};

const submitComment = async () => {
  if (!newComment.value || !article.value) return;
  if (!isLoggedIn.value) {
    alert('Please login to comment.');
    return;
  }
  
  isSubmittingComment.value = true;
  try {
    const res = await addComment(article.value.id, newComment.value);
    if (res.isSuccess) {
      newComment.value = '';
      loadComments(article.value.id);
    }
  } catch (error) {
    console.error('Failed to submit comment:', error);
  } finally {
    isSubmittingComment.value = false;
  }
};

const toggleLike = async () => {
  if (!article.value) return;
  if (!isLoggedIn.value) {
    alert('Please login to like.');
    return;
  }
  try {
    if (isLiked.value) {
      await unlikeArticle(article.value.id);
      if (article.value.likes !== undefined) article.value.likes--;
      isLiked.value = false;
    } else {
      await likeArticle(article.value.id);
      if (article.value.likes !== undefined) article.value.likes++;
      isLiked.value = true;
    }
  } catch (error) {
    console.error('Failed to toggle like:', error);
  }
};

const toggleFavorite = async () => {
  if (!article.value) return;
  if (!isLoggedIn.value) {
    alert('Please login to favorite.');
    return;
  }
  try {
    if (isFavorited.value) {
      await unfavoriteArticle(article.value.id);
      if (article.value.favorites !== undefined) article.value.favorites--;
      isFavorited.value = false;
    } else {
      await favoriteArticle(article.value.id);
      if (article.value.favorites !== undefined) article.value.favorites++;
      isFavorited.value = true;
    }
  } catch (error) {
    console.error('Failed to toggle favorite:', error);
  }
};
</script>


<template>
  <div class="container article-layout" v-if="isLoading && !article">
       <div class="glass-panel" style="padding: 2rem; text-align: center;">
         Loading article...
       </div>
    </div>
    <div class="container article-layout" v-else-if="!article && !isLoading">
       <div class="glass-panel" style="padding: 2rem; text-align: center;">
         Article not found.
       </div>
    </div>
    <div class="container article-layout" v-else>
    <main class="article-content">
      <article class="glass-panel" v-if="article">
        <header class="article-header">
          <div class="article-meta">
            <span class="date">{{ d(new Date(article.publishAt || article.createdAt), 'long') }}</span>
            <span class="dot">•</span>
            <span class="read-time">{{ article.readingTimeMinutes || 5 }} min read</span>
            <span class="dot" v-if="article.tags && article.tags.length > 0">•</span>
            <div class="tags" v-if="article.tags && article.tags.length > 0">
              <span class="tag" v-for="tag in article.tags" :key="tag">#{{ tag }}</span>
            </div>
          </div>

          <h1 class="article-title">{{ article.title }}</h1>
          <div class="author-info">
            <div class="avatar">
              <IconUser :size="24" />
            </div>
            <div class="author-details">
              <span class="name">{{ authorName || article.authorId }}</span>
              <span class="stats">{{ article.views || 0 }} views • {{ article.likes || 0 }} likes</span>
            </div>
          </div>
        </header>


        <div class="article-body">
           <MdPreview 
             v-if="articleContent" 
             :modelValue="articleContent" 
             :theme="theme" 
             preview-theme="github" 
             style="background: transparent;"
           />
           <div v-else-if="articleHtml" v-html="articleHtml"></div>
           <div class="loading-placeholder" v-else-if="isLoading">Loading content...</div>
           <div class="no-content" v-else>No content available.</div>
        </div>

        <div class="article-actions" v-if="article">
          <button class="action-btn" :class="{ active: isLiked }" @click="toggleLike" aria-label="Like article">
            <span class="icon">♥</span> 
            <span class="btn-text">{{ article.likes }} Likes</span>
          </button>
          <button class="action-btn" aria-label="Share article">
            <span class="icon">↗</span> 
            <span class="btn-text">Share</span>
          </button>
          <button class="action-btn" :class="{ active: isFavorited }" @click="toggleFavorite" aria-label="Add to favorites">
            <span class="icon">★</span> 
            <span class="btn-text">{{ isFavorited ? 'Favorited' : 'Favorite' }}</span>
          </button>
        </div>
      </article>

      <nav class="article-navigation glass-panel">
        <router-link 
          v-if="prevArticle" 
          :to="{ name: 'article-detail', params: { id: prevArticle.id } }" 
          class="nav-link prev"
        >
          <span class="nav-label">← Previous</span>
          <span class="nav-title">{{ prevArticle.title }}</span>
        </router-link>
        <div v-else class="nav-spacer"></div>

        <div class="nav-divider"></div>

        <router-link 
          v-if="nextArticle" 
          :to="{ name: 'article-detail', params: { id: nextArticle.id } }" 
          class="nav-link next"
        >
          <span class="nav-label">Next →</span>
          <span class="nav-title">{{ nextArticle.title }}</span>
        </router-link>
        <div v-else class="nav-spacer"></div>
      </nav>

      <!-- Comments Section -->
      <section class="comments-section glass-panel">
        <h3>Comments ({{ article?.comments ?? comments.length }})</h3>
        
        <div class="comment-form main-form">
          <div class="avatar-wrapper">
            <div class="user-avatar">
              <img v-if="user && user.avatarUrl" :src="user.avatarUrl" :alt="user.username" style="width: 100%; height: 100%; object-fit: cover; border-radius: 50%;" />
              <IconUser v-else :size="24" />
            </div>
          </div>
          <div class="input-wrapper">
            <textarea 
              v-model="newComment" 
              :placeholder="isLoggedIn ? 'Leave a comment...' : 'Please login to leave a comment'" 
              rows="3" 
              aria-label="Comment content"
              :disabled="!isLoggedIn"
            ></textarea>
            <div class="form-footer">
              <button @click="submitComment" class="submit-btn" :disabled="!newComment.trim() || !isLoggedIn || isSubmittingComment">
                 <span v-if="isSubmittingComment">Posting...</span>
                 <span v-else>Post Comment</span>
              </button>
            </div>
          </div>
        </div>

        <div class="comments-list">
          <CommentItem 
            v-for="comment in comments" 
            :key="comment.id" 
            :comment="comment"
            :loading-replies="loadingRepliesSet.has(comment.id)"
            @vote="handleVote"
            @reply="handleReply"
            @fetch-replies="handleFetchReplies"
          />
        </div>
      </section>
    </main>

    <aside class="sidebar-section">
      <Sidebar />
    </aside>
  </div>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.article-layout {
  display: grid;
  grid-template-columns: 1fr 22rem;
  gap: $spacing-xxl;
  margin-top: 7.5rem; // Space for fixed navbar
  padding-bottom: $spacing-xxl;
  max-width: 100%; /* Ensure no overflow */
  overflow-x: hidden; /* Prevent horizontal scroll */

  @media (max-width: $breakpoint-desktop) {
    grid-template-columns: 1fr;
    gap: $spacing-xl;
  }

  @media (max-width: $breakpoint-tablet) {
    gap: $spacing-lg;
  }

  @media (max-width: $breakpoint-mobile) {
    margin-top: 5rem;
    gap: $spacing-lg;
    padding-bottom: $spacing-lg;
    padding-left: $spacing-md;
    padding-right: $spacing-md;
  }
}

.article-content {
  min-width: 0; /* Allow grid item to shrink below content size */
}

.glass-panel {
  background: $color-bg-glass;
  backdrop-filter: blur(10px);
  border: 1px solid $color-border;
  border-radius: 16px;
  padding: $spacing-xl;
  margin-bottom: $spacing-lg;

  @media (max-width: $breakpoint-mobile) {
    padding: $spacing-lg;
    border-radius: 12px;
  }
}

.article-header {
  margin-bottom: $spacing-xl;
  border-bottom: 1px solid $color-border;
  padding-bottom: $spacing-lg;

  @media (max-width: $breakpoint-mobile) {
    margin-bottom: $spacing-lg;
  }

  .article-meta {
    display: flex;
    gap: $spacing-md;
    margin-bottom: $spacing-md;
    color: $color-text-secondary;
    font-size: 0.9rem;
    flex-wrap: wrap;

    .tag {
      color: $color-accent-primary;
    }
  }

  .article-title {
    font-size: 2.5rem;
    margin-bottom: $spacing-lg;
    line-height: 1.2;
    word-break: break-word; // Prevent long words from overflowing
    color: $color-text-primary;

    @media (max-width: $breakpoint-mobile) {
      font-size: 1.75rem;
      line-height: 1.3;
      margin-bottom: $spacing-md;
    }
  }

  .author-info {
    display: flex;
    align-items: center;
    gap: $spacing-md;

    .avatar {
      width: 2.5rem;
      height: 2.5rem;
      background: $color-bg-secondary;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .author-details {
      display: flex;
      flex-direction: column;
      
      .name {
        font-weight: bold;
        color: $color-text-primary;
      }
      
      .stats {
        font-size: 0.85rem;
        color: $color-text-secondary;
      }
    }
  }
}

.article-body {
  font-size: 1.1rem;
  line-height: 1.8;
  color: $color-text-secondary;
  margin-bottom: $spacing-xl;
  overflow-wrap: break-word; /* Break long words */
  word-wrap: break-word;

  @media (max-width: $breakpoint-mobile) {
    font-size: 1rem;
    line-height: 1.6;
    margin-bottom: $spacing-lg;
  }

  :deep(h2) {
    font-size: 1.8rem;
    margin: $spacing-xl 0 $spacing-md;
    color: $color-text-primary;

    @media (max-width: $breakpoint-mobile) {
      font-size: 1.4rem;
      margin: $spacing-lg 0 $spacing-sm;
    }
  }

  :deep(p) {
    margin-bottom: $spacing-md;
  }

  :deep(ul) {
    margin-bottom: $spacing-md;
    padding-left: $spacing-lg;
    
    li {
      margin-bottom: $spacing-xs;
    }
  }

  :deep(pre) {
    overflow-x: auto;
    max-width: 100%;
    background: $color-bg-secondary;
    padding: $spacing-md;
    border-radius: 8px;
    margin-bottom: $spacing-md;
    -webkit-overflow-scrolling: touch; // Smooth scrolling on iOS
    border: 1px solid $color-border;
    
    code {
      font-family: $font-family-code;
      color: $color-text-primary;
    }
  }

  :deep(img) {
    max-width: 100%;
    height: auto;
    border-radius: $radius-md;
    margin: $spacing-md 0;
    display: block;
  }

  :deep(table) {
    display: block;
    overflow-x: auto;
    max-width: 100%;
    margin-bottom: $spacing-md;
    border-collapse: collapse;
    -webkit-overflow-scrolling: touch;
    
    th, td {
      padding: $spacing-sm;
      border: 1px solid $color-border;
      min-width: 6.25rem; // Ensure readable columns
      color: $color-text-primary;
    }
    
    th {
      background: $color-bg-secondary;
      font-weight: bold;
    }
  }
}

.article-actions {
  display: flex;
  flex-wrap: wrap;
  gap: $spacing-md;
  padding-top: $spacing-lg;
  border-top: 1px solid $color-border;

  .action-btn {
    background: rgba(255, 255, 255, 0.05); /* Keep slight transparency or use var */
    background: $color-bg-secondary; /* Better for light mode compatibility */
    border: 1px solid $color-border;
    color: $color-text-primary;
    padding: $spacing-sm $spacing-md;
    border-radius: 1.25rem;
    cursor: pointer;
    transition: all 0.3s ease;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: $spacing-xs;
    min-height: 3rem; // Touch target size
    min-width: 3rem;

    @media (max-width: $breakpoint-mobile) {
      flex: 1; // Full width distribution on mobile
      padding: 8px 12px;
    }

    &:hover {
      background: $color-border;
      transform: translateY(-2px);
    }

    &.active {
      color: $color-accent-tertiary;
      border-color: rgba($color-accent-tertiary, 0.3);
      background: rgba($color-accent-tertiary, 0.1);
    }
  }
}

.article-navigation {
  display: flex;
  justify-content: space-between;
  align-items: stretch;
  padding: 0;
  overflow: hidden;
  margin-bottom: $spacing-lg;

  @media (max-width: $breakpoint-mobile) {
    flex-direction: column;
    gap: $spacing-md;
  }

  .nav-link {
    flex: 1;
    padding: $spacing-lg;
    display: flex;
    flex-direction: column;
    gap: $spacing-xs;
    text-decoration: none;
    color: $color-text-primary;
    transition: background 0.3s;
    min-height: 5rem; // Larger touch area

    @media (max-width: $breakpoint-mobile) {
      padding: $spacing-md;
      background: $color-bg-secondary; // Distinct background on mobile
      border-radius: $radius-md;
    }

    &:hover {
      background: $color-bg-secondary;
      
      .nav-title {
        color: $color-accent-primary;
      }
    }

    &.prev {
      text-align: start;
      align-items: flex-start;
    }

    &.next {
      text-align: end;
      align-items: flex-end;

      @media (max-width: $breakpoint-mobile) {
        text-align: start;
        align-items: flex-start;
      }
    }

    .nav-label {
      font-size: 0.85rem;
      color: $color-text-secondary;
    }

    .nav-title {
      font-weight: bold;
      font-size: 1.1rem;
      transition: color 0.3s;
    }
  }

  .nav-divider {
    width: 1px;
    background: $color-border;

    @media (max-width: $breakpoint-mobile) {
      display: none;
    }
  }

  .nav-spacer {
    flex: 1;
  }
}

.comments-section {
  h3 {
    margin-bottom: $spacing-lg;
    font-size: 1.5rem;
    color: $color-text-primary;
    
    @media (max-width: $breakpoint-mobile) {
      font-size: 1.3rem;
    }
  }

  .comment-form {
    margin-bottom: $spacing-xl;
    display: flex;
    gap: $spacing-md;
    
    @media (max-width: $breakpoint-mobile) {
      flex-direction: column;
    }

    &.main-form {
      // Glassmorphism overhaul - "Texture Max"
      background: rgba(255, 255, 255, 0.05); // Very subtle base
      backdrop-filter: blur(24px) saturate(180%); // Heavy blur + saturation for premium feel
      -webkit-backdrop-filter: blur(24px) saturate(180%);
      padding: $spacing-lg;
      border-radius: 20px; // Softer corners
      border: 1px solid rgba(255, 255, 255, 0.3); // Crisper border
      box-shadow: 
        0 4px 24px -1px rgba(0, 0, 0, 0.05),
        inset 0 0 0 1px rgba(255, 255, 255, 0.1); // Inner light ring

      :global(.dark) & {
        background: rgba(0, 0, 0, 0.2);
        border-color: rgba(255, 255, 255, 0.08);
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
      }

      @media (max-width: $breakpoint-mobile) {
        padding: $spacing-md;
      }
    }

    .avatar-wrapper {
      flex-shrink: 0;
      
      .user-avatar {
        width: 2.5rem;
        height: 2.5rem;
        border-radius: 50%;
        background: $color-bg-primary;
        display: flex;
        align-items: center;
        justify-content: center;
        border: 1px solid $color-border;
      }
    }

    .input-wrapper {
      flex-grow: 1;
      display: flex;
      flex-direction: column;
      gap: $spacing-md;
    }
    
    textarea {
      width: 100%;
      // Glassy Input
      background: rgba(255, 255, 255, 0.03); 
      border: 1px solid rgba(0, 0, 0, 0.05);
      border-radius: 16px;
      padding: $spacing-md;
      color: $color-text-primary;
      resize: vertical;
      font-family: inherit;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      min-height: 6.25rem;
      
      :global(.dark) & {
        background: rgba(0, 0, 0, 0.2);
        border-color: rgba(255, 255, 255, 0.05);
      }

      &:focus {
        outline: none;
        background: rgba(255, 255, 255, 0.08);
        border-color: rgba(var(--color-accent-primary-rgb), 0.3); // Subtle hint only
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
        transform: translateY(-1px);
      }
    }

    .form-footer {
      display: flex;
      justify-content: center; // Changed from flex-end to center as requested
      margin-top: $spacing-md; // Add some space above button
    }

    .submit-btn {
      // Premium Dark/Light Button - NO BLUE
      background: #1a1a1a; // Premium Dark Grey for Light Mode
      color: #ffffff;
      border: 1px solid rgba(255,255,255,0.1);
      padding: 0.75rem 2rem; // Wider
      border-radius: 30px; // Full pill
      font-weight: 600;
      cursor: pointer;
      transition: all 0.4s cubic-bezier(0.22, 1, 0.36, 1);
      font-family: $font-family-code;
      min-height: 3rem;
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;
      overflow: hidden;
      box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15); // Elegant shadow
      
      :global(.dark) & {
        background: #ffffff; // White for Dark Mode
        color: #000000;
        box-shadow: 0 4px 20px rgba(255, 255, 255, 0.15); // White glow
      }

      // Inner gloss shine
      &::after {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 50%;
        background: linear-gradient(to bottom, rgba(255,255,255,0.1) 0%, transparent 100%);
        pointer-events: none;
      }

      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
        background: #888; // Neutral grey
        box-shadow: none;
      }

      &:not(:disabled):hover {
        transform: translateY(-2px) scale(1.02);
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.25);
        
        :global(.dark) & {
           box-shadow: 0 8px 30px rgba(255, 255, 255, 0.25);
        }
      }
      
      &:not(:disabled):active {
        transform: translateY(0) scale(0.98);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }
      
      @media (max-width: $breakpoint-mobile) {
        width: 100%;
      }
    }
  }
}

.sidebar-section {
  @media (max-width: $breakpoint-desktop) {
    margin-top: $spacing-lg;
  }
}
</style>
