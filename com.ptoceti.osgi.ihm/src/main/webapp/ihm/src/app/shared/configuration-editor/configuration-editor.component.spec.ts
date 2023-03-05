import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ConfigurationEditorComponent} from './configuration-editor.component';

describe('ConfigurationEditorComponent', () => {
  let component: ConfigurationEditorComponent;
  let fixture: ComponentFixture<ConfigurationEditorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ConfigurationEditorComponent]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfigurationEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
