import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../services/product.service';
import { Product, ProductFilter } from '../models';

@Component({
  selector: 'app-products',
  imports: [CommonModule, FormsModule],
  templateUrl: './products.component.html',
  styleUrl: './products.component.scss'
})
export class ProductsComponent implements OnInit {
  searchQuery: string = '';
  selectedCategory: string = 'all';
  selectedCondition: string = 'all';
  isCategoryDropdownOpen: boolean = false;
  isConditionDropdownOpen: boolean = false;
  
  products: Product[] = [];
  filteredProducts: Product[] = [];
  isLoading: boolean = false;
  errorMessage: string = '';
  
  categories = [
    { id: 'all', name: 'All Categories' },
    { id: 'Electronics', name: 'Electronics' },
    { id: 'Clothing', name: 'Clothing' },
    { id: 'Home', name: 'Home' },
    { id: 'Books', name: 'Books' },
    { id: 'Sports', name: 'Sports' }
  ];
  
  conditions = [
    { id: 'all', name: 'All Conditions' },
    { id: 'New', name: 'New' },
    { id: 'Used - Excellent', name: 'Like New' },
    { id: 'Used - Good', name: 'Good' },
    { id: 'Used - Fair', name: 'Fair' }
  ];

  constructor(private productService: ProductService) {}

  ngOnInit() {
    this.loadProducts();
  }

  loadProducts() {
    this.isLoading = true;
    this.errorMessage = '';

    this.productService.getAvailableProducts().subscribe({
      next: (products) => {
        this.products = products;
        this.filteredProducts = products;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Failed to load products:', error);
        this.errorMessage = 'Failed to load products. Please try again.';
        this.isLoading = false;
      }
    });
  }
  
  toggleCategoryDropdown() {
    this.isCategoryDropdownOpen = !this.isCategoryDropdownOpen;
    this.isConditionDropdownOpen = false;
  }
  
  toggleConditionDropdown() {
    this.isConditionDropdownOpen = !this.isConditionDropdownOpen;
    this.isCategoryDropdownOpen = false;
  }
  
  selectCategory(category: any) {
    this.selectedCategory = category.id;
    this.isCategoryDropdownOpen = false;
    this.filterProducts();
  }
  
  selectCondition(condition: any) {
    this.selectedCondition = condition.id;
    this.isConditionDropdownOpen = false;
    this.filterProducts();
  }
  
  onSearch() {
    this.filterProducts();
  }
  
  filterProducts() {
    let filtered = [...this.products];

    // Filter by search query
    if (this.searchQuery) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(product =>
        product.title.toLowerCase().includes(query) ||
        product.description.toLowerCase().includes(query) ||
        product.location.toLowerCase().includes(query)
      );
    }

    // Filter by category
    if (this.selectedCategory !== 'all') {
      filtered = filtered.filter(product =>
        product.category === this.selectedCategory
      );
    }

    // Filter by condition
    if (this.selectedCondition !== 'all') {
      filtered = filtered.filter(product =>
        product.condition === this.selectedCondition
      );
    }

    this.filteredProducts = filtered;
  }
  
  getCategoryName(): string {
    return this.categories.find(c => c.id === this.selectedCategory)?.name || 'All Categories';
  }
  
  getConditionName(): string {
    return this.conditions.find(c => c.id === this.selectedCondition)?.name || 'All Conditions';
  }

  formatPrice(price: number): string {
    return `$${price.toFixed(2)}`;
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    // Prevent infinite loop: only set placeholder if not already set
    if (!img.src.endsWith('/assets/placeholder-image.jpg')) {
      img.src = '/assets/placeholder-image.jpg';
    }
  }
}
