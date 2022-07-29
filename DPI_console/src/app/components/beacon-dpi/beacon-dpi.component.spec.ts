import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BeaconDPIComponent } from './beacon-dpi.component';

describe('BeaconDPIComponent', () => {
  let component: BeaconDPIComponent;
  let fixture: ComponentFixture<BeaconDPIComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BeaconDPIComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BeaconDPIComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
