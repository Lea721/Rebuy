import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormGroup, FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.scss'
})
export class SignupComponent {

  signupForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.signupForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required]
    });
  }

  submitSignup() {
    if (this.signupForm.invalid) {
      alert("Fill all fields correctly");
      return;
    }

    const v = this.signupForm.value;

    if (v.password !== v.confirmPassword) {
      alert("Passwords do not match!");
      return;
    }

    this.auth.signup({
      name: v.name,
      email: v.email,
      password: v.password
    }).subscribe({
      next: () => {
        alert("Account created!");
        this.router.navigate(['/login']);
      },
      error: () => alert("Signup failed")
    });
  }
}
