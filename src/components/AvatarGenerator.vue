<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps<{
  username: string;
  size?: number;
}>();

const size = computed(() => props.size || 40);

interface ColorScheme {
  name: string;
  bgStart: string;    // Gradient Start
  bgEnd: string;      // Gradient End
  accent: string;     // Neon Accent
  glow: string;       // Glow Effect Color
  text: string;       // Text Color
}

// 1. Futuristic Tech Color Schemes
const colorSchemes: ColorScheme[] = [
  {
    name: 'Deep Space Cobalt',
    bgStart: '#0f172a',     // Deep Slate
    bgEnd: '#1e1b4b',       // Deep Indigo
    accent: '#38bdf8',      // Cyan
    glow: 'rgba(56, 189, 248, 0.6)',
    text: '#e0f2fe'         // Light Blue
  },
  {
    name: 'Cyber Void',
    bgStart: '#18181b',     // Zinc 900
    bgEnd: '#09090b',       // Zinc 950
    accent: '#22d3ee',      // Electric Blue
    glow: 'rgba(34, 211, 238, 0.5)',
    text: '#ecfeff'         // Cyan White
  },
  {
    name: 'Neon Synth',
    bgStart: '#2e1065',     // Deep Violet
    bgEnd: '#1e1b4b',       // Indigo
    accent: '#a855f7',      // Purple Neon
    glow: 'rgba(168, 85, 247, 0.5)',
    text: '#faf5ff'         // Purple White
  }
];

const selectedScheme = computed(() => {
  if (!props.username) return colorSchemes[0];
  let hash = 0;
  for (let i = 0; i < props.username.length; i++) {
    hash = props.username.charCodeAt(i) + ((hash << 5) - hash);
  }
  const index = Math.abs(hash % colorSchemes.length);
  return colorSchemes[index];
});

const initials = computed(() => {
  return props.username ? props.username.charAt(0).toUpperCase() : '?';
});
</script>

<template>
  <div class="avatar-generator fade-in-entry" :style="{ width: `${size}px`, height: `${size}px` }">
    <svg
      viewBox="0 0 100 100"
      xmlns="http://www.w3.org/2000/svg"
      class="avatar-svg"
    >
      <defs>
        <!-- 3. Tech Texture & Lighting -->
        <linearGradient :id="`tech-grad-${username}`" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" :stop-color="selectedScheme.bgStart" />
          <stop offset="100%" :stop-color="selectedScheme.bgEnd" />
        </linearGradient>

        <!-- Holographic Glitch Effect -->
        <filter id="holo-glitch">
          <feTurbulence type="fractalNoise" baseFrequency="0.5" numOctaves="1" result="noise"/>
          <feColorMatrix type="matrix" values="1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 0.2 0" in="noise" result="coloredNoise"/>
          <feComposite operator="in" in="coloredNoise" in2="SourceGraphic" result="compositeNoise"/>
          <feBlend mode="overlay" in="compositeNoise" in2="SourceGraphic"/>
        </filter>

        <!-- Inner Glow for Neon Text -->
        <filter id="text-glow">
          <feGaussianBlur stdDeviation="1" result="coloredBlur"/>
          <feMerge>
            <feMergeNode in="coloredBlur"/>
            <feMergeNode in="SourceGraphic"/>
          </feMerge>
        </filter>
      </defs>
      
      <!-- Base Background -->
      <circle 
        cx="50" 
        cy="50" 
        r="48" 
        :fill="`url(#tech-grad-${username})`" 
        class="tech-bg"
      />

      <!-- Tech Ring (Rotating) -->
      <path
        d="M50 6 A44 44 0 0 1 94 50"
        fill="none"
        :stroke="selectedScheme.accent"
        stroke-width="2"
        stroke-linecap="round"
        class="tech-ring-1"
      />
      <path
        d="M50 94 A44 44 0 0 1 6 50"
        fill="none"
        :stroke="selectedScheme.accent"
        stroke-width="2"
        stroke-linecap="round"
        opacity="0.5"
        class="tech-ring-2"
      />

      <!-- Decorative Tech Lines -->
      <line x1="50" y1="10" x2="50" y2="15" :stroke="selectedScheme.accent" stroke-width="2" />
      <line x1="50" y1="85" x2="50" y2="90" :stroke="selectedScheme.accent" stroke-width="2" />
      <line x1="10" y1="50" x2="15" y2="50" :stroke="selectedScheme.accent" stroke-width="2" />
      <line x1="85" y1="50" x2="90" y2="50" :stroke="selectedScheme.accent" stroke-width="2" />

      <!-- 2. Custom Tech Typography -->
      <text
        x="50"
        y="55"
        text-anchor="middle"
        :fill="selectedScheme.text"
        font-family="'Orbitron', 'Exo 2', sans-serif" 
        font-weight="700"
        font-size="40"
        filter="url(#text-glow)"
        class="avatar-text"
      >
        {{ initials }}
      </text>
      
      <!-- Scanning Scanline -->
      <rect x="0" y="0" width="100" height="2" fill="rgba(255,255,255,0.1)" class="scanline" />
    </svg>
  </div>
</template>

<style scoped>
/* Import Tech Fonts */
@import url('https://fonts.googleapis.com/css2?family=Orbitron:wght@700&display=swap');

.avatar-generator {
  border-radius: 50%;
  overflow: hidden;
  display: inline-block;
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.5), inset 0 0 10px rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  background: #000; /* Fallback */
}

.avatar-generator:hover {
  box-shadow: 0 0 25px v-bind('selectedScheme.glow'), inset 0 0 15px rgba(255, 255, 255, 0.2);
  transform: scale(1.05);
}

.avatar-svg {
  width: 100%;
  height: 100%;
  display: block;
}

/* Tech Ring Animations */
.tech-ring-1 {
  transform-origin: 50% 50%;
  animation: rotate-cw 10s linear infinite;
}

.tech-ring-2 {
  transform-origin: 50% 50%;
  animation: rotate-ccw 15s linear infinite;
}

@keyframes rotate-cw {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes rotate-ccw {
  from { transform: rotate(360deg); }
  to { transform: rotate(0deg); }
}

/* Scanline Animation */
.scanline {
  animation: scan 3s linear infinite;
  opacity: 0.5;
}

@keyframes scan {
  0% { transform: translateY(-10%); opacity: 0; }
  10% { opacity: 1; }
  90% { opacity: 1; }
  100% { transform: translateY(110%); opacity: 0; }
}

.avatar-text {
  pointer-events: none;
  user-select: none;
}

/* Entry Animation */
.fade-in-entry {
  animation: fadeInTech 0.5s cubic-bezier(0.16, 1, 0.3, 1) forwards;
  opacity: 0;
  transform: scale(0.8);
}

@keyframes fadeInTech {
  to {
    opacity: 1;
    transform: scale(1);
  }
}
</style>
