import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CartComponent } from './cart.component';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { CartService } from '../services/cart.service';
import { RouterTestingModule } from '@angular/router/testing';

describe('CartComponent', () => {
  let component: CartComponent;
  let fixture: ComponentFixture<CartComponent>;

  let mockAuthService: any;
  let mockCartService: any;
  let mockRouter: any;

  beforeEach(async () => {
    mockAuthService = {
      getCurrentUser: jasmine.createSpy('getCurrentUser'),
    };

    mockCartService = {
      getCart: jasmine.createSpy('getCart'),
      removeCartItem: jasmine.createSpy('removeCartItem'),
      clearCart: jasmine.createSpy('clearCart'),
    };

    mockRouter = {
      navigate: jasmine.createSpy('navigate'),
    };

    await TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule,
        CartComponent, // standalone component
      ],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: CartService, useValue: mockCartService },
        { provide: Router, useValue: mockRouter },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(CartComponent);
    component = fixture.componentInstance;
  });

  // ==================================
  // REDIRECT IF NOT AUTHENTICATED
  // ==================================
  it('should redirect to /signup if user is not authenticated', () => {
    mockAuthService.getCurrentUser.and.returnValue(null);

    fixture.detectChanges();

    expect(mockRouter.navigate).toHaveBeenCalledWith(['/signup']);
  });

  // ==================================
  // LOAD CART SUCCESS
  // ==================================
  it('should load cart items on init', () => {
    const mockUser = { id: 10 };
    const mockItems = [
      { id: 1, product: { title: 'Test', price: 10 } },
      { id: 2, product: { title: 'Item', price: 20 } },
    ];

    mockAuthService.getCurrentUser.and.returnValue(mockUser);
    mockCartService.getCart.and.returnValue(of(mockItems));

    fixture.detectChanges();

    expect(component.items).toEqual(mockItems);
    expect(component.isLoading).toBeFalse();
  });

  // ==================================
  // EMPTY CART MESSAGE
  // ==================================
  it('should show empty cart message', () => {
    mockAuthService.getCurrentUser.and.returnValue({ id: 5 });
    mockCartService.getCart.and.returnValue(of([]));

    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.textContent).toContain('Your cart is empty.');
  });

  // ==================================
  // LOAD CART ERROR
  // ==================================
  it('should show error message if loadCart fails', () => {
    mockAuthService.getCurrentUser.and.returnValue({ id: 5 });
    mockCartService.getCart.and.returnValue(throwError(() => new Error('fail')));

    fixture.detectChanges();

    expect(component.error).toBe('Failed to load cart.');
  });

  // ==================================
  // REMOVE CART ITEM
  // ==================================
  it('should call removeCartItem when remove() is executed', () => {
    const mockUser = { id: 5 };
    const mockItems = [{ id: 1, product: { title: 'Test' } }];

    mockAuthService.getCurrentUser.and.returnValue(mockUser);
    mockCartService.getCart.and.returnValue(of(mockItems));
    mockCartService.removeCartItem.and.returnValue(of(null));

    fixture.detectChanges();
    component.remove(1);

    expect(mockCartService.removeCartItem).toHaveBeenCalledWith(1);
  });

  // ==================================
  // CHECKOUT – CLEAR CART
  // ==================================
  it('should clear the cart on checkout', () => {
    const mockUser = { id: 5 };
    mockAuthService.getCurrentUser.and.returnValue(mockUser);
    mockCartService.getCart.and.returnValue(of([]));
    mockCartService.clearCart.and.returnValue(of(null));

    fixture.detectChanges();
    component.proceedToCheckout();

    expect(mockCartService.clearCart).toHaveBeenCalledWith(5);
  });
});
