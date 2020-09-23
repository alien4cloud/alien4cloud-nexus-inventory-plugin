import {Component, ElementRef, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {ExportService} from "@app/core/services/export.service";
import {ExportResult} from "@app/core/models/export.model";
import * as _ from 'lodash';
import {TranslateService} from "@ngx-translate/core";
import {MatDialog} from "@angular/material/dialog";
import {ConfirmationDialogComponent} from "@alien4cloud/wizard4cloud-commons";

@Component({
  selector: 'w4c-package-list',
  templateUrl: './package-list.component.html',
  styleUrls: ['./package-list.component.css']
})
export class PackageListComponent implements OnInit {

  constructor(
    private exportService: ExportService,
    private dialog: MatDialog,
    private translate: TranslateService
  ) { }

  availableExports: ExportResult[];

  displayedColumns = ['name', 'date', 'size', 'actions'];

  @Output() availableExportCount = new EventEmitter<number>();

  private refreshRate: number = 60000;

  refreshError: string;

  ngOnInit() {
    this.refresh();
    this.scheduleRefresh();
  }

  delete(element: ExportResult) {
    const title = this.translate.instant("nexusInventoryExport.export.list.actions.deleteConfirmationTitle");
    const msg = this.translate.instant("nexusInventoryExport.export.list.actions.deleteConfirmationMsg");
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      width: '35%',
      data: {
        actionDescription: title,
        message: msg
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if(result) {
        this.exportService.delete(element).subscribe(value2 => {
          this.refresh();
        });
      }
    });
  }

  download(element: ExportResult) {
    this.exportService.download(element);
  }

  scheduleRefresh() {
    setTimeout(value => {
      this.refresh();
      this.scheduleRefresh();
    }, this.refreshRate);
  }

  refresh() {
    this.exportService.listExports().subscribe(value => {
      this.refreshError = undefined
      // in progress first, then sort by name
      this.availableExports = value;
      this.availableExports = _.sortBy(this.availableExports, [function(o) { return !o.in_progress; }, function(o) { return o.name; }])
      const availableCount = this.availableExports.filter(value1 => !value1.in_progress).length;
      // if we have in progress items, we refresh with higher frequency
      this.refreshRate = (this.availableExports.length > availableCount)  ? 10000 : 60000;
      this.availableExportCount.emit(availableCount);
    }, error => {
      this.refreshError = error.message;
    });
  }

}
