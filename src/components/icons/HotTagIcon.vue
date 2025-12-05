<template>
  <svg 
    width="24" 
    height="24" 
    viewBox="0 0 24 24" 
    fill="none" 
    xmlns="http://www.w3.org/2000/svg"
    class="hot-tag-icon"
  >
    <defs>
      <linearGradient id="techFireGradient" x1="12" y1="2" x2="12" y2="22" gradientUnits="userSpaceOnUse">
        <stop offset="0%" stop-color="#FF512F" />
        <stop offset="100%" stop-color="#DD2476" />
      </linearGradient>
      <filter id="glow" x="-4" y="-4" width="32" height="32" filterUnits="userSpaceOnUse">
        <feGaussianBlur stdDeviation="1.5" result="blur" />
        <feComposite in="SourceGraphic" in2="blur" operator="over" />
      </filter>
    </defs>
    
    <!-- Tech Circuit Elements -->
    <path d="M12 22C16.4183 22 20 18.4183 20 14C20 8 12 2 12 2C12 2 4 8 4 14C4 18.4183 7.58172 22 12 22Z" stroke="url(#techFireGradient)" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round" fill="url(#techFireGradient)" fill-opacity="0.1"/>
    
    <!-- Core Flame -->
    <path d="M12 18C14.2091 18 16 16.2091 16 14C16 11 12 6 12 6C12 6 8 11 8 14C8 16.2091 9.79086 18 12 18Z" fill="url(#techFireGradient)" />
    
    <!-- Digital Spark/Circuit -->
    <circle cx="12" cy="14" r="1.5" fill="white" />
    <path d="M12 14V11" stroke="white" stroke-width="1" stroke-linecap="round" />
    
    <!-- Floating Bits -->
    <rect x="16" y="8" width="2" height="2" rx="0.5" fill="#FF512F" opacity="0.8" />
    <rect x="6" y="10" width="2" height="2" rx="0.5" fill="#FF512F" opacity="0.6" />
    <rect x="15" y="17" width="1.5" height="1.5" rx="0.5" fill="#FF512F" opacity="0.7" />
  </svg>
</template>

<style scoped>
.hot-tag-icon {
  display: inline-block;
  vertical-align: middle;
  filter: drop-shadow(0 2px 4px rgba(221, 36, 118, 0.3));
  transition: transform 0.3s ease;
}

.hot-tag-icon:hover {
  transform: scale(1.1) rotate(5deg);
}
</style>
