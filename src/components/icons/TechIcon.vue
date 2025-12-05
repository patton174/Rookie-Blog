<script setup lang="ts">
import { computed } from 'vue';

const props = withDefaults(defineProps<{
  name: string;
  size?: number | string;
}>(), {
  size: 16
});

// Simple hash function to generate a color from string if needed, 
// though we will mostly use specific brand colors
const getBrandColor = (name: string) => {
  const n = name.toLowerCase();
  if (n.includes('java')) return '#f89820';
  if (n.includes('spring')) return '#6db33f';
  if (n.includes('vue')) return '#42b883';
  if (n.includes('typescript')) return '#3178c6';
  if (n.includes('docker')) return '#2496ed';
  if (n.includes('kubernetes')) return '#326ce5';
  if (n.includes('aws')) return '#ff9900';
  if (n.includes('redis')) return '#dc382d';
  if (n.includes('mysql')) return '#4479a1';
  if (n.includes('react')) return '#61dafb';
  return 'currentColor';
};

const iconPath = computed(() => {
  const n = props.name.toLowerCase();
  
  // Java
  if (n.includes('java')) return 'M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm-1-13h2v6h-2zm0 8h2v2h-2z'; // Generic placeholder for complex logos, or simplified versions
  
  // Spring
  if (n.includes('spring')) return 'M12 2L2 7v10l10 5 10-5V7L12 2zm0 2.8l6 3v6.4l-6 3-6-3V7.8l6-3z'; // Leaf-like simplified
  
  // Vue
  if (n.includes('vue')) return 'M12 22L2 4h3.2l6.8 12.4L18.8 4H22L12 22zm0-5l-3.4-6H15.4L12 17z';
  
  // TypeScript
  if (n.includes('typescript')) return 'M4 2h16a2 2 0 0 1 2 2v16a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2zm10 13.5h-2v-7h-2.5V6h7v2.5H14v7zm-5 0H7v-2.5h2v-2H7V6h4v2.5H9v2h2v3z';
  
  // Docker
  if (n.includes('docker')) return 'M2.5 11.5v5h19v-5h-19zm2 2h2v1h-2v-1zm4 0h2v1h-2v-1zm4 0h2v1h-2v-1zm-6-3h2v1h-2v-1zm4 0h2v1h-2v-1zm4 0h2v1h-2v-1zm-8-3h2v1h-2v-1zm4 0h2v1h-2v-1z';
  
  // Kubernetes
  if (n.includes('kubernetes')) return 'M12 2l8.5 5v10L12 22 3.5 17V7L12 2zm0 2.5L5.8 8v8l6.2 3.5 6.2-3.5V8L12 4.5z';
  
  // AWS
  if (n.includes('aws')) return 'M12 16.5c-2.3 0-4.3-.8-5.8-2.2l1.4-1.4c1.1 1 2.6 1.6 4.4 1.6 2.4 0 3.8-1.2 3.8-3s-1.4-3-3.8-3c-1.5 0-2.8.5-3.7 1.4l-1.3-1.4C8.2 7.3 10 6.5 12 6.5c3.6 0 6.2 2 6.2 5s-2.6 5-6.2 5z';
  
  // Redis
  if (n.includes('redis')) return 'M4 4h16v16H4V4zm2 2v3h12V6H6zm0 5v7h12v-7H6z';
  
  // MySQL
  if (n.includes('mysql')) return 'M12 2C6.5 2 2 6.5 2 12s4.5 10 10 10 10-4.5 10-10S17.5 2 12 2zm0 16c-3.3 0-6-2.7-6-6s2.7-6 6-6 6 2.7 6 6-2.7 6-6 6zm-2-6h4v2h-4v-2z';
  
  // Default / Fallback (Hash/Tag icon)
  return 'M12 2L2 7l10 5 10-5-10-5zm0 9l2-1-2-1-2 1 2 1zm0 2l-5-2.5-5 2.5L12 22l10-9-5-2.5-5 2.5z'; 
});

// Use specific SVG paths for better logos
const isCustomSvg = computed(() => {
  const n = props.name.toLowerCase();
  return ['java', 'spring', 'vue', 'typescript', 'docker', 'kubernetes', 'aws', 'redis', 'mysql'].some(k => n.includes(k));
});
</script>

<template>
  <svg 
    :width="size" 
    :height="size" 
    viewBox="0 0 24 24" 
    fill="none" 
    xmlns="http://www.w3.org/2000/svg"
    class="tech-icon"
    :style="{ color: getBrandColor(name) }"
  >
    <template v-if="isCustomSvg">
       <path :d="iconPath" fill="currentColor" />
    </template>
    <template v-else>
      <!-- Default Tag Icon -->
      <path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
      <line x1="7" y1="7" x2="7.01" y2="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
    </template>
  </svg>
</template>

<style scoped>
.tech-icon {
  display: inline-block;
  vertical-align: middle;
  transition: transform 0.3s ease;
}
</style>
