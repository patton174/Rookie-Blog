<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import FooterLogo from './FooterLogo.vue';
import IconButton from './IconButton.vue';

const { t } = useI18n();

const links = {
  product: ['Features', 'Integrations', 'Pricing', 'Changelog', 'Docs'],
  company: ['About Us', 'Careers', 'Blog', 'Contact', 'Partners'],
  legal: ['Privacy', 'Terms', 'Security']
};
</script>

<template>
  <footer class="footer">
    <!-- Background Overlay -->
    <div class="footer__bg-overlay"></div>

    <div class="container footer__container">
      
      <!-- Top Section: Brand & Main Info -->
      <div class="footer__top">
        <div class="footer__brand-col">
          <FooterLogo variant="horizontal" class="footer__logo-component" />
          <p class="footer__desc">
            {{ t('footer.desc') }}
          </p>
          
          <!-- Social Links -->
          <div class="footer__socials">
            <IconButton size="sm" variant="ghost" aria-label="GitHub">
              <template #icon>GH</template>
            </IconButton>
            <IconButton size="sm" variant="ghost" aria-label="Twitter">
              <template #icon>TW</template>
            </IconButton>
            <IconButton size="sm" variant="ghost" aria-label="LinkedIn">
              <template #icon>LI</template>
            </IconButton>
          </div>
        </div>

        <!-- Links Grid -->
        <div class="footer__nav-grid">
          <div class="nav-col">
            <h4 class="nav-title">Product</h4>
            <ul>
              <li v-for="link in links.product" :key="link"><a href="#" class="nav-link">{{ link }}</a></li>
            </ul>
          </div>
          <div class="nav-col">
            <h4 class="nav-title">Company</h4>
            <ul>
              <li v-for="link in links.company" :key="link"><a href="#" class="nav-link">{{ link }}</a></li>
            </ul>
          </div>
          <div class="nav-col">
            <h4 class="nav-title">Resources</h4>
            <ul>
              <li><a href="#" class="nav-link">Community</a></li>
              <li><a href="#" class="nav-link">Help Center</a></li>
              <li><a href="#" class="nav-link">Status</a></li>
            </ul>
          </div>
        </div>
      </div>

      <!-- Middle Section: Stats & Testimonials (Removed as requested) -->
      <!-- 
      <div class="footer__middle">
        <div class="stats-row">
          <div v-for="stat in stats" :key="stat.label" class="stat-card">
            <span class="stat-value">{{ stat.value }}</span>
            <span class="stat-label">{{ stat.label }}</span>
          </div>
        </div>
        
        <div class="testimonial-card">
          <p class="quote">"{{ testimonials[0].quote }}"</p>
          <div class="author-info">
            <span class="author-name">{{ testimonials[0].author }}</span>
            <span class="author-role">{{ testimonials[0].role }}</span>
          </div>
        </div>
      </div>
      -->

      <!-- Divider -->
      <div class="footer__divider"></div>

      <!-- Bottom Section: Copyright & Utility -->
      <div class="footer__bottom">
        <p class="copyright">{{ t('footer.rights') }}</p>
        
        <div class="footer__utility">
          <div class="footer__links">
            <a href="#" v-for="link in links.legal" :key="link">{{ link }}</a>
          </div>
          
          <!-- Language Switcher Placeholder (Removed as requested) -->
          <!-- 
          <button class="lang-switch">
            <span class="icon">🌐</span> English
          </button>
          -->
        </div>
      </div>
    </div>
  </footer>
</template>

<style lang="scss" scoped>
@use 'sass:color';
@use '../styles/variables' as *;

.footer {
  position: relative;
  background-color: $color-footer-bg;
  color: $color-footer-text;
  padding: 80px 0 40px;
  margin-top: 80px;
  overflow: hidden;
  font-family: 'Helvetica Neue', 'Inter', sans-serif;
  transition: background-color 0.3s, color 0.3s;

  // Glassmorphism Background
  &__bg-overlay {
    position: absolute;
    top: 0;
    inset-inline-start: 0;
    width: 100%;
    height: 100%;
    background: $color-footer-overlay; // Fallback / Overlay
    backdrop-filter: blur(8px);
    -webkit-backdrop-filter: blur(8px);
    z-index: 0;
    pointer-events: none;
    transition: background 0.3s;
    
    &::before {
      content: '';
      position: absolute;
      top: -50%;
      inset-inline-start: -20%;
      width: 80%;
      height: 80%;
      background: radial-gradient(circle, rgba($color-accent-primary, 0.05) 0%, transparent 70%);
      pointer-events: none;
    }
  }

  &__container {
    position: relative;
    z-index: 1;
    display: flex;
    flex-direction: column;
    gap: 60px;
  }

  /* --- TOP SECTION --- */
  &__top {
    display: grid;
    grid-template-columns: 4fr 8fr;
    gap: 60px;

    @media (max-width: $breakpoint-tablet) {
      grid-template-columns: 1fr;
      gap: 40px;
    }
  }

  &__brand-col {
    display: flex;
    flex-direction: column;
    gap: 24px;
  }

  &__desc {
    font-size: 16px;
    line-height: 1.6;
    max-width: 320px;
    color: $color-text-tertiary;
  }

  &__socials {
    display: flex;
    gap: 12px;
  }

  &__nav-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 30px;

    @media (max-width: $breakpoint-mobile) {
      grid-template-columns: repeat(2, 1fr);
    }
  }

  .nav-col {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .nav-title {
    color: $color-footer-heading;
    font-size: 14px;
    font-weight: 600;
    text-transform: uppercase;
    letter-spacing: 1px;
    margin: 0;
  }

  .nav-link {
    color: $color-footer-text;
    font-size: 15px;
    text-decoration: none;
    transition: color 0.2s ease, transform 0.2s ease;
    display: inline-block;

    &:hover {
      color: $color-accent-primary;
      transform: translateX(4px);
    }
  }

  ul {
    list-style: none;
    padding: 0;
    margin: 0;
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  /* --- MIDDLE SECTION --- */
  &__middle {
    display: grid;
    grid-template-columns: 7fr 5fr;
    gap: 40px;
    align-items: center;
    padding: 40px;
    background: rgba(255, 255, 255, 0.03);
    border-radius: 12px;
    border: 1px solid $color-footer-border;

    @media (max-width: $breakpoint-tablet) {
      grid-template-columns: 1fr;
      padding: 24px;
    }
  }

  .stats-row {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;

    @media (max-width: $breakpoint-mobile) {
      grid-template-columns: repeat(2, 1fr);
    }
  }

  .stat-card {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .stat-value {
    color: $color-accent-primary;
    font-size: 24px;
    font-weight: 700;
    font-family: 'Fira Code', monospace;
  }

  .stat-label {
    font-size: 13px;
    color: $color-footer-text;
  }

  .testimonial-card {
    border-inline-start: 2px solid $color-accent-secondary;
    padding-inline-start: 20px;
    
    .quote {
      font-size: 16px;
      font-style: italic;
      color: $color-footer-heading;
      margin-bottom: 12px;
      line-height: 1.5;
    }

    .author-info {
      display: flex;
      flex-direction: column;
      font-size: 13px;
      
      .author-name { color: $color-footer-heading; font-weight: 600; }
      .author-role { color: $color-footer-text; }
    }
  }

  /* --- BOTTOM SECTION --- */
  &__divider {
    height: 1px;
    background: linear-gradient(
      90deg, 
      transparent, 
      $color-footer-border, 
      transparent
    );
    width: 100%;
    opacity: 0.5;
  }

  &__bottom {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 14px;

    @media (max-width: $breakpoint-mobile) {
      flex-direction: column;
      gap: 20px;
      text-align: center;
    }
  }

  &__utility {
    display: flex;
    align-items: center;
    gap: 30px;

    @media (max-width: $breakpoint-mobile) {
      flex-direction: column;
      gap: 16px;
    }
  }

  &__links {
    display: flex;
    gap: 24px;

    a {
      color: $color-footer-text;
      text-decoration: none;
      transition: color 0.2s;

      &:hover {
        color: $color-footer-heading;
      }
    }
  }

  .lang-switch {
    background: transparent;
    border: 1px solid $color-footer-border;
    color: $color-footer-text;
    padding: 6px 12px;
    border-radius: 4px;
    cursor: pointer;
    font-size: 13px;
    display: flex;
    align-items: center;
    gap: 6px;
    transition: all 0.2s;

    &:hover {
      border-color: $color-footer-text;
      color: $color-footer-heading;
    }
  }
}
</style>
