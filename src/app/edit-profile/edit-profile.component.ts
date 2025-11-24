import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { User, UserUpdateRequest } from '../models';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-profile.component.html',
  styleUrls: ['./edit-profile.component.scss']
})
export class EditProfileComponent implements OnInit {
  currentUser: User | null = null;
  isLoading: boolean = true;
  isSaving: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  // Form data
  name: string = '';
  phone: string = '';
  city: string = '';
  shippingAddress: string = '';
  profileImageUrl: string = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadUserProfile();
  }

  loadUserProfile() {
    this.isLoading = true;
    this.currentUser = this.authService.getCurrentUser();
    
    if (!this.currentUser || !this.currentUser.id) {
      this.router.navigate(['/login']);
      return;
    }

    // Load fresh user data from backend
    this.authService.getUserById(this.currentUser.id).subscribe({
      next: (userResponse: User) => {
        this.currentUser = userResponse;
        this.populateForm(userResponse);
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading user profile:', error);
        this.errorMessage = 'Failed to load profile data.';
        this.isLoading = false;
      }
    });
  }

  populateForm(user: User) {
    this.name = user.name || '';
    this.phone = user.phone || '';
    this.city = user.city || '';
    this.shippingAddress = user.shippingAddress || '';
    this.profileImageUrl = user.profileImageUrl || '';
  }

  onSubmit() {
    if (!this.currentUser?.id) {
      this.errorMessage = 'User not found. Please log in again.';
      return;
    }

    if (!this.name.trim()) {
      this.errorMessage = 'Name is required.';
      return;
    }

    this.isSaving = true;
    this.errorMessage = '';
    this.successMessage = '';

    const updateData: UserUpdateRequest = {
      name: this.name.trim(),
      phone: this.phone.trim() || undefined,
      city: this.city.trim() || undefined,
      shippingAddress: this.shippingAddress.trim() || undefined,
      profileImageUrl: this.profileImageUrl.trim() || undefined
    };

    this.authService.updateUser(this.currentUser.id, updateData).subscribe({
      next: (updatedUser: User) => {
        this.successMessage = 'Profile updated successfully!';
        this.currentUser = updatedUser;
        this.isSaving = false;
        
        // Navigate back to profile after a short delay
        setTimeout(() => {
          this.router.navigate(['/profile']);
        }, 1500);
      },
      error: (error: any) => {
        console.error('Error updating profile:', error);
        this.errorMessage = error.error?.message || 'Failed to update profile. Please try again.';
        this.isSaving = false;
      }
    });
  }

  cancel() {
    this.router.navigate(['/profile']);
  }
}