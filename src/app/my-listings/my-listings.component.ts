import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ProductService } from '../services/product.service';
import { AuthService } from '../services/auth.service';
import { Product } from '../models';

@Component({
  selector: 'app-my-listings',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './my-listings.component.html',
  styleUrl: './my-listings.component.scss'
})
export class MyListingsComponent implements OnInit {
  products: Product[] = [];
  filteredProducts: Product[] = [];
  searchTerm: string = '';
  selectedCategory: string = '';
  selectedCondition: string = '';
  isLoading: boolean = true;

  categories = ['Electronics', 'Clothing', 'Home', 'Books', 'Sports', 'Other'];
  conditions = ['New', 'Like New', 'Good', 'Fair', 'Poor'];

  constructor(
    private productService: ProductService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadMyListings();
  }

  loadMyListings() {
    this.isLoading = true;
    const currentUser = this.authService.getCurrentUser();
    
    if (!currentUser || !currentUser.id) {
      this.router.navigate(['/login']);
      return;
    }

    // Try to get user products first
    this.productService.getUserProducts(currentUser.id).subscribe({
      next: (products: Product[]) => {
        this.products = products;
        this.filteredProducts = products;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading user listings:', error);
        // Fallback: Get all products and filter by current user
        this.productService.getAllProducts().subscribe({
          next: (allProducts: Product[]) => {
            this.products = allProducts.filter(product => 
              product.seller?.id === currentUser.id
            );
            this.filteredProducts = this.products;
            this.isLoading = false;
          },
          error: (fallbackError: any) => {
            console.error('Error loading products:', fallbackError);
            this.products = [];
            this.filteredProducts = [];
            this.isLoading = false;
          }
        });
      }
    });
  }

  filterProducts() {
    this.filteredProducts = this.products.filter(product => {
      const matchesSearch = product.title.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
                          product.description.toLowerCase().includes(this.searchTerm.toLowerCase());
      const matchesCategory = !this.selectedCategory || product.category === this.selectedCategory;
      const matchesCondition = !this.selectedCondition || product.condition === this.selectedCondition;
      
      return matchesSearch && matchesCategory && matchesCondition;
    });
  }

  onSearchChange() {
    this.filterProducts();
  }

  onCategoryChange() {
    this.filterProducts();
  }

  onConditionChange() {
    this.filterProducts();
  }

  editProduct(productId: number) {
    this.router.navigate(['/edit-product', productId]);
  }

  deleteProduct(productId: number) {
    if (confirm('Are you sure you want to delete this listing?')) {
      this.productService.deleteProduct(productId).subscribe({
        next: () => {
          this.loadMyListings(); // Reload the listings
        },
        error: (error: any) => {
          console.error('Error deleting product:', error);
          alert('Error deleting product. Please try again.');
        }
      });
    }
  }

  onImageError(event: any) {
    event.target.src = 'assets/images/placeholder.png';
  }

  addNewListing() {
    this.router.navigate(['/sell']);
  }

  formatPrice(price: number): string {
    return `$${price.toFixed(2)}`;
  }
}
