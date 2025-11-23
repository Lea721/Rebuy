import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private api = 'http://localhost:8080/api/auth';

  private userSubject = new BehaviorSubject<any>(null);
  currentUser = this.userSubject.asObservable();

  constructor(private http: HttpClient) {
    const saved = localStorage.getItem('user');
    if (saved) {
      this.userSubject.next(JSON.parse(saved));
    }
  }

  signup(data: any): Observable<any> {
    return this.http.post(`${this.api}/register`, data);
  }

  login(data: any): Observable<any> {
    return this.http.post(`${this.api}/login`, data);
  }

  setUser(user: any) {
    localStorage.setItem('user', JSON.stringify(user));
    this.userSubject.next(user);
  }

  logout() {
    localStorage.removeItem('user');
    this.userSubject.next(null);
  }
}
