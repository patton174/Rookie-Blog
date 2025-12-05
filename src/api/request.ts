import axios from 'axios';
import i18n from '../i18n'; // Import the i18n instance

const service = axios.create({
  baseURL: '', // Proxy will handle /api
  timeout: 10000,
  withCredentials: true, // Crucial for cookie handling
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor
service.interceptors.request.use(
  (config) => {
    // Add 'lang' header based on current locale
    // i18n.global.locale.value gives the current locale string (e.g., 'en' or 'zh')
    const currentLocale = i18n.global.locale.value;
    config.headers['lang'] = currentLocale === 'zh' ? 'zh' : 'en';
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor
service.interceptors.response.use(
  (response) => {
    const res = response.data;
    // You might want to check custom error codes here
    // For now, we just return the response data
    return res;
  },
  (error) => {
    // Only log error if it's NOT a 401 (Unauthorized) which is expected when not logged in
    if (error.response && error.response.status !== 401) {
        console.error('API Error:', error);
    }
    return Promise.reject(error);
  }
);

export default service;
