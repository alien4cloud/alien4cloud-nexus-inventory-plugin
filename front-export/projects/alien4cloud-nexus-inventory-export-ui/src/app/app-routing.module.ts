import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {W4cCommonsModule} from "@alien4cloud/wizard4cloud-commons";

const routes: Routes = [
  { path: '', redirectTo: 'catalog', pathMatch: 'full' },
  // Lazy load LoginModule is necessary to avoid init exception.
  {
    path: 'login',
    loadChildren: () => import('./login-wrapper.module').then(mod => mod.LoginWrapperModule)
  },
  {
    path: 'catalog',
    loadChildren: () => import('./features/inventory-export/inventory-export.module').then(mod => mod.InventoryExportModule)
  }
];
@NgModule({
  imports: [
    RouterModule.forRoot(routes/*, { enableTracing: true } */),
    W4cCommonsModule
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
export const routingComponents = [ ];
