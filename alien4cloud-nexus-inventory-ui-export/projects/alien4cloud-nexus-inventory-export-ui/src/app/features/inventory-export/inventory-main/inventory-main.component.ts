import {ChangeDetectorRef, Component, ElementRef, Inject, OnInit, ViewChild} from '@angular/core';
import {InventoryService} from "@app/core/services/inventory.service";
import {InventoryItem, ItemSelection} from "@app/core/models/inventory.model";
import {AbstractControl, FormControl, ValidatorFn, Validators} from "@angular/forms";
import {debounceTime} from "rxjs/operators";
import {ReplaySubject} from "rxjs";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {DOCUMENT} from "@angular/common";
import {ExportService} from "@app/core/services/export.service";
import {ExportRequest, ItemReference} from "@app/core/models/export.model";
import {PackageListComponent} from "@app/features/inventory-export/package-list/package-list.component";

let nameRegexp: RegExp = new RegExp('^[A-Za-z0-9_]+$');
export function exportNameValidator(): ValidatorFn {
  return (control: AbstractControl): {[key: string]: any} | null => {
    let valid = nameRegexp.test(control.value)
    return valid ? null : {'i18nKey': 'nexusInventoryExport.export.name.errors.OnlyWordsCharacters'};
  };
}

@Component({
  selector: 'w4c-inventory-main',
  templateUrl: './inventory-main.component.html',
  styleUrls: ['./inventory-main.component.scss'],
    animations: [
        // this animation will operate when adding or removing an item in the cart
      trigger('fade', [
        state('in', style({opacity: 1})),
        transition(':enter', [
          style({opacity: 0}),
          animate(600 )
        ]),
        transition(':leave',
            animate(600, style({opacity: 0})))
      ]),
        // this animation will operate when we try to add an already existing item in the cart
      trigger('focus', [
        transition('* => true', [
          //animate(100, style({opacity: 0})),
          //animate(800, style({opacity: 1}))
          animate(300, style({transform:'scale(1.1)'})),
          animate(800, style({transform:'scale(1)'})),
        ])
      ]),
      trigger('error', [
        state('in', style({opacity: 1})),
        transition(':enter', [
          style({opacity: 0}),
          animate(600 )
        ]),
        transition(':leave',
          animate(600, style({opacity: 0})))
      ])
    ]
})
export class InventoryMainComponent implements OnInit {

  // a form control to bind to search input
  searchField: FormControl = new FormControl();

  nameField: FormControl = new FormControl(
      undefined,
      [
        Validators.required,
        exportNameValidator()
      ]
  );

/*
  @ViewChild('cartViewport', {static: true}) cartViewport: CdkVirtualScrollViewport;
*/

  @ViewChild('cartContainer', {static: true})
  private cartContainer: ElementRef;

  @ViewChild('listExportPanelContainer', {static: true})
  private listExportPanelContainer: ElementRef;

  @ViewChild('listExportPanelComponent', {static: true})
  private listExportPanelComponent: PackageListComponent;

  submitError: string;

  constructor(
      private inventoryService: InventoryService,
      private exportService: ExportService,
      @Inject(DOCUMENT) private document
  ) { }

  /**
   * All the item retrieved from backend
   */
  db: InventoryItem[] = [];

  /**
   * Filtered items.
   */
  inventory: InventoryItem[] = [];

  private inventorySubject = new ReplaySubject<InventoryItem[]>(1);
  inventorySubject$ = this.inventorySubject.asObservable();

  /**
   * The cart content (user selection).
   */
  cart: ItemSelection[] = [];

  availableExportCount: number;

  search() {
    if (this.searchField.value === null || this.searchField.value.length === 0) {
      this.inventory = this.db;
    } else {
      let query = this.searchField.value.toLowerCase();
      this.inventory = this.db.filter(value => {
        return value.id.toLowerCase().indexOf(query) > -1;
      });
    }
    this.inventorySubject.next(this.inventory);
  }

  ngOnInit() {
    this.inventoryService.listItems().subscribe(value => {
      this.db = value
      this.search();
    });

    this.searchField.valueChanges
        .pipe(debounceTime(500))
        .subscribe(term => {
          this.search();
        });
  }

  addToCart(itemSelection: ItemSelection) {
    const i = this.cart.findIndex(value => value.getKey() == itemSelection.getKey());
    if (i > -1) {
      // scroll the element into view
      this.document.getElementById(itemSelection.getKey()).scrollIntoView();
      // fire the animation to emphasis the element
      this.cart[i].focus = true;
      setTimeout(() => {
        // set the focus back to false (after the animation has finished)
        this.cart[i].focus = false;
      }, 1000);
      return;
    }
    this.cart.push(itemSelection);
    // Necessary timeout since we have animation on :enter
    setTimeout(() => {
      this.cartContainer.nativeElement.scrollTop = this.cartContainer.nativeElement.scrollHeight;
    }, 100);
  }

  removeFromCart(itemSelection: ItemSelection) {
    const i = this.cart.findIndex(value => value.getKey() == itemSelection.getKey());
    this.cart.splice(i, 1);
  }

  cancel() {
    this.cart = [];
    this.nameField.reset();
    this.submitError = undefined;
  }

  export() {
    const request: ExportRequest = new ExportRequest();
    request.name = this.nameField.value;
    request.items = this.cart.map<ItemReference>(value => {return { "id": value.item.id, "version": value.version}});
    this.submitError = undefined;
    this.exportService.doExport(request).subscribe(value => {
      this.cancel();
      setTimeout(value => {
        this.listExportPanelComponent.refresh();
        // scroll to top after refresh
        this.listExportPanelContainer.nativeElement.scrollTop = 0;
      }, 500);
    }, error => {
      this.submitError = error.message;
      console.log("Error occured while submitting export request: " + error);
      setTimeout(value => { this.submitError = undefined }, 3000);
    });
  }

  setAvailableExportCount(availableExportCount: number) {
    this.availableExportCount = availableExportCount;
  }

}
