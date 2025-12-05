<script setup lang="ts">
import { computed } from 'vue';
import Logo from './Logo.vue';
import TextLogo from './TextLogo.vue';

interface Props {
  variant?: 'horizontal' | 'vertical' | 'minimal';
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'horizontal'
});

const emit = defineEmits(['click']);

const handleClick = () => {
  emit('click');
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const containerClass = computed(() => `footer-logo footer-logo--${props.variant}`);
</script>

<template>
  <div :class="containerClass" @click="handleClick">
    <!-- Layout 1: Horizontal (Standard) -->
    <template v-if="variant === 'horizontal'">
      <div class="logo-icon-wrapper">
        <Logo width="48" height="48" />
        <div class="logo-glow"></div>
      </div>
      <div class="logo-text-wrapper">
        <TextLogo width="160" height="32" variant="light" idSuffix="footer_h" />
        <span class="logo-slogan">Tech • Code • Future</span>
      </div>
    </template>

    <!-- Layout 2: Vertical (Centered Stack) -->
    <template v-else-if="variant === 'vertical'">
      <div class="logo-icon-wrapper large">
        <Logo width="64" height="64" />
        <div class="logo-glow"></div>
      </div>
      <TextLogo width="180" height="36" variant="light" class="mt-4" idSuffix="footer_v" />
      <div class="logo-divider"></div>
      <span class="logo-slogan">Exploring the Tech Frontier</span>
    </template>

    <!-- Layout 3: Minimal (Compact/Integrated) -->
    <template v-else>
      <div class="logo-minimal-wrapper">
        <Logo width="40" height="40" class="logo-icon" />
        <span class="logo-text-simple">Rookie<span class="highlight">Blog</span></span>
      </div>
    </template>
  </div>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.footer-logo {
  display: flex;
  cursor: pointer;
  user-select: none;
  transition: opacity 0.3s ease;

  &:hover {
    opacity: 0.95;
  }

  // Layout 1: Horizontal
  &--horizontal {
    align-items: center;
    gap: $spacing-md;

    .logo-icon-wrapper {
      position: relative;
      
      .logo-glow {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 150%;
        height: 150%;
        background: radial-gradient(circle, rgba($color-accent-primary, 0.2) 0%, transparent 70%);
        filter: blur(10px);
        opacity: 0;
        transition: opacity 0.4s ease;
      }
    }

    .logo-text-wrapper {
      display: flex;
      flex-direction: column;
      gap: 4px;
    }

    .logo-slogan {
      font-size: 0.75rem;
      color: $color-text-secondary;
      letter-spacing: 2px;
      text-transform: uppercase;
      opacity: 0.7;
      font-family: $font-family-code;
    }

    &:hover {
      .logo-glow {
        opacity: 1;
      }
      
      .logo-slogan {
        color: $color-accent-primary;
        opacity: 1;
      }
    }
  }

  // Layout 2: Vertical
  &--vertical {
    flex-direction: column;
    align-items: center;
    text-align: center;
    
    .logo-icon-wrapper {
      position: relative;
      transition: transform 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
      
      .logo-glow {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 200%;
        height: 200%;
        background: radial-gradient(circle, rgba($color-accent-secondary, 0.15) 0%, transparent 60%);
        filter: blur(15px);
        z-index: -1;
      }
    }

    .mt-4 {
      margin-top: $spacing-md;
    }

    .logo-divider {
      width: 40px;
      height: 2px;
      background: linear-gradient(90deg, transparent, $color-accent-primary, transparent);
      margin: $spacing-md 0;
      opacity: 0.5;
    }

    .logo-slogan {
      font-size: 0.9rem;
      color: $color-text-secondary;
      letter-spacing: 1px;
    }

    &:hover {
      .logo-icon-wrapper {
        transform: translateY(-5px) scale(1.05);
      }
      
      .logo-divider {
        width: 80px;
        opacity: 1;
      }
    }
  }

  // Layout 3: Minimal
  &--minimal {
    align-items: center;
    background: rgba(255, 255, 255, 0.03);
    padding: 8px 20px 8px 12px;
    border-radius: 30px;
    border: 1px solid rgba(255, 255, 255, 0.05);
    transition: all 0.3s ease;

    .logo-minimal-wrapper {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .logo-text-simple {
      font-family: $font-family-code;
      font-size: 1.2rem;
      font-weight: 700;
      color: $color-text-primary;
      letter-spacing: -0.5px;

      .highlight {
        color: $color-accent-primary;
      }
    }

    &:hover {
      background: rgba(255, 255, 255, 0.08);
      border-color: rgba($color-accent-primary, 0.3);
      box-shadow: 0 0 20px rgba($color-accent-primary, 0.1);
      transform: translateY(-2px);
    }
  }
}

// Responsive adjustments
@media (max-width: $breakpoint-mobile) {
  .footer-logo {
    &--horizontal {
      flex-direction: column;
      text-align: center;
      
      .logo-text-wrapper {
        align-items: center;
      }
    }
  }
}
</style>
