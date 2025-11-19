import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-sell',
  imports: [CommonModule, FormsModule],
  templateUrl: './sell.component.html',
  styleUrl: './sell.component.scss'
})
export class SellComponent {
  // Form data
  productTitle: string = '';
  description: string = '';
  selectedCategory: string = '';
  selectedCondition: string = '';
  price: number | null = null;
  images: {url: string, file: File}[] = [];
  
  // Dropdown states
  isCategoryDropdownOpen: boolean = false;
  isConditionDropdownOpen: boolean = false;
  
  // Options
  categories = [
    { id: 'electronics', name: 'Electronics' },
    { id: 'clothing', name: 'Clothing' },
    { id: 'home', name: 'Home' },
    { id: 'books', name: 'Books' },
    { id: 'sports', name: 'Sports' }
  ];
  
  conditions = [
    { id: 'new', name: 'New' },
    { id: 'like-new', name: 'Like New' },
    { id: 'good', name: 'Good' },
    { id: 'fair', name: 'Fair' }
  ];
  
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
  }
  
  selectCondition(condition: any) {
    this.selectedCondition = condition.id;
    this.isConditionDropdownOpen = false;
  }
  
  getCategoryName(): string {
    return this.categories.find(c => c.id === this.selectedCategory)?.name || 'Select category';
  }
  
  getConditionName(): string {
    return this.conditions.find(c => c.id === this.selectedCondition)?.name || 'Select condition';
  }
  
  onFileSelect(event: any) {
    const files = Array.from(event.target.files) as File[];
    if (this.images.length + files.length <= 5) {
      const newImages = files.map(file => ({
        url: URL.createObjectURL(file),
        file: file
      }));
      this.images = [...this.images, ...newImages];
    } else {
      alert('Maximum 5 images allowed');
    }
  }
  
  removeImage(index: number) {
    const image = this.images[index];
    URL.revokeObjectURL(image.url);
    this.images.splice(index, 1);
  }
  
  onSubmit() {
    if (this.isFormValid()) {
      const formData = {
        title: this.productTitle,
        description: this.description,
        category: this.selectedCategory,
        condition: this.selectedCondition,
        price: this.price,
        images: this.images.map(img => img.file)
      };
      
      console.log('Product listing data:', formData);
      // Here you would typically send the data to your backend service
      alert('Product listed successfully!');
    } else {
      alert('Please fill in all required fields');
    }
  }
  
  isFormValid(): boolean {
    return !!(this.productTitle && 
             this.description && 
             this.selectedCategory && 
             this.selectedCondition && 
             this.price && 
             this.price > 0);
  }
}
