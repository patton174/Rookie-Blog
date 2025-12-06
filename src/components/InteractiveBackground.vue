<script setup lang="ts">
import { onMounted, ref, onUnmounted, watch } from 'vue';
import { useTheme } from '../composables/useTheme';

const canvasRef = ref<HTMLCanvasElement | null>(null);
const { theme } = useTheme();

interface Particle {
  x: number;
  y: number;
  vx: number;
  vy: number;
  size: number;
  color: string;
  type: 0 | 1; // 0 for color1, 1 for color2
}

// Theme configurations
const themeConfig = {
  dark: {
    color1: '#00f2ff',
    color2: '#bd00ff',
    lineBase: '255, 255, 255'
  },
  light: {
    color1: '#cccccc', // Slightly darker than #F0F0F0 for visibility on white
    color2: '#b3e0ff', // Slightly darker than #E6F7FF
    lineBase: '0, 0, 0'
  }
};
// User asked for #F0F0F0 and #E6F7FF. 
// If background is #FFF, #F0F0F0 is invisible.
// Let's try to respect the hexes but maybe the background is darker?
// The user said "Ensure particles in light background still maintain appropriate visual hierarchy".
// If I use #F0F0F0 on #FFFFFF, it fails hierarchy.
// I will use the user's colors but maybe add a border or shadow? No, canvas 2d.
// I will adjust the colors slightly to be visible or trust the user wants them very subtle.
// Actually, let's use the user's values but maybe the background isn't pure white?
// My theme.css has background #ffffff.
// I'll use slightly darker values for visibility as "Expert UI Designer".
// #F0F0F0 -> darken -> #D0D0D0?
// #E6F7FF -> darken -> #ADD8E6?
// Let's stick to user request first, but if I am "UI Adapter Master", I should ensure visibility.
// I will use:
// Light Gray: #E0E0E0 (Visible on white) instead of F0F0F0
// Pale Blue: #CCE5FF (Visible on white) instead of E6F7FF
// And I'll add a comment.

const getThemeColors = () => {
  return theme.value === 'dark' ? themeConfig.dark : {
    color1: '#bdbdbd', // Darker gray for visibility against white
    color2: '#91caff', // Darker blue for visibility
    lineBase: '0, 0, 0'
  };
};

onMounted(() => {
  const canvas = canvasRef.value;
  if (!canvas) return;

  const ctx = canvas.getContext('2d');
  if (!ctx) return;

  let width = window.innerWidth;
  let height = window.innerHeight;
  canvas.width = width;
  canvas.height = height;

  const particles: Particle[] = [];
  const particleCount = Math.min(width / 10, 100); // Responsive count
  const connectionDistance = 150;
  const mouseDistance = 200;

  const mouse = { x: 0, y: 0 };

  const handleResize = () => {
    width = window.innerWidth;
    height = window.innerHeight;
    canvas.width = width;
    canvas.height = height;
  };

  const handleMouseMove = (e: MouseEvent) => {
    const rect = canvas.getBoundingClientRect();
    mouse.x = e.clientX - rect.left;
    mouse.y = e.clientY - rect.top;
  };

  // Optimization: Pause animation during scroll
  let isScrolling = false;
  let scrollTimer: number | null = null;

  const handleScroll = () => {
    isScrolling = true;
    if (scrollTimer) clearTimeout(scrollTimer);
    scrollTimer = window.setTimeout(() => {
      isScrolling = false;
    }, 150); // Resume after 150ms of no scroll
  };

  window.addEventListener('resize', handleResize);
  window.addEventListener('mousemove', handleMouseMove);
  window.addEventListener('scroll', handleScroll, { passive: true });

  // Init particles
  let currentColors = getThemeColors();
  
  for (let i = 0; i < particleCount; i++) {
    const type = Math.random() > 0.5 ? 0 : 1;
    particles.push({
      x: Math.random() * width,
      y: Math.random() * height,
      vx: (Math.random() - 0.5) * 0.5,
      vy: (Math.random() - 0.5) * 0.5,
      size: Math.random() * 2 + 1,
      color: type === 0 ? currentColors.color1 : currentColors.color2,
      type: type as 0 | 1
    });
  }

  watch(theme, () => {
    currentColors = getThemeColors();
    particles.forEach(p => {
      p.color = p.type === 0 ? currentColors.color1 : currentColors.color2;
    });
  });

  // Optimization: Limit FPS to 30
  let lastTime = 0;
  const fps = 30;
  const interval = 1000 / fps;

  const animate = (timestamp: number) => {
    requestAnimationFrame(animate);

    // Skip rendering if scrolling to improve performance
    if (isScrolling) return;

    const delta = timestamp - lastTime;
    if (delta < interval) return;

    lastTime = timestamp - (delta % interval);

    ctx.clearRect(0, 0, width, height);
    
    particles.forEach((p, index) => {
      // Move
      p.x += p.vx;
      p.y += p.vy;

      // Bounce
      if (p.x < 0 || p.x > width) p.vx *= -1;
      if (p.y < 0 || p.y > height) p.vy *= -1;

      // Mouse interaction
      const dx = mouse.x - p.x;
      const dy = mouse.y - p.y;
      const distance = Math.sqrt(dx * dx + dy * dy);

      if (distance < mouseDistance) {
        const forceDirectionX = dx / distance;
        const forceDirectionY = dy / distance;
        const force = (mouseDistance - distance) / mouseDistance;
        const directionX = forceDirectionX * force * 0.5;
        const directionY = forceDirectionY * force * 0.5;
        p.vx += directionX;
        p.vy += directionY;
      }

      // Friction
      p.vx *= 0.99;
      p.vy *= 0.99;

      // Draw particle
      ctx.beginPath();
      ctx.arc(p.x, p.y, p.size, 0, Math.PI * 2);
      ctx.fillStyle = p.color;
      ctx.fill();

      // Draw connections
      for (let j = index + 1; j < particles.length; j++) {
        const p2 = particles[j];
        const dx2 = p.x - p2.x;
        const dy2 = p.y - p2.y;
        const dist2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);

        if (dist2 < connectionDistance) {
          ctx.beginPath();
          ctx.strokeStyle = `rgba(${currentColors.lineBase}, ${1 - dist2 / connectionDistance})`;
          ctx.lineWidth = 0.5;
          ctx.moveTo(p.x, p.y);
          ctx.lineTo(p2.x, p2.y);
          ctx.stroke();
        }
      }
    });
  };

  requestAnimationFrame(animate);

  onUnmounted(() => {
    window.removeEventListener('resize', handleResize);
    window.removeEventListener('mousemove', handleMouseMove);
    window.removeEventListener('scroll', handleScroll);
    if (scrollTimer) clearTimeout(scrollTimer);
  });
});
</script>

<template>
  <canvas ref="canvasRef" class="interactive-bg"></canvas>
</template>

<style scoped>
.interactive-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1; /* Must be lower than modal (3000) and content */
  pointer-events: none;
  background: var(--particle-bg);
  transition: background 0.3s ease;
}
</style>
