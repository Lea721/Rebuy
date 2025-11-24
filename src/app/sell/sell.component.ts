import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../services/product.service';
import { AuthService } from '../services/auth.service';
import { ProductRequest, Product } from '../models';

@Component({
  selector: 'app-sell',
  imports: [CommonModule, FormsModule],
  templateUrl: './sell.component.html',
  styleUrl: './sell.component.scss'
})
export class SellComponent implements OnInit {
  // Edit mode
  isEditMode: boolean = false;
  editProductId: number | null = null;
  currentProduct: Product | null = null;
  
  // Form data
  productTitle: string = '';
  description: string = '';
  selectedCategory: string = '';
  selectedCondition: string = '';
  location: string = '';
  price: number | null = null;
  selectedImage: File | null = null;
  imagePreviewUrl: string = '';
  
  // States
  isCategoryDropdownOpen: boolean = false;
  isConditionDropdownOpen: boolean = false;
  isUploading: boolean = false;
  isSubmitting: boolean = false;
  isLoading: boolean = false;
  errorMessage: string = '';
  
  // Options (matching backend categories)
  categories = [
    { id: 'Electronics', name: 'Electronics' },
    { id: 'Clothing', name: 'Clothing' },
    { id: 'Home', name: 'Home' },
    { id: 'Books', name: 'Books' },
    { id: 'Sports', name: 'Sports' }
  ];
  
  conditions = [
    { id: 'New', name: 'New' },
    { id: 'Used - Excellent', name: 'Like New' },
    { id: 'Used - Good', name: 'Good' },
    { id: 'Used - Fair', name: 'Fair' }
  ];

  constructor(
    private productService: ProductService,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}
  
  ngOnInit() {
    // Check if we're in edit mode
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.editProductId = +params['id'];
        this.loadProductForEdit();
      }
    });
  }
  
  loadProductForEdit() {
    if (!this.editProductId) return;
    
    this.isLoading = true;
    this.productService.getProductById(this.editProductId).subscribe({
      next: (product: Product) => {
        this.currentProduct = product;
        this.populateForm(product);
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading product for edit:', error);
        this.errorMessage = 'Failed to load product for editing.';
        this.isLoading = false;
      }
    });
  }
  
  populateForm(product: Product) {
    this.productTitle = product.title;
    this.description = product.description;
    this.selectedCategory = product.category;
    this.selectedCondition = product.condition;
    this.location = product.location;
    this.price = product.price;
    
    // Set image preview if product has an image
    if (product.imageUrl) {
      this.imagePreviewUrl = product.imageUrl;
    }
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
    const file = event.target.files[0] as File;
    if (file) {
      this.selectedImage = file;
      this.imagePreviewUrl = URL.createObjectURL(file);
    }
  }
  
  removeImage() {
    if (this.imagePreviewUrl) {
      URL.revokeObjectURL(this.imagePreviewUrl);
    }
    this.selectedImage = null;
    this.imagePreviewUrl = '';
  }
  
  onSubmit() {
    if (!this.isFormValid()) {
      this.errorMessage = 'Please fill in all required fields.';
      return;
    }

    const currentUser = this.authService.getCurrentUser();
    if (!currentUser || !currentUser.id) {
      this.errorMessage = 'Please login to list a product.';
      this.router.navigate(['/login']);
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    // Handle image upload if a new image was selected
    if (this.selectedImage) {
      this.isUploading = true;
      
      this.productService.uploadImage(this.selectedImage).subscribe({
        next: (imageUrl) => {
          this.isUploading = false;
          if (this.isEditMode) {
            this.updateProduct(imageUrl, currentUser.id!);
          } else {
            this.createProduct(imageUrl, currentUser.id!);
          }
        },
        error: (error) => {
          console.error('Image upload failed:', error);
          this.errorMessage = 'Failed to upload image. Please try again.';
          this.isUploading = false;
          this.isSubmitting = false;
        }
      });
    } else {
      // Use existing image URL for edit mode, or empty string for create
      const imageUrl = this.isEditMode ? (this.currentProduct?.imageUrl || '') : '';
      
      if (this.isEditMode) {
        this.updateProduct(imageUrl, currentUser.id!);
      } else {
        this.createProduct(imageUrl, currentUser.id!);
      }
    }
  }

  private createProduct(imageUrl: string, sellerId: number) {
    const productRequest: ProductRequest = {
      title: this.productTitle,
      description: this.description,
      price: this.price!,
      category: this.selectedCategory,
      condition: this.selectedCondition,
      location: this.location,
      imageUrl: imageUrl,
      sellerId: sellerId
    };

    this.productService.createProduct(productRequest).subscribe({
      next: (product) => {
        console.log('Product created successfully:', product);
        this.router.navigate(['/my-listings']);
      },
      error: (error) => {
        console.error('Product creation failed:', error);
        this.errorMessage = error.error?.message || 'Failed to create product. Please try again.';
        this.isSubmitting = false;
      }
    });
  }
  
  private updateProduct(imageUrl: string, sellerId: number) {
    if (!this.editProductId) {
      this.errorMessage = 'Invalid product ID for editing.';
      this.isSubmitting = false;
      return;
    }

    const productRequest: ProductRequest = {
      title: this.productTitle,
      description: this.description,
      price: this.price!,
      category: this.selectedCategory,
      condition: this.selectedCondition,
      location: this.location,
      imageUrl: imageUrl,
      sellerId: sellerId
    };

    this.productService.updateProduct(this.editProductId, productRequest).subscribe({
      next: (product) => {
        console.log('Product updated successfully:', product);
        this.router.navigate(['/my-listings']);
      },
      error: (error) => {
        console.error('Product update failed:', error);
        this.errorMessage = error.error?.message || 'Failed to update product. Please try again.';
        this.isSubmitting = false;
      }
    });
  }
  
  isFormValid(): boolean {
    return !!(this.productTitle && 
             this.description && 
             this.selectedCategory && 
             this.selectedCondition &&
             this.location &&
             this.price && 
             this.price > 0);
  }
}
