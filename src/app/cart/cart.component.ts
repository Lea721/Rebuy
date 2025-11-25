import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CartService } from '../services/cart.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss']
})
export class CartComponent implements OnInit {
  items: any[] = [];
  isLoading = false;
  error = '';

  constructor(
    private cartService: CartService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.authService.getCurrentUser();
    if (!user || !user.id) {
      // Visitors must sign up to view cart / checkout
      this.router.navigate(['/signup']);
      return;
    }

    this.loadCart(user.id);
  }

  loadCart(userId: number) {
    this.isLoading = true;
    this.cartService.getCart(userId).subscribe({
      next: items => { this.items = items || []; this.isLoading = false; },
      error: err => { this.error = 'Failed to load cart.'; this.isLoading = false; }
    });
  }

  remove(itemId: number) {
    const user = this.authService.getCurrentUser();
    if (!user || !user.id) return;
    this.cartService.removeCartItem(itemId).subscribe({
      next: () => this.loadCart(user.id!),
      error: () => this.error = 'Failed to remove item.'
    });
  }

  proceedToCheckout() {
    const user = this.authService.getCurrentUser();
    if (!user || !user.id) return;
    // In a real flow we'd create an order. Here we simulate and stop.
    console.log('Proceed to checkout for user', user.id);
    this.cartService.clearCart(user.id).subscribe({
      next: () => {
        this.items = [];
        alert('Order would be created now (simulated). Cart cleared.');
      },
      error: () => alert('Failed to clear cart after checkout simulation.')
    });
  }
}
