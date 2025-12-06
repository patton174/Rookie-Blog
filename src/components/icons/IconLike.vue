<template>
  <SvgIcon 
    name="icon-like" 
    :size="size" 
    :style="{ 
      fill: filled ? 'currentColor' : 'none', 
      stroke: filled ? 'none' : 'currentColor',
      strokeWidth: '2',
      strokeLinecap: 'round',
      strokeLinejoin: 'round'
    }"
  />
</template>

<script setup lang="ts">
import SvgIcon from '../SvgIcon.vue';

withDefaults(defineProps<{ 
  size?: number; 
  filled?: boolean 
}>(), {
  size: 24,
  filled: false
});
</script>
