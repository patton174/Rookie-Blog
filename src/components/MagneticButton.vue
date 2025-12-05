<script setup lang="ts">
// import { ref } from 'vue';

const { variant = 'primary' } = defineProps<{ variant?: 'primary' | 'secondary' | 'outline' }>();
</script>

<template>
  <button 
    class="magnetic-btn" 
    :class="[`magnetic-btn--${variant}`]"
  >
    <span class="magnetic-btn__content">
      <span v-if="$slots.icon" class="magnetic-btn__icon">
        <slot name="icon"></slot>
      </span>
      <slot></slot>
    </span>
    <div class="magnetic-btn__glow"></div>
    <div class="magnetic-btn__particles" v-if="variant === 'primary'"></div>
  </button>
</template>

<style lang="scss" scoped>
@use 'sass:color';
@use '../styles/variables' as *;

.magnetic-btn {
  position: relative;
  padding: 14px 36px;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-family: 'Inter', sans-serif;
  font-weight: 400; /* Thinner font weight */
  font-size: 1rem;
  transition: transform 0.2s cubic-bezier(0.25, 0.46, 0.45, 0.94), box-shadow 0.3s ease, background 0.3s ease;
  overflow: hidden;
  z-index: 1;
  letter-spacing: 0.5px;
  display: inline-flex;
  align-items: center;
  justify-content: center;

  &__content {
    position: relative;
    z-index: 2;
    display: flex;
    align-items: center;
    gap: 10px;
  }

  &__icon {
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.2rem;
  }

  &__glow {
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255,255,255,0.2) 0%, transparent 60%);
    opacity: 0;
    transform: scale(0.5);
    transition: opacity 0.3s, transform 0.3s;
    z-index: 1;
    pointer-events: none;
  }

  &:hover {
    transform: scale(1.05); /* Subtle scale instead of drag */
  }

  &:hover &__glow {
    opacity: 1;
    transform: scale(1);
  }

  // Variants
  &--primary {
    background: $color-btn-primary-bg;
    color: $color-btn-primary-text;
    border: 1px solid $color-btn-primary-border;
    box-shadow: 0 4px 12px $shadow-card;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      // Subtle gradient for light mode, or keep original for dark?
      // Using accent colors with low opacity works for both if accents are correct.
      background: linear-gradient(135deg, rgba($color-accent-primary, 0.1) 0%, rgba($color-accent-secondary, 0.1) 100%);
      z-index: 0;
      transition: opacity 0.3s ease;
    }

    &:hover {
      background: $color-btn-primary-hover-bg;
      border-color: $color-accent-primary;
      box-shadow: 0 6px 16px rgba($color-accent-primary, 0.2);
      
      &::before {
        opacity: 0.8;
      }
    }
    
    &:active {
      background: $color-btn-primary-active-bg;
      transform: scale(0.98);
    }
  }

  &--outline {
    background: transparent;
    border: 1px solid rgba($color-text-secondary, 0.3);
    color: $color-text-secondary;
    backdrop-filter: blur(5px);

    &:hover {
      background: rgba($color-text-primary, 0.05); // Adaptive hover
      border-color: $color-text-primary;
      color: $color-text-primary;
      box-shadow: 0 0 15px rgba($color-text-primary, 0.1);
    }
    
    &:active {
      transform: scale(0.98);
    }
  }
}
</style>
