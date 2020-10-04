import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router, RouterState, ActivatedRoute, ParamMap} from '@angular/router';

import {NgbCollapse} from '@ng-bootstrap/ng-bootstrap';

@Component({
  // selector: 'app-layout',
  templateUrl: './layout.component.html'
})
export class LayoutComponent implements OnInit, OnDestroy {

  router: Router;

  productUrl: string;
  productName: string;

  isNavbarCollapsed = true;

  constructor(router: Router) {
    this.router = router;
  }

  ngOnInit(): void {
  }


  ngOnDestroy() {
    console.log('Full layout initialisation.');
  }

}
