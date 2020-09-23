import {Inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {TranslateService} from "@ngx-translate/core";
import {Observable} from "rxjs";
import {InventoryItem} from "@app/core/models/inventory.model";
import {BOOTSTRAP_SETTINGS, BootstrapSettings, GenericService} from "@alien4cloud/wizard4cloud-commons";

@Injectable({
    providedIn: 'root'
})
export class InventoryService extends GenericService {

    constructor(
        http: HttpClient,
        translate: TranslateService,
        @Inject(BOOTSTRAP_SETTINGS) protected bootstrapSettings: BootstrapSettings
    ) {
        super(http, translate, bootstrapSettings);
    }

    listItems(): Observable<InventoryItem[]> {
        const url = this.baseUrl + "/alien4cloud-back-nexus-inventory-plugin";
        return this.handleResult<InventoryItem[]>(this.http.get(url));
    }

}
