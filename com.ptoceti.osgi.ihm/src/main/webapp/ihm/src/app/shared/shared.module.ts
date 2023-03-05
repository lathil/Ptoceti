import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {BreadcrumbComponent} from './breadcrumb/breadcrumb.component';
import {ConfigurationEditorComponent} from './configuration-editor/configuration-editor.component';
import {ReactiveFormsModule} from '@angular/forms';

import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {ToggleDirective, ToggleOffDirective, ToggleOnDirective} from './toggle.directive';
import {ItemComponent} from './item/item.component';

@NgModule({
  declarations: [BreadcrumbComponent, ConfigurationEditorComponent, ToggleDirective, ToggleOnDirective, ToggleOffDirective, ItemComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    NgbModule,
  ],
  exports: [BreadcrumbComponent, ConfigurationEditorComponent, ToggleDirective, ToggleOnDirective, ToggleOffDirective, ItemComponent]
})
export class SharedModule {
}
