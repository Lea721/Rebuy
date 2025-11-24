import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Product, ProductRequest, ProductFilter } from '../models';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  /**
   * Get all products - matches GET /api/products
   */
  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/products`);
  }

  /**
   * Get available products only - matches GET /api/products/available
   */
  getAvailableProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/products/available`);
  }

  /**
   * Get product by ID - matches GET /api/products/{id}
   */
  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/products/${id}`);
  }

  /**
   * Create new product - matches POST /api/products
   */
  createProduct(productRequest: ProductRequest): Observable<Product> {
    return this.http.post<Product>(`${this.apiUrl}/products`, productRequest);
  }

  /**
   * Update product - matches PUT /api/products/{id}
   */
  updateProduct(id: number, productRequest: ProductRequest): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/products/${id}`, productRequest);
  }

  /**
   * Delete product - matches DELETE /api/products/{id}
   */
  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/products/${id}`);
  }

  /**
   * Upload product image - matches POST /api/upload/image
   */
  uploadImage(file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file); // Backend expects 'file' field name
    
    return this.http.post(`${this.apiUrl}/upload/image`, formData, {
      responseType: 'text' // Backend returns plain text URL
    });
  }

  /**
   * Get products by user ID - matches GET /api/products/user/{userId}
   */
  getUserProducts(userId: number): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/products/user/${userId}`);
  }

  /**
   * Search/filter products (frontend filtering for now)
   */
  searchProducts(filter: ProductFilter): Observable<Product[]> {
    let params = new HttpParams();
    
    if (filter.search) {
      params = params.set('search', filter.search);
    }
    if (filter.category) {
      params = params.set('category', filter.category);
    }
    if (filter.condition) {
      params = params.set('condition', filter.condition);
    }
    if (filter.location) {
      params = params.set('location', filter.location);
    }

    // For now, get all available products and filter on frontend
    // Later you can implement backend search endpoint
    return this.getAvailableProducts();
  }
}