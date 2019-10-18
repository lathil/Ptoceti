import { Component, EventEmitter, Output, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';

import {faChevronUp, faChevronDown, faSearch, faSync, faPlus} from '@fortawesome/free-solid-svg-icons';

import {map} from "rxjs/operators";


import { Obj, Contract, Ref, List, Uri, SearchOut, WatchService, Watch, WatchOut, Nil } from '../obix/obix';
import { SearchService } from '../obix/obix.searchservice';


@Component( {
    selector: 'search',
    templateUrl: './search.component.html'
} )
export class SearchComponent {

    searchService: SearchService;

    public isCollapsed: boolean = true;

    searchField: string;
    searchResults: Array<Ref> = new Array<Ref>();

    faChevronUp = faChevronUp;
    faChevronDown = faChevronDown;
    faSearch = faSearch;
    faSync = faSync;
    faPlus = faPlus;


    @Output() onAdd = new EventEmitter<Ref>();
    @Output() onRefresh = new EventEmitter();
    @Output() onCreate = new EventEmitter();


    @Input() contractFilter : Contract;
    @Input() objectFilterList: Array<Obj>;
    
    constructor( searchService: SearchService ) {
        this.searchService = searchService;
    }

    onClickSearch( name: string ) {

        let filteredName = "";
        if ( name !== undefined ) {
            filteredName = name.trim();
        }

        let searchRef: Ref = new Ref();
        if ( filteredName.length > 0 ) {
            searchRef.displayName = filteredName;
            searchRef.is = new Contract( [] );
        } else {
            searchRef.is = this.contractFilter;
        }


        this.searchResults.splice( 0, this.searchResults.length );

        this.searchService.search(searchRef).pipe(
            map(item => {
                if (item != null) {
                    let searchOut: SearchOut = new SearchOut();
                    searchOut.parse(item);
                    return searchOut;
                }
            })
        ).subscribe((searchOut) => {
                this.isCollapsed = false;
                let searchList: List = searchOut.getValueList();
                for ( let listItem of searchList.childrens ) {
                    if( this.objectFilterList.findIndex( elem => elem.href.val == listItem.href.val ) < 0 ){
                    this.searchResults.push( listItem );
                    }
                }
            } );

    }

    public collapsed( event: any ): void {

    }

    public expanded( event: any ): void {

    }

    public onClickAdd( itemUrl: string ) {

        let searchIndex = this.searchResults.findIndex( elem => elem.href.val == itemUrl );
        if ( searchIndex > -1 ) {
            this.onAdd.emit( this.searchResults[searchIndex] );
            this.searchResults.splice( searchIndex, 1 );
        }
    }

    public onClickRefresh() {
        this.onRefresh.emit();
    }
    
    public onClickCreate(){
        this.onCreate.emit();
    }

}