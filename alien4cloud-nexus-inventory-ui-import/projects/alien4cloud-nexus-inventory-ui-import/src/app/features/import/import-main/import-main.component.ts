import {Component, OnInit, ViewChild} from '@angular/core';
import {ImportService} from "@app/core/services/import.service";
import * as _ from "lodash";
import {HttpErrorResponse, HttpEventType} from "@angular/common/http";
import {catchError, map} from "rxjs/operators";
import {of} from "rxjs";
import {ImportClaim, ImportStatus} from "@app/core/models/import-claim.model";
import {AuthService, ConfirmationDialogComponent} from "@alien4cloud/wizard4cloud-commons";
import {MatDialog} from "@angular/material/dialog";
import {DetailsDialogComponent} from "@app/features/import/details-dialog/details-dialog.component";
import {TranslateService} from "@ngx-translate/core";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'w4c-inmport-main',
  templateUrl: './import-main.component.html',
  styleUrls: ['./import-main.component.scss']
})
export class ImportMainComponent implements OnInit {

  @ViewChild('fileUpload', {static: false}) fileUpload

  // make lodash usable from template
  lodash = _;

  public file: File;

  typeList: any = {};
  public selectedType: string;

  displayedColumns = ['fileName', 'category', 'user', 'status', 'actions'];

  // All the local data (Uploading or UploadError)
  localDataSource : ImportClaim[] = [];
  // Data coming from backend
  remoteDataSource : ImportClaim[] = [];
  // Table datasource
  dataSource : ImportClaim[] = [];

  constructor(
    private importService: ImportService,
    private authService: AuthService,
    private dialog: MatDialog,
    private translate: TranslateService,
    private toastr: ToastrService,
  ) { }

  ngOnInit() {
    this.importService.listImportsCategories().subscribe(value => this.typeList = value);
    this.updateDatasource();
    this.fetchRemoteClaims();
  }

  private fetchRemoteClaims() {
    this.importService.listImports().subscribe(value => {
      this.remoteDataSource = value;
      this.updateDatasource();
      setTimeout(() => { this.fetchRemoteClaims(); }, 10000);
    }, error => {
      console.log(error);
      setTimeout(() => { this.fetchRemoteClaims(); }, 30000);
    })
  }

  private refreshRemoteClaims() {
    this.importService.listImports().subscribe(value => {
      this.remoteDataSource = value;
      this.updateDatasource();
    });
  }

  addFile() {
    this.fileUpload.nativeElement.click();
  }

  onFilesAdded() {
    const files: { [key: string]: File } = this.fileUpload.nativeElement.files;
    for (let key in files) {
      if (!isNaN(parseInt(key))) {
        console.log(files[key]);
        this.file = files[key];
      }
    }
  }

  onFileDropped(files: Array<any>) {
    if (files.length == 1) {
      this.file = files[0];
    } else {
      this.toastr.warning(this.translate.instant("nexusInventoryImport.1FileAtATime"));
    }
  }

  upload() {
    const formData = new FormData();
    formData.append('file', this.file);
    let importClaim: ImportClaim = {id: this.file.name, fileName: this.file.name, status: ImportStatus.Uploading, progress: 0, remotePath: this.selectedType, body: "", user: this.authService.userStatus.username};
    this.localDataSource = [importClaim].concat(this.localDataSource);
    this.updateDatasource();
    this.importService.upload(this.selectedType, formData).pipe(
      map(event => {
        console.log("event:" + JSON.stringify(event));
        switch (event.type) {
          case HttpEventType.UploadProgress:
            importClaim.progress = Math.round(event.loaded * 100 / event.total);
            break;
          case HttpEventType.Response:
            console.log("Response received");
            return event;
        }
      }),
      catchError((error: HttpErrorResponse) => {
        importClaim.status = ImportStatus.UploadError;
        importClaim.body = JSON.stringify(error);
        return of(`${importClaim.fileName} upload failed.`);
      })).subscribe((event: any) => {
      console.log("event:" + JSON.stringify(event));
      if (typeof (event) === 'object') {
        if (event.status) {
          if (event.status == 200) {
            if (event.body.error) {
              importClaim.status = ImportStatus.UploadError;
              importClaim.body = JSON.stringify(event.body);
            } else {
              // All is ok
              this.doDelete(importClaim);
              this.refreshRemoteClaims();
            }
          } else {
            importClaim.status = ImportStatus.UploadError;
            importClaim.body = JSON.stringify(event);
          }
        }
        console.log(event.body);
      }
    });
    this.file = undefined;
    this.selectedType = undefined;
  }

  getCategory(claim: ImportClaim) {
    return (this.typeList[claim.remotePath]) ? this.typeList[claim.remotePath] : claim.remotePath;
  }

  displayBody(claim: ImportClaim) {
    const dialogRef = this.dialog.open(DetailsDialogComponent, {
      width: '75%',
      height: '480px',
      data: {
        importClaim: claim
      }
    });
  }

  public canDelete(claim: ImportClaim): boolean {
    switch (claim.status) {
      case ImportStatus.UploadError:
      case ImportStatus.Uploaded:
      case ImportStatus.ValidationError:
      case ImportStatus.Imported:
        return true;
      default:
        return false;
    }
  }

  private delete(claim: ImportClaim) {
    const title = this.translate.instant("nexusInventoryImport.deleteConfirmation.title");
    const msg = this.translate.instant("nexusInventoryImport.deleteConfirmation.msg", {fileName: claim.fileName});
    const dialogRef = this.dialog.open(ConfirmationDialogComponent, {
      width: '35%',
      data: {
        actionDescription: title,
        message: msg
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if(result) {
        this.doDelete(claim);
      }
    });
  }

  private doDelete(claim: ImportClaim) {
    if ([ImportStatus.Uploading, ImportStatus.UploadError].includes(claim.status)) {
      this.localDataSource = this.localDataSource.filter(value => value != claim);
    } else {
      this.importService.deleteImport(claim.id).subscribe(value => {
        this.refreshRemoteClaims();
      }, error => {
        const title = this.translate.instant("nexusInventoryImport.deleteConfirmation.title");
        this.toastr.error(title, error.message);
        this.refreshRemoteClaims();
      });
    }
    this.updateDatasource();
  }

  private updateDatasource() {
    this.dataSource = this.localDataSource.concat(this.remoteDataSource);
  }
}
