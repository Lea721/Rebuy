import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Product, ProductRequest, ProductFilter } from '../models';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private apiUrl = environment.apiUrl;  // MUST be like: http://localhost:8080/api

  constructor(private http: HttpClient) {}

  // ===========================
  // GET ALL PRODUCTS
  // ===========================
  getAllProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/products`);
  }

  // ===========================
  // GET AVAILABLE PRODUCTS
  // ===========================
  getAvailableProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/products/available`);
  }

  // ===========================
  // GET PRODUCT BY ID
  // ===========================
  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/products/${id}`);
  }

  // ===========================
  // CREATE PRODUCT (NO IMAGE)
  // ===========================
  createProduct(productRequest: ProductRequest): Observable<Product> {
    return this.http.post<Product>(`${this.apiUrl}/products`, productRequest);
  }

  // ===========================
  // CREATE PRODUCT WITH IMAGE (correct)
  // ===========================
  createProductWithImage(
    productRequest: ProductRequest,
    file: File
  ): Observable<Product> {

    const formData = new FormData();

    // Backend expects EXACT names:
    // 1) product (STRING JSON)
    // 2) file (binary)
    formData.append("product", JSON.stringify(productRequest));
    formData.append("file", file);

    return this.http.post<Product>(
      `${this.apiUrl}/products/with-image`,
      formData
    );
  }

  // ===========================
  // UPDATE PRODUCT (JSON ONLY)
  // ===========================
  updateProduct(id: number, productRequest: ProductRequest): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/products/${id}`, productRequest);
  }

  // ===========================
  // DELETE PRODUCT
  // ===========================
  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/products/${id}`);
  }

  // ===========================
  // GET PRODUCTS BY SELLER
  // ===========================
  getUserProducts(userId: number): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/products/user/${userId}`);
  }

  // ===========================
  // SIMPLE FRONTEND FILTERING
  // ===========================
  searchProducts(filter: ProductFilter): Observable<Product[]> {
    let params = new HttpParams();

    if (filter.search) params = params.set('search', filter.search);
    if (filter.category) params = params.set('category', filter.category);
    if (filter.condition) params = params.set('condition', filter.condition);
    if (filter.location) params = params.set('location', filter.location);

    return this.getAvailableProducts();
  }
}
