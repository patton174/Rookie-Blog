<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { MdEditor } from 'md-editor-v3';
import 'md-editor-v3/lib/style.css';
import { publishArticle, saveDraft, getArticleBySlug, getArticleContent, getArticleById } from '../api/article';
// import { useUserStore } from '../store/user';
import { useTheme } from '../composables/useTheme';

import { marked } from 'marked';

const router = useRouter();
const route = useRoute();
const { t } = useI18n();
// const userStore = useUserStore();
const { theme } = useTheme();

const title = ref('');
const content = ref('');
const isSubmitting = ref(false);
const isSavingDraft = ref(false);
const showPublishModal = ref(false);

const isDragging = ref(false);
const fileInputRef = ref<HTMLInputElement | null>(null);

const processFile = (file: File) => {
  if (file.type.startsWith('image/')) {
     const reader = new FileReader();
     reader.onload = (ev) => {
       publishForm.coverUrl = ev.target?.result as string;
     };
     reader.readAsDataURL(file);
  }
};

const handleDrop = async (e: DragEvent) => {
  isDragging.value = false;
  const files = e.dataTransfer?.files;
  if (files && files.length > 0) {
    processFile(files[0]);
  }
};

const handleFileSelect = (e: Event) => {
  const files = (e.target as HTMLInputElement).files;
  if (files && files.length > 0) {
    processFile(files[0]);
  }
  // Reset input value to allow selecting the same file again
  if (e.target instanceof HTMLInputElement) {
    e.target.value = '';
  }
};

const triggerFileUpload = () => {
  fileInputRef.value?.click();
};

const publishForm = reactive({
  summary: '',
  coverUrl: '',
  tags: ''
});

const wordCount = computed(() => {
  return content.value.length;
});

// Helper to process tags
const getTagsArray = () => publishForm.tags.split(/[,，]/).map(t => t.trim()).filter(t => t);

const handlePublishClick = () => {
  if (!title.value.trim()) {
    alert('Please enter a title');
    return;
  }
  if (!content.value.trim()) {
    alert('Please enter some content');
    return;
  }
  showPublishModal.value = true;
};

// Helper for markdown
const renderMarkdown = (text: string) => {
  // Handle both function and object exports for marked
  if (typeof marked === 'function') {
     return (marked as any)(text);
  } else if (marked && typeof (marked as any).parse === 'function') {
     return (marked as any).parse(text);
  }
  return text; // Fallback
};

const handleSaveDraft = async () => {
  if (isSavingDraft.value || isSubmitting.value) return;
  
  if (!title.value.trim()) {
    alert('Please enter a title to save draft');
    return;
  }

  isSavingDraft.value = true;
  try {
    const contentHtml = await renderMarkdown(content.value);
    const res = await saveDraft({
      title: title.value,
      contentMd: content.value,
      contentHtml: contentHtml,
      summary: publishForm.summary,
      coverUrl: publishForm.coverUrl,
      tags: getTagsArray()
    });

    if (res.isSuccess) {
      // Maybe show a small toast or status instead of alert?
      // For now, using console and a temporary status indicator could be better
      // but alert is consistent with current implementation
      console.log('Draft saved');
    } else {
      console.error('Failed to save draft:', res.errMsg);
    }
  } catch (error) {
    console.error('Save draft error:', error);
  } finally {
    isSavingDraft.value = false;
  }
};

// Shortcut Ctrl+S
const handleKeydown = (e: KeyboardEvent) => {
  if ((e.ctrlKey || e.metaKey) && e.key === 's') {
    e.preventDefault();
    handleSaveDraft();
  }
};

const loadArticleForEdit = async (id: string) => {
  try {
    // First get metadata (though if we have slug/id, we might just need content if we trust the caller?)
    // But usually editor might be opened with a slug or id. 
    // If id is passed in query.
    
    // If we assume the route query 'id' is the slug or id.
    // The user said "id" in the JSON is "e9fd...". 
    
    // Let's try to fetch content directly if we assume it's an ID.
    const contentRes = await getArticleContent(id);
    
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
        if (contentData.contentMd) {
            content.value = contentData.contentMd;
        } else if (contentData.contentRaw) {
            content.value = contentData.contentRaw;
        } else if (contentData.content) {
            content.value = contentData.content; // Fallback to HTML if no markdown
        }
    }

    // We also need title and other metadata. 
    // getArticleBySlug might fail if 'id' is a UUID.
    // Let's try to get article by ID first (new API endpoint might be needed or check if getArticleBySlug handles ID)
    // Assuming we added getArticleById or can reuse a generic fetch.
    // For now, let's try getArticleById if available or fallback to getArticleBySlug.
    
    let metaRes;
    try {
       metaRes = await getArticleById(id);
    } catch (e) {
       // Fallback or maybe it was a slug?
       metaRes = await getArticleBySlug(id);
    }

    if (metaRes && metaRes.isSuccess && metaRes.data) {
        title.value = metaRes.data.title;
        publishForm.summary = metaRes.data.summary || '';
        publishForm.coverUrl = metaRes.data.coverUrl || '';
        if (metaRes.data.tags) {
            publishForm.tags = metaRes.data.tags.join(', ');
        }
    } else {
        // If getArticleById failed (maybe API not implemented?), try getArticleBySlug as fallback
        // (If the above try/catch didn't already cover it)
        if (!metaRes || !metaRes.isSuccess) {
            const slugRes = await getArticleBySlug(id);
            if (slugRes.isSuccess && slugRes.data) {
                title.value = slugRes.data.title;
                publishForm.summary = slugRes.data.summary || '';
                publishForm.coverUrl = slugRes.data.coverUrl || '';
                if (slugRes.data.tags) {
                    publishForm.tags = slugRes.data.tags.join(', ');
                }
            }
        }
    }

  } catch (error) {
    console.error('Failed to load article for edit:', error);
  }
};

onMounted(() => {
  window.addEventListener('keydown', handleKeydown);
  const id = route.query.id as string;
  if (id) {
    loadArticleForEdit(id);
  }
});

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown);
});

const handleUploadImage = async (files: File[], callback: (urls: string[]) => void) => {
  const res = await Promise.all(
    files.map((file) => {
      return new Promise<string>((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = (e) => {
          resolve(e.target?.result as string);
        };
        reader.onerror = (e) => {
          reject(e);
        };
        reader.readAsDataURL(file);
      });
    })
  );
  callback(res);
};

const submitArticle = async () => {
  if (isSubmitting.value) return;
  
  isSubmitting.value = true;
  try {
    const contentHtml = await renderMarkdown(content.value);
    const res = await publishArticle({
      id: route.query.id as string, // Pass ID for update
      title: title.value,
      contentMd: content.value,
      contentHtml: contentHtml,
      summary: publishForm.summary,
      coverUrl: publishForm.coverUrl,
      tags: getTagsArray()
    });

    if (res.isSuccess) {
      // Success Feedback
      router.push('/'); 
    } else {
      alert('Failed to publish: ' + res.errMsg);
    }
  } catch (error) {
    console.error('Publish error:', error);
    alert('An error occurred while publishing.');
  } finally {
    isSubmitting.value = false;
    showPublishModal.value = false;
  }
};
</script>

<template>
  <div class="editor-layout">
    <!-- Top Bar -->
    <header class="editor-header">
      <div class="header-left">
        <button class="back-btn" @click="router.back()" :title="t('editor.back')">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M19 12H5M12 19l-7-7 7-7"/>
          </svg>
        </button>
        <input 
          v-model="title" 
          type="text" 
          class="title-input" 
          :placeholder="t('editor.titlePlaceholder')" 
        />
      </div>
      
      <div class="header-right">
        <span class="status-text" v-if="isSavingDraft">{{ t('editor.saving') }}</span>
        <span class="word-count" v-if="wordCount > 0 && !isSavingDraft">{{ wordCount }} {{ t('editor.words') }}</span>
        
        <button class="icon-btn" @click="showPublishModal = true" :title="t('editor.settings')">
           <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
             <path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.38a2 2 0 0 0-.73-2.73l-.15-.1a2 2 0 0 1-1-1.72v-.51a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"></path>
             <circle cx="12" cy="12" r="3"></circle>
           </svg>
        </button>

        <button class="draft-btn" @click="handleSaveDraft" :disabled="isSavingDraft">
          <span>{{ isSavingDraft ? t('editor.draftSaved') : t('editor.saveDraft') }}</span>
        </button>

        <button class="publish-btn" @click="handlePublishClick">
          <span>{{ t('editor.publish') }}</span>
        </button>
      </div>
    </header>
    
    <!-- Editor Area -->
    <main class="editor-main">
      <div class="editor-wrapper">
        <MdEditor 
          v-model="content" 
          :theme="theme" 
          class="md-editor-custom" 
          preview-theme="github"
          :toolbarsExclude="['save', 'github']"
          :no-katex="true"
          :no-mermaid="true"
          :scroll-auto="false"
          :no-img-zoom-in="true"
          :code-foldable="false"
          :placeholder="t('editor.requiredContent')"
          @onUploadImg="handleUploadImage"
        />
      </div>
    </main>

    <!-- Publish Modal -->
    <Transition name="modal-fade">
      <div v-if="showPublishModal" class="modal-overlay" @click.self="showPublishModal = false">
        <div class="modal-content glass-panel">
          <div class="modal-header">
            <h3>{{ t('editor.publish') }}</h3>
            <button class="close-btn" @click="showPublishModal = false">×</button>
          </div>
          
          <div class="modal-body">
            <div class="grid-layout">
              <!-- Left Column: Cover Image -->
              <div class="grid-col left-col">
                <div class="form-group">
                  <label>{{ t('editor.coverImage') }}</label>
                  <div class="cover-uploader">
                    <!-- Preview / Click Area -->
                    <div 
                      class="cover-preview-area"
                      :class="{ dragging: isDragging, 'has-image': !!publishForm.coverUrl }"
                      @click="triggerFileUpload"
                      @dragover.prevent="isDragging = true" 
                      @dragleave.prevent="isDragging = false" 
                      @drop.prevent="handleDrop"
                    >
                      <img v-if="publishForm.coverUrl" :src="publishForm.coverUrl" class="cover-img" alt="Cover Preview" />
                      
                      <div class="placeholder-content">
                        <div class="icon-wrapper">
                          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                            <rect x="3" y="3" width="18" height="18" rx="2" ry="2"></rect>
                            <circle cx="8.5" cy="8.5" r="1.5"></circle>
                            <polyline points="21 15 16 10 5 21"></polyline>
                          </svg>
                        </div>
                        <span v-if="publishForm.coverUrl">{{ t('editor.dragDrop') }}</span>
                        <span v-else>{{ t('editor.dragDrop') }}</span>
                      </div>
                    </div>

                    <!-- URL Input -->
                    <input 
                      v-model="publishForm.coverUrl" 
                      type="text" 
                      placeholder="Or paste image URL..." 
                      class="custom-input url-input"
                    />
                    
                    <!-- Hidden File Input -->
                    <input 
                      type="file" 
                      ref="fileInputRef" 
                      accept="image/*" 
                      class="hidden-input"
                      @change="handleFileSelect"
                    />
                  </div>
                </div>
              </div>

              <!-- Right Column: Metadata -->
              <div class="grid-col right-col">
                <div class="form-group">
                  <label>{{ t('editor.summary') }}</label>
                  <textarea 
                    v-model="publishForm.summary" 
                    rows="4" 
                    :placeholder="t('editor.summary')"
                    class="custom-input"
                  ></textarea>
                </div>
                
                <div class="form-group">
                  <label>{{ t('editor.tags') }}</label>
                  <input 
                    v-model="publishForm.tags" 
                    type="text" 
                    :placeholder="t('editor.tagsPlaceholder')" 
                    class="custom-input"
                  />
                </div>
              </div>
            </div>
          </div>

          <div class="modal-footer">
            <button @click="showPublishModal = false" class="cancel-btn">{{ t('editor.cancel') }}</button>
            <button @click="submitArticle" class="confirm-btn" :disabled="isSubmitting">
              <span v-if="isSubmitting" class="spinner"></span>
              {{ isSubmitting ? t('editor.saving') : t('editor.publishNow') }}
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.editor-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  padding-top: 70px; // Space for fixed Navbar
  background: transparent; // Let app background show through
  position: relative;
  z-index: 10;
}

.glass-effect {
  background: transparent;
  border: none;
}

/* Header Styles */
.editor-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.5rem 2rem;
  margin: 0 auto;
  max-width: 100%;
  width: 100%;
  gap: 1rem;
  transition: all 0.3s ease;

  @media (max-width: $breakpoint-mobile) {
    padding: 0.5rem 1rem;
  }

  .header-left {
    display: flex;
    align-items: center;
    gap: 1rem;
    flex: 1;
  }

  .back-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    
    border: 1px solid transparent;
    background: transparent;
    color: $color-text-secondary;
    
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      background: rgba(0,0,0,0.05);
      color: $color-text-primary;
      
      :global(.dark) & {
        background: rgba(255,255,255,0.1);
      }
    }
  }

  .title-input {
    flex: 1;
    background: rgba(255, 255, 255, 0.1); // Frosted glass base
    backdrop-filter: blur(8px); // Frosted glass effect
    border: none;
    border-radius: 8px;
    font-size: 1.25rem; // Smaller font size
    font-family: inherit; 
    font-weight: 600; 
    color: $color-text-primary;
    outline: none;
    padding: 0.4rem 0.8rem; // Smaller padding
    min-width: 0;
    transition: all 0.3s ease;

    :global(.dark) & {
      background: rgba(255, 255, 255, 0.05);
    }

    &:focus {
      background: rgba(255, 255, 255, 0.2);
      box-shadow: none; // Ensure no blue glow/border
      
      :global(.dark) & {
        background: rgba(255, 255, 255, 0.08);
      }
    }

    &::placeholder {
      color: rgba($color-text-primary, 0.2); 
      font-style: normal;
    }

    @media (max-width: $breakpoint-mobile) {
      font-size: 1.2rem;
      padding: 0.4rem 0.8rem;
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 1rem;

    .word-count {
      font-size: 0.75rem;
      color: $color-text-secondary;
      font-family: $font-family-code;
      opacity: 0.7;
      
      @media (max-width: $breakpoint-mobile) {
        display: none;
      }
    }

    .status-text {
      font-size: 0.75rem;
      color: $color-text-secondary;
      font-style: italic;
      animation: pulse 2s infinite;
    }
  }

  .icon-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 32px;
    height: 32px;
    border-radius: 50%;
    border: none;
    background: transparent;
    color: $color-text-secondary;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      color: $color-text-primary;
      background: rgba(0,0,0,0.03);
      
      :global(.dark) & {
        background: rgba(255,255,255,0.05);
      }
    }
  }

  .draft-btn {
    background: transparent;
    color: $color-text-secondary;
    border: none;
    padding: 0.4rem 0.8rem;
    border-radius: 4px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
    font-size: 0.8rem;
    text-transform: uppercase;
    letter-spacing: 0.5px;

    &:hover {
      color: $color-text-primary;
      background: rgba(0,0,0,0.03);
      
      :global(.dark) & {
        background: rgba(255,255,255,0.05);
      }
    }

    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  }

  .publish-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    background: #000; // Solid black for light mode
    color: #fff;
    border: 1px solid transparent;
    padding: 0.4rem 1.2rem;
    border-radius: 20px; // Pill shape
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;
    font-size: 0.8rem;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    min-width: 80px;

    :global(.dark) & {
       background: #fff;
       color: #000;
    }

    &:hover {
      opacity: 0.8;
      transform: translateY(-1px);
    }

    &:active {
      transform: translateY(0);
      opacity: 1;
    }
  }
}

/* Editor Main Area */
.editor-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0 2rem 2rem;
  overflow: hidden;
  max-width: 100%; 
  margin: 0 auto;
  width: 100%;

  @media (max-width: $breakpoint-mobile) {
    padding: 0 1rem 1rem;
  }
}

.editor-wrapper {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  
  // Minimalist editor style
  :deep(.md-editor) {
    height: 100%;
    background: transparent;
    border: none;
  }

  :deep(.md-editor-toolbar-wrapper) {
    border-bottom: 1px solid rgba(0,0,0,0.05);
    padding: 0.5rem 0;
    margin-bottom: 1rem;

    :global(.dark) & {
      border-bottom: 1px solid rgba(255,255,255,0.05);
    }
  }
  
  :deep(.md-editor-content) {
    background: transparent;
  }

  :deep(.md-editor-input-wrapper), :deep(.md-editor-preview-wrapper) {
    background: transparent;
    scroll-behavior: auto !important; // Optimization: Avoid scroll conflict
    will-change: transform; // Optimization: Layer promotion
  }
  
  :deep(.cm-scroller) {
    font-family: $font-family-code;
  }

  // Preview Styles from ArticleDetail
  :deep(.md-editor-preview) {
    font-size: 1.1rem;
    line-height: 1.8;
    color: $color-text-secondary;
    word-wrap: break-word;

    h1, h2, h3, h4, h5, h6 {
      color: $color-text-primary;
    }

    h2 {
      font-size: 1.8rem;
      margin: $spacing-xl 0 $spacing-md;
    }

    p {
      margin-bottom: $spacing-md;
      
      &[align="center"] {
        text-align: center;
        img { 
          margin: 0 auto; 
          display: inline-block;
        }
      }
    }

    ul, ol {
      margin-bottom: $spacing-md;
      padding-left: $spacing-lg;
      li { margin-bottom: $spacing-xs; }
    }

    pre {
      background: rgba(255, 255, 255, 0.03);
      padding: $spacing-md;
      border-radius: 8px;
      margin-bottom: $spacing-md;
      border: 1px solid rgba(255, 255, 255, 0.08);
      
      code {
        font-family: $font-family-code;
        color: $color-text-primary;
        background: transparent !important;
      }
    }

    img {
      max-width: 100%;
      height: auto;
      border-radius: $radius-md;
      margin: $spacing-md 0;
      display: inline-block;
      background: transparent !important;
      
      &[valign="middle"], &[valign="bottom"] {
        display: inline-block;
        margin: 0 4px;
        vertical-align: middle;
      }
    }

    blockquote {
      margin: $spacing-md 0;
      padding: $spacing-md;
      border-left: 4px solid $color-accent-primary;
      background: rgba($color-accent-primary, 0.05);
      border-radius: 0 8px 8px 0;
      color: $color-text-secondary;
      
      p { margin: 0; }
    }

    table {
      display: block;
      width: fit-content;
      overflow-x: auto;
      max-width: 100%;
      margin-bottom: $spacing-md;
      border-collapse: separate;
      border-spacing: 0;
      border-radius: 8px;
      overflow: hidden;
      border: 1px solid $color-border !important;
      background: transparent !important;
      
      tr { background-color: transparent !important; }
      tr:nth-child(2n) { background-color: rgba(0, 0, 0, 0.02) !important; }
      
      th, td {
        padding: $spacing-sm $spacing-md;
        border-right: 1px solid $color-border !important;
        border-bottom: 1px solid $color-border !important;
        border-left: none !important;
        border-top: none !important;
        min-width: 6.25rem;
        color: $color-text-primary;
        background-color: transparent !important;
        vertical-align: top;
        
        &:last-child { border-right: none !important; }
      }

      tr:last-child td { border-bottom: none !important; }

      th {
        background: rgba(0, 0, 0, 0.04) !important;
        font-weight: 600;
        text-align: left;
      }
    }
  }
}

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.modal-content {
  width: 800px; // Increased width for side-by-side layout
  max-width: 90%;
  background: $color-bg-secondary;
  border: 1px solid $color-border;
  border-radius: 16px;
  padding: 0; // Reset padding to handle header/footer separately
  box-shadow: $shadow-lg;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 1.5rem;
  border-bottom: 1px solid $color-border;
  display: flex;
  justify-content: space-between;
  align-items: center;

  h3 {
    margin: 0;
    font-size: 1.25rem;
    color: $color-text-primary;
  }

  .close-btn {
    background: none;
    border: none;
    color: $color-text-secondary;
    font-size: 1.5rem;
    cursor: pointer;
    padding: 0.25rem;
    line-height: 1;
    transition: color 0.2s;

    &:hover {
      color: $color-text-primary;
    }
  }
}

.modal-body {
  padding: 1.5rem;
}

.grid-layout {
  display: grid;
  grid-template-columns: 1.2fr 1fr; // Left column slightly larger
  gap: 2rem;
  
  @media (max-width: $breakpoint-tablet) {
    grid-template-columns: 1fr; // Stack on mobile
    gap: 1rem;
  }
}

.grid-col {
  display: flex;
  flex-direction: column;
  
  &.right-col {
    gap: 1rem; // Spacing between summary and tags
  }
}

.form-group {
  margin-bottom: 0; // Remove default margin as grid handles it or we handle it in right-col
  
  &:last-child {
    margin-bottom: 0;
  }
  
  label {
    display: block;
    margin-bottom: 0.5rem;
    color: $color-text-secondary;
    font-size: 0.9rem;
    font-weight: 500;
  }
  
  .cover-uploader {
    display: flex;
    flex-direction: column;
    gap: 0.8rem;
  }

  .cover-preview-area {
    position: relative;
    width: 100%;
    height: 160px;
    border: 2px dashed rgba(0,0,0,0.1);
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s ease;
    background: rgba(0,0,0,0.02);
    overflow: hidden;

    :global(.dark) & {
      border-color: rgba(255,255,255,0.1);
      background: rgba(255,255,255,0.02);
    }

    &:hover {
      border-color: $color-text-primary;
      background: rgba(0,0,0,0.04);
      
      :global(.dark) & {
        background: rgba(255,255,255,0.05);
      }
    }

    &.dragging {
      border-color: $color-text-primary;
      background: rgba(0,0,0,0.06);
      transform: scale(1.01);
    }

    &.has-image {
      border-style: solid;
      border-width: 1px;
      
      .placeholder-content {
        opacity: 0;
        background: rgba(0,0,0,0.6);
        backdrop-filter: blur(2px);
        color: #fff;
        transition: opacity 0.2s;
        position: absolute;
        inset: 0;
        z-index: 2;
        flex-direction: column;
        gap: 0.5rem;
      }

      &:hover .placeholder-content {
        opacity: 1;
      }
    }
  }

  .cover-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    position: absolute;
    top: 0;
    left: 0;
    z-index: 1;
  }

  .placeholder-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 0.5rem;
    color: $color-text-secondary;
    font-size: 0.9rem;
    font-weight: 500;
    pointer-events: none; // Let clicks pass to parent
    
    .icon-wrapper {
      padding: 0.5rem;
      background: rgba(0,0,0,0.05);
      border-radius: 50%;
      display: flex;
      
      :global(.dark) & {
        background: rgba(255,255,255,0.1);
      }
    }
  }

  .hidden-input {
    display: none;
  }

  .custom-input {
    width: 100%;
    padding: 0.75rem 1rem;
    background: rgba(0, 0, 0, 0.05); // Light mode
    border: 1px solid $color-border;
    border-radius: 8px;
    color: $color-text-primary;
    font-family: inherit;
    transition: all 0.3s;
    
    :global(.dark) & {
      background: rgba(0, 0, 0, 0.2);
    }
    
    &:focus {
      outline: none;
      border-color: $color-text-primary;
      background: rgba(0, 0, 0, 0.08); // Light focus
      box-shadow: 0 0 0 1px rgba(0,0,0, 0.1);

      :global(.dark) & {
         background: rgba(0, 0, 0, 0.3);
         border-color: #fff;
         box-shadow: 0 0 0 1px rgba(255,255,255, 0.1);
      }
    }
    
    &::placeholder {
      color: rgba($color-text-primary, 0.2);
    }
  }
  
  textarea.custom-input {
    resize: vertical;
    min-height: 100px;
  }
}

.modal-footer {
    padding: 1.5rem;
    background: rgba(0, 0, 0, 0.03);
    border-top: 1px solid $color-border;
    display: flex;
    justify-content: flex-end;
    gap: 1rem;

    :global(.dark) & {
      background: rgba(0, 0, 0, 0.1);
    }

  button {
    padding: 0.6rem 1.25rem;
    border-radius: 8px;
    font-weight: 600;
    font-size: 0.95rem;
    cursor: pointer;
    border: none;
    transition: all 0.2s;
  }

  .cancel-btn {
    background: transparent;
    color: $color-text-secondary;
    
    &:hover {
      background: rgba(0, 0, 0, 0.05);
      color: $color-text-primary;

      :global(.dark) & {
        background: rgba(255, 255, 255, 0.05);
      }
    }
  }

  .confirm-btn {
    background: #000;
    color: #fff;
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.6rem 1.5rem;
    border-radius: 8px;
    font-size: 0.9rem;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    
    :global(.dark) & {
      background: #fff;
      color: #000;
    }

    &:hover {
      opacity: 0.8;
      transform: translateY(-1px);
    }
    
    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
      transform: none;
    }
  }
}

// Spinner
.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(0,0,0,0.3);
  border-radius: 50%;
  border-top-color: #000;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@keyframes pulse {
  0% { opacity: 1; }
  50% { opacity: 0.5; }
  100% { opacity: 1; }
}

/* Transitions */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.3s ease;
  
  .modal-content {
    transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  }
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
  
  .modal-content {
    transform: scale(0.9) translateY(20px);
  }
}
</style>
