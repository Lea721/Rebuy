import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CartService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getCart(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/cart/${userId}`);
  }

  addToCart(userId: number, productId: number): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/cart/${userId}/${productId}`, {});
  }

  removeCartItem(cartItemId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/cart/${cartItemId}`);
  }

  clearCart(userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/cart/clear/${userId}`);
  }
}
