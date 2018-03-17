import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

// Layouts
import { FullLayoutComponent } from './layouts/full-layout.component';
import { SimpleLayoutComponent } from './layouts/simple-layout.component';

import { AppComponent } from './app.component';

export const routes: Routes = [
    {
        path: '',  component: AppComponent
    },
    {
        path: 'main',
        component: FullLayoutComponent,
        data: {
            title: 'Home'
        },
        children: [
            {
                path: 'dashboard',
                loadChildren: './dashboard/dashboard.module#DashboardModule'
            },
            {
                path: 'watches',
                loadChildren: './watches/watches.module#WatchesModule'
            },
            {
                path: 'histories',
                loadChildren: './histories/histories.module#HistoriesModule'
            },
            {
                path: 'alarms',
                loadChildren: './alarms/alarms.module#AlarmsModule'
            }
        ]
    },
    {
        path: 'pages',
        component: SimpleLayoutComponent,
        data: {
            title: 'Pages'
        },
        children: [
            {
                path: '',
                loadChildren: './pages/pages.module#PagesModule',
            }
        ]
    }
];

@NgModule( {
    imports: [RouterModule.forRoot( routes )],
    exports: [RouterModule]
} )
export class AppRoutingModule { }
