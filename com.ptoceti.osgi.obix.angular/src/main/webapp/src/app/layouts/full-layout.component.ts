import { Component, OnInit } from '@angular/core';
import { Observable  } from 'rxjs/Observable';
import { Subscription  } from 'rxjs/Subscription';

import { AboutService } from '../obix/obix.aboutservice';
import { About } from '../obix/obix';

@Component({
  selector: 'app-dashboard',
  templateUrl: './full-layout.component.html'
})
export class FullLayoutComponent implements OnInit {

  public disabled = false;
  public status: {isopen: boolean} = {isopen: false};
  
  aboutService : AboutService;
  //about: Observable<About>;
  
  productUrl : string;
  productName : string;
  
  subscription : Subscription ;

  public toggled(open: boolean): void {
    console.log('Dropdown is now: ', open);
  }

  public toggleDropdown($event: MouseEvent): void {
    $event.preventDefault();
    $event.stopPropagation();
    this.status.isopen = !this.status.isopen;
  }

  constructor(aboutService : AboutService){
      this.aboutService = aboutService;
  }
  
  ngOnInit(): void { 
      this.subscription = this.aboutService.getAbout().subscribe((about) => {
          this.productUrl = about.getProductUrl().val;
          this.productName = about.getProductName().val;
      });
  }
  
  ngOnDestroy() { 
      if (this.subscription) {
        this.subscription.unsubscribe();
      }
    }
}
