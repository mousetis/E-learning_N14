export function formatVnd(amount?: number | null): string {
  if (amount == null || amount === 0) return '0 đ';
  const rounded = Math.round(amount);
  const sign = rounded < 0 ? '-' : '';
  return `${sign}${new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(Math.abs(rounded))}`;
}

export function formatUsd(amount: number): string {
  return formatVnd(amount);
}

export function digitsOnly(value: string, maxLength: number): string {
  return value.replace(/\D/g, '').slice(0, maxLength);
}

export function formatCardNumber(value: string): string {
  return digitsOnly(value, 16).replace(/(\d{4})(?=\d)/g, '$1 ');
}

export function formatExpiry(value: string): string {
  const digits = digitsOnly(value, 4);
  if (digits.length <= 2) {
    return digits;
  }
  return `${digits.slice(0, 2)}/${digits.slice(2)}`;
}
