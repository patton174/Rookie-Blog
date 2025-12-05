<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { ref, onMounted, onUnmounted } from 'vue';
import TechIcon from './icons/TechIcon.vue';
import HotTagIcon from './icons/HotTagIcon.vue';
import { useUserStore } from '../store/user';
import Matter from 'matter-js';

const { t } = useI18n();
const { isLoggedIn, user } = useUserStore();

interface Tag {
  name: string;
  category: 'backend' | 'frontend' | 'devops' | 'cloud' | 'database' | 'architecture';
}

const tags: Tag[] = [
  { name: 'Java', category: 'backend' },
  { name: 'Spring Boot', category: 'backend' },
  { name: 'Vue.js', category: 'frontend' },
  { name: 'TypeScript', category: 'frontend' },
  { name: 'Docker', category: 'devops' },
  { name: 'Kubernetes', category: 'devops' },
  { name: 'Microservices', category: 'architecture' },
  { name: 'AWS', category: 'cloud' },
  { name: 'Redis', category: 'database' },
  { name: 'MySQL', category: 'database' }
];

// Physics Engine using Matter.js
const tagContainerRef = ref<HTMLElement | null>(null);
const tagRefs = ref<HTMLElement[]>([]);
let engine: Matter.Engine;
let runner: Matter.Runner;
let lastScrollY = window.scrollY;
let isMobile = false;

// Optimization State
let animationFrameId: number | null = null;
let isVisible = false;
let isAllSleeping = false;
let scrollTimeout: number | null = null;
let orientationTimeout: number | null = null;

// Optimized initialization
const initPhysics = () => {
  if (!tagContainerRef.value) return;
  
  const container = tagContainerRef.value;
  const width = container.clientWidth;
  const height = 300; 
  
  container.style.height = `${height}px`;
  container.style.position = 'relative';
  
  // Create engine with optimizations
  engine = Matter.Engine.create({
    enableSleeping: true, // Allow bodies to sleep when stable
    positionIterations: 4, // Reduced for performance
    velocityIterations: 3, // Reduced for performance
    constraintIterations: 2
  });
  engine.world.gravity.y = 0.5; 
  
  const bodies: Matter.Body[] = [];
  
  // Create walls
  const wallThickness = 1000;
  const walls = [
    Matter.Bodies.rectangle(width / 2, height + wallThickness / 2, width + wallThickness * 2, wallThickness, { isStatic: true }), 
    Matter.Bodies.rectangle(width / 2, -wallThickness / 2, width + wallThickness * 2, wallThickness, { isStatic: true }), 
    Matter.Bodies.rectangle(width + wallThickness / 2, height / 2, wallThickness, height + wallThickness * 2, { isStatic: true }), 
    Matter.Bodies.rectangle(-wallThickness / 2, height / 2, wallThickness, height + wallThickness * 2, { isStatic: true }) 
  ];
  
  Matter.Composite.add(engine.world, walls);
  
    // Create balls for tags
    tagRefs.value.forEach((el, index) => {
      if (!el) return;
      
      const tag = tags[index];
      const scale = isMobile ? 0.85 : 1;
      const textLen = tag.name.length;
      const baseRadius = 24; 
      const extraRadius = Math.max(0, (textLen - 3) * 4);
      const radius = (baseRadius + extraRadius) * scale;
      
      // Spawn safely inside
      const x = Math.random() * (width - radius * 2) + radius;
      const y = Math.random() * (height / 2) + radius;
      
      const body = Matter.Bodies.circle(x, y, radius, {
        restitution: 0.5, // Reduced bounciness for faster settling
        friction: 0.05, 
        frictionAir: 0.02, 
        density: 0.04,
        label: `tag-${index}`,
        sleepThreshold: 30 // Sleep faster
      });
      
      bodies.push(body);
      
      // Set initial absolute position styles
      el.style.position = 'absolute';
      el.style.left = '0';
      el.style.top = '0';
      el.style.width = `${radius * 2}px`;
      el.style.height = `${radius * 2}px`;
      el.style.display = 'flex';
      el.style.justifyContent = 'center';
      el.style.alignItems = 'center';
      el.style.borderRadius = '50%';
      el.style.willChange = 'transform'; 
      el.style.transform = `translate3d(${x - radius}px, ${y - radius}px, 0)`; // Initial pos
      
      let color = '#888888';
      switch(tag.category) {
        case 'backend': color = '#f89820'; break;
        case 'frontend': color = '#42b883'; break;
        case 'devops': color = '#2496ed'; break;
        case 'cloud': color = '#ff9900'; break;
        case 'database': color = '#dc382d'; break;
        case 'architecture': color = '#ffffff'; break; 
      }
      el.style.setProperty('--ball-color', color);
    });
  
  Matter.Composite.add(engine.world, bodies);
  
  // Create runner
  runner = Matter.Runner.create();
  
  // Mouse interaction
  const mouse = Matter.Mouse.create(container);
  const mouseConstraint = Matter.MouseConstraint.create(engine, {
    mouse: mouse,
    constraint: {
      stiffness: 0.2,
      render: { visible: false }
    }
  });
  
  // Wake up physics on mouse interaction
  Matter.Events.on(mouseConstraint, 'mousedown', () => {
    wakeUp();
  });

  Matter.Composite.add(engine.world, mouseConstraint);
  
  mouse.element.removeEventListener("mousewheel", (mouse as any).mousewheel);
  mouse.element.removeEventListener("DOMMouseScroll", (mouse as any).mousewheel);

  // Setup Intersection Observer
  if (observer) observer.observe(container);
  
  // Initial start if visible
  if (isVisible) startLoop();
};

const startLoop = () => {
  if (animationFrameId === null) {
    updateLoop();
  }
};

const stopLoop = () => {
  if (animationFrameId !== null) {
    cancelAnimationFrame(animationFrameId);
    animationFrameId = null;
  }
};

const wakeUp = () => {
  if (isAllSleeping) {
    isAllSleeping = false;
    // Wake all non-static bodies
    Matter.Composite.allBodies(engine.world).forEach(b => {
      if (!b.isStatic) Matter.Sleeping.set(b, false);
    });
  }
  // Restart loop if it was stopped
  if (isVisible && animationFrameId === null) {
    startLoop();
  }
};

const updateLoop = () => {
  if (!tagContainerRef.value || !engine) return;
  
  // Check for sleeping state to pause loop
  const dynamicBodies = engine.world.bodies.filter(b => !b.isStatic);
  const allSleeping = dynamicBodies.every(b => b.isSleeping);

  if (allSleeping && !isAllSleeping) {
    isAllSleeping = true;
    stopLoop(); // Fully stop the loop
    return;
  } else if (!allSleeping) {
    isAllSleeping = false;
  }

  // Update physics
  Matter.Runner.tick(runner, engine, 1000 / 60);

  // Update DOM
  for (let i = 0; i < dynamicBodies.length; i++) {
    const body = dynamicBodies[i];
    // Extract index from label "tag-{index}"
    const indexStr = body.label.split('-')[1];
    if (indexStr) {
      const index = parseInt(indexStr);
      const el = tagRefs.value[index];
      
      if (el && !body.isSleeping) {
        const { x, y } = body.position;
        const angle = body.angle;
        const radius = (body as any).circleRadius || 30;
        el.style.transform = `translate3d(${x - radius}px, ${y - radius}px, 0) rotate(${angle}rad)`;
      }
    }
  }
  
  animationFrameId = requestAnimationFrame(updateLoop);
};

// Intersection Observer to pause physics when off-screen
const observer = new IntersectionObserver((entries) => {
  entries.forEach(entry => {
    isVisible = entry.isIntersecting;
    if (isVisible) {
      wakeUp();
    } else {
      stopLoop();
    }
  });
}, { threshold: 0.1 });

const handleScroll = () => {
  if (!engine) return;
  
  // Throttle scroll updates
  if (scrollTimeout) return;
  
  scrollTimeout = window.setTimeout(() => {
    const currentScrollY = window.scrollY;
    const deltaY = currentScrollY - lastScrollY;
    
    // Only wake up if scroll is significant
    if (Math.abs(deltaY) > 5) {
      const impulse = deltaY * 0.001;
      
      let wokeUp = false;
      Matter.Composite.allBodies(engine.world).forEach(body => {
        if (!body.isStatic) {
          Matter.Body.applyForce(body, body.position, { x: 0, y: impulse * body.mass });
          if (body.isSleeping) {
            Matter.Sleeping.set(body, false);
            wokeUp = true;
          }
        }
      });
      
      if (wokeUp) wakeUp();
    }
    
    lastScrollY = currentScrollY;
    scrollTimeout = null;
  }, 50); // 20fps throttle for scroll interaction
};

const handleOrientation = (event: DeviceOrientationEvent) => {
  if (!engine || !isMobile) return;
  
  if (orientationTimeout) return;

  orientationTimeout = window.setTimeout(() => {
    const gamma = event.gamma || 0; 
    const beta = event.beta || 0;   
    
    const x = Math.min(Math.max(gamma / 45, -1), 1);
    const y = Math.min(Math.max(beta / 45, -1), 1);
    
    engine.world.gravity.x = x;
    engine.world.gravity.y = Math.abs(y) < 0.2 ? 1 : y;
    
    wakeUp();
    
    orientationTimeout = null;
  }, 100); // 10fps throttle for orientation
};

onMounted(() => {
  isMobile = window.matchMedia('(max-width: 768px)').matches;
  setTimeout(initPhysics, 100);
  window.addEventListener('scroll', handleScroll, { passive: true });
  if (window.DeviceOrientationEvent && isMobile) {
    window.addEventListener('deviceorientation', handleOrientation, true);
  }
});

onUnmounted(() => {
  stopLoop();
  if (observer) observer.disconnect();
  if (engine) Matter.Engine.clear(engine);
  if (scrollTimeout) clearTimeout(scrollTimeout);
  if (orientationTimeout) clearTimeout(orientationTimeout);
  window.removeEventListener('scroll', handleScroll);
  window.removeEventListener('deviceorientation', handleOrientation);
});
</script>

<template>
  <aside class="sidebar">
    <div class="sidebar__widget author-card glass-panel" v-if="isLoggedIn && user">
      <div class="author-card__avatar">
        <img v-if="user.avatarUrl" :src="user.avatarUrl" :alt="user.username" class="user-avatar-img" />
        <div v-else class="avatar-placeholder">{{ user.username.charAt(0).toUpperCase() }}</div>
      </div>
      <h3 class="author-card__name text-gradient">{{ user.username }}</h3>
      
      <!-- Optimized Email Display -->
      <div class="author-card__contact">
        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="contact-icon"><path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"></path><polyline points="22,6 12,13 2,6"></polyline></svg>
        <span class="contact-text">{{ user.email }}</span>
      </div>

      <div class="author-card__info">
        <!-- Bio / Self Introduction -->
        <p class="bio-text">
          {{ user.bio || '这个人真高冷，没什么想说的呢！' }}
        </p>
      </div>
    </div>

    <div class="sidebar__widget author-card glass-panel" v-else>
      <div class="author-card__avatar">
        <div class="avatar-placeholder">RC</div>
      </div>
      <h3 class="author-card__name text-gradient">{{ t('sidebar.author') }}</h3>
      <div class="author-card__info">
        <p class="bio-text">
          {{ t('sidebar.bio') }}
        </p>
      </div>
      <div class="author-card__socials">
        <a href="#" class="social-link">GH</a>
        <a href="#" class="social-link">TW</a>
        <a href="#" class="social-link">LI</a>
      </div>
    </div>

    <div class="sidebar__widget tag-cloud glass-panel">
      <h3 class="sidebar__title">
        <HotTagIcon class="sidebar-icon" />
        {{ t('sidebar.hotTags') }}
      </h3>
      <div class="tag-cloud__container" ref="tagContainerRef">
        <a 
          href="#" 
          v-for="(tag, index) in tags" 
          :key="tag.name" 
          :ref="el => { if(el) tagRefs[index] = el as HTMLElement }"
          class="tag-cloud__item"
          :class="`tag-cloud__item--${tag.category}`"
        >
          <TechIcon :name="tag.name" :size="16" />
          <span class="tag-text">{{ tag.name }}</span>
        </a>
      </div>
    </div>
  </aside>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.sidebar {
  &__widget {
    padding: 5px; // Reduced from $spacing-xl (24px) to 5px for tighter layout
    margin-bottom: $spacing-xl;
    position: relative;
    overflow: hidden;
    border-radius: 20px;
    background: var(--color-card-bg);
    border: 1px solid var(--color-card-border);
    transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);

    &:hover {
      transform: translateY(-2px);
      border-color: rgba($color-accent-primary, 0.1);
      box-shadow: var(--color-card-hover-shadow);
      background: var(--color-card-bg);
    }
  }

  &__title {
    font-size: 1.15rem;
    margin: 10px 0 15px 5px;
    padding-left: 0; // Removed padding-left as we use flex gap now
    border-left: none; // Removed border-left for icon style
    color: $color-text-primary;
    font-weight: 600;
    letter-spacing: 0.5px;
    opacity: 0.95;
    line-height: 1.2;
    display: flex;
    align-items: center;
    gap: 8px;

    .sidebar-icon {
      width: 24px;
      height: 24px;
    }
  }
}

.author-card {
  text-align: center;
  padding: 10px 5px; // Extra internal breathing room

  &__avatar {
    width: 100px;
    height: 100px;
    border-radius: 50%;
    border: 2px solid var(--color-border); // Slightly thicker border
    padding: 4px;
    margin: 5px auto $spacing-md; // Reduced margins
    background: var(--color-bg-secondary);
    
    .avatar-placeholder {
      width: 100%;
      height: 100%;
      background: linear-gradient(135deg, $color-bg-secondary, $color-bg-primary);
      color: $color-text-secondary;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.5rem;
      font-weight: 600;
      border-radius: 50%;
      border: 1px solid var(--color-border);
      transition: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);
    }

    &:hover .avatar-placeholder {
      color: $color-accent-primary;
      border-color: rgba($color-accent-primary, 0.3);
      box-shadow: 0 0 20px rgba($color-accent-primary, 0.15);
      transform: scale(1.05);
    }
  }

  &__name {
    font-size: 1.4rem;
    font-weight: 700;
    margin-bottom: 5px; // Tightened
    letter-spacing: -0.5px;
  }

  &__contact {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    padding: 6px 14px;
    margin-bottom: 15px;
    background: rgba(var(--color-bg-secondary-rgb), 0.05); // Fallback or variable usage
    background: var(--color-bg-secondary);
    border-radius: 50px; // Pill shape
    border: 1px solid transparent;
    transition: all 0.3s ease;
    max-width: 100%;
    
    .contact-icon {
      color: $color-text-secondary;
      opacity: 0.7;
      transition: all 0.3s ease;
      flex-shrink: 0;
    }
    
    .contact-text {
      font-size: 0.85rem;
      color: $color-text-secondary;
      font-weight: 500;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
    
    &:hover {
      background: var(--color-card-bg);
      border-color: rgba($color-accent-primary, 0.3);
      box-shadow: 0 2px 8px rgba(0,0,0,0.05);
      transform: translateY(-1px);
      
      .contact-icon {
        color: $color-accent-primary;
        opacity: 1;
      }
      
      .contact-text {
        color: $color-text-primary;
      }
    }
  }

  &__socials {
    display: flex;
    justify-content: center;
    gap: $spacing-md;
  }

  .social-link {
    width: 42px;
    height: 42px;
    border-radius: 12px;
    background: var(--color-bg-secondary);
    color: $color-text-secondary;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px solid var(--color-border);
    font-size: 0.85rem;
    font-weight: 600;
    transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);

    &:hover {
      color: #fff;
      background: rgba($color-accent-primary, 0.8); // Changed for visibility
      border-color: rgba($color-accent-primary, 0.3);
      transform: translateY(-2px) scale(1.05);
      box-shadow: 0 5px 15px rgba($color-accent-primary, 0.1);
    }
  }

  .user-avatar-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    border-radius: 50%;
    border: 1px solid var(--color-border);
  }

  .author-card__info {
    margin-top: 0;
    margin-bottom: 20px;
    padding: 0 15px;
    
    .bio-text {
      font-size: 0.9rem;
      line-height: 1.6;
      color: $color-text-secondary;
      font-style: italic;
      opacity: 0.85;
      position: relative;
      
      &::before {
        content: '"';
        font-size: 1.4rem;
        color: var(--color-accent-primary);
        opacity: 0.5;
        margin-right: 4px;
        vertical-align: -3px;
      }
      
      &::after {
        content: '"';
        font-size: 1.4rem;
        color: var(--color-accent-primary);
        opacity: 0.5;
        margin-left: 4px;
        vertical-align: -6px;
        line-height: 0;
      }
    }
  }
}

.tag-cloud {
  &__container {
    // Physics layout overrides
    display: block; // Needed for absolute positioning context
    width: 100%;
    overflow: hidden; // Keep balls inside
    position: relative; // Anchor
  }

  &__item {
    display: flex;
    flex-direction: column; // Stack icon and text
    align-items: center;
    justify-content: center;
    gap: 2px; // Tighter gap
    font-size: 0.85rem;
    color: $color-text-secondary;
    
    // Make them perfect circles
    width: 48px; // Base size, overridden by inline style
    height: 48px; // Base size, overridden by inline style
    padding: 0; // Reset padding
    
    // 3D Ball Effect
    // Use CSS variables set in JS for dynamic color base
    background: radial-gradient(circle at 30% 30%, rgba(255, 255, 255, 0.9) 0%, rgba(255, 255, 255, 0.1) 20%, transparent 50%),
                radial-gradient(circle at 50% 50%, var(--ball-color, var(--color-bg-secondary)) 0%, darken(#ffffff, 20%) 100%);
    // Fallback if var not set or complex:
    background-color: var(--ball-color, var(--color-bg-secondary));
    
    border: none; // Remove border for cleaner 3D look
    border-radius: 50%; // Circle
    
    // Deep shadow for 3D depth
    box-shadow: inset -5px -5px 15px rgba(0, 0, 0, 0.3), // Inner shadow for volume
                5px 5px 15px rgba(0, 0, 0, 0.2); // Drop shadow
    
    // Remove transition for smoother physics sync
    transition: box-shadow 0.3s ease; // Only shadow animates on hover
    
    cursor: pointer;
    font-weight: 500;
    letter-spacing: 0.3px;
    line-height: 1;
    
    // Prevent selection during physics interaction
    user-select: none;
    
    // Physics state
    position: absolute;
    left: 0;
    top: 0;
    will-change: transform;
    z-index: 1;
    
    // Hide text if too small or show on hover? 
    // For now, keep it. Might overflow circle if name is long.
    // Requirement says "ball-like tag elements".
    
    .tech-icon {
      // Maybe just show icon? Or stack?
      // Let's keep icon only or stack vertically if space allows.
      // With 60px circle, short text fits.
      color: #fff; // White icon for contrast on colored ball
      filter: drop-shadow(0 1px 2px rgba(0,0,0,0.3));
    }

    .tag-text {
      display: block; // Show text inside ball
      font-size: 0.75rem; // Small font
      font-weight: 600;
      color: #fff; // White text
      text-shadow: 0 1px 2px rgba(0,0,0,0.5); // Shadow for readability
      pointer-events: none; // Let clicks pass through to ball
      white-space: nowrap;
      
      // Mobile text scaling
      @media (max-width: 768px) {
        font-size: 0.6rem; // Smaller on mobile
        transform: scale(0.9); // Scale down further if needed
      }
    }
    
    &:hover {
      z-index: 10;
      // Enhance 3D effect on hover
      box-shadow: inset -5px -5px 20px rgba(0, 0, 0, 0.4),
                  0 0 20px var(--ball-color, rgba(255,255,255,0.5));
      
      .tag-text {
         // Maybe show on hover?
      }
    }
  }
  
  // Legacy overrides removed, color handled by inline style
  // But we keep them just in case for non-ball fallback or specific tweaks
  // Actually, we can remove them to clean up since we use inline --ball-color
  &__item--backend { }
  &__item--frontend { }
  &__item--devops { }
  &__item--cloud { }
  &__item--database { }
  &__item--architecture { }
}
</style>
