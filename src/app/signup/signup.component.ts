import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';
import { RegisterRequest } from '../models';

@Component({
  selector: 'app-signup',
  imports: [RouterLink, FormsModule, CommonModule],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.scss'
})
export class SignupComponent {
  registerData: RegisterRequest = {
    name: '',
    email: '',
    password: '',
    phone: '',
    city: '',
    shippingAddress: ''
  };

  confirmPassword = '';
  isLoading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    if (!this.isFormValid()) {
      this.errorMessage = 'Please fill in all required fields.';
      return;
    }

    if (this.registerData.password !== this.confirmPassword) {
      this.errorMessage = 'Passwords do not match.';
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.authService.register(this.registerData).subscribe({
      next: (userResponse) => {
        console.log('Registration successful:', userResponse);
        this.router.navigate(['/products']); // Redirect to products page
      },
      error: (error) => {
        console.error('Registration failed:', error);
        this.errorMessage = error.error?.message || 'Registration failed. Please try again.';
        this.isLoading = false;
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }

  private isFormValid(): boolean {
    return !!(this.registerData.name && this.registerData.email && this.registerData.password);
  }
}
