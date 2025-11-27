import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { CartService } from './cart.service';
import { environment } from '../../environments/environment';

describe('CartService', () => {
  let service: CartService;
  let httpMock: HttpTestingController;
  const apiUrl = environment.apiUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [CartService]
    });

    service = TestBed.inject(CartService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should GET cart items', () => {
    const mockResponse = [{ id: 1, product: 'Test' }];

    service.getCart(10).subscribe(items => {
      expect(items).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${apiUrl}/cart/10`);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should POST add to cart', () => {
    const mockResponse = { message: 'added' };

    service.addToCart(10, 5).subscribe(res => {
      expect(res).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${apiUrl}/cart/10/5`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({});
    req.flush(mockResponse);
  });

  it('should DELETE a cart item', () => {
    service.removeCartItem(3).subscribe(res => {
      expect(res).toBeNull();   // ✅ FIXED
    });

    const req = httpMock.expectOne(`${apiUrl}/cart/3`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);            // DELETE returns null
  });

  it('should DELETE clear the cart', () => {
    service.clearCart(10).subscribe(res => {
      expect(res).toBeNull();   // ✅ FIXED
    });

    const req = httpMock.expectOne(`${apiUrl}/cart/clear/10`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);            
  });
});
