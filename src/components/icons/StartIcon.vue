<script setup lang="ts">
import { computed } from 'vue';

interface Props {
  variant?: 'rocket-minimal' | 'rocket-metallic' | 'rocket-dynamic';
  size?: number;
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'rocket-minimal',
  size: 20
});

const isMinimal = computed(() => props.variant === 'rocket-minimal');
const isMetallic = computed(() => props.variant === 'rocket-metallic');

</script>

<template>
  <!-- 
    OPTION 1: Minimal Line (极简线条)
    - Style: Clean strokes, GitHub-like simplicity, sharp angles
    - Color: CurrentColor (inherits button text)
  -->
  <svg 
    v-if="isMinimal"
    xmlns="http://www.w3.org/2000/svg" 
    :width="size" 
    :height="size" 
    viewBox="0 0 24 24" 
    fill="none"
    stroke="currentColor"
    stroke-width="2"
    stroke-linecap="round"
    stroke-linejoin="round"
    class="rocket-icon minimal"
  >
    <path d="M4.5 16.5c-1.5 1.26-2 5-2 5s3.74-.5 5-2c.71-.84.7-2.13-.09-2.91a2.18 2.18 0 0 0-2.91-.09z" />
    <path d="M12 15l-3-3a22 22 0 0 1 2-3.95A12.88 12.88 0 0 1 22 2c0 2.72-.78 7.5-6 11a22.35 22.35 0 0 1-4 2z" />
    <path d="M9 12H4s.55-3.03 2-4c1.62-1.08 5 0 5 0" />
    <path d="M12 15v5s3.03-.55 4-2c1.08-1.62 0-5 0-5" />
  </svg>

  <!-- 
    OPTION 2: Metallic Solid (金属质感)
    - Style: Solid fill, subtle gradients/shadows for 3D feel (GitHub logo style)
    - Color: Silver/Grey with depth
  -->
  <svg 
    v-else-if="isMetallic"
    xmlns="http://www.w3.org/2000/svg" 
    :width="size" 
    :height="size" 
    viewBox="0 0 24 24" 
    fill="none"
    class="rocket-icon metallic"
  >
    <defs>
      <linearGradient id="metal-grad" x1="0" y1="0" x2="1" y2="1">
        <stop offset="0%" stop-color="#FFFFFF" />
        <stop offset="100%" stop-color="#B0BEC5" />
      </linearGradient>
      <filter id="drop-shadow" x="-20%" y="-20%" width="140%" height="140%">
        <feDropShadow dx="0" dy="1" stdDeviation="1" flood-color="#000" flood-opacity="0.3" />
      </filter>
    </defs>
    
    <!-- Main Body -->
    <path 
      d="M13 2.05S13.5 7 17 11c3 3.5 5 5 5 5s-2.5 1-5 2l-4-4-4 4c-1 2.5-2 5-2 5s1.5-2 5-5l4-4c1-3.5 2-8.95 2-8.95z" 
      fill="url(#metal-grad)" 
      filter="url(#drop-shadow)"
    />
    <!-- Fin Detail -->
    <path d="M6 9l4 4-3 3c-2 2-3 3-3 3s1-4 3-6l3-3" fill="#90A4AE" />
    <path d="M15 18l-4-4 3-3c2-2 6-3 6-3s-1 4-3 6l-3 3" fill="#90A4AE" opacity="0.5"/>
  </svg>

  <!-- 
    OPTION 3: Dynamic Thrust (动态推进)
    - Style: Modern flat, high contrast, motion lines
    - Color: White body, Blue accent
  -->
  <svg 
    v-else
    xmlns="http://www.w3.org/2000/svg" 
    :width="size" 
    :height="size" 
    viewBox="0 0 24 24" 
    fill="none"
    class="rocket-icon dynamic"
  >
    <!-- Thrust Lines -->
    <g class="thrust-lines">
      <path d="M4 22l2-6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" opacity="0.5" />
      <path d="M2 20l2-6" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" opacity="0.3" />
    </g>
    
    <!-- Rocket Body -->
    <path 
      d="M14.5 4.5C14.5 4.5 15 8 18 11C20.5 13.5 22 15 22 15C22 15 20 16 17 15L13 11L9 15C7 17 6 19 6 19C6 19 7 17 9 15L13 11C10 8 8.5 4.5 8.5 4.5C8.5 4.5 11.5 2.5 14.5 4.5Z" 
      fill="currentColor" 
      stroke="currentColor" 
      stroke-width="1.5" 
      stroke-linejoin="round"
    />
    <!-- Window -->
    <circle cx="14" cy="9" r="1.5" fill="white" stroke="none" />
  </svg>
</template>

<style scoped>
.rocket-icon {
  display: inline-block;
  vertical-align: middle;
  overflow: visible;
}

/* Minimal: Subtle hover lift */
.minimal {
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

/* Metallic: Shine effect */
.metallic path {
  transition: filter 0.3s ease;
}

/* Dynamic: Thrust animation */
.dynamic .thrust-lines path {
  stroke-dasharray: 10;
  stroke-dashoffset: 10;
  animation: thrust 1s linear infinite;
}

@keyframes thrust {
  to {
    stroke-dashoffset: 0;
    opacity: 0;
  }
}
</style>
