<template>
  <SvgIcon 
    name="icon-share" 
    :size="size"
    :style="{ 
      fill: 'none', 
      stroke: 'currentColor',
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
}>(), {
  size: 24
});
</script>
