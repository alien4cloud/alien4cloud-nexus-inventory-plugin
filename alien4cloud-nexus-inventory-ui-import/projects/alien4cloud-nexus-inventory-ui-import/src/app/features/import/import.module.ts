import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {W4cMaterialModule, W4cCommonsModule} from "@alien4cloud/wizard4cloud-commons";
import {ImportMainComponent} from "@app/features/import/import-main/import-main.component";
import {ImportRoutingModule} from "@app/features/import/import-routing.module";
import {DetailsDialogComponent} from "@app/features/import/details-dialog/details-dialog.component";

@NgModule({
  declarations: [
    ImportMainComponent,
    DetailsDialogComponent
  ],
  imports: [
    CommonModule,
    ImportRoutingModule,
    W4cCommonsModule,
    W4cMaterialModule
  ],
  entryComponents: [
    DetailsDialogComponent
  ]
})
export class ImportModule { }
