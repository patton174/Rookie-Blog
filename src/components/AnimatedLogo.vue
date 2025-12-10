<script setup lang="ts">
import { onMounted, ref } from 'vue';

const isAnimating = ref(false);

onMounted(() => {
  setTimeout(() => {
    isAnimating.value = true;
  }, 100);
});
</script>

<template>
  <div class="animated-logo-container">
    <svg viewBox="0 0 100 100" xmlns="http://www.w3.org/2000/svg" class="main-logo-animated">
      <defs>
        <!-- Tech Gradient -->
        <linearGradient id="logo-gradient" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stop-color="#00f2ff" />
          <stop offset="100%" stop-color="#bd00ff" />
        </linearGradient>
        
        <!-- Glow Filter -->
        <filter id="logo-glow" x="-50%" y="-50%" width="200%" height="200%">
          <feGaussianBlur stdDeviation="3" result="blur" />
          <feComposite in="SourceGraphic" in2="blur" operator="over" />
        </filter>
      </defs>

      <!-- Main 'R' Monogram Construction -->
      <g filter="url(#logo-glow)" class="logo-group">
        
        <!-- Left Vertical Bar -->
        <path d="M20 20 L35 20 L30 80 L20 80 Z" 
              fill="url(#logo-gradient)" 
              class="part-vertical" />
              
        <!-- Top Curve Loop -->
        <path d="M35 20 L60 20 C70 20 80 27.5 80 40 C80 50 72.5 57.5 65 60 H50 V47.5 H60 C65 47.5 67.5 45 67.5 42.5 C67.5 40 65 35 60 35 H45 V57.5 H35 V20 Z" 
              fill="url(#logo-gradient)" 
              class="part-loop" />
              
        <!-- Bottom Right Leg -->
        <path d="M55 57.5 H67.5 L80 80 H67.5 L55 57.5 Z" 
              fill="url(#logo-gradient)" 
              class="part-leg" />
        
        <!-- Floating Accent Dot -->
        <circle cx="85" cy="20" r="4" fill="#00f2ff" class="accent-dot" />
      </g>
      
      <!-- Orbiting Particles for "Dynamic" feel removed -->


    </svg>
  </div>
</template>

<style scoped lang="scss">
.animated-logo-container {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  overflow: hidden;
  position: relative;
}

.main-logo-animated {
  width: 140px;
  height: 140px;
  filter: drop-shadow(0 0 10px rgba(0, 242, 255, 0.2));
}

// Animations

.part-vertical {
  opacity: 0;
  transform: translateY(20px);
  animation: slideUp 0.8s cubic-bezier(0.16, 1, 0.3, 1) forwards 0.2s;
}

.part-loop {
  opacity: 0;
  stroke-dasharray: 200;
  stroke-dashoffset: 200;
  fill-opacity: 0;
  animation: drawPath 1.2s cubic-bezier(0.65, 0, 0.35, 1) forwards 0.6s,
             fillFade 0.8s ease-out forwards 1.4s;
  stroke: url(#logo-gradient); // Temporarily stroke for draw effect
  stroke-width: 2;
}

.part-leg {
  opacity: 0;
  transform: translateX(-10px) translateY(-10px);
  animation: slideDiag 0.6s cubic-bezier(0.16, 1, 0.3, 1) forwards 1.2s;
}

.accent-dot {
  transform: scale(0);
  animation: popIn 0.5s cubic-bezier(0.34, 1.56, 0.64, 1) forwards 1.6s,
             pulse 3s ease-in-out infinite 2.2s;
}

.logo-group {
  animation: float 6s ease-in-out infinite 2.5s;
}

.orbit-ring {
  transform-origin: 50px 50px;
  animation: spin 20s linear infinite;
}

.orbit-particle {
  transform-origin: 50px 50px;
  animation: spin 10s linear infinite reverse;
}

@keyframes slideUp {
  to { opacity: 1; transform: translateY(0); }
}

@keyframes drawPath {
  to { stroke-dashoffset: 0; opacity: 1; }
}

@keyframes fillFade {
  to { fill-opacity: 1; stroke-width: 0; }
}

@keyframes slideDiag {
  to { opacity: 1; transform: translate(0, 0); }
}

@keyframes popIn {
  to { transform: scale(1); }
}

@keyframes pulse {
  0%, 100% { fill: #00f2ff; r: 4; }
  50% { fill: #bd00ff; r: 5; }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
