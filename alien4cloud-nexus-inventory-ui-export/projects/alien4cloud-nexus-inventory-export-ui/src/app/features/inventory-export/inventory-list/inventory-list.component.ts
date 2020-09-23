import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {InventoryItem, ItemSelection} from "@app/core/models/inventory.model";
import {Observable} from "rxjs";

@Component({
  selector: 'w4c-inventory-list',
  templateUrl: './inventory-list.component.html',
  styleUrls: ['./inventory-list.component.scss']
})
export class InventoryListComponent implements OnInit {

  inventory: InventoryItem[] = [];

  @Input()
  inventorySubject$: Observable<InventoryItem[]>;

  @Input()
  itemTypeFilter: string;

  @Output() selected = new EventEmitter<ItemSelection>();

  constructor(
  ) { }

  ngOnInit() {
    this.inventorySubject$.subscribe(value => {
      this.inventory = value.filter(item => {
        return item.type == this.itemTypeFilter;
      })
    });
  }

  addToCart(selection: ItemSelection) {
    this.selected.emit(selection);
  }

}
