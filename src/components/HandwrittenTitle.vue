<script setup lang="ts">
import { ref, computed, onMounted, watch, nextTick, onUnmounted } from 'vue';
import { useI18n } from 'vue-i18n';
import { useText3dEffect } from '../utils/text3dEffect';

interface Props {
  text?: string;
  locale?: string;
  glitchEffect?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  text: '',
  locale: 'en',
  glitchEffect: true
});

const { t } = useI18n();

// --- Content Configuration ---
const content = computed(() => {
  if (props.text) return { title: props.text, subtitle: '', tag: '' };
  
  return { 
    title: t('home.title'), 
    subtitle: t('home.subtitle'),
    tag: t('home.tag')
  };
});

const isVisible = ref(false);
const { init: init3dEffect, destroy: destroy3dEffect, playEntrance } = useText3dEffect('.main-title');
const { isLoading } = useAppStore();
import { useAppStore } from '../store/app';

const emit = defineEmits(['ready']);

const getEffectConfig = () => {
  const text = content.value.title;
  // Check if text contains Chinese characters
  if (/[\u4e00-\u9fa5]/.test(text) || props.locale === 'zh') {
    return {
      fontFamily: "'华康海报体', 'DFPHaiBaoW12', 'DFPoster', 'ZCOOL KuaiLe', cursive",
      letterSpacing: '4px' // Use positive spacing for Chinese to avoid overlapping
    };
  }
  return {
    fontFamily: "'Luckiest Guy', cursive, sans-serif",
    letterSpacing: '-10px'
  };
};

onMounted(async () => {
  // Wait for fonts to load before initializing effect
  const config = getEffectConfig();
  const fontToLoad = config.fontFamily?.split(',')[0].replace(/['"]/g, '') || 'Luckiest Guy';
  
  try {
    await document.fonts.load(`1em "${fontToLoad}"`);
  } catch (e) {
    console.warn('Font load failed or timeout', e);
  }

  // Ensure immediate visibility without transition delay
  isVisible.value = true;
  
  // Initialize 3D Text Effect (this will hide the text spans initially)
  init3dEffect(config);
  
  // Ensure DOM update before signaling ready
  await nextTick();
  
  // Signal ready to parent
  emit('ready');
});

// Watch for loading state to play animation
watch(isLoading, (val) => {
  if (!val) {
    // Play entrance animation immediately when loading stops
    playEntrance();
  }
});

onUnmounted(() => {
  destroy3dEffect();
});

watch(() => content.value.title, async () => {
  await nextTick();
  init3dEffect(getEffectConfig());
});
</script>

<template>
  <div class="brand-title-wrapper" :class="{ 'is-visible': isVisible }" style="min-height: 120px;">
    
    <!-- Decorative Grid Background -->
    <div class="bg-grid"></div>

    <!-- Main Content Card -->
    <div class="content-card">
      <!-- Top Tag (Code Style) -->
      <!-- Removed code-tag element -->
  
      <!-- Main Title (Neon Gradient) -->
      <div class="title-container">
        <h1 class="main-title" :data-text="content.title">
          {{ content.title }}
        </h1>
        <!-- Glitch Layers (Disabled for 3D effect compatibility) -->
        <!-- <span v-if="glitchEffect" class="glitch-layer layer-1" aria-hidden="true">{{ content.title }}</span> -->
        <!-- <span v-if="glitchEffect" class="glitch-layer layer-2" aria-hidden="true">{{ content.title }}</span> -->
      </div>

      <!-- Separator -->
      <!-- Removed separator-line element -->
  
      <!-- Subtitle -->
      <p class="subtitle fade-slide-up">
        {{ content.subtitle }}
      </p>
    </div>

  </div>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.brand-title-wrapper {
  width: 100%;
  max-width: 1200px; /* Increased max-width to ensure content fits */
  margin: 0 auto;
  position: relative;
  padding: 60px 10px; /* Reduced side padding to give more room */
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  
  &.is-visible {
    opacity: 1;
  }
}

.bg-grid {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    linear-gradient(rgba($color-accent-primary, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba($color-accent-primary, 0.05) 1px, transparent 1px);
  background-size: 40px 40px;
  mask-image: radial-gradient(circle at center, black 30%, transparent 70%);
  z-index: 0;
  pointer-events: none;
}

.content-card {
  position: relative;
  z-index: 1;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.5rem;
}

/* --- Code Tag --- */
/* Removed styles for code-tag as it is commented out */
/* .code-tag {
  font-family: 'Fira Code', monospace;
  font-size: 0.9rem;
  background: rgba($color-accent-primary, 0.1);
  padding: 6px 16px;
  border-radius: 4px;
  border: 1px solid rgba($color-accent-primary, 0.2);
  box-shadow: 0 0 15px rgba($color-accent-primary, 0.1);
  
  white-space: nowrap;
  overflow: visible;
  max-width: 100%;

  .operator { color: $color-text-secondary; }
  .keyword { color: $color-accent-primary; font-weight: 600; }
} */

/* --- Main Title --- */
.title-container {
  position: relative;
  display: inline-block;
}

.main-title {
  font-family: 'Inter', sans-serif;
  font-weight: 900;
  font-size: 5rem;
  line-height: 1.1;
  letter-spacing: -2px;
  margin: 0;
  color: #fff;
  
  // Gradient Fill
  background: linear-gradient(135deg, #ffffff 0%, $color-accent-primary 50%, $color-accent-secondary 100%);
  -webkit-background-clip: text;
  background-clip: text;
  -webkit-text-fill-color: transparent;
  
  // Drop Shadow
  filter: drop-shadow(0 0 20px rgba($color-accent-secondary, 0.3));

  @media (max-width: 768px) {
    font-size: 3.5rem;
  }
}

/* --- Glitch Effect --- */
.glitch-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  font-family: 'Inter', sans-serif;
  font-weight: 900;
  font-size: 5rem;
  line-height: 1.1;
  letter-spacing: -2px;
  background: $color-bg-primary;
  color: $color-accent-primary;
  opacity: 0.7;
  mix-blend-mode: hard-light;
  pointer-events: none;
  z-index: -1;
  display: none; // Hidden by default, shown in animation

  &.layer-1 {
    color: $color-accent-primary;
    clip-path: polygon(0 0, 100% 0, 100% 45%, 0 45%);
    transform: translate(-2px, 0);
    animation: glitch-anim-1 3s infinite linear alternate-reverse;
    display: block;
  }

  &.layer-2 {
    color: $color-accent-secondary;
    clip-path: polygon(0 60%, 100% 60%, 100% 100%, 0 100%);
    transform: translate(2px, 0);
    animation: glitch-anim-2 2.5s infinite linear alternate-reverse;
    display: block;
  }

  @media (max-width: 768px) {
    font-size: 3.5rem;
  }
}

/* --- Separator --- */
/* Removed styles for separator-line as it is commented out */
/* .separator-line {
  width: 60px;
  height: 4px;
  background: linear-gradient(90deg, $color-accent-primary, $color-accent-secondary);
  border-radius: 2px;
  box-shadow: 0 0 10px rgba($color-accent-primary, 0.5);
} */

/* --- Subtitle --- */
.subtitle {
  font-family: 'Inter', sans-serif;
  font-size: clamp(0.8rem, 2vw, 1.2rem);
  font-weight: 300;
  letter-spacing: 4px;
  color: $color-text-secondary;
  text-transform: uppercase;
  margin: 0;
}

/* --- Animations --- */
.fade-slide-down {
  animation: fadeSlideDown 1s ease forwards;
  opacity: 0;
  transform: translateY(-20px);
}

.fade-slide-up {
  animation: fadeSlideUp 1s ease forwards;
  opacity: 0;
  transform: translateY(20px);
  animation-delay: 0.3s;
}

@keyframes fadeSlideDown {
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fadeSlideUp {
  to { opacity: 1; transform: translateY(0); }
}

@keyframes glitch-anim-1 {
  0% { clip-path: inset(20% 0 80% 0); transform: translate(-2px, 1px); }
  20% { clip-path: inset(60% 0 10% 0); transform: translate(2px, -1px); }
  40% { clip-path: inset(40% 0 50% 0); transform: translate(-2px, 2px); }
  60% { clip-path: inset(80% 0 5% 0); transform: translate(2px, -2px); }
  80% { clip-path: inset(10% 0 70% 0); transform: translate(-1px, 1px); }
  100% { clip-path: inset(30% 0 20% 0); transform: translate(1px, -1px); }
}

@keyframes glitch-anim-2 {
  0% { clip-path: inset(10% 0 60% 0); transform: translate(2px, -1px); }
  20% { clip-path: inset(80% 0 5% 0); transform: translate(-2px, 2px); }
  40% { clip-path: inset(30% 0 20% 0); transform: translate(2px, 1px); }
  60% { clip-path: inset(10% 0 80% 0); transform: translate(-1px, -2px); }
  80% { clip-path: inset(50% 0 30% 0); transform: translate(1px, 2px); }
  100% { clip-path: inset(70% 0 10% 0); transform: translate(-2px, -1px); }
}
</style>
