<script setup lang="ts">
import { computed } from 'vue';

interface Props {
  size?: 'sm' | 'md' | 'lg';
  variant?: 'ghost' | 'solid' | 'gradient';
  active?: boolean;
  disabled?: boolean;
  label?: string;
  title?: string;
}

const props = withDefaults(defineProps<Props>(), {
  size: 'md',
  variant: 'ghost',
  active: false,
  disabled: false,
});

const emit = defineEmits(['click']);

const classes = computed(() => [
  'icon-btn',
  `icon-btn--${props.size}`,
  `icon-btn--${props.variant}`,
  { 
    'icon-btn--active': props.active,
    'icon-btn--disabled': props.disabled 
  }
]);
</script>

<template>
  <button :class="classes" @click="emit('click')" :title="title || label">
    <div class="icon-btn__icon">
      <slot name="icon"></slot>
    </div>
    <span v-if="label" class="icon-btn__label">{{ label }}</span>
    <div class="icon-btn__glow"></div>
  </button>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.icon-btn {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: none;
  background: transparent; // Borderless / Flat default
  color: $color-icon; // Updated to use icon color variable
  cursor: pointer;
  border-radius: 12px; // Soft rounded corners
  transition: all 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  overflow: hidden;
  font-family: $font-family-main;
  font-weight: 500;
  
  // Reset user agent styles
  padding: 0;
  outline: none;

  // Sizes
  &--sm {
    height: 32px;
    padding: 0 8px;
    min-width: 32px;
    font-size: 0.8rem;

    .icon-btn__icon {
      width: 16px;
      height: 16px;
    }
  }

  &--md {
    height: 40px;
    padding: 0 12px;
    min-width: 40px;
    font-size: 0.9rem;

    .icon-btn__icon {
      width: 20px;
      height: 20px;
    }
  }

  &--lg {
    height: 48px;
    padding: 0 16px;
    min-width: 48px;
    font-size: 1rem;

    .icon-btn__icon {
      width: 24px;
      height: 24px;
    }
  }

  // Variants
  &--ghost {
    background: transparent;
    
    &:hover {
      background: transparent;
    }
  }

  &--solid {
    background: rgba($color-text-primary, 0.1); // Adaptive background
    
    &:hover {
      background: rgba($color-text-primary, 0.15);
    }
  }

  &--gradient {
    background: linear-gradient(135deg, rgba($color-accent-primary, 0.1), rgba($color-accent-secondary, 0.1));
    color: $color-accent-primary;

    &:hover {
      background: linear-gradient(135deg, rgba($color-accent-primary, 0.2), rgba($color-accent-secondary, 0.2));
      box-shadow: 0 0 15px rgba($color-accent-primary, 0.2);
    }
  }

  // Icon Wrapper
  &__icon {
    display: flex;
    align-items: center;
    justify-content: center;
    transition: transform 0.3s ease;
    
    :deep(svg) {
      width: 100%;
      height: 100%;
      fill: currentColor;
    }
  }

  // Glow Effect (Subtle background)
  &__glow {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: radial-gradient(circle at center, rgba($color-text-primary, 0.1) 0%, transparent 70%); // Adaptive glow
    opacity: 0;
    transition: opacity 0.3s ease;
    pointer-events: none;
    z-index: 0;
  }

  // States
  &:hover {
    color: $color-text-primary;
    // background handled by variant
    transform: translateY(-1px);

    .icon-btn__glow {
      opacity: 1;
    }

    .icon-btn__icon {
      transform: scale(1.1);
      color: $color-accent-primary; // Highlight icon
    }
  }

  &:active {
    transform: scale(0.96);
    background: rgba($color-text-primary, 0.05);
  }
  
  // ... (Rest remains same)

  &--active {
    color: $color-accent-primary;
    background: rgba($color-accent-primary, 0.1);

    &:hover {
      background: rgba($color-accent-primary, 0.15);
    }
  }

  // Disabled State
  &--disabled {
    opacity: 0.5;
    cursor: not-allowed;
    pointer-events: none;
    filter: grayscale(100%);
    
    &:hover {
      transform: none;
      background: transparent;
    }
    
    .icon-btn__icon {
      transform: none;
    }
  }
}
</style>
