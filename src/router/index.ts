import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '../views/HomeView.vue';
import ArticleDetail from '../views/ArticleDetail.vue';
import SearchPage from '../views/SearchPage.vue';
import UserProfile from '../views/UserProfile.vue';
import EmailVerification from '../views/EmailVerification.vue';
import ArticleEditor from '../views/ArticleEditor.vue';
import { useAppStore } from '../store/app';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/article/:id',
      name: 'article-detail',
      component: ArticleDetail,
      props: true
    },
    {
      path: '/search',
      name: 'search',
      component: SearchPage
    },
    {
      path: '/profile',
      name: 'profile',
      component: UserProfile
    },
    {
      path: '/editor',
      name: 'editor',
      component: ArticleEditor
    },
    {
      path: '/email/verify',
      name: 'email-verify',
      component: EmailVerification
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

router.beforeEach((_to, _from, next) => {
  const { startLoading } = useAppStore();
  startLoading();
  next();
});

router.afterEach(() => {
  const { stopLoading } = useAppStore();
  // Short delay to allow animation to play a bit if page loads instantly
  // But user wants to see the animation. 
  // 1500ms is the full cycle.
  stopLoading(1500);
});

export default router;
