import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './styles/theme.css'
import './styles/main.scss'
import 'highlight.js/styles/atom-one-dark.css'
import i18n from './i18n'

createApp(App).use(router).use(i18n).mount('#app')
