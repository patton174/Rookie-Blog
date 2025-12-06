<template>
  <svg 
    :width="size" 
    :height="size" 
    :class="['svg-icon', className]"
    aria-hidden="true"
  >
    <use :href="'#' + name" />
  </svg>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
  name: string;
  size?: number | string;
  className?: string;
}>(), {
  size: 24,
  className: ''
});
</script>

<style scoped>
.svg-icon {
  /* Default styles, can be overridden */
  display: inline-block;
  vertical-align: middle;
}
</style>
