
      
import { NgModule }      from '@angular/core';



//Services


import { CookieService } from './cookie.service';




@NgModule({
    imports: [ ],
    providers: [{ provide: CookieService, useClass: CookieService }]
})
export class CookieModule {
    
}