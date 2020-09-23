import {Inject, Injectable} from '@angular/core';
import {GenericService, BOOTSTRAP_SETTINGS, BootstrapSettings} from "@alien4cloud/wizard4cloud-commons";
import {HttpClient} from "@angular/common/http";
import {TranslateService} from "@ngx-translate/core";
import {Observable} from "rxjs";
import {ExportRequest, ExportResult} from "@app/core/models/export.model";

@Injectable({
    providedIn: 'root'
})
export class ExportService extends GenericService {

    private readonly url: string;

    constructor(
        http: HttpClient,
        translate: TranslateService,
        @Inject(BOOTSTRAP_SETTINGS) protected bootstrapSettings: BootstrapSettings
    ) {
        super(http, translate, bootstrapSettings);
        this.url = this.baseUrl + "/alien4cloud-back-nexus-inventory-plugin/export";
    }

    listExports(): Observable<ExportResult[]> {
        return this.handleResult<ExportResult[]>(this.http.get(this.url));
    }

    doExport(request: ExportRequest): Observable<void> {
        return this.handleResult<void>(this.http.post(this.url, request));
    }

    download(element: ExportResult) {
        window.location.assign(this.url + "/" + element.name + "/file");
    }

    delete(element: ExportResult) {
        return this.handleResult<void>(this.http.delete(this.url + "/" + element.name + "/file"));
    }

}
