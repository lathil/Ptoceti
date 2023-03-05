import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ThingsComponent} from './things.component';

describe('DevicesComponent', () => {
  let component: ThingsComponent;
  let fixture: ComponentFixture<ThingsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ThingsComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ThingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
