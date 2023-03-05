import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ThingItemsComponent} from './thing-items.component';

describe('ListThingComponent', () => {
  let component: ThingItemsComponent;
  let fixture: ComponentFixture<ThingItemsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ThingItemsComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ThingItemsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
