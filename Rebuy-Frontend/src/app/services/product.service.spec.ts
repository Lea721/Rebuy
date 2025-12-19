import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ProductService } from './product.service';
import { environment } from '../../environments/environment';
import { Product, ProductRequest, ProductFilter } from '../models';

describe('ProductService', () => {
  let service: ProductService;
  let httpMock: HttpTestingController;

  const apiUrl = environment.apiUrl;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ProductService],
    });

    service = TestBed.inject(ProductService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  // ==============================
  // GET ALL PRODUCTS
  // ==============================
  it('should GET all products', () => {
    const mockProducts: Product[] = [
      { id: 1, title: 'Test', price: 10 } as Product,
      { id: 2, title: 'Another', price: 20 } as Product,
    ];

    service.getAllProducts().subscribe(products => {
      expect(products).toEqual(mockProducts);
    });

    const req = httpMock.expectOne(`${apiUrl}/products`);
    expect(req.request.method).toBe('GET');
    req.flush(mockProducts);
  });

  // ==============================
  // GET AVAILABLE PRODUCTS
  // ==============================
  it('should GET available products', () => {
    const mockProducts: Product[] = [
      { id: 1, title: 'Test', price: 10 } as Product,
    ];

    service.getAvailableProducts().subscribe(products => {
      expect(products).toEqual(mockProducts);
    });

    const req = httpMock.expectOne(`${apiUrl}/products/available`);
    expect(req.request.method).toBe('GET');
    req.flush(mockProducts);
  });

  // ==============================
  // GET PRODUCT BY ID
  // ==============================
  it('should GET product by id', () => {
    const mockProduct: Product = { id: 1, title: 'Test', price: 10 } as Product;

    service.getProductById(1).subscribe(product => {
      expect(product).toEqual(mockProduct);
    });

    const req = httpMock.expectOne(`${apiUrl}/products/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockProduct);
  });

  // ==============================
  // CREATE PRODUCT (JSON)
  // ==============================
  it('should POST create product (JSON)', () => {
    const mockRequest: ProductRequest = { title: 'New', price: 10 } as ProductRequest;
    const mockResponse: Product = { id: 1, title: 'New', price: 10 } as Product;

    service.createProduct(mockRequest).subscribe(product => {
      expect(product).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${apiUrl}/products`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockRequest);
    req.flush(mockResponse);
  });

  // ==============================
  // CREATE PRODUCT WITH IMAGE
  // ==============================
  it('should POST create product with image', () => {
    const mockRequest: ProductRequest = { title: 'New', price: 10 } as ProductRequest;
    const mockResponse: Product = { id: 1, title: 'New', price: 10 } as Product;

    const fakeFile = new File(['test'], 'test.png', { type: 'image/png' });

    service.createProductWithImage(mockRequest, fakeFile).subscribe(product => {
      expect(product).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${apiUrl}/products/with-image`);
    expect(req.request.method).toBe('POST');

    // FormData cannot be checked directly → but we check type
    expect(req.request.body instanceof FormData).toBeTrue();

    req.flush(mockResponse);
  });

  // ==============================
  // UPDATE PRODUCT
  // ==============================
  it('should PUT update product', () => {
    const mockRequest: ProductRequest = { title: 'Updated', price: 30 } as ProductRequest;
    const mockResponse: Product = { id: 1, title: 'Updated', price: 30 } as Product;

    service.updateProduct(1, mockRequest).subscribe(product => {
      expect(product).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(`${apiUrl}/products/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(mockRequest);
    req.flush(mockResponse);
  });

  // ==============================
  // DELETE PRODUCT
  // ==============================
  it('should DELETE product', () => {
    service.deleteProduct(1).subscribe(res => {
      expect(res).toBeNull();
    });

    const req = httpMock.expectOne(`${apiUrl}/products/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  // ==============================
  // GET USER PRODUCTS
  // ==============================
  it('should GET products by user', () => {
    const mockProducts: Product[] = [
      { id: 1, title: 'User Product', price: 20 } as Product,
    ];

    service.getUserProducts(1).subscribe(products => {
      expect(products).toEqual(mockProducts);
    });

    const req = httpMock.expectOne(`${apiUrl}/products/user/1`);
    expect(req.request.method).toBe('GET');
    req.flush(mockProducts);
  });

  // ==============================
  // SEARCH PRODUCTS (frontend — returns available products)
  // ==============================
  it('should search products using getAvailableProducts', () => {
    const mockProducts: Product[] = [
      { id: 1, title: 'Available Product', price: 50 } as Product,
    ];

    const filter: ProductFilter = {
      search: 'test',
      category: 'electronics'
    };

    service.searchProducts(filter).subscribe(products => {
      expect(products).toEqual(mockProducts);
    });

    const req = httpMock.expectOne(`${apiUrl}/products/available`);
    expect(req.request.method).toBe('GET');
    req.flush(mockProducts);
  });
});
