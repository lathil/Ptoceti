import {Component, OnInit, OnDestroy, Pipe, PipeTransform} from '@angular/core';
import {ActivatedRoute, Router, NavigationExtras, NavigationStart, NavigationEnd} from '@angular/router';
import {startWith, filter, pairwise, map, subscribeOn} from 'rxjs/operators';
import * as glob from '../../services/globals';

@Component({
  // moduleId: module.id.toString(),
  selector: 'app-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.scss']
})
export class BreadcrumbComponent implements OnInit, OnDestroy {
  routeDataName = 'breadcrumbItem';
  $router: any;
  breadcrumbList: BreadcrumbItem[] = [];
  bcInitItem: BreadcrumbItem = {
    key: 'Home',
    labelName: 'Home',
    path: '/home'
  };
  navState: any = {};
  // isBrowserRefresh: boolean = false;
  breadcrumbHistoryList: Array<any> = [];

  constructor(private router: Router, private activatedRoute: ActivatedRoute) {

    const pThis: any = this;
    // For handle browser back/forward/history scenarios.
    this.router.events.pipe(filter(value => value instanceof NavigationStart)).subscribe((value: any) => {
      pThis.navState = {
        id: value.id,
        navigationTrigger: value.navigationTrigger,
        restoredState: value.restoredState
      };
      // pThis.isBrowserRefresh = pThis.router.navigated;
    });

    this.router.events.pipe(filter(value => value instanceof NavigationEnd)).subscribe((value: any) => {
      // Restore history list after browser refresh.
      if (pThis.breadcrumbHistoryList.length < 1 && window.sessionStorage.getItem('breadcrumbHistoryList') != null) {
        pThis.breadcrumbHistoryList = JSON.parse(window.sessionStorage.getItem('breadcrumbHistoryList'));
      }

      // Browser back/forward/history.
      if (pThis.navState.navigationTrigger === 'popstate' && pThis.navState.restoredState != null) {
        for (let idx: number = pThis.breadcrumbHistoryList.length - 1; idx >= 0; idx--) {
          if (pThis.breadcrumbHistoryList[idx].id === pThis.navState.restoredState.navigationId &&
            pThis.breadcrumbHistoryList[idx].url === value.urlAfterRedirects) {
            pThis.breadcrumbList = pThis.breadcrumbHistoryList[idx].breadcrumbList;
            break;
          }
        }
      }
      // Imperetive action.
      else {
        const rootRoute: ActivatedRoute = pThis.activatedRoute.root;
        // Test for confirmation.
        // let fullUrl: string = pThis.router.routerState.snapshot.url;

        // Any primary route side-menu action will add base breadcrumb items.
        if (glob.caches.isMenuAction) {
          glob.caches.isMenuAction = false;
          pThis.loadBaseBreadcrumbList();
        }

        // Check and get cached breadcrumbList.
        if (pThis.breadcrumbList.length < 1 &&
          // Get cached breadcrumbList when browser refresh.
          window.sessionStorage.getItem('breadcrumbList') != null) {
          pThis.breadcrumbList = JSON.parse(window.sessionStorage.getItem('breadcrumbList'));
        }

        // Refresh breadcrumb items.
        pThis.refreshBreadcrumbs(rootRoute);
      }

      // Save breadcrumbList to session object after every breadcrumb update for browser refreshing action.
      window.sessionStorage.setItem('breadcrumbList', JSON.stringify(pThis.breadcrumbList));

      // Save history item for browser back/forward.
      const bcHistoryItem = {
        id: pThis.navState.id,
        url: value.urlAfterRedirects,
        breadcrumbList: glob.deepClone(pThis.breadcrumbList)
      };
      pThis.breadcrumbHistoryList.push(bcHistoryItem);
      // Save to sessionStorage for browser refresh.
      window.sessionStorage.setItem('breadcrumbHistoryList', JSON.stringify(pThis.breadcrumbHistoryList));
    });
  }

  ngOnInit(): void {
    const pThis: any = this;

    console.log('BreadcrumbComponent initialisation.');
  }

  ngOnDestroy(): void {
  }

  loadBaseBreadcrumbList(): void {
    this.breadcrumbList.length = 0;
    this.breadcrumbList.push(this.bcInitItem);
  }

  refreshBreadcrumbs(route: ActivatedRoute, pathParams: PathParams = {path: '', pathParamList: []}): void {
    // Recursively find the child route with breadcrumbItem object.
    let child: ActivatedRoute;
    if (route.firstChild) {
      child = route.firstChild;
    } else {
      return;
    }

    // If the route doesn't have specified breadcrumbItem, add possible parent url, and recursive to its child.
    if (!child.snapshot.data.hasOwnProperty(this.routeDataName)) {
      // Take URL parts from parents.
      pathParams = this.getPathAndParams(child, pathParams);

      // Recursive call.
      this.refreshBreadcrumbs(child, pathParams);
      return;
    }

    if (this.breadcrumbList.length > 0) {
      if (child.snapshot.data[this.routeDataName].afterBaseOnly) {
        // Reset to base breadcrumbs.
        this.loadBaseBreadcrumbList();
      } else {
        // Find breadcrumb key and index.
        const bcKey: string = child.snapshot.data[this.routeDataName].key;
        const bcIndex: number = this.getBreadcrumbPositionByKey(bcKey);

        if (bcIndex >= 0) {
          // Remove the breadcrumb trailing items.
          // Also remove the same item if already exists to handling hierarchical issue for URL segment parameter.
          // this.breadcrumbList.splice(bcIndex);
          pathParams = this.getPathAndParams(child, pathParams);
          this.refreshBreadcrumbs(child, pathParams);
          return;
        }

        // Remove existing breadcrumb of route for terminalOnly which should not be displayed, and
        // no navigation action on breadcrumb for the terminalOnly route.
        if (this.breadcrumbList.length > 0 && this.breadcrumbList[this.breadcrumbList.length - 1].terminalOnly) {
          this.breadcrumbList.length = this.breadcrumbList.length - 1;
        }
      }
    }

    // Bypass adding breadcrumb for the current route if it contains a child with crossBranch 'y' and segmentParam 'y'.
    // Scenario: cross-branch navigation to a segment param route.
    // In this case, the parent route should be excluded from the breadcrumb list.
    // Comment out below block to reproduce the issue.
    const cbpRoute: ActivatedRoute = this.findCrossBranchSegmentParamRoute(child);
    if (cbpRoute) {
      // Recursive call.
      this.refreshBreadcrumbs(child, pathParams);
      return;
    }

    // Add URL parts for this route with breadcrumb item.
    pathParams = this.getPathAndParams(child, pathParams);

    // Test: compare full url taken from routerState:
    // let te: string = this.router.routerState.snapshot.url;

    // Set breadcrumb item object.
    const breadcrumbItem: BreadcrumbItem = {
      key: child.snapshot.data[this.routeDataName].key,
      labelName: child.snapshot.data[this.routeDataName].labelName,
      path: pathParams.path,
      terminalOnly: child.snapshot.data[this.routeDataName].terminalOnly || false,
      afterBaseOnly: child.snapshot.data[this.routeDataName].afterBaseOnly || false,
      pathParamList: pathParams.pathParamList,
      queryParams: pathParams.queryParams,
      fragment: pathParams.fragment
    };
    // Add item to breadcrumb list.
    this.breadcrumbList.push(breadcrumbItem);

    // Recursive call.
    this.refreshBreadcrumbs(child, pathParams);
    return;
  }

  findCrossBranchSegmentParamRoute(route: ActivatedRoute): ActivatedRoute {
    // Recursively find the child route with matrix params crossBranch and segmentParam.
    let child: ActivatedRoute;
    if (route.firstChild) {
      child = route.firstChild;
    } else {
      return null;
    }

    const crossBranch = 'crossBranch';
    const segmentParam = 'segmentParam';
    if (child.snapshot.params[crossBranch] === 'y' && child.snapshot.params[segmentParam] === 'y') {
      return child;
    } else {
      this.findCrossBranchSegmentParamRoute(child);
    }
  }

  openPageWithBreadcrumb(index: number): void {
    // Check and get queryParams and fragment.
    let navigationExtras: NavigationExtras;
    if (this.breadcrumbList[index].queryParams) {
      navigationExtras = {
        queryParams: this.breadcrumbList[index].queryParams
      };
    }
    if (this.breadcrumbList[index].fragment) {
      if (!navigationExtras) {
        navigationExtras = {};
      }
      navigationExtras.fragment = this.breadcrumbList[index].fragment;
    }

    this.breadcrumbList.splice(index + 1);

    // check and get matrix params.
    if (this.breadcrumbList[index].pathParamList &&
      this.breadcrumbList[index].pathParamList.length > 0) {

      // Ignore the pathParamList and do general path if object contains 'ignoreParam'.
      if (this.breadcrumbList[index].pathParamList.find(x => x.ignoreParam)) {
        if (navigationExtras) {
          this.router.navigate([this.breadcrumbList[index].path], navigationExtras);
        } else {
          this.router.navigate([this.breadcrumbList[index].path]);
        }
      } else {
        if (navigationExtras) {
          this.router.navigate(this.breadcrumbList[index].pathParamList, navigationExtras);
        } else {
          this.router.navigate(this.breadcrumbList[index].pathParamList);
        }
      }
    }
    // Do general path.
    else {
      if (navigationExtras) {
        this.router.navigate([this.breadcrumbList[index].path], navigationExtras);
      } else {
        this.router.navigate([this.breadcrumbList[index].path]);
      }
    }
  }

  getBreadcrumbPositionByKey(key: string): number {
    let rtnIndex = -1;
    for (let idx: number = this.breadcrumbList.length - 1; idx >= 0; idx--) {
      if (this.breadcrumbList[idx].key === key) {
        rtnIndex = idx;
        break;
      }
    }
    return rtnIndex;
  }

  getPathAndParams(route: ActivatedRoute, pathParams: PathParams): PathParams {
    let thisPath = '';

    // Url param '/:id' is a segment.path.
    thisPath = route.snapshot.url.map(segment => segment.path).join('/');
    if (thisPath !== '') {
      // Process matrix params.
      // Format of pathParamList: ['path', {param data}, 'path', {param data}].
      const matParams: any = route.snapshot.url.map(segment => segment.parameters);
      if (matParams.length > 0 && Object.getOwnPropertyNames(matParams[0]).length > 0) {
        pathParams.pathParamList.push(thisPath);
        const params: any = {};
        for (const item of matParams) {
          for (const prop of Object.keys(item)) {
            params[prop] = item[prop];
          }
        }
        pathParams.pathParamList.push(params);
      }

      // Get query params if any - always for the last segment.
      if (route.snapshot.queryParamMap.keys.length > 0) {
        pathParams.queryParams = {};
        for (const key of route.snapshot.queryParamMap.keys) {
          pathParams.queryParams[key] = route.snapshot.queryParamMap.get(key);
        }
        //// Not good using below logic since this whould have proto and symbol properties.
        // route.queryParamMap.subscribe(params => {
        //    let paramMap: any = { ...params };
        //    pathParams.queryParams = paramMap.params;
        // });
      }
      // Get fragment if any - always for the last segment.
      if (route.snapshot.fragment) {
        route.fragment.subscribe(value => {
          pathParams.fragment = value;
        });
      }

      pathParams.path += `/${thisPath}`;
    }
    return pathParams;
  }
}

export class BreadcrumbItem {
  key: string = undefined;
  labelName: string = undefined;
  path = '';
  terminalOnly?: boolean = undefined;
  afterBaseOnly?: boolean = undefined;
  pathParamList?: Array<any> = [];
  queryParams?: any = undefined;
  fragment?: string = undefined;
}

export class PathParams {
  path = '';
  pathParamList?: Array<any> = [];
  queryParams?: any = undefined;
  fragment?: string = undefined;
}

