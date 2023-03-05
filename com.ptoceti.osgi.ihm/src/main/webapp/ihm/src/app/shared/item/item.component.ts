import {Component, Input, OnInit} from '@angular/core';
import {FunctionDataWrapper, ItemWrapper} from '../../api';
import {select, Store} from '@ngrx/store';
import {selectFunctionDataByUid} from '../../state/functiondata.selectors';
import {ItemFunctionData} from '../../state/functiondata.reducer';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Component({
  selector: 'app-item',
  templateUrl: './item.component.html',
  styleUrls: ['./item.component.scss']
})
export class ItemComponent implements OnInit {

  @Input()
  item: ItemWrapper;

  store: Store;
  functionDatas$: Observable<ItemFunctionData>;

  constructor(store: Store) {
    this.store = store;
  }

  ngOnInit(): void {
    this.functionDatas$ = this.store.pipe(select(selectFunctionDataByUid(this.item.uid)));
  }

  getFunctionProperty(propertyName: string): Observable<FunctionDataWrapper> {
    return this.functionDatas$.pipe(map(functiondatas => {
      const functionPropertyDataWrapper = functiondatas.propertyFunctionData.find(item => item.propertyName === propertyName);
      if (functionPropertyDataWrapper) {
        return functionPropertyDataWrapper.propertyData;
      }
      return null;
    }));
  }
}
