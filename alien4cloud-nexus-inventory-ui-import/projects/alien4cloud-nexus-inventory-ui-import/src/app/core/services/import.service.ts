import {Inject, Injectable} from '@angular/core';
import {GenericService, BOOTSTRAP_SETTINGS, BootstrapSettings} from "@alien4cloud/wizard4cloud-commons";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {TranslateService} from "@ngx-translate/core";
import {Observable} from "rxjs";
import {ImportClaim} from "@app/core/models/import-claim.model";

@Injectable({
    providedIn: 'root'
})
export class ImportService extends GenericService {

    private readonly url: string;

    constructor(
        http: HttpClient,
        translate: TranslateService,
        @Inject(BOOTSTRAP_SETTINGS) protected bootstrapSettings: BootstrapSettings
    ) {
        super(http, translate, bootstrapSettings);
        this.url = this.baseUrl + "/alien4cloud-back-nexus-inventory-plugin/importClaim";
    }

    listImportsCategories(): Observable<Map<string, string>> {
        const endpointUrl = this.url + "/categories";
        return this.handleResult<Map<string, string>>(this.http.get(endpointUrl));
    }

    listImports(): Observable<ImportClaim[]> {
        const endpointUrl = this.url + "/list";
        return this.handleResult<ImportClaim[]>(this.http.get(endpointUrl));
    }

    upload(category: string, formData: FormData) {
        const endpointUrl = this.url + "/" + encodeURI(category);
        return this.http.post<any>(endpointUrl, formData, {
            reportProgress: true,
            observe: 'events'
        });
    }

    deleteImport(id: string): Observable<void> {
        const endpointUrl = this.url + "/" + encodeURI(id);
        return this.handleResult<void>(this.http.delete(endpointUrl));
    }

}
