import {Component, OnDestroy, OnInit} from '@angular/core';
import {Router, RouterState, ActivatedRoute, ParamMap, RouterStateSnapshot} from '@angular/router';

// import {NgbCollapse, NgbNav, NgbNavItem, NgbNavLink, NgbNavChangeEvent} from '@ng-bootstrap/ng-bootstrap';

import {NgbNavChangeEvent} from '@ng-bootstrap/ng-bootstrap';

import * as glob from '../services/globals';
import {AuthenticationService} from "../services/authentication.service";
import {Observable} from "rxjs";


@Component({
  // selector: 'app-layout',
  templateUrl: './layout.component.html'
})
export class LayoutComponent implements OnInit, OnDestroy {

  activeId: any;

  router: Router;
  route: ActivatedRoute;

  productUrl: string;
  productName: string;

  isNavbarCollapsed = true;

  constructor(router: Router, route: ActivatedRoute, private authenticationService: AuthenticationService) {
    this.router = router;
    this.route = route;
  }

  ngOnInit(): void {
    let url = this.route.snapshot.url.join().split(',');
    switch (url[0]) {
      case 'dashboard':
        this.activeId = 1;
        break;
      case 'gateway':
        this.activeId = 2;
        break;
      case 'cloud':
        this.activeId = 3;
        break;
    }
    console.log('LayoutComponent initialisation.');
  }


  ngOnDestroy(): void {
    console.log('Full layout initialisation.');
  }

  onNavChange(changeEvent: NgbNavChangeEvent): void {
    this.activeId = changeEvent.nextId;
  }

  linkClicked(): void {
    glob.caches.isMenuAction = true;
  }

  isAuthenticated(): Observable<boolean> {
    return this.authenticationService.isAuthenticated();
  }

  get userName(): string {
    return this.authenticationService.getUser();
  }

  doLogin(): void {
    this.router.navigate(['/login'], {queryParams: {returnUrl: this.router.routerState.snapshot.url}});
  }

  doLogout(): void {
    this.authenticationService.doLogout();
  }

  hasRole(role: string): boolean {
    return this.authenticationService.hasRole(role);
  }

  hasAnyRole(roles: string[]): boolean {
    return this.authenticationService.hasAnyRole(roles);
  }
}
