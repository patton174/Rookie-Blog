<script setup lang="ts">
import { computed } from 'vue';

const props = withDefaults(defineProps<{
  width?: number | string;
  height?: number | string;
  variant?: 'light' | 'dark';
  idSuffix?: string;
}>(), {
  width: 200,
  height: 40,
  variant: 'light',
  idSuffix: 'main'
});

const gradientId = computed(() => `text_gradient_${props.idSuffix}`);
const glowId = computed(() => `text_glow_${props.idSuffix}`);
const gradientUrl = computed(() => `url(#${gradientId.value})`);
const glowUrl = computed(() => `url(#${glowId.value})`);
</script>

<template>
  <svg :width="width" :height="height" viewBox="0 0 240 40" fill="none" xmlns="http://www.w3.org/2000/svg" aria-label="Rookie Blog">
    <defs>
      <linearGradient :id="gradientId" x1="0" y1="0" x2="240" y2="40" gradientUnits="userSpaceOnUse">
        <template v-if="variant === 'dark'">
          <stop offset="0%" stop-color="#00f2ff" />
          <stop offset="50%" stop-color="#ffffff" />
          <stop offset="100%" stop-color="#bd00ff" />
        </template>
        <template v-else>
          <!-- Light mode: Darker, higher contrast gradient -->
          <stop offset="0%" stop-color="#00a8b3" />
          <stop offset="50%" stop-color="#2c3e50" />
          <stop offset="100%" stop-color="#8e00c2" />
        </template>
      </linearGradient>
      
      <filter :id="glowId" x="-20%" y="-20%" width="140%" height="140%">
        <feGaussianBlur stdDeviation="2" result="blur" />
        <feComposite in="SourceGraphic" in2="blur" operator="over" />
      </filter>
    </defs>

    <g class="logo-text">
      <!-- "Rookie" - Stylized with tech cuts -->
      <path d="M10 30V10H18C22 10 24 12 24 16C24 19 22.5 21 20 22L25 30H21L16.5 22H13V30H10Z" :fill="gradientUrl" />
      <path d="M28 20C28 14.5 32.5 10 38 10C43.5 10 48 14.5 48 20C48 25.5 43.5 30 38 30C32.5 30 28 25.5 28 20ZM31 20C31 23.8 34.2 27 38 27C41.8 27 45 23.8 45 20C45 16.2 41.8 13 38 13C34.2 13 31 16.2 31 20Z" :fill="gradientUrl" />
      <path d="M52 20C52 14.5 56.5 10 62 10C67.5 10 72 14.5 72 20C72 25.5 67.5 30 62 30C56.5 30 52 25.5 52 20ZM55 20C55 23.8 58.2 27 62 27C65.8 27 69 23.8 69 20C69 16.2 65.8 13 62 13C58.2 13 55 16.2 55 20Z" :fill="gradientUrl" />
      <path d="M76 10V30H79V22L84 17V10H81V15L79 17V10H76ZM84 30V22L81 25V30H84Z" :fill="gradientUrl" />
      <path d="M88 10V30H91V10H88Z" :fill="gradientUrl" />
      <path d="M96 10V30H106V27H99V21H105V18H99V13H106V10H96Z" :fill="gradientUrl" />

      <!-- Separator Dot -->
      <circle cx="116" cy="25" r="2" :fill="variant === 'dark' ? '#00f2ff' : '#00a8b3'" :filter="glowUrl" />

      <!-- "Blog" - Stylized -->
      <g :fill="variant === 'dark' ? '#ffffff' : '#2c3e50'" :fill-opacity="variant === 'dark' ? 0.9 : 1">
        <path d="M126 10V30H134C137.5 30 140 28 140 25C140 22.5 138.5 21 136 20.5C138 20 139 18.5 139 16.5C139 13 136.5 10 133 10H126ZM129 13H133C135 13 136 14 136 16C136 18 135 19 133 19H129V13ZM129 21H134C136 21 137 22.5 137 25C137 27.5 136 28 133 28H129V21Z" />
        <path d="M144 10V30H154V27H147V10H144Z" />
        <path d="M158 20C158 14.5 162.5 10 168 10C173.5 10 178 14.5 178 20C178 25.5 173.5 30 168 30C162.5 30 158 25.5 158 20ZM161 20C161 23.8 164.2 27 168 27C171.8 27 175 23.8 175 20C175 16.2 171.8 13 168 13C164.2 13 161 16.2 161 20Z" />
        <path d="M182 20C182 14.5 186.5 10 192 10H198V13H192C188.5 13 186 15.5 186 19V20H194V30H191V23H186V24C186 27.5 188.5 30 192 30H195C196.5 30 197.5 29 198 27.5V22H201V28C200 30.5 197.5 33 194 33H191C185.5 33 182 28.5 182 24V20Z" />
      </g>
    </g>
  </svg>
</template>

<style scoped>
.logo-text {
  font-family: 'Fira Code', monospace; /* Fallback if SVG fails to load */
}
</style>
