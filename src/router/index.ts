import { createRouter, createWebHistory } from 'vue-router';
import { useAppStore } from '../store/app';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue')
    },
    {
      path: '/article/:id',
      name: 'article-detail',
      component: () => import('../views/ArticleDetail.vue'),
      props: true
    },
    {
      path: '/search',
      name: 'search',
      component: () => import('../views/SearchPage.vue')
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/UserProfile.vue')
    },
    {
      path: '/editor',
      name: 'editor',
      component: () => import('../views/ArticleEditor.vue')
    },
    {
      path: '/email/verify',
      name: 'email-verify',
      component: () => import('../views/EmailVerification.vue')
    }
  ],
  scrollBehavior(_to, _from, savedPosition) {
    if (savedPosition) {
      return savedPosition;
    } else {
      return { top: 0 };
    }
  }
});

let loadStartTime = 0;

router.beforeEach((_to, _from, next) => {
  const { startLoading } = useAppStore();
  loadStartTime = Date.now();
  startLoading();
  next();
});

router.afterEach(() => {
  const { stopLoading } = useAppStore();
  const MIN_LOADING_TIME = 600;
  const elapsed = Date.now() - loadStartTime;
  const remaining = MIN_LOADING_TIME - elapsed;
  
  stopLoading(remaining > 0 ? remaining : 0);
});

export default router;
