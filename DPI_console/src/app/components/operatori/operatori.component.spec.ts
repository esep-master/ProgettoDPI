import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OperatoriComponent } from './operatori.component';

describe('OperatoriComponent', () => {
  let component: OperatoriComponent;
  let fixture: ComponentFixture<OperatoriComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OperatoriComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OperatoriComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
