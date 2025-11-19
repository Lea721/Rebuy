import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-products',
  imports: [CommonModule, FormsModule],
  templateUrl: './products.component.html',
  styleUrl: './products.component.scss'
})
export class ProductsComponent {
  searchQuery: string = '';
  selectedCategory: string = 'all';
  selectedCondition: string = 'all';
  isCategoryDropdownOpen: boolean = false;
  isConditionDropdownOpen: boolean = false;
  
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
  
  products: any[] = []; // This will be populated with real data later
  
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
    // Filter logic will be implemented here
    console.log('Filtering products:', {
      search: this.searchQuery,
      category: this.selectedCategory,
      condition: this.selectedCondition
    });
  }
  
  getCategoryName(): string {
    return this.categories.find(c => c.id === this.selectedCategory)?.name || 'All Categories';
  }
  
  getConditionName(): string {
    return this.conditions.find(c => c.id === this.selectedCondition)?.name || 'All Conditions';
  }
}
