<template>
  <svg xmlns="http://www.w3.org/2000/svg" :width="size" :height="size" viewBox="0 0 48 48" fill="none">
    <defs>
      <linearGradient id="zh_gradient_v2" x1="0" y1="0" x2="48" y2="48" gradientUnits="userSpaceOnUse">
        <stop offset="0%" stop-color="#ef4444" /> <!-- Red 500 -->
        <stop offset="100%" stop-color="#f59e0b" /> <!-- Amber 500 -->
      </linearGradient>
    </defs>
    
    <!-- Geometric "中" (Zhong) -->
    <!-- Central vertical line with slight extension -->
    <path 
      d="M24 6V42" 
      stroke="url(#zh_gradient_v2)" 
      stroke-width="3" 
      stroke-linecap="round" 
      stroke-linejoin="round"
    />
    
    <!-- The "Mouth" (Kou) part - Rounded Rectangle -->
    <rect 
      x="10" 
      y="14" 
      width="28" 
      height="20" 
      rx="4" 
      stroke="url(#zh_gradient_v2)" 
      stroke-width="3" 
      stroke-linejoin="round"
    />
    
    <!-- Subtle accent dot for balance -->
    <circle cx="36" cy="10" r="2" fill="url(#zh_gradient_v2)" />
  </svg>
</template>

<script setup lang="ts">
defineProps({
  size: {
    type: [Number, String],
    default: 48
  }
});
</script>
