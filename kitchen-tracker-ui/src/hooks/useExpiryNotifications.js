import { useEffect } from "react";

const STORAGE_KEY = "kt_last_notified";

function daysUntil(expiryDate) {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const expiry = new Date(expiryDate + "T00:00:00");
  return Math.ceil((expiry - today) / 86_400_000);
}

function todayStr() {
  return new Date().toISOString().split("T")[0];
}

function plural(n, word) {
  return `${n} ${word}${n === 1 ? "" : "s"}`;
}

function notify(title, body, tag) {
  new Notification(title, {
    body,
    icon: "/pwa-192x192.png",
    badge: "/pwa-64x64.png",
    tag,
  });
}

function notifyExpired(group) {
  if (!group.length) return;
  const single = group.length === 1;
  notify(
    single ? `${group[0].name} has expired` : `${group.length} items have expired`,
    single ? "Check your kitchen and discard if needed." : group.map((i) => i.name).join(", "),
    "kt-expired"
  );
}

function notifyToday(group) {
  if (!group.length) return;
  const single = group.length === 1;
  notify(
    single ? `${group[0].name} expires today` : `${group.length} items expire today`,
    single ? "Use it today!" : group.map((i) => i.name).join(", "),
    "kt-today"
  );
}

function notifySoon(group) {
  if (!group.length) return;
  const body = group.map((i) => `${i.name} (${plural(daysUntil(i.expiryDate), "day")})`).join(", ");
  const single = group.length === 1;
  const d = daysUntil(group[0].expiryDate);
  notify(
    single ? `${group[0].name} expires in ${plural(d, "day")}` : `${group.length} items expiring soon`,
    body,
    "kt-soon"
  );
}

function fireNotifications(items) {
  const alertItems = items.filter((item) => item.expiryDate && daysUntil(item.expiryDate) <= 3);
  if (!alertItems.length) return;
  notifyExpired(alertItems.filter((i) => daysUntil(i.expiryDate) < 0));
  notifyToday(alertItems.filter((i) => daysUntil(i.expiryDate) === 0));
  notifySoon(alertItems.filter((i) => daysUntil(i.expiryDate) > 0));
}

export function useExpiryNotifications(items, pushSubscribed) {
  useEffect(() => {
    if (pushSubscribed) return;
    if (!items.length) return;
    if (!("Notification" in globalThis)) return;
    if (Notification.permission !== "granted") return;

    const last = localStorage.getItem(STORAGE_KEY);
    if (last === todayStr()) return;

    fireNotifications(items);
    localStorage.setItem(STORAGE_KEY, todayStr());
  }, [items, pushSubscribed]);
}
