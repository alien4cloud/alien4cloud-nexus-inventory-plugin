import {Component, Inject, OnInit} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material';
import JSONFormatter from "json-formatter-js";
import {ImportClaim} from "@app/core/models/import-claim.model";

export interface DialogData {
  importClaim: ImportClaim;
}

@Component({
  selector: 'w4c-details-dialog',
  templateUrl: './details-dialog.component.html',
  styleUrls: ['./details-dialog.component.css']
})
export class DetailsDialogComponent implements OnInit {

  private formatter: JSONFormatter;

  constructor(
    public dialogRef: MatDialogRef<DetailsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) { }

  ngOnInit() {
    const config = {
      hoverPreviewEnabled: true,
      hoverPreviewArrayCount: 1,
      hoverPreviewFieldCount: 1,
      theme: "",
      animateOpen: true,
      animateClose: true,
      useToJSON: true
    };
    this.formatter = new JSONFormatter(JSON.parse(this.data.importClaim.body), 1, config);
    document.getElementById("json").appendChild(this.formatter.render());
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  expandAll() {
    this.formatter.openAtDepth(1000);
  }

  collapseAll() {
    this.formatter.openAtDepth(0);
  }

  copyToClipboard() {
    let selBox = document.createElement('textarea');
    selBox.style.position = 'fixed';
    selBox.style.left = '0';
    selBox.style.top = '0';
    selBox.style.opacity = '0';
    selBox.value = this.data.importClaim.body;
    document.body.appendChild(selBox);
    selBox.focus();
    selBox.select();
    document.execCommand('copy');
    document.body.removeChild(selBox);
  }

}
