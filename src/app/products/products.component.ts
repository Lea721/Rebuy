import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../services/product.service';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss']
})
export class ProductsComponent {

  searchQuery: string = '';
  selectedCategory: string = 'all';
  selectedCondition: string = 'all';

  isCategoryDropdownOpen = false;
  isConditionDropdownOpen = false;

  categories = [
    { id: 'all', name: 'All Categories' },
    { id: 'electronics', name: 'Electronics' },
    { id: 'clothing', name: 'Clothing' },
    { id: 'home', name: 'Home' },
    { id: 'books', name: 'Books' },
    { id: 'sports', name: 'Sports' }
  ];

  conditions = [
    { id: 'all', name: 'All Conditions' },
    { id: 'new', name: 'New' },
    { id: 'like-new', name: 'Like New' },
    { id: 'good', name: 'Good' },
    { id: 'fair', name: 'Fair' }
  ];

  products: any[] = [];
  filteredProducts: any[] = [];

  constructor(private productService: ProductService) {}

  ngOnInit() {
    this.productService.getProducts().subscribe({
      next: (products: any[]) => {
        this.products = products;
        this.filteredProducts = products;
      },
      error: err => console.error("Error fetching products:", err)
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
    this.filteredProducts = this.products.filter(p => {
      const matchesSearch =
        p.title.toLowerCase().includes(this.searchQuery.toLowerCase());

      const matchesCategory =
        this.selectedCategory === 'all' || p.category === this.selectedCategory;

      const matchesCondition =
        this.selectedCondition === 'all' || p.condition === this.selectedCondition;

      return matchesSearch && matchesCategory && matchesCondition;
    });
  }

  getCategoryName(): string {
    return this.categories.find(c => c.id === this.selectedCategory)?.name || 'All Categories';
  }

  getConditionName(): string {
    return this.conditions.find(c => c.id === this.selectedCondition)?.name || 'All Conditions';
  }
}
