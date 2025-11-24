import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { User, LoginRequest, RegisterRequest, UserResponse, UserUpdateRequest } from '../models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiUrl;
  private currentUserSubject = new BehaviorSubject<User | null>(null);

  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    // Check for existing user data on service initialization
    const userData = sessionStorage.getItem('currentUser');
    if (userData) {
      this.currentUserSubject.next(JSON.parse(userData));
    }
  }

  /**
   * Login user - matches backend POST /api/auth/login
   */
  login(credentials: LoginRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${this.apiUrl}/auth/login`, credentials)
      .pipe(
        tap(userResponse => {
          // Store user data in sessionStorage and update current user
          sessionStorage.setItem('currentUser', JSON.stringify(userResponse));
          this.currentUserSubject.next(userResponse);
        })
      );
  }

  /**
   * Register user - matches backend POST /api/auth/register
   */
  register(userData: RegisterRequest): Observable<UserResponse> {
    return this.http.post<UserResponse>(`${this.apiUrl}/auth/register`, userData)
      .pipe(
        tap(userResponse => {
          // Store user data in sessionStorage and update current user
          sessionStorage.setItem('currentUser', JSON.stringify(userResponse));
          this.currentUserSubject.next(userResponse);
        })
      );
  }

  /**
   * Logout user
   */
  logout(): void {
    sessionStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  /**
   * Check if user is authenticated
   */
  isAuthenticated(): boolean {
    return !!this.currentUserSubject.value;
  }

  /**
   * Get current user
   */
  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Get current user ID
   */
  getCurrentUserId(): number | null {
    const user = this.getCurrentUser();
    return user ? user.id || null : null;
  }

  /**
   * Get user by ID - matches backend GET /api/users/{id}
   */
  getUserById(id: number): Observable<UserResponse> {
    return this.http.get<UserResponse>(`${this.apiUrl}/users/${id}`);
  }

  /**
   * Update user profile - matches backend PUT /api/users/{id}
   */
  updateUser(id: number, updateData: UserUpdateRequest): Observable<UserResponse> {
    return this.http.put<UserResponse>(`${this.apiUrl}/users/${id}`, updateData)
      .pipe(
        tap(userResponse => {
          // Update stored user data and current user
          sessionStorage.setItem('currentUser', JSON.stringify(userResponse));
          this.currentUserSubject.next(userResponse);
        })
      );
  }
}