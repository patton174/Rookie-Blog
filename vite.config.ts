import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          'md-editor': ['md-editor-v3'],
          'vue-vendor': ['vue', 'vue-router', 'vue-i18n'],
        }
      }
    },
    chunkSizeWarningLimit: 1000,
  },
  server: {
    host: '0.0.0.0',
    proxy: {
      '/api': {
        // target: 'https://api.lxsweb.top',
        target: 'http://localhost:8080',
        changeOrigin: true,
        // The backend might be seeing the request as Cross-Origin because of the headers sent by the proxy.
        // Sa-Token often blocks requests that don't look "local" or trusted if not configured for CORS.
        // By default, 'changeOrigin: true' changes the 'Origin' header to match the target (localhost:12345).
        // This usually TRICKS the backend into thinking it's a same-origin request.
        // However, if the backend specifically checks for the *client's* origin (localhost:5173) and rejects it, that's an issue.
        // BUT, usually "Invalid CORS request" comes when the Origin header is present but not allowed, OR when the backend detects a mismatch.
        
        // Let's try to NOT rewrite the path first, as the user said "Base URL: localhost:12345/api".
        // So /api/user/auth/login should map to http://localhost:12345/api/user/auth/login.
        // My previous rewrite was potentially wrong if I uncommented it.
        
        // If "Invalid CORS request" is the response body:
        // It means the request reached the server, but the server rejected it.
        // This is often due to Sa-Token's checkSafe logic or Spring Security.
        // We can try adding Referer header.
        configure: (proxy, _options) => {
          proxy.on('proxyReq', (proxyReq, _req, _res) => {
            // Sometimes backends check Referer
            proxyReq.setHeader('Referer', 'http://localhost:12345/');
            // Ensure Origin matches target for strict same-origin checks
            proxyReq.setHeader('Origin', 'http://localhost:12345');
          });
        },
      }
    }
  }
})
