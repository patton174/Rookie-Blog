<script setup lang="ts">
import { useI18n } from 'vue-i18n';

defineProps<{
  id: number | string;
  title: string;
  summary: string;
  date: string;
  tags: string[];
  image: string;
}>();

const { t, d } = useI18n();
</script>

<template>
  <div class="card-container">
    <div class="card">
      <div class="card__image-wrapper">
        <img 
          v-if="image" 
          :src="image" 
          loading="lazy" 
          class="card__image" 
          alt="Article thumbnail"
          decoding="async"
        />
        <div v-else class="card__image-placeholder"></div>
      </div>
      <div class="card__content">
        <div class="card__meta">
          <span class="date">{{ d(new Date(date), 'short') }}</span>
          <div class="tags">
            <span v-for="tag in tags" :key="tag" class="tag">#{{ tag }}</span>
          </div>
        </div>
        <router-link :to="{ name: 'article-detail', params: { id } }" class="card__link">
          <h3 class="card__title">{{ title }}</h3>
        </router-link>
        <p class="card__summary">{{ summary }}</p>
        <router-link :to="{ name: 'article-detail', params: { id } }" class="card__btn">
          {{ t('common.readMore') }}
        </router-link>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
@use '../styles/variables' as *;

.card__link {
  text-decoration: none;
  color: inherit;
  display: block;
}


.card-container {
  width: 100%;
  height: 100%;
  min-height: 420px;
  position: relative;
  z-index: 1;
}

.card {
  width: 100%;
  height: 100%;
  background: var(--color-card-bg);
  border: 1px solid var(--color-card-border);
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  display: flex;
  flex-direction: column;
  backdrop-filter: blur(10px);

  &:hover {
    transform: translateY(-10px);
    box-shadow: var(--color-card-hover-shadow);
    border-color: rgba($color-accent-primary, 0.3);

    .card__image {
      transform: scale(1.05);
    }

    .card__title {
      color: $color-accent-primary;
    }
  }

  &__image-wrapper {
    height: 200px;
    width: 100%;
    position: relative;
    overflow: hidden;
    background: radial-gradient(circle at center, #1a1a2e 0%, #050505 100%);

    &::after {
      content: '';
      position: absolute;
      bottom: 0;
      inset-inline-start: 0;
      width: 100%;
      height: 100%;
      background: linear-gradient(to bottom, transparent 0%, rgba(5, 5, 5, 0.8) 100%);
      pointer-events: none;
    }
  }

  &__image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.6s ease;
  }

  &__image-placeholder {
    width: 100%;
    height: 100%;
    background: var(--color-bg-secondary);
  }

  &__content {
    padding: $spacing-lg;
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  &__meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $spacing-md;
    font-size: 0.8rem;
    color: $color-text-secondary;
    font-family: $font-family-code;

    .tags {
      display: flex;
      gap: 6px;
    }

    .tag {
      color: $color-accent-primary;
      background: rgba($color-accent-primary, 0.1);
      padding: 2px 8px;
      border-radius: 4px;
    }
  }

  &__title {
    font-size: 1.4rem;
    margin-bottom: $spacing-sm;
    color: $color-text-primary;
    font-weight: 700;
    line-height: 1.3;
    transition: color 0.3s;
  }

  &__summary {
    font-size: 0.95rem;
    color: $color-text-secondary;
    line-height: 1.6;
    margin-bottom: $spacing-lg;
    flex: 1;
  }

  &__btn {
    align-self: flex-start;
    background: transparent;
    color: $color-text-primary;
    border: none;
    padding: 0;
    font-family: $font-family-code;
    font-weight: 600;
    cursor: pointer;
    position: relative;
    display: flex;
    align-items: center;
    gap: 8px;
    transition: gap 0.3s;
    text-decoration: none;

    &::after {
      content: '→';
      font-size: 1.2rem;
      color: $color-accent-primary;
      transition: transform 0.3s;
    }

    // RTL Support for arrow
    :global([dir="rtl"]) &::after {
      transform: scaleX(-1);
    }

    &:hover {
      color: $color-accent-primary;
      gap: 16px;
    }
  }
}
</style>
