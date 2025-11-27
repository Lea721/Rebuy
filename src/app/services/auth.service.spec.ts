import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';
import { UserResponse, LoginRequest, RegisterRequest, UserUpdateRequest } from '../models';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const apiUrl = environment.apiUrl;

  // Mock sessionStorage
  let store: any = {};
  const mockSessionStorage = {
    getItem: (key: string) => store[key] || null,
    setItem: (key: string, value: string) => { store[key] = value; },
    removeItem: (key: string) => { delete store[key]; },
    clear: () => { store = {}; }
  };

  beforeEach(() => {
    store = {}; // reset

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);

    // Replace sessionStorage
    spyOn(sessionStorage, 'getItem').and.callFake(mockSessionStorage.getItem);
    spyOn(sessionStorage, 'setItem').and.callFake(mockSessionStorage.setItem);
    spyOn(sessionStorage, 'removeItem').and.callFake(mockSessionStorage.removeItem);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should login and store user in sessionStorage', () => {
    const mockRequest: LoginRequest = { email: 'test@gmail.com', password: '1234' };
    const mockResponse: UserResponse = { id: 1, name: 'John', email: 'test@gmail.com' };

    service.login(mockRequest).subscribe(response => {
      expect(response).toEqual(mockResponse);
      expect(sessionStorage.setItem)
        .toHaveBeenCalledWith('currentUser', JSON.stringify(mockResponse));
      expect(service.getCurrentUser()).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${apiUrl}/auth/login`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should register and store user in sessionStorage', () => {
    const mockRequest: RegisterRequest = { name: 'Test', email: 'test@gmail.com', password: '1234' };
    const mockResponse: UserResponse = { id: 1, name: 'Test', email: 'test@gmail.com' };

    service.register(mockRequest).subscribe(response => {
      expect(response).toEqual(mockResponse);
      expect(sessionStorage.setItem)
        .toHaveBeenCalledWith('currentUser', JSON.stringify(mockResponse));
      expect(service.getCurrentUser()).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${apiUrl}/auth/register`);
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('should logout and clear session', () => {
    service.logout();
    expect(sessionStorage.removeItem).toHaveBeenCalledWith('currentUser');
    expect(service.getCurrentUser()).toBeNull();
  });

  it('should return true when authenticated', () => {
    const mockUser: UserResponse = { id: 1, name: 'Test', email: 'a@a.com' };
    mockSessionStorage.setItem('currentUser', JSON.stringify(mockUser));
    service['currentUserSubject'].next(mockUser);

    expect(service.isAuthenticated()).toBeTrue();
  });

  it('should return false when not authenticated', () => {
    service['currentUserSubject'].next(null);
    expect(service.isAuthenticated()).toBeFalse();
  });

  it('should get user by id', () => {
    const mockResponse: UserResponse = { id: 1, name: 'Test', email: 't@t.com' };

    service.getUserById(1).subscribe(response => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${apiUrl}/users/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should update user and store new data', () => {
    const mockRequest: UserUpdateRequest = { name: 'Updated' };
    const mockResponse: UserResponse = { id: 1, name: 'Updated', email: 'test@test.com' };

    service.updateUser(1, mockRequest).subscribe(response => {
      expect(response).toEqual(mockResponse);
      expect(sessionStorage.setItem)
        .toHaveBeenCalledWith('currentUser', JSON.stringify(mockResponse));
      expect(service.getCurrentUser()).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${apiUrl}/users/1`);
    expect(req.request.method).toBe('PUT');
    req.flush(mockResponse);
  });
});
