<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { ref, onMounted } from 'vue';
import { useTheme } from '../composables/useTheme';
import Logo from '../components/Logo.vue';
import TextLogo from '../components/TextLogo.vue';
import TechBadge from '../components/TechBadge.vue';
import TechIcon from '../components/icons/TechIcon.vue';

const { t } = useI18n();
const { theme } = useTheme();
const activeSection = ref('intro');

const techStacks = [
  {
    category: 'Frontend',
    icon: 'mdi:monitor-dashboard',
    items: [
      { name: 'Vue 3', badge: 'Composition API' },
      { name: 'TypeScript', badge: '5.0+' },
      { name: 'Vite', badge: 'Build Tool' },
      { name: 'SCSS', badge: 'Styling' },
      { name: 'Pinia', badge: 'State' },
    ]
  },
  {
    category: 'Backend',
    icon: 'mdi:server',
    items: [
      { name: 'Spring Boot', badge: '3.5.7' },
      { name: 'MyBatis Plus', badge: 'ORM' },
      { name: 'Sa-Token', badge: 'Auth' },
      { name: 'Elasticsearch', badge: 'Search' },
    ]
  },
  {
    category: 'Infra',
    icon: 'mdi:database',
    items: [
      { name: 'MySQL', badge: '8.0' },
      { name: 'Redis', badge: 'Cache' },
      { name: 'Docker', badge: 'Deploy' }
    ]
  }
];

const features = [
  {
    title: 'Immersive Reading',
    desc: 'Markdown rendering, syntax highlighting, math formulas',
    icon: 'mdi:book-open-page-variant'
  },
  {
    title: 'Responsive Design',
    desc: 'Perfectly adapted for Desktop, Tablet, Mobile',
    icon: 'mdi:responsive'
  },
  {
    title: 'Powerful Search',
    desc: 'Full-text search based on Elasticsearch',
    icon: 'mdi:magnify'
  },
  {
    title: 'Community',
    desc: 'Nested comments, likes/favorites, anti-spam',
    icon: 'mdi:account-group'
  }
];

const quickStartSteps = [
  {
    num: '01',
    title: 'Clone',
    code: 'git clone https://github.com/patton174/Rookie-Blog.git'
  },
  {
    num: '02',
    title: 'Setup',
    content: 'JDK 21+, Node.js 18+, MySQL 8.0+, Redis 7.0+'
  },
  {
    num: '03',
    title: 'Run',
    code: 'cd frontend && npm install && npm run dev'
  }
];

const scrollTo = (id: string) => {
  activeSection.value = id;
  const el = document.getElementById(id);
  if (el) {
    const offset = 100;
    const elementPosition = el.getBoundingClientRect().top;
    const offsetPosition = elementPosition + window.pageYOffset - offset;
    window.scrollTo({
      top: offsetPosition,
      behavior: "smooth"
    });
  }
};

onMounted(() => {
  window.scrollTo(0, 0);
  // Optional: Intersection Observer for active section
  const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        activeSection.value = entry.target.id;
      }
    });
  }, { threshold: 0.5 });
  
  document.querySelectorAll('.content-section').forEach(section => {
    observer.observe(section);
  });
});
</script>

<template>
  <div class="about-page">
    <div class="about-layout">
      
      <!-- Left Sidebar (Sticky) -->
      <aside class="about-sidebar">
        <div class="sidebar-inner">
          <div class="brand-area">
            <Logo :width="64" :height="64" class="brand-icon" />
            <TextLogo 
              :width="180" 
              :height="40" 
              :variant="theme === 'dark' ? 'dark' : 'light'" 
              idSuffix="sidebar"
            />
            <p class="slogan">Exploring The Tech Frontier</p>
          </div>

          <nav class="about-nav">
            <a @click.prevent="scrollTo('intro')" :class="{ active: activeSection === 'intro' }">
              <span class="nav-dot"></span> Introduction
            </a>
            <a @click.prevent="scrollTo('tech')" :class="{ active: activeSection === 'tech' }">
              <span class="nav-dot"></span> Tech Stack
            </a>
            <a @click.prevent="scrollTo('features')" :class="{ active: activeSection === 'features' }">
              <span class="nav-dot"></span> Features
            </a>
            <a @click.prevent="scrollTo('start')" :class="{ active: activeSection === 'start' }">
              <span class="nav-dot"></span> Quick Start
            </a>
          </nav>

          <div class="sidebar-footer">
            <a href="https://github.com/patton174/Rookie-Blog" target="_blank" class="github-btn">
              <TechIcon name="mdi:github" :size="20" />
              <span>Star on GitHub</span>
            </a>
          </div>
        </div>
      </aside>

      <!-- Right Content (Scrollable) -->
      <main class="about-content">
        
        <!-- Intro Section -->
        <section id="intro" class="content-section intro-section">
          <div class="glass-card main-card">
            <div class="section-header">
              <h2>About Project</h2>
              <span class="decorative-line"></span>
            </div>
            <p class="lead-text">
              Rookie Blog is a modern full-stack personal blog system designed for developers who appreciate 
              <span class="highlight">clean aesthetics</span> and <span class="highlight">robust architecture</span>.
            </p>
            <p class="sub-text">
              Built with the latest Spring Boot 3 and Vue 3 ecosystem, featuring Glassmorphism UI design 
              and enterprise-level capabilities like Elasticsearch.
            </p>
            
            <div class="badges-row">
              <TechBadge label="Spring Boot" message="3.5.7" color="6DB33F" logo="simple-icons:springboot" />
              <TechBadge label="Vue.js" message="3.4+" color="4FC08D" logo="simple-icons:vuedotjs" />
              <TechBadge label="TypeScript" message="5.0+" color="3178C6" logo="simple-icons:typescript" />
            </div>
          </div>
        </section>

        <!-- Tech Stack Section -->
        <section id="tech" class="content-section tech-section">
          <div class="section-header">
            <h2>Tech Stack</h2>
          </div>
          <div class="tech-grid">
            <div v-for="stack in techStacks" :key="stack.category" class="tech-column glass-card">
              <div class="column-header">
                <TechIcon :name="stack.icon" :size="24" class="header-icon" />
                <h3>{{ stack.category }}</h3>
              </div>
              <ul class="tech-list">
                <li v-for="item in stack.items" :key="item.name">
                  <span class="name">{{ item.name }}</span>
                  <span class="badge">{{ item.badge }}</span>
                </li>
              </ul>
            </div>
          </div>
        </section>

        <!-- Features Section -->
        <section id="features" class="content-section features-section">
          <div class="section-header">
            <h2>Core Features</h2>
          </div>
          <div class="features-grid">
            <div v-for="feature in features" :key="feature.title" class="feature-item glass-card">
              <div class="icon-box">
                <TechIcon :name="feature.icon" :size="28" />
              </div>
              <div class="feature-text">
                <h3>{{ feature.title }}</h3>
                <p>{{ feature.desc }}</p>
              </div>
            </div>
          </div>
        </section>

        <!-- Quick Start Section -->
        <section id="start" class="content-section start-section">
          <div class="section-header">
            <h2>Quick Start</h2>
          </div>
          <div class="timeline">
            <div v-for="(step, index) in quickStartSteps" :key="index" class="timeline-item glass-card">
              <div class="timeline-marker">{{ step.num }}</div>
              <div class="timeline-content">
                <h3>{{ step.title }}</h3>
                <p v-if="step.content">{{ step.content }}</p>
                <div v-if="step.code" class="code-snippet">
                  <code>{{ step.code }}</code>
                </div>
              </div>
            </div>
          </div>
        </section>

        <footer class="content-footer">
          <p>© 2023 Rookie Blog Team. Open Source under MIT License.</p>
        </footer>

      </main>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.about-page {
  min-height: 100vh;
  padding-top: 80px;
  padding-bottom: 4rem;
  background: transparent;
}

.about-layout {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  display: grid;
  grid-template-columns: 280px 1fr;
  gap: 4rem;
  
  @media (max-width: $breakpoint-tablet) {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
}

/* Sidebar Styles */
.about-sidebar {
  position: relative;
  
  @media (max-width: $breakpoint-tablet) {
    display: none; // Hide sidebar on mobile for cleaner look, or move to top
  }
}

.sidebar-inner {
  position: sticky;
  top: 100px;
  display: flex;
  flex-direction: column;
  gap: 2.5rem;
}

.brand-area {
  .brand-icon {
    margin-bottom: 1rem;
    filter: drop-shadow(0 4px 12px rgba(0,0,0,0.1));
  }
  
  .slogan {
    margin-top: 0.5rem;
    color: $color-text-secondary;
    font-size: 0.95rem;
    font-weight: 500;
  }
}

.about-nav {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  
  a {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 0.5rem 0;
    color: $color-text-secondary;
    text-decoration: none;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s ease;
    
    .nav-dot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background-color: transparent;
      border: 2px solid $color-border;
      transition: all 0.3s ease;
    }
    
    &:hover {
      color: $color-text-primary;
      .nav-dot { border-color: $color-accent-primary; }
    }
    
    &.active {
      color: $color-text-primary;
      font-weight: 600;
      
      .nav-dot {
        background-color: $color-accent-primary;
        border-color: $color-accent-primary;
        transform: scale(1.2);
      }
    }
  }
}

.github-btn {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  padding: 10px 20px;
  background: $color-text-primary;
  color: $color-bg-primary;
  border-radius: 30px;
  font-weight: 600;
  text-decoration: none;
  transition: transform 0.2s, box-shadow 0.2s;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  }
}

/* Content Styles */
.about-content {
  display: flex;
  flex-direction: column;
  gap: 4rem;
}

.section-header {
  margin-bottom: 1.5rem;
  
  h2 {
    font-size: 1.5rem;
    font-weight: 700;
    color: $color-text-primary;
    margin-bottom: 0.5rem;
  }
  
  .decorative-line {
    display: block;
    width: 40px;
    height: 3px;
    background: linear-gradient(90deg, $color-accent-primary, $color-accent-secondary);
    border-radius: 2px;
  }
}

/* Glass Card Generic */
.glass-card {
  background: rgba(255, 255, 255, 0.7); /* More opaque for better readability */
  backdrop-filter: blur(16px); /* Stronger blur */
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 24px; /* Softer corners */
  padding: 2.5rem;
  box-shadow: 
    0 4px 6px -1px rgba(0, 0, 0, 0.05),
    0 10px 15px -3px rgba(0, 0, 0, 0.05);
  
  :global(.dark) & {
    /* Dark Gray Frosted Glass - As Requested */
    background: rgba(30, 30, 35, 0.6); /* Increased opacity slightly for "gray" look */
    backdrop-filter: blur(20px); /* Enhance blur for frosted effect */
    border: 1px solid rgba(255, 255, 255, 0.08); /* Crisp edge */
    box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.3); /* Deep shadow */
  }
}

/* Intro Section */
.intro-section {
  .lead-text {
    font-size: 1.25rem; /* Larger text */
    line-height: 1.8;
    color: $color-text-primary;
    margin-bottom: 1.5rem;
    font-weight: 400;
    
    .highlight {
      color: transparent;
      background: linear-gradient(120deg, $color-accent-primary, $color-accent-secondary);
      background-clip: text;
      -webkit-background-clip: text;
      font-weight: 700;
    }
  }
  
  .sub-text {
    color: $color-text-secondary;
    margin-bottom: 2rem;
    font-size: 1.05rem;
    line-height: 1.6;
  }
}

/* Tech Grid */
.tech-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
}

.tech-column {
  padding: 1.5rem;
  
  .column-header {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-bottom: 1rem;
    padding-bottom: 0.8rem;
    border-bottom: 1px solid rgba(0,0,0,0.05);
    
    :global(.dark) & { border-bottom-color: rgba(255,255,255,0.1); }
    
    .header-icon { color: $color-accent-primary; }
    h3 { font-size: 1.1rem; font-weight: 600; margin: 0; }
  }
  
  .tech-list {
    list-style: none;
    padding: 0;
    margin: 0;
    
    li {
      display: flex;
      justify-content: space-between;
      margin-bottom: 0.6rem;
      font-size: 0.95rem;
      
      .name { color: $color-text-primary; }
      .badge { 
        font-size: 0.8rem; 
        color: $color-text-secondary; 
        background: rgba(0,0,0,0.03); 
        padding: 2px 6px; 
        border-radius: 4px;
        
        :global(.dark) & { background: rgba(255,255,255,0.1); }
      }
    }
  }
}

/* Features Grid */
.features-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1.5rem;
  
  @media (max-width: $breakpoint-mobile) {
    grid-template-columns: 1fr;
  }
}

.feature-item {
  display: flex;
  gap: 1rem;
  align-items: flex-start;
  padding: 1.5rem;
  transition: transform 0.3s;
  
  &:hover { transform: translateY(-4px); }
  
  .icon-box {
    padding: 10px;
    background: linear-gradient(135deg, rgba($color-accent-primary, 0.1), rgba($color-accent-secondary, 0.1));
    border-radius: 12px;
    color: $color-accent-primary;
  }
  
  .feature-text {
    h3 { font-size: 1.1rem; margin-bottom: 0.4rem; font-weight: 600; }
    p { font-size: 0.9rem; color: $color-text-secondary; line-height: 1.5; }
  }
}

/* Timeline */
.timeline {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.timeline-item {
  display: flex;
  gap: 1.5rem;
  align-items: flex-start;
  
  .timeline-marker {
    font-size: 1.5rem;
    font-weight: 800;
    color: rgba($color-accent-primary, 0.2);
    font-family: $font-family-code;
    line-height: 1;
    padding-top: 0.2rem;
  }
  
  .timeline-content {
    flex: 1;
    
    h3 { font-size: 1.1rem; margin-bottom: 0.5rem; font-weight: 600; }
    p { color: $color-text-secondary; margin-bottom: 0.5rem; }
    
    .code-snippet {
      background: rgba(0,0,0,0.8);
      padding: 0.8rem;
      border-radius: 8px;
      
      code {
        color: #4ade80;
        font-family: $font-family-code;
        font-size: 0.85rem;
      }
    }
  }
}

.content-footer {
  text-align: center;
  color: $color-text-secondary;
  font-size: 0.9rem;
  margin-top: 2rem;
  opacity: 0.6;
}
</style>

<style lang="scss">
/* 
  NUCLEAR OPTION: Global Override for Dark Mode Glass Cards
  Forces dark gray frosted glass effect with maximum specificity
*/
html.dark .about-page .glass-card {
  background-color: rgba(30, 30, 35, 0.75) !important; /* Dark Gray Base */
  backdrop-filter: blur(24px) !important; /* Heavy Blur */
  -webkit-backdrop-filter: blur(24px) !important;
  border: 1px solid rgba(255, 255, 255, 0.1) !important; /* Subtle White Border */
  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.5) !important; /* Deep Shadow */
  color: #ffffff !important; /* Ensure text is readable */
}

/* Ensure text inside cards is high contrast */
html.dark .about-page .glass-card h2,
html.dark .about-page .glass-card h3,
html.dark .about-page .glass-card .lead-text {
  color: #ffffff !important;
}

html.dark .about-page .glass-card p,
html.dark .about-page .glass-card li {
  color: rgba(255, 255, 255, 0.8) !important;
}
</style>