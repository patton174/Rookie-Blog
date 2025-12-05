<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue';

const isOpen = ref(false);
const dropdownRef = ref<HTMLElement | null>(null);

const emit = defineEmits(['login', 'register']);

const toggleDropdown = () => {
  isOpen.value = !isOpen.value;
};

const handleAction = (action: 'login' | 'register') => {
  emit(action);
  isOpen.value = false;
};

const handleClickOutside = (event: MouseEvent) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target as Node)) {
    isOpen.value = false;
  }
};

onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<template>
  <div class="auth-dropdown" ref="dropdownRef">
    <button class="auth-dropdown__trigger" @click="toggleDropdown" :class="{ 'active': isOpen }">
      Account
      <span class="arrow" :class="{ 'arrow--up': isOpen }">▼</span>
    </button>

    <Transition name="dropdown">
      <div v-if="isOpen" class="auth-dropdown__menu">
        <button class="menu-item" @click="handleAction('login')">
          Login
        </button>
        <button class="menu-item" @click="handleAction('register')">
          Register
        </button>
      </div>
    </Transition>
  </div>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.auth-dropdown {
  position: relative;

  &__trigger {
    background: rgba(255, 255, 255, 0.05);
    border: 1px solid $color-border;
    color: $color-text-primary;
    padding: 8px 20px;
    border-radius: 20px;
    font-size: 0.9rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    font-family: $font-family-main;
    display: flex;
    align-items: center;
    gap: 8px;

    &:hover, &.active {
      background: rgba(255, 255, 255, 0.1);
      border-color: $color-accent-primary;
      color: $color-accent-primary;
      box-shadow: 0 0 15px rgba($color-accent-primary, 0.2);
    }

    .arrow {
      font-size: 0.7rem;
      transition: transform 0.3s;

      &--up {
        transform: rotate(180deg);
      }
    }
  }

  &__menu {
    position: absolute;
    top: calc(100% + 10px);
    right: 0;
    width: 140px;
    background: rgba(15, 15, 25, 0.95);
    backdrop-filter: blur(10px);
    border: 1px solid rgba($color-accent-primary, 0.2);
    border-radius: 12px;
    padding: 8px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
    display: flex;
    flex-direction: column;
    gap: 4px;
    z-index: 100;
  }

  .menu-item {
    background: transparent;
    border: none;
    color: $color-text-secondary;
    padding: 10px 16px;
    text-align: left;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.2s;
    font-family: $font-family-main;
    font-size: 0.9rem;

    &:hover {
      background: rgba($color-accent-primary, 0.1);
      color: $color-accent-primary;
    }
  }
}

.dropdown-enter-active,
.dropdown-leave-active {
  transition: opacity 0.2s, transform 0.2s;
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
