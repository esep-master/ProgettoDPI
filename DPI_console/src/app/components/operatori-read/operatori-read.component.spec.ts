import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OperatoriReadComponent } from './operatori-read.component';

describe('OperatoriReadComponent', () => {
  let component: OperatoriReadComponent;
  let fixture: ComponentFixture<OperatoriReadComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OperatoriReadComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OperatoriReadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
