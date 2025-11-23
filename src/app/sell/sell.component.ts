import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-sell',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sell.component.html',
  styleUrls: ['./sell.component.scss']
})
export class SellComponent {

  productTitle: string = '';
  description: string = '';
  selectedCategory: string = '';
  selectedCondition: string = '';
  price: number | null = null;

  images: string[] = []; // 🔥 Liste des previews
  imageFiles: File[] = []; // 🔥 Liste des vraies images

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

  constructor() {}

  // ----------- IMAGE UPLOAD MULTIPLE -----------
  onImageSelected(event: any) {
    const files = event.target.files;

    for (let file of files) {
      if (this.images.length >= 5) break; // Maximum 5 images

      this.imageFiles.push(file);

      const reader = new FileReader();
      reader.onload = () => {
        this.images.push(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  }

  removeImage(index: number) {
    this.images.splice(index, 1);
    this.imageFiles.splice(index, 1);
  }

  // ----------- FORM SUBMIT -----------
  onSubmit() {
    if (!this.productTitle || !this.description || !this.selectedCategory || !this.selectedCondition || !this.price) {
      alert("Please fill all fields!");
      return;
    }

    // 👉 Ici normalement tu enverrais au backend
    console.log("Uploaded files: ", this.imageFiles);
    console.log("Form data:", {
      title: this.productTitle,
      desc: this.description,
      category: this.selectedCategory,
      condition: this.selectedCondition,
      price: this.price
    });

    alert("Product uploaded successfully!");
  }
}
