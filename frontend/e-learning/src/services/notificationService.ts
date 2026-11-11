import { apiClient, ENDPOINTS } from '@/api';
import type { NotificationItem } from '@/types/notification';

function formatRelativeTime(dateString?: string): string {
  if (!dateString) return 'Vừa xong';
  const diff = Date.now() - new Date(dateString).getTime();
  const mins = Math.floor(diff / 60000);
  if (mins < 60) return `${Math.max(1, mins)} phút trước`;
  const hours = Math.floor(mins / 60);
  if (hours < 24) return `${hours} giờ trước`;
  const days = Math.floor(hours / 24);
  return `${days} ngày trước`;
}

function getIconColor(type?: string): string {
  switch (type) {
    case 'PROMOTION':
      return '#ffd6a3';
    case 'CERTIFICATE':
      return '#b2eedf';
    case 'PAYMENT':
      return '#c9bcff';
    case 'QA_REPLY':
    default:
      return '#ffbfd2';
  }
}

export const notificationService = {
  async getNotifications(): Promise<NotificationItem[]> {
    try {
      const data = await apiClient.get<any>(ENDPOINTS.NOTIFICATIONS.LIST);
      const items = data?.items || data;
      if (Array.isArray(items) && items.length > 0) {
        return items.map((n: any) => ({
          id: n.id,
          title: n.title,
          body: n.content,
          time: formatRelativeTime(n.createdAt),
          iconColor: getIconColor(n.type),
          unread: !n.isRead && !n.read,
        }));
      }
      return [];
    } catch (err) {
      console.warn('notificationService.getNotifications error:', err);
      return [];
    }
  },

  async getUnreadCount(): Promise<number> {
    try {
      const data = await apiClient.get<any>(ENDPOINTS.NOTIFICATIONS.UNREAD_COUNT);
      return Number(data?.unreadCount ?? 0);
    } catch {
      return 0;
    }
  },

  async markAsRead(id: string): Promise<any> {
    return apiClient.put(ENDPOINTS.NOTIFICATIONS.MARK_READ(id));
  },

  async markAllAsRead(): Promise<any> {
    return apiClient.put(ENDPOINTS.NOTIFICATIONS.READ_ALL);
  },
};
