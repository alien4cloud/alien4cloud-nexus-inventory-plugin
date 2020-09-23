import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { InventoryListComponent } from './inventory-list/inventory-list.component';
import { InventoryExportRoutingModule } from './inventory-export-routing.module';
import {W4cMaterialModule, W4cCommonsModule} from "@alien4cloud/wizard4cloud-commons";
import { InventoryItemPanelComponent } from './inventory-item-panel/inventory-item-panel.component';
import { InventoryMainComponent } from './inventory-main/inventory-main.component';
import { PackageListComponent } from './package-list/package-list.component';

@NgModule({
  declarations: [
    InventoryListComponent,
    InventoryItemPanelComponent,
    InventoryMainComponent,
    PackageListComponent
  ],
  imports: [
    CommonModule,
    InventoryExportRoutingModule,
    W4cCommonsModule,
    W4cMaterialModule
  ]
})
export class InventoryExportModule { }
