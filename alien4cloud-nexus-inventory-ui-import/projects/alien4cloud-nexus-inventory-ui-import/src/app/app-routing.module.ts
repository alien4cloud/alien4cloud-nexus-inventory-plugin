import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

const routes: Routes = [
  { path: '', redirectTo: 'import', pathMatch: 'full' },
  // Lazy load LoginModule is necessary to avoid init exception.
  {
    path: 'login',
    loadChildren: () => import('./login-wrapper.module').then(mod => mod.LoginWrapperModule)
  },
  {
    path: 'import',
    loadChildren: () => import('./features/import/import.module').then(mod => mod.ImportModule)
  }
];
@NgModule({
  imports: [
    RouterModule.forRoot(routes/*, { enableTracing: true } */)
  ],
  exports: [RouterModule]
})
export class AppRoutingModule { }
export const routingComponents = [ ];
