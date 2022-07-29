import { ComponentFixture, TestBed } from '@angular/core/testing';

import { KitDpiComponent } from './kit-dpi.component';

describe('KitDpiComponent', () => {
  let component: KitDpiComponent;
  let fixture: ComponentFixture<KitDpiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ KitDpiComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(KitDpiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
