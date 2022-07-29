import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DettaglioAlertComponent } from './dettaglio-alert.component';

describe('DettaglioAlertComponent', () => {
  let component: DettaglioAlertComponent;
  let fixture: ComponentFixture<DettaglioAlertComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DettaglioAlertComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DettaglioAlertComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
