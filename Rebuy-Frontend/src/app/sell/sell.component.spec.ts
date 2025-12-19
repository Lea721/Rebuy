import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SellComponent } from './sell.component';

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';

describe('SellComponent', () => {
  let component: SellComponent;
  let fixture: ComponentFixture<SellComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        SellComponent,          // standalone
        HttpClientTestingModule, // ProductService → HttpClient
        RouterTestingModule
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SellComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
