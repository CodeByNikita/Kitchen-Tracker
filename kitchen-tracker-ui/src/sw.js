const CACHE = 'kt-v1';
// self.__WB_MANIFEST is replaced at build time by vite-plugin-pwa
const MANIFEST = self.__WB_MANIFEST || [];

globalThis.addEventListener('install', (event) => {
  event.waitUntil(
    globalThis.caches.open(CACHE).then((cache) => cache.addAll(MANIFEST.map((e) => e.url)))
  );
  globalThis.skipWaiting();
});

globalThis.addEventListener('activate', (event) => {
  event.waitUntil(
    globalThis.caches
      .keys()
      .then((keys) =>
        Promise.all(keys.filter((k) => k !== CACHE).map((k) => globalThis.caches.delete(k)))
      )
      .then(() => globalThis.clients.claim())
  );
});

globalThis.addEventListener('fetch', (event) => {
  if (event.request.method !== 'GET') return;
  event.respondWith(
    globalThis.caches.match(event.request).then((hit) => hit ?? fetch(event.request))
  );
});

globalThis.addEventListener('push', (event) => {
  let data = { title: 'Kitchen Tracker', body: '', tag: 'kt-expiry' };
  if (event.data) {
    try {
      data = { ...data, ...event.data.json() };
    } catch {
      data.body = event.data.text();
    }
  }
  event.waitUntil(
    globalThis.registration.showNotification(data.title, {
      body: data.body,
      icon: '/pwa-192x192.png',
      badge: '/pwa-64x64.png',
      tag: data.tag,
    })
  );
});

globalThis.addEventListener('notificationclick', (event) => {
  event.notification.close();
  event.waitUntil(
    globalThis.clients.matchAll({ type: 'window', includeUncontrolled: true }).then((list) => {
      for (const client of list) {
        if ('focus' in client) return client.focus();
      }
      return globalThis.clients.openWindow('/');
    })
  );
});
