import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {InventoryItem, ItemSelection} from "@app/core/models/inventory.model";
import * as _ from "lodash";

@Component({
  selector: 'w4c-inventory-item-panel',
  templateUrl: './inventory-item-panel.component.html',
  styleUrls: ['./inventory-item-panel.component.css']
})
export class InventoryItemPanelComponent implements OnInit {

  @Input() item: InventoryItem;

  @Input() version: string;

  /**
   * If this component is in the cart or not (inventory).
   */
  @Input() isInCart: boolean;

  @Output() selected = new EventEmitter<ItemSelection>();

  @Output() removed = new EventEmitter<ItemSelection>();

  // make lodash usable from template
  lodash = _;

  constructor() { }

  ngOnInit() {
    if (this.item.versions.length == 1) {
      this.version = this.item.versions[0];
    }
  }

  addToCart(version: string) {
    this.selected.emit(new ItemSelection(this.item, version));
  }

  remove() {
    // A timeout to avoid animation collision
    this.removed.emit(new ItemSelection(this.item, this.version));
  }

  getItemTypeIcon(item: InventoryItem) {
    if (item.type == 'APPLICATION') {
      return "desktop_mac";
    } else if (item.type == 'MODULE') {
      return "description";
    } else if (item.type == 'CAS_USAGE') {
      return "apps";
    } else {
      return "device_unknown";
    }
  }

}
