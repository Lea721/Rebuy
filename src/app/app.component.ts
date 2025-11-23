import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink, RouterOutlet } from '@angular/router';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {

  user: any = null;

  constructor(private auth: AuthService, private router: Router) {}

  ngOnInit() {
    this.auth.currentUser.subscribe(u => {
      this.user = u;
    });
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/']);
  }
}
