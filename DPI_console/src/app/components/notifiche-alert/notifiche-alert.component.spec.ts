import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotificheAlertComponent } from './notifiche-alert.component';

describe('NotificheAlertComponent', () => {
  let component: NotificheAlertComponent;
  let fixture: ComponentFixture<NotificheAlertComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NotificheAlertComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotificheAlertComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
