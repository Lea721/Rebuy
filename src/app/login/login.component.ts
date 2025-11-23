import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  loginForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  submitLogin() {
    if (this.loginForm.invalid) {
      alert("Please fill all fields correctly.");
      return;
    }

    this.auth.login(this.loginForm.value).subscribe({
      next: (user) => {
        // 🔥 Mise à jour dynamique du header
        this.auth.setUser(user);

        // Redirection
        this.router.navigate(['/']);
      },
      error: () => alert("Invalid email or password.")
    });
  }
}
