<template>
  <svg 
    xmlns="http://www.w3.org/2000/svg" 
    :width="size" 
    :height="size" 
    viewBox="0 0 24 24" 
    fill="none"
    class="tech-search-icon"
  >
    <defs>
      <!-- Tech Gradient: Cyan to Blue -->
      <linearGradient id="tech-search-grad" x1="0%" y1="0%" x2="100%" y2="100%">
        <stop offset="0%" stop-color="#22d3ee" />
        <stop offset="100%" stop-color="#3b82f6" />
      </linearGradient>
      
      <!-- Glow Filter -->
      <filter id="search-glow" x="-50%" y="-50%" width="200%" height="200%">
        <feGaussianBlur stdDeviation="1.5" result="coloredBlur"/>
        <feMerge>
          <feMergeNode in="coloredBlur"/>
          <feMergeNode in="SourceGraphic"/>
        </feMerge>
      </filter>
    </defs>
    
    <!-- Lens Group -->
    <g class="lens-group">
      <!-- Main Ring -->
      <circle 
        cx="11" cy="11" r="7" 
        stroke="url(#tech-search-grad)" 
        stroke-width="2.2" 
        stroke-linecap="round"
        class="lens"
      />
      
      <!-- Tech Highlight (Reflection) -->
      <path 
        d="M7 9C7 6.79086 8.79086 5 11 5" 
        stroke="white" 
        stroke-width="1.5" 
        stroke-linecap="round" 
        stroke-opacity="0.3"
        class="reflection"
      />
    </g>
    
    <!-- Handle -->
    <path 
      d="M20 20L16.5 16.5" 
      stroke="url(#tech-search-grad)" 
      stroke-width="2.5" 
      stroke-linecap="round" 
      class="handle"
    />
  </svg>
</template>

<script setup lang="ts">
defineProps({
  size: {
    type: [Number, String],
    default: 24
  }
});
</script>

<style scoped>
.tech-search-icon {
  overflow: visible;
  transition: all 0.3s ease;
}

/* Groups */
.lens-group {
  transform-origin: 11px 11px;
  transition: transform 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.lens {
  transition: stroke-width 0.3s ease, filter 0.3s ease;
}

.reflection {
  transition: stroke-opacity 0.3s ease, transform 0.3s ease;
  transform-origin: 11px 11px;
}

.handle {
  transform-origin: 16.5px 16.5px;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1), stroke 0.3s ease;
}

/* Hover State */
.tech-search-icon:hover .lens-group {
  transform: scale(1.05);
}

.tech-search-icon:hover .lens {
  stroke-width: 2.5;
  filter: url(#search-glow);
}

.tech-search-icon:hover .reflection {
  stroke-opacity: 0.8;
  transform: rotate(-15deg);
}

.tech-search-icon:hover .handle {
  transform: translate(2px, 2px);
  stroke: #22d3ee;
  filter: url(#search-glow);
}

/* Active State */
.tech-search-icon:active .lens-group {
  transform: scale(0.95);
}
</style>
