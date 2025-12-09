<script setup lang="ts">
import { onMounted, ref, onUnmounted } from 'vue';

const canvasRef = ref<HTMLCanvasElement | null>(null);

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
// const themeConfig = { ... } removed as we now use CSS variables directly
const getThemeColors = () => {
  // Legacy function removed
  return {};
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
  // const particleCount moved to logic below
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
  // Removed scroll optimization as it causes rendering issues during navigation
  // let isScrolling = false;
  // let scrollTimer: number | null = null;

  // const handleScroll = () => {
  //   isScrolling = true;
  //   if (scrollTimer) clearTimeout(scrollTimer);
  //   scrollTimer = window.setTimeout(() => {
  //     isScrolling = false;
  //   }, 150);
  // };

  // Optimization for Mobile: Check if device is low-end or mobile
  const isMobile = window.innerWidth < 768;
  if (isMobile) {
    // Disable or reduce particles on mobile
    // Here we just return to avoid running canvas on mobile if performance is critical
    // Or reduce count drastically
  }
  
  // Mobile Optimization: Reduced particle count
  const isMobileDevice = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent) || window.innerWidth < 768;
  const particleCount = isMobileDevice ? 20 : Math.min(width / 10, 100); 

  window.addEventListener('resize', handleResize);
  if (!isMobileDevice) {
    window.addEventListener('mousemove', handleMouseMove);
  }
  // window.addEventListener('scroll', handleScroll, { passive: true });

  // Init particles
  // Optimization: Read colors from CSS variables to support dynamic theme switching without JS state
  const getCssVar = (name: string) => {
    // Helper function removed in favor of direct access in updateParticleColors
    return '';
  };

  const updateParticleColors = () => {
    // Force a reflow/re-read of CSS variables
    // Use window.getComputedStyle on document.documentElement
    // Note: In Chrome, getComputedStyle might not update immediately if called synchronously after class change.
    // The timeout in observer handles this.
    
    // Default Fallbacks
    let color1 = '#e0e7ff';
    let color2 = '#fae8ff';
    let lineBase = 'rgba(99, 102, 241, 0.05)';

    const styles = getComputedStyle(document.documentElement);
    const c1 = styles.getPropertyValue('--particle-color-1').trim();
    const c2 = styles.getPropertyValue('--particle-color-2').trim();
    const lb = styles.getPropertyValue('--particle-line-color').trim();

    if (c1) color1 = c1;
    if (c2) color2 = c2;
    if (lb) lineBase = lb;
    
    // Extract RGB from lineBase if it's in rgba format for the alpha calculation
    // This is a simplified check, assuming standard rgba format
    const lineBaseMatch = lineBase.match(/rgba?\((\d+,\s*\d+,\s*\d+)/);
    const lineBaseRgb = lineBaseMatch ? lineBaseMatch[1] : '99, 102, 241';

    return { color1, color2, lineBase: lineBaseRgb };
  };

  let currentColors = updateParticleColors();
  
  for (let i = 0; i < particleCount; i++) {
    const type = Math.random() > 0.5 ? 0 : 1;
    particles.push({
      x: Math.random() * width,
      y: Math.random() * height,
      vx: (Math.random() - 0.5) * (isMobileDevice ? 0.2 : 0.5),
      vy: (Math.random() - 0.5) * (isMobileDevice ? 0.2 : 0.5),
      size: Math.random() * 2 + 1,
      color: type === 0 ? currentColors.color1 : currentColors.color2,
      type: type as 0 | 1
    });
  }

  // Watch for theme changes via CSS class mutation instead of Vue prop
  // This is the most robust way to detect CSS variable changes
  const observer = new MutationObserver((mutations) => {
    mutations.forEach((mutation) => {
      if (mutation.type === 'attributes' && (mutation.attributeName === 'class' || mutation.attributeName === 'data-theme')) {
        // Small delay to ensure CSS is applied
        setTimeout(() => {
          currentColors = updateParticleColors();
          particles.forEach(p => {
            p.color = p.type === 0 ? currentColors.color1 : currentColors.color2;
          });
        }, 50);
      }
    });
  });

  observer.observe(document.documentElement, {
    attributes: true,
    attributeFilter: ['class', 'data-theme']
  });

  // Optimization: Limit FPS to 30
  let lastTime = 0;
  const fps = 30;
  const interval = 1000 / fps;

  const animate = (timestamp: number) => {
    requestAnimationFrame(animate);

    // Skip rendering if scrolling to improve performance
    // if (isScrolling) return;

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
    // window.removeEventListener('scroll', handleScroll);
    // if (scrollTimer) clearTimeout(scrollTimer);
    observer.disconnect();
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
  z-index: 0; /* Changed from -1 to 0 to avoid being hidden by body background in some contexts */
  pointer-events: none;
  background: var(--particle-bg);
  transition: background 0.3s ease;
}
</style>
