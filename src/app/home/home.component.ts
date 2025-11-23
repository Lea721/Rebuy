import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

  user: any = null;

  ngOnInit() {
    const saved = localStorage.getItem('user');
    if (saved) {
      this.user = JSON.parse(saved);
    }
  }
}
