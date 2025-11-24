import { Component, OnInit, HostListener } from '@angular/core';
import { RouterOutlet, RouterLink, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';
import { User } from './models';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {
  title = 'rebuy-simple';
  currentUser: User | null = null;
  isUserDropdownOpen = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    // Subscribe to current user changes
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event) {
    const target = event.target as HTMLElement;
    const userDropdown = document.querySelector('.user-dropdown');
    const userButton = document.querySelector('.user-button');
    
    if (userDropdown && userButton && 
        !userDropdown.contains(target) && 
        !userButton.contains(target)) {
      this.isUserDropdownOpen = false;
    }
  }

  toggleUserDropdown() {
    this.isUserDropdownOpen = !this.isUserDropdownOpen;
  }

  closeUserDropdown() {
    this.isUserDropdownOpen = false;
  }

  onLogout() {
    this.authService.logout();
    this.closeUserDropdown();
    this.router.navigate(['/']);
  }

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  getUserInitials(): string {
    if (!this.currentUser?.name) return 'U';
    return this.currentUser.name
      .split(' ')
      .map(n => n[0])
      .join('')
      .toUpperCase();
  }
}
