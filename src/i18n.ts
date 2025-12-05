import { createI18n } from 'vue-i18n'
import en from './locales/en'
import zh from './locales/zh'

const datetimeFormats = {
  en: {
    short: {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    },
    long: {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      weekday: 'long'
    }
  },
  zh: {
    short: {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    },
    long: {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      weekday: 'long'
    }
  }
} as const

const savedLocale = localStorage.getItem('locale') || 'en'

const i18n = createI18n({
  legacy: false, // use Composition API
  locale: savedLocale, // default locale
  fallbackLocale: 'en',
  messages: {
    en,
    zh
  },
  datetimeFormats
})

export default i18n
