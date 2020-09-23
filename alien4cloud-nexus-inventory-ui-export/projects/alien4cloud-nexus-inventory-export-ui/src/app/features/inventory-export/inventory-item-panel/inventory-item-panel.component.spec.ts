import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InventoryItemPanelComponent } from './inventory-item-panel.component';

describe('InventoryItemPanelComponent', () => {
  let component: InventoryItemPanelComponent;
  let fixture: ComponentFixture<InventoryItemPanelComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InventoryItemPanelComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InventoryItemPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
