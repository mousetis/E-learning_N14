export function formatUsd(amount: number): string {
  const rounded = Math.round(amount);
  const sign = rounded < 0 ? '-' : '';
  return `${sign}$${Math.abs(rounded)}`;
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
