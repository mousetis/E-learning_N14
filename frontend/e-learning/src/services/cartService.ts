import { apiClient, ENDPOINTS } from '@/api';
import type { CartItem } from '@/types/cart';

const PASTEL_COLORS = ['#c9bcff', '#ffd6a3', '#b2eedf', '#ffbfd2', '#ffe8a3', '#d0e1fd'];

export const cartService = {
  async getCart(): Promise<{ items: CartItem[]; subtotal: number; savings: number; total: number }> {
    try {
      const data = await apiClient.get<any>(ENDPOINTS.CART.GET);
      const rawItems = data?.items || [];
      const items: CartItem[] = rawItems.map((item: any, idx: number) => {
        const c = item.course || {};
        const price = item.priceAtAdded ?? c.salePrice ?? c.originalPrice ?? 0;
        return {
          id: c.id,
          courseId: c.id,
          title: c.title || 'Khóa học',
          author: c.instructor?.fullName || 'Giảng viên',
          price: Number(price),
          thumbnailColor: PASTEL_COLORS[idx % PASTEL_COLORS.length],
          thumbnailLabel: (c.category?.name || 'Khóa học').slice(0, 3).toUpperCase(),
          image: c.thumbnailUrl || undefined,
        };
      });

      return {
        items,
        subtotal: Number(data?.originalTotal ?? 0),
        savings: Number(data?.savings ?? 0),
        total: Number(data?.subtotal ?? 0),
      };
    } catch (err) {
      console.warn('cartService.getCart error:', err);
      return { items: [], subtotal: 0, savings: 0, total: 0 };
    }
  },

  async addToCart(courseId: string): Promise<any> {
    return apiClient.post(ENDPOINTS.CART.ADD_ITEM, { courseId });
  },

  async removeFromCart(courseId: string): Promise<any> {
    return apiClient.delete(ENDPOINTS.CART.REMOVE_ITEM(courseId));
  },

  async validateCoupon(code: string, courseIds: string[]): Promise<any> {
    return apiClient.post(ENDPOINTS.COUPONS.VALIDATE, { code, courseIds });
  },

  async checkout(params: {
    courseIds: string[];
    method?: 'MOCK' | 'VNPAY';
    couponCode?: string;
  }): Promise<{ orderCode: string; totalAmount: number; status: string; paymentUrl?: string }> {
    const method = params.method || 'MOCK';
    const order = await apiClient.post<any>(ENDPOINTS.ORDERS.CREATE, {
      courseIds: params.courseIds,
      method,
      couponCode: params.couponCode || undefined,
    });

    if (method === 'MOCK' && order?.orderCode) {
      try {
        await apiClient.post(ENDPOINTS.PAYMENTS.MOCK_COMPLETE(order.orderCode) + '?result=SUCCESS');
      } catch (e) {
        console.warn('Mock payment complete callback note:', e);
      }
    }

    return {
      orderCode: order.orderCode,
      totalAmount: Number(order.totalAmount || 0),
      status: 'PAID',
      paymentUrl: order.paymentUrl,
    };
  },

  async getOrderDetail(orderCode: string): Promise<any> {
    return apiClient.get(ENDPOINTS.ORDERS.DETAIL(orderCode));
  },
};
