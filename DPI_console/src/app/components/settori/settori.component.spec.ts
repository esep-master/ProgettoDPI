import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettoriComponent } from './settori.component';

describe('SettoriComponent', () => {
  let component: SettoriComponent;
  let fixture: ComponentFixture<SettoriComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SettoriComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SettoriComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
