import { useEffect, useState } from 'react';
import axios from 'axios';
import { apiUrl } from '../config';

const PUSH_API = apiUrl('/api/push');
const storageKey = (identity) => `kt_push_subscribed_${identity || 'anonymous'}`;

function urlBase64ToUint8Array(base64String) {
  const padding = '='.repeat((4 - (base64String.length % 4)) % 4);
  const base64 = (base64String + padding).replace(/-/g, '+').replace(/_/g, '/');
  const raw = atob(base64);
  const output = new Uint8Array(raw.length);
  for (let i = 0; i < raw.length; i++) {
    output[i] = raw.charCodeAt(i);
  }
  return output;
}

export function usePushSubscription(notifPermission, enabled, identity) {
  const [subscribed, setSubscribed] = useState(
    () => localStorage.getItem(storageKey(identity)) === 'true'
  );

  useEffect(() => {
    queueMicrotask(() => {
      setSubscribed(localStorage.getItem(storageKey(identity)) === 'true');
    });
  }, [identity]);

  useEffect(() => {
    if (!enabled) return;
    if (notifPermission !== 'granted') return;
    if (!('serviceWorker' in navigator) || !('PushManager' in window)) return;
    if (subscribed) return;

    async function subscribe() {
      try {
        const { data: vapidKey } = await axios.get(`${PUSH_API}/vapid-public-key`);
        const reg = await navigator.serviceWorker.ready;
        const pushSub = await reg.pushManager.subscribe({
          userVisibleOnly: true,
          applicationServerKey: urlBase64ToUint8Array(vapidKey),
        });
        await axios.post(`${PUSH_API}/subscribe`, pushSub.toJSON());
        localStorage.setItem(storageKey(identity), 'true');
        setSubscribed(true);
      } catch (e) {
        console.error('Push subscription failed:', e);
      }
    }

    subscribe();
  }, [enabled, identity, notifPermission, subscribed]);

  return subscribed;
}
