<script setup lang="ts">
import { useToast } from '../composables/useToast';

const { toasts, removeToast } = useToast();
</script>

<template>
  <div class="toast-container">
    <transition-group name="toast">
      <div 
        v-for="toast in toasts" 
        :key="toast.id" 
        class="toast-item"
        :class="`toast-${toast.type}`"
        @click="removeToast(toast.id)"
      >
        <span class="toast-icon">
          <span v-if="toast.type === 'success'">✓</span>
          <span v-else-if="toast.type === 'error'">✕</span>
          <span v-else-if="toast.type === 'warning'">⚠</span>
          <span v-else>ℹ</span>
        </span>
        <span class="toast-message">{{ toast.message }}</span>
      </div>
    </transition-group>
  </div>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.toast-container {
  position: fixed;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  display: flex;
  flex-direction: column;
  gap: 10px;
  pointer-events: none; // Allow clicks to pass through container
}

.toast-item {
  pointer-events: auto;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  padding: 10px 20px;
  border-radius: 50px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 200px;
  cursor: pointer;
  border: 1px solid rgba(0, 0, 0, 0.05);
  font-weight: 500;
  font-size: 0.95rem;
  
  // Dark mode support
  @media (prefers-color-scheme: dark) {
    background: rgba(30, 41, 59, 0.9);
    color: #fff;
    border-color: rgba(255, 255, 255, 0.1);
  }

  &.toast-success {
    color: #10b981;
    .toast-icon { background: rgba(16, 185, 129, 0.1); }
  }
  
  &.toast-error {
    color: #ef4444;
    .toast-icon { background: rgba(239, 68, 68, 0.1); }
  }
  
  &.toast-warning {
    color: #f59e0b;
    .toast-icon { background: rgba(245, 158, 11, 0.1); }
  }
  
  &.toast-info {
    color: #3b82f6;
    .toast-icon { background: rgba(59, 130, 246, 0.1); }
  }
}

.toast-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: bold;
}

// Transitions
.toast-enter-active,
.toast-leave-active {
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.toast-enter-from {
  opacity: 0;
  transform: translateY(-20px) scale(0.9);
}

.toast-leave-to {
  opacity: 0;
  transform: translateY(-20px) scale(0.9);
}
</style>
