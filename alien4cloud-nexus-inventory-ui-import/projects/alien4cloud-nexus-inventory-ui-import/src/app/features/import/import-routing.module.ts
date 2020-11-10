import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ImportMainComponent} from "@app/features/import/import-main/import-main.component";

const routes: Routes = [{
  path: '',
  component: ImportMainComponent
}
];

@NgModule({
  declarations: [],
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ImportRoutingModule { }

