<script setup lang="ts">
// No props needed, controlled by v-if in parent
</script>

<template>
  <div class="loader-container">
    <svg class="loader-svg" viewBox="0 0 600 400" xmlns="http://www.w3.org/2000/svg">
      <!-- Defs for gradients/filters -->
      <defs>
        <filter id="glow" x="-20%" y="-20%" width="140%" height="140%">
          <feGaussianBlur stdDeviation="2" result="blur" />
          <feComposite in="SourceGraphic" in2="blur" operator="over" />
        </filter>
        <linearGradient id="steam-gradient" x1="0%" y1="100%" x2="0%" y2="0%">
          <stop offset="0%" stop-color="var(--color-accent-primary)" stop-opacity="0" />
          <stop offset="50%" stop-color="var(--color-accent-primary)" stop-opacity="0.5" />
          <stop offset="100%" stop-color="var(--color-accent-primary)" stop-opacity="0" />
        </linearGradient>
      </defs>

      <!-- Stage 1: Window Container (Path Drawing + Fade Fill) -->
      <g class="window-group">
        <rect class="window-bg" x="100" y="50" width="400" height="300" rx="10" ry="10" />
        <rect class="window-border" x="100" y="50" width="400" height="300" rx="10" ry="10" />
        
        <!-- Window Controls -->
        <g class="window-controls">
          <circle cx="125" cy="75" r="6" fill="#ff5f56" />
          <circle cx="145" cy="75" r="6" fill="#ffbd2e" />
          <circle cx="165" cy="75" r="6" fill="#27c93f" />
        </g>
        
        <!-- Window Title -->
        <text x="300" y="80" text-anchor="middle" class="window-title">Main.java</text>
      </g>

      <!-- Stage 2: Java Coffee Cup (Morph/Draw) -->
      <g class="java-icon" transform="translate(260, 120) scale(0.8)">
        <!-- Cup Body -->
        <path class="cup-path" d="M10,20 L90,20 C90,70 75,90 50,90 C25,90 10,70 10,20 Z" />
        <!-- Handle -->
        <path class="cup-handle" d="M90,35 C105,35 105,65 90,65" />
        <!-- Steam -->
        <path class="steam steam-1" d="M30,0 Q40,-20 30,-40" />
        <path class="steam steam-2" d="M50,0 Q60,-20 50,-40" />
        <path class="steam steam-3" d="M70,0 Q80,-20 70,-40" />
      </g>

      <!-- Stage 3: Code Typing -->
      <g class="code-group" transform="translate(130, 230)">
        <!-- Line 1 -->
        <text class="code-line line-1" x="0" y="0">
          <tspan class="keyword">public</tspan> <tspan class="keyword">static</tspan> <tspan class="keyword">void</tspan> <tspan class="func">main</tspan>(<tspan class="type">String</tspan>[] args) {
        </text>
        
        <!-- Line 2 -->
        <text class="code-line line-2" x="20" y="30">
          <tspan class="obj">System</tspan>.out.println(<tspan class="string">"Hello World"</tspan>);
        </text>
        
        <!-- Line 3 -->
        <text class="code-line line-3" x="0" y="60">}</text>
      </g>

      <!-- Stage 4: Output/Result -->
      <g class="output-group" transform="translate(300, 320)">
        <text class="output-text" text-anchor="middle">Hello World</text>
      </g>
    </svg>
  </div>
</template>

<style scoped lang="scss">
.loader-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: var(--color-bg-primary);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 999; /* Lower than NavBar (1000) */
  /* Removed transition/opacity logic here, handled by parent transition */
}

.loader-svg {
  width: 100%;
  max-width: 600px;
  height: auto;
  
  // Responsive scaling
  @media (max-width: 768px) {
    max-width: 90%;
  }
}

// --- Stage 1: Window ---
.window-border {
  fill: none;
  stroke: var(--color-border);
  stroke-width: 2;
  stroke-dasharray: 1400;
  stroke-dashoffset: 1400;
  // Smoother draw
  animation: drawBorder 0.5s cubic-bezier(0.33, 1, 0.68, 1) forwards;
}

.window-bg {
  fill: var(--color-bg-secondary);
  opacity: 0;
  animation: fadeInWindow 0.4s ease 0.2s forwards;
}

.window-controls circle {
  opacity: 0;
  transform: scale(0);
  transform-origin: center;
  // Bouncy pop
  animation: popIn 0.4s cubic-bezier(0.34, 1.56, 0.64, 1) forwards;
}
.window-controls circle:nth-child(1) { animation-delay: 0.3s; }
.window-controls circle:nth-child(2) { animation-delay: 0.35s; }
.window-controls circle:nth-child(3) { animation-delay: 0.4s; }

.window-title {
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 14px;
  fill: var(--color-text-tertiary);
  opacity: 0;
  animation: fadeIn 0.4s ease 0.4s forwards;
}

// --- Stage 2: Java Icon ---
.cup-path, .cup-handle {
  fill: none;
  stroke: var(--color-accent-primary);
  stroke-width: 4;
  stroke-linecap: round;
  stroke-linejoin: round;
  stroke-dasharray: 300;
  stroke-dashoffset: 300;
  // Start earlier, overlapping with window creation
  animation: drawCup 0.6s cubic-bezier(0.33, 1, 0.68, 1) 0.3s forwards, fillCup 0.5s ease 0.8s forwards;
}

.steam {
  fill: none;
  stroke: var(--color-accent-secondary);
  stroke-width: 3;
  stroke-linecap: round;
  opacity: 0;
  stroke-dasharray: 50;
  stroke-dashoffset: 50;
}

.steam-1 { animation: riseSteam 1.5s infinite 0.8s, drawSteam 0.5s ease 0.8s forwards; }
.steam-2 { animation: riseSteam 1.5s infinite 0.9s, drawSteam 0.5s ease 0.9s forwards; }
.steam-3 { animation: riseSteam 1.5s infinite 1.0s, drawSteam 0.5s ease 1.0s forwards; }

// --- Stage 3: Code Typing ---
.code-group text {
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 16px;
  fill: var(--color-text-primary);
}

.keyword { fill: var(--color-accent-secondary); font-weight: bold; }
.type { fill: var(--color-accent-tertiary); }
.func { fill: var(--color-accent-primary); }
.string { fill: var(--color-code-string); }
.obj { fill: var(--color-text-primary); font-weight: bold; }

.code-line {
  opacity: 0;
  transform: translateX(-10px);
  // Faster slide
  animation: slideInCode 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards;
}

// Rapid fire typing
.line-1 { animation-delay: 0.6s; }
.line-2 { animation-delay: 0.75s; }
.line-3 { animation-delay: 0.9s; }

// --- Stage 4: Output ---
.output-text {
  font-family: 'Consolas', 'Monaco', monospace;
  font-size: 24px;
  font-weight: bold;
  fill: var(--color-accent-primary);
  opacity: 0;
  transform: scale(0.8);
  filter: url(#glow);
  // Snappy pop at the end
  animation: popResult 0.5s cubic-bezier(0.34, 1.56, 0.64, 1) 1.1s forwards;
}

// --- Keyframes ---
@keyframes drawBorder {
  to { stroke-dashoffset: 0; }
}

@keyframes fadeInWindow {
  to { opacity: 0.8; } /* Glass effect opacity */
}

@keyframes popIn {
  to { opacity: 1; transform: scale(1); }
}

@keyframes fadeIn {
  to { opacity: 1; }
}

@keyframes drawCup {
  to { stroke-dashoffset: 0; }
}

@keyframes fillCup {
  to { fill: rgba(var(--color-accent-primary-rgb), 0.1); }
}

@keyframes drawSteam {
  to { stroke-dashoffset: 0; opacity: 0.6; }
}

@keyframes riseSteam {
  0% { transform: translateY(0); opacity: 0.6; }
  50% { transform: translateY(-5px); opacity: 0.3; }
  100% { transform: translateY(0); opacity: 0.6; }
}

@keyframes slideInCode {
  to { opacity: 1; transform: translateX(0); }
}

@keyframes popResult {
  to { opacity: 1; transform: scale(1); }
}
</style>
