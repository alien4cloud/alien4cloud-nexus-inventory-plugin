import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {InventoryMainComponent} from "@app/features/inventory-export/inventory-main/inventory-main.component";

const routes: Routes = [{
  path: '',
  component: InventoryMainComponent
}
];

@NgModule({
  declarations: [],
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class InventoryExportRoutingModule { }

