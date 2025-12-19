import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../services/product.service';
import { AuthService } from '../services/auth.service';
import { ProductRequest, Product } from '../models';

@Component({
  selector: 'app-sell',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sell.component.html',
  styleUrl: './sell.component.scss'
})
export class SellComponent implements OnInit {

  // Edit mode
  isEditMode = false;
  editProductId: number | null = null;
  currentProduct: Product | null = null;

  // Form fields
  productTitle = '';
  description = '';
  selectedCategory = '';
  selectedCondition = '';
  location = '';
  price: number | null = null;

  selectedImage: File | null = null;
  imagePreviewUrl: string = '';

  // UI flags
  isCategoryDropdownOpen = false;
  isConditionDropdownOpen = false;
  isSubmitting = false;
  isLoading = false;
  isUploading = false;
  errorMessage = '';

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
      next: (product) => {
        this.currentProduct = product;
        this.populateForm(product);
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load product.';
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

  getCategoryName() {
    return this.categories.find(c => c.id === this.selectedCategory)?.name || 'Select category';
  }

  getConditionName() {
    return this.conditions.find(c => c.id === this.selectedCondition)?.name || 'Select condition';
  }

  onFileSelect(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedImage = file;
      this.imagePreviewUrl = URL.createObjectURL(file);
    }
  }

  removeImage() {
    if (this.imagePreviewUrl) URL.revokeObjectURL(this.imagePreviewUrl);
    this.selectedImage = null;

    this.imagePreviewUrl = this.currentProduct?.imageUrl || '';
  }

  async onSubmit() {
    if (!this.isFormValid()) {
      this.errorMessage = 'Please fill in all required fields.';
      return;
    }

    const user = this.authService.getCurrentUser();
    if (!user?.id) {
      this.router.navigate(['/login']);
      return;
    }

    this.isSubmitting = true;

    const productRequest: ProductRequest = {
      title: this.productTitle,
      description: this.description,
      price: this.price!,
      category: this.selectedCategory,
      condition: this.selectedCondition,
      location: this.location,
      imageUrl: this.currentProduct?.imageUrl || '',
      sellerId: user.id
    };

    if (this.isEditMode) {
      this.updateProduct(productRequest);
    } else {
      this.createProduct(productRequest);
    }
  }

  private createProduct(productRequest: ProductRequest) {
    if (this.selectedImage) {
      this.isUploading = true;

      this.productService
        .createProductWithImage(productRequest, this.selectedImage)
        .subscribe({
          next: () => this.router.navigate(['/my-listings']),
          error: () => {
            this.errorMessage = 'Failed to create product.';
            this.isUploading = false;
            this.isSubmitting = false;
          }
        });
    } else {
      this.productService.createProduct(productRequest).subscribe({
        next: () => this.router.navigate(['/my-listings']),
        error: () => {
          this.errorMessage = 'Failed to create product.';
          this.isSubmitting = false;
        }
      });
    }
  }

  private updateProduct(productRequest: ProductRequest) {
    if (!this.editProductId) return;

    if (this.selectedImage) {
      this.errorMessage = 'Updating image is not supported.';
      this.isSubmitting = false;
      return;
    }

    this.productService.updateProduct(this.editProductId, productRequest).subscribe({
      next: () => this.router.navigate(['/my-listings']),
      error: () => {
        this.errorMessage = 'Failed to update product.';
        this.isSubmitting = false;
      }
    });
  }

  isFormValid() {
    return (
      this.productTitle &&
      this.description &&
      this.selectedCategory &&
      this.selectedCondition &&
      this.location &&
      this.price &&
      this.price > 0
    );
  }
}
