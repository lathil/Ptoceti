import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';

import {LayoutComponent} from './layout/layout.component';
import {AuthGuard} from './shared/guards/auth.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadChildren: () => import('src/app/login/login.module').then(m => m.LoginModule)
  },
  {
    path: '',
    component: LayoutComponent,
    children: [
      {
        path: 'dashboard',
        loadChildren: () => import('./dashboard/dashboard.module').then(m => m.DashboardModule),
        data: {
          breadcrumbItem: {key: 'dashboard', labelName: 'Dashboard'}
        },
        canActivate: [AuthGuard]
      },
      {
        path: 'gateway',
        loadChildren: () => import('./gateway/gateway.module').then(m => m.GatewayModule),
        data: {
          breadcrumbItem: {key: 'gateway', labelName: 'Gateway'},
          roles: ['things', 'devices']
        },
        canActivate: [AuthGuard]
      },
      {
        path: 'cloud',
        loadChildren: () => import('./cloud/cloud.module').then(m => m.CloudModule),
        data: {
          breadcrumbItem: {key: 'cloud', labelName: 'Cloud'},
          roles: ['cloud']
        },
        canActivate: [AuthGuard]
      },
      {
        path: '',
        pathMatch: 'full',
        redirectTo: 'dashboard'
      }
    ],
    data: {
      breadcrumbItem: {key: 'Home', labelName: 'Home'}
    }
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes,
    {
      enableTracing: false, // <-- debugging purposes only
    })],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
