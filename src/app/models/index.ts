// User models - matching backend User entity
export interface User {
  id?: number;
  email: string;
  name: string;
  phone?: string;
  city?: string;
  shippingAddress?: string;
  profileImageUrl?: string;
}

// Auth DTOs - matching backend DTOs
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  phone?: string;
  city?: string;
  shippingAddress?: string;
}

export interface UserUpdateRequest {
  name?: string;
  phone?: string;
  city?: string;
  shippingAddress?: string;
  profileImageUrl?: string;
}

export interface UserResponse {
  id: number;
  email: string;
  name: string;
  phone?: string;
  city?: string;
  shippingAddress?: string;
  profileImageUrl?: string;
}

// Product models (to be updated later when you share Product entity)
export interface Product {
  id?: number;
  title: string;
  description: string;
  price: number;
  category: string;
  condition: string;
  images: string[];
  sellerId?: number;
  sellerName?: string;
  createdAt?: Date;
}

export interface ProductRequest {
  title: string;
  description: string;
  price: number;
  category: string;
  condition: string;
  images: File[];
}

export interface ProductFilter {
  category?: string;
  condition?: string;
  search?: string;
  minPrice?: number;
  maxPrice?: number;
}