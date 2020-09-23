export interface InventoryItem {
    id: string
    type: string;
    name: string;
    versions: string[];
    cu: string;
    gitPath: string;
}

export class ItemSelection {
    constructor(
        public item: InventoryItem,
        public version: string
    ) {
    }

    // just to trigger an animation when item already exist in the cart
    public focus: boolean;

    public getKey() {
        return this.item.id + "#" + this.version;
    }
}
