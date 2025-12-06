<script setup lang="ts">
import TechIcon from './icons/TechIcon.vue';
const props = defineProps<{ label: string; size?: 'sm' | 'md'; showIcon?: boolean }>();
const sizeClass = props.size === 'sm' ? 'tag-badge--sm' : 'tag-badge--md';
</script>

<template>
  <router-link :to="{ name: 'search', query: { q: props.label } }" class="tag-badge" :class="sizeClass" aria-label="Tag">
    <TechIcon v-if="props.showIcon !== false" :name="props.label" :size="sizeClass === 'tag-badge--sm' ? 14 : 16" />
    <span class="tag-badge__text">{{ props.label }}</span>
  </router-link>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.tag-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  border-radius: $radius-full;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: $color-text-primary;
  text-decoration: none;
  transition: $transition-base;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.05);
  will-change: transform;
}

.tag-badge:hover {
  transform: translateY(-1px);
  border-color: $color-accent-primary;
  box-shadow: 0 4px 12px rgba($color-accent-primary, 0.18);
  background: rgba($color-accent-primary, 0.12);
}

.tag-badge--sm {
  padding: 4px 10px;
}

.tag-badge__text {
  font-size: 0.85rem;
  font-weight: 600;
}
</style>
