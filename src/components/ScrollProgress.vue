<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';

const progress = ref(0);
let ticking = false;

const updateProgress = () => {
  const scrollTop = window.scrollY;
  const docHeight = document.documentElement.scrollHeight - window.innerHeight;
  const newProgress = (scrollTop / docHeight) * 100;
  
  // Only update if changed significantly to avoid micro-updates
  if (Math.abs(newProgress - progress.value) > 0.1) {
    progress.value = newProgress;
  }
  ticking = false;
};

const onScroll = () => {
  if (!ticking) {
    window.requestAnimationFrame(updateProgress);
    ticking = true;
  }
};

onMounted(() => {
  window.addEventListener('scroll', onScroll, { passive: true });
});

onUnmounted(() => {
  window.removeEventListener('scroll', onScroll);
});
</script>

<template>
  <div class="scroll-progress">
    <div class="scroll-progress__bar" :style="{ width: `${progress}%` }"></div>
  </div>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.scroll-progress {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 3px;
  z-index: 2000;
  background: rgba($color-bg-secondary, 0.5);

  &__bar {
    height: 100%;
    background: $gradient-primary;
    box-shadow: 0 0 10px $color-accent-primary;
    transition: width 0.1s linear;
  }
}
</style>
