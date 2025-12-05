<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import MagneticButton from './MagneticButton.vue';
import HandwrittenTitle from './HandwrittenTitle.vue';
import StartIcon from './icons/StartIcon.vue'; // Import new icon component

const { t, locale } = useI18n();

const typedText = ref("");
const typingSpeed = 80;
let timer: ReturnType<typeof setInterval> | null = null;

const typeText = () => {
  if (timer) clearInterval(timer);
  typedText.value = "";
  
  const slogan = t('hero.slogan');
  let i = 0;
  
  timer = setInterval(() => {
    if (i < slogan.length) {
      typedText.value += slogan.charAt(i);
      i++;
    } else {
      if (timer) clearInterval(timer);
    }
  }, typingSpeed);
};

watch(locale, () => {
  typeText();
});

onMounted(() => {
  typeText();
});
</script>

<template>
  <section class="hero">
    <div class="container hero__content">
      
      <h1 class="hero__title fade-in-up stagger-delay-2">
        <HandwrittenTitle :locale="locale" />
      </h1>
      

      
      <div class="hero__actions fade-in-up" style="transition-delay: 0.4s">
        <MagneticButton variant="primary">
          <template #icon>
            <!-- Choose variant: 'rocket-minimal' | 'rocket-metallic' | 'rocket-dynamic' -->
            <StartIcon variant="rocket-minimal" :size="20" />
          </template>
          {{ t('hero.start') }}
        </MagneticButton>
        <MagneticButton variant="outline">
          <template #icon>
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
            </svg>
          </template>
          GitHub
        </MagneticButton>
      </div>
    </div>
    
  </section>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.hero {
  position: relative;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  padding-top: 60px;

  &__content {
    position: relative;
    z-index: 10;
    text-align: center;
    display: flex;
    flex-direction: column;
    align-items: center;
  }

  &__badge {
    display: inline-block;
    padding: 6px 16px;
    background: rgba($color-accent-primary, 0.1);
    border: 1px solid $color-accent-primary;
    border-radius: 20px;
    color: $color-accent-primary;
    font-size: 0.8rem;
    margin-bottom: $spacing-lg;
    letter-spacing: 1px;
    text-transform: uppercase;
  }

  &__title {
    font-size: 5rem;
    font-weight: 900;
    margin-bottom: $spacing-md;
    font-family: 'Inter', $font-family-main;
    line-height: 1.1;
    letter-spacing: -2px;
    text-transform: uppercase;
    
    // Artistic Font / Logo Style Effect
    .hero__title-text {
      background: linear-gradient(135deg, #fff 30%, $color-accent-primary 80%, $color-accent-secondary 100%);
      -webkit-background-clip: text;
      background-clip: text;
      -webkit-text-fill-color: transparent;
      color: transparent;
      position: relative;
      display: inline-block;
      
      // Stroke Effect
      filter: drop-shadow(0 0 15px rgba($color-accent-primary, 0.3));
      
      &::after {
        content: attr(data-text);
        position: absolute;
        inset-inline-start: 0;
        top: 0;
        width: 100%;
        height: 100%;
        z-index: -1;
        background: none;
        -webkit-text-stroke: 2px rgba($color-accent-primary, 0.2);
      }
    }

    @media (max-width: $breakpoint-tablet) {
      font-size: 2.8rem;
      letter-spacing: -1px;
    }
  }

  &__cursor {
    display: inline-block;
    width: 6px;
    height: 0.9em;
    background: linear-gradient(to bottom, $color-accent-primary, $color-accent-secondary);
    animation: blink 1s step-end infinite;
    vertical-align: middle;
    margin-inline-start: 8px;
    border-radius: 2px;
    box-shadow: 0 0 10px $color-accent-primary;
  }



  &__actions {
    display: flex;
    gap: $spacing-md;
  }

  &__scroll-indicator {
    position: absolute;
    bottom: 40px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
    color: $color-text-secondary;
    font-size: 0.8rem;
    opacity: 0.7;
    animation: float 2s ease-in-out infinite;

    .mouse {
      width: 26px;
      height: 40px;
      border: 2px solid $color-text-secondary;
      border-radius: 13px;
      position: relative;

      &::after {
        content: '';
        position: absolute;
        top: 8px;
        left: 50%;
        transform: translateX(-50%);
        width: 4px;
        height: 4px;
        background-color: $color-text-secondary;
        border-radius: 50%;
        animation: scrollWheel 1.5s infinite;
      }
    }
  }
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

@keyframes float {
  0%, 100% { transform: translate(-50%, 0); }
  50% { transform: translate(-50%, 10px); }
}

@keyframes scrollWheel {
  0% { opacity: 1; top: 8px; }
  100% { opacity: 0; top: 20px; }
}
</style>
