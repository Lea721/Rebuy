import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private api = 'http://localhost:8080/api/products';

  constructor(private http: HttpClient) {}

  createProduct(data: any): Observable<any> {
    return this.http.post(`${this.api}/create`, data);
  }

  getProducts(): Observable<any> {
    return this.http.get(`${this.api}`);
  }
}
