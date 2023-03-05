import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewEncapsulation
} from '@angular/core';
import {DeviceFactoryInfoWrapper, ItemWrapper, ThingWrapper} from '../../api';
import {Observable} from 'rxjs';
import {select, Store} from '@ngrx/store';
import {selectItemsByDeviceUid} from '../../state/items.selectors';
import {first, map} from 'rxjs/operators';

// import {ToggleDirective, ToggleOffDirective, ToggleOnDirective} from '../../shared/toggle.directive';

@Component({
  selector: 'app-thing-items',
  templateUrl: './thing-items.component.html',
  styleUrls: ['./thing-items.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ThingItemsComponent implements OnInit {

  @Input()
  thing: ThingWrapper;
  @Input()
  hasConfig: boolean;
  @Output()
  deleteThingEvent = new EventEmitter<ThingWrapper>();

  itemsToggled = false;

  store: Store;
  items$: Observable<ItemWrapper[]>;

  constructor(store: Store) {
    this.store = store;
  }

  ngOnInit(): void {
    this.items$ = this.store.pipe(select(selectItemsByDeviceUid(this.thing.uid)));
  }

  onDelete(): void {
    this.deleteThingEvent.emit(this.thing);
  }

  toogleItems(toggled: boolean): void {
    this.itemsToggled = toggled;
  }

  hasItems(): Observable<boolean> {
    return this.items$.pipe(first(),
      map((items: ItemWrapper[]) => {
        return items.length > 0;
      }));
  }
}
