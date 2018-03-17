


export class Obj {

    static classType: string = 'obj';
    type: string = Obj.classType;
    name: string = '';
    href: Uri = null;
    is: Contract = null;
    isNull: boolean = false;
    icon: Uri = null;
    displayName: string = '';
    display: string = '';
    writable: boolean = false;
    status: string = null;
    childrens: Array<Obj> = new Array<Obj>();
    
    private parent : Obj = null;
    
    timestamp: Date;
    
    protected setParent(parent : Obj){
        this.parent = parent;
    }
    
    getUrl(rootUrl : string) : string {
        
        let href : string;
        // if we have an href, it must be part of effective url
        if ((this.href != null) && ((href = this.href.val) != null)) {
            // if href start by /, it is absolute href
            if (href.charAt(0) === '/') {
                // ... url = root + href
                href =  rootUrl.substr(0, rootUrl.charAt(rootUrl.length - 1) === '/' ? (rootUrl.length - 1) : (rootUrl.length)) + href;
            } else if (href.indexOf('../') == 0) {
                // if start by ../ it is relative to grand parent
            } else {
                // otherwise relative to parent
                if (this.parent == null) {
                    // if there is no parent, take root
                    if (rootUrl.charAt(rootUrl.length - 1) !== '/')
                        rootUrl = rootUrl + '/';
                    href =  rootUrl + href;
                } else {
                    let parentUrl = this.parent.getUrl(rootUrl);
                    if (parentUrl.charAt(rootUrl.length - 1) !== '/')
                        parentUrl = parentUrl + '/';
                    href =  parentUrl + href;
                }
            }
        } else {
            // if there is no href, simply return parent'url
            if (this.parent != null)
                href =  this.parent.getUrl(rootUrl);
            else
                href =  rootUrl;
        }
        
        return href;
    }
    
    /*
    toJSON(value: any){
        if(value === "") {
            var keyNames = Object.keys(this);
            let result:any = {};
            for( let prop of keyNames){
                if( prop != "parent"){
                    result[prop] = this[prop];
                }
            }
            return result;
        } else if( this.hasOwnProperty(value)){
            if( value != "parent"){
                return this[value];
            } else {
                return undefined;
            }
        }
    }
    */
    
    parse( json: any ) {
        for ( var prop in json ) {
            if ( !json.hasOwnProperty( prop ) ) {
                continue;
            }

            if (Array.isArray(json[prop]) && prop == "childrens") { 
                for( let entry of json[prop]){
                    let child : Obj = Obj.getClassFromType( entry.type );
                    child.parse( entry );
                    child.setParent(this);
                    this.childrens.push(child);
                }
            } else if (prop == "is"){
                let contract : Contract = new Contract([]);
                if( json[prop]){
                    contract.parse(json[prop]);
                }
                this[prop] = contract;
            } else if (prop == "href"){
                let href : Uri = new Uri();
                if( json[prop] && json[prop] !== ""){
                    href.parse(json[prop]);
                    this[prop] = href;
                }
                
            } else if (prop == "icon"){
                let icon : Uri = new Uri();
                if( json[prop] && json[prop] !== ""){
                    icon.parse(json[prop]);
                    this[prop] = icon;
                }
                
            } else if ( prop =='timestamp') {
                if( json[prop]){
                    let date: Date = new Date(json[prop]);
                    this[prop] = date;
                }
            } else if (prop == "name" || prop == "display" || prop == "displayName" || prop == "writable" || prop == "isNull" || prop == "status"){
                this[prop] = json[prop];
            }
        }
    }

    static obixParse(json: any) : Obj {
        let result : Obj = this.getClassFromType(json.type);
        result.parse(json);
        return result;
    }

    
    static getClassFromType( type: string ) : Obj {
        
        let result: Obj;
    
        if ( type == Obj.classType ) result = new Obj();
        else if ( type == Uri.classType ) result = new Uri();
        else if ( type == Abstime.classType ) result = new Abstime();
        else if ( type == Bool.classType ) result = new Bool();
        else if ( type == Enum.classType ) result = new Enum();
        else if ( type == Err.classType ) result = new Err();
        else if ( type == Feed.classType ) result = new Feed();
        else if ( type == Int.classType ) result = new Int();
        else if ( type == List.classType ) result = new List();
        else if ( type == Op.classType ) result = new Op();
        else if ( type == Real.classType ) result = new Real();
        else if ( type == Ref.classType ) result = new Ref();
        else if ( type == Reltime.classType ) result = new Reltime();
        else if ( type == Str.classType ) result = new Str();
        else if ( type == About.classType ) result = new About();
        else if ( type == Lobby.classType ) result = new Lobby();
        else if ( type == WatchService.classType ) result = new WatchService();
        else if ( type == Watch.classType ) result = new Watch();
        else if ( type == WatchIn.classType ) result = new WatchIn();
        else if ( type == WatchOut.classType ) result = new WatchOut();
        else if ( type == WatchInItem.classType ) result = new WatchInItem();
        else if ( type == HistoryService.classType ) result = new HistoryService();
        else if ( type == HistoryFilter.classType ) result = new HistoryFilter();
        else if ( type == HistoryRecord.classType ) result = new HistoryRecord();
        else if ( type == HistoryQueryOut.classType ) result = new HistoryQueryOut();
        else if ( type == HistoryRollupIn.classType ) result = new HistoryRollupIn();
        else if ( type == HistoryRollupOut.classType ) result = new HistoryRollupOut();
        else if ( type == HistoryRollupRecord.classType ) result = new HistoryRollupRecord();
        else if ( type == AlarmService.classType ) result = new AlarmService();
        else if ( type == Alarm.classType) result = new Alarm();
        else if ( type == AckAlarm.classType) result = new AckAlarm();
        else if ( type == AlarmAckIn.classType) result = new AlarmAckIn();
        else if ( type == AlarmAckOut.classType) result = new AlarmAckOut();
        else if ( type == DigitAlarm.classType) result = new DigitAlarm();
        else if ( type == PointAlarm.classType) result = new PointAlarm();
        else if ( type == RangeAlarm.classType) result = new RangeAlarm();
        else if ( type == StatefullAlarm.classType) result = new StatefullAlarm();
        
       
        
        //else if ( type == Contract.type ) result = new Contract();

        return result;
    }
}


export class Uri extends Obj {
    static classType: string = 'uri';
    type: string = Uri.classType;
    val: string = null;
    
    constructor(val?: string){
        super();
        this.val = val;
    }
    
    parse( json: any ) {
        super.parse(json);
        
        if ( json.hasOwnProperty( "val" ) ) {
            this.val = json["val"];
        }
    }
    
}

export class Contract {
    static classType: string = 'contract';
    type: string = Contract.classType;
    uris: Array<Uri> = new Array<Uri>();

    constructor(uris: Array<Uri>) {
        this.uris = uris;
    }
    
    parse( json: any ) {
        for ( var prop in json ) {
            if ( !json.hasOwnProperty( prop ) ) {
                continue;
            }
    
            if(Array.isArray(json[prop]) && prop == "uris") { 
                for( let entry of json[prop]){
                    
                    if( entry.type == Uri.classType){
                        let uri : Uri = new Uri();
                        uri.parse(entry);
                        this.uris.push(uri);
                    }
                }
            }
        }
    }
    
    /**
     * Check if the contract contains a specific Uri
     * @param uri
     */
    contains(uri: Uri) : boolean{
        let result: boolean = false;
    
        let uriIndex = this.uris.findIndex( contracturi => contracturi.val == uri.val); 
        if( uriIndex >= 0) result = true;
    
        return result;
    }
}

export class Abstime extends Obj {

    static classType: string = 'abstime';
    type: string = Abstime.classType;
    val: string = null;
    max: Abstime = null;
    min: Abstime = null;
    
    parse( json: any ) {
        super.parse(json);
        
        if ( json.hasOwnProperty( "val" ) ) {
            this.val = json["val"];
        }
        
        if ( json.hasOwnProperty( "max" ) && json["max"]) {
            let max : Abstime = new Abstime();
            max.parse(json["max"]);
            this.max = max;
        }
        
        if ( json.hasOwnProperty( "min" ) && json["min"]) {
            let min : Abstime = new Abstime();
            min.parse(json["min"]);
            this.min = min;
        }
    }
    
}

export class Bool extends Obj {

    static classType: string = 'bool';
    type: string = Bool.classType;
    val: boolean = null;
    range: string = null;
    
    parse( json: any ) {
        super.parse(json);
        
        if ( json.hasOwnProperty( "val" ) ) {
            this.val = json["val"] === "true";
        }
        
        if ( json.hasOwnProperty( "range" ) ) {
            this.range = json["range"];
        }
        
    }
    
}

export class Enum extends Obj {

    static classType: string = 'enum';
    type: string = Enum.classType;
    val: string = null;
    range: string = null;
    
    parse( json: any ) {
        super.parse(json);
        
        if ( json.hasOwnProperty( "val" ) ) {
            this.val = json["val"];
        }
        
        if ( json.hasOwnProperty( "range" ) ) {
            this.val = json["range"];
        }
    }
   
}

export class Err extends Obj {

    static classType: string = 'err';
    type: string = Err.classType;
  
}

export class Feed extends Obj {

    static classType: string = 'feed';
    type: string = Feed.classType;
    in: Contract = null;
    of: Contract = null;
    
    parse( json: any ) {
        super.parse(json);
        
        
        if ( json.hasOwnProperty( "in" ) && json["in"]) {
            let contract : Contract = new Contract([]);
            contract.parse(json["in"]);
            this.in = contract;
        }
        
        if ( json.hasOwnProperty( "of" ) && json["of"]) {
            let contract : Contract = new Contract([]);
            contract.parse(json["of"]);
            this.of = contract;
        }
    }
    
}

export class Int extends Obj {

    static classType: string = 'int';
    type: string = Int.classType;
    val: number = null;
    max: number = null;
    min: number = null;
    unit: Uri = null;
    
    parse( json: any ) {
        super.parse(json);
        
        if ( json.hasOwnProperty( "val" ) ) {
            this.val = parseInt(json["val"]);
        }
        
        if ( json.hasOwnProperty( "min" ) && json["min"]) {
            this.min = parseInt(json["min"]);
        }
        
        if ( json.hasOwnProperty( "max" ) && json["max"]) {
            this.max = parseInt(json["max"]);
        }
        
        if ( json.hasOwnProperty( "unit" ) && json["unit"]) {
            let unit : Uri = new Uri();
            unit.parse(json["unit"]);
            this.unit = unit;
        }
    }
    
}

export class List extends Obj {
    static classType: string = 'list';
    type: string = List.classType;
    of: Contract = null;
    max: number = null;
    min: number = null;
    
    parse( json: any ) {
        super.parse(json);
        
        
        if ( json.hasOwnProperty( "of" ) ) {
            let contract : Contract = new Contract([]);
            contract.parse(json["of"]);
            this.of = contract;
        }
        
    }
    
}

export class Op extends Obj {

    static classType: string = 'op';
    type: string = Op.classType;
    in: Contract = null;
    out: Contract = null;
    
    
    parse( json: any ) {
        super.parse(json);
        
        
        if ( json.hasOwnProperty( "in" ) && json["in"]) {
            let contract : Contract = new Contract([]);
            contract.parse(json["in"]);
            this.in = contract;
        }
        
        if ( json.hasOwnProperty( "out" ) && json["out"]) {
            let contract : Contract = new Contract([]);
            contract.parse(json["out"]);
            this.out = contract;
        }
    }
    
}

export class Real extends Obj {
    static classType: string = 'real';
    type: string = Real.classType;
    val: number = null;
    max: number = null;
    min: number = null;
    unit: Uri = null;
    precision: string = null;
    
    parse( json: any ) {
        super.parse(json);
        
        if ( json.hasOwnProperty( "val" ) ) {
            this.val = parseFloat(json["val"]);
        }
        
        if ( json.hasOwnProperty( "min" ) && json["min"]) {
            this.min = parseFloat(json["min"]);;
        }
        
        if ( json.hasOwnProperty( "max" ) && json["max"]) {
            this.max = parseFloat(json["max"]);;
        }
        
        if ( json.hasOwnProperty( "unit" ) && json["unit"]) {
            let unit : Uri = new Uri();
            unit.parse(json["unit"]);
            this.unit = unit;
        }
    }
    
}

export class Ref extends Obj {
    static classType: string = 'ref';
    type: string = Ref.classType;
    
}

export class Reltime extends Obj {
    static classType: string = 'reltime';
    type: string = Reltime.classType;
    val: string = null;
    max: Reltime = null;
    min: Reltime = null;
    
    parse( json: any ) {
        super.parse(json);
        
        if ( json.hasOwnProperty( "val" ) ) {
            this.val = json["val"];
        }
        
        if ( json.hasOwnProperty( "min" ) && json["min"]) {
            let min : Reltime = new Reltime();
            min.parse(json["min"]);
            this.min = min;
        }
        
        if ( json.hasOwnProperty( "max" ) && json["max"]) {
            let max : Reltime = new Reltime();
            max.parse(json["max"]);
            this.max = max;
        }
    }
    
}

export class Str extends Obj {
    static classType: string = 'str';
    type: string = Str.classType;
    val: string = null;
    max: string = null;
    min: string = null;
    
    parse( json: any ) {
        super.parse(json);
        
        if ( json.hasOwnProperty( "val" ) ) {
            this.val = json["val"];
        }
    }

}


export var Status = {

    DISABLED: 'disabled',
    FAULT: "fault",
    DOWN: "down",
    UNAKEDALARM: "unackedAlarm",
    ALARM: "alarm",
    UNACKED: "unacked",
    OVERRIDEN: "overridden",
    OK: "ok"
}

export class Nil extends Obj{
    static classType: string = 'nil';
    type: string = Nil.classType;
    is: Contract = new Contract( [ new Uri('obix:Nil')]);
    
}

export class SearchOut extends Obj{
    static classType: string = 'searchout';
    type: string = SearchOut.classType;
    is: Contract = new Contract( [ new Uri('obix:SearchOut')]);
    
    getValueList() : List {
        let result : List  = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "results"}) as List ;
        return result;
    
    }
}

export class Lobby extends Obj{
    static classType: string = 'lobby';
    type: string = Lobby.classType;
    is: Contract = new Contract( [ new Uri('obix:Lobby')]);
    
    getAbout() : About {
        let result : About = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "about"}) as About ;
        return result;
    }
    
    getWatchServiceRef() : Ref {
        let result : Ref = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "watchService"})as Ref;
        return result;
    }
    
    getHistoryServiceRef() : Ref {
        let result : Ref = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "historyService"}) as Ref;
        return result;
    }
    
    getAlarmServiceRef() : Ref {
        let result : Ref = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "alarmService"}) as Ref;
        return result;
    }
    
    getBatchOp() : Op {
        let result : Op = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "batch"}) as Op;
        return result;
    }
    
    getSearchOp() : Op {
        let result : Op = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "search"}) as Op;
        return result;
    }
}

export class About extends Obj{
    static classType: string = 'about';
    type: string = About.classType;
    is: Contract = new Contract( [ new Uri('obix:About')]);
    
    getServerBootTime() : Abstime{
        let result : Abstime = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "serverBootTime"}) as Abstime;
        return result;
    }

    getServerTime() : Abstime {
        let result : Abstime = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "serverTime"}) as Abstime;
        return result;
    }
    
    getObixVersion() : Str {
        let result : Str = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "obixVersion"}) as Str;
        return result;
    }
    
    getProductUrl() : Uri {
        let result : Uri = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "productUrl"}) as Uri;
        return result;
    }
    
    getProductName() : Str{
        let result : Str = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "productName"}) as Str;
        return result;
    }
    
    getProductVersion() : Str {
        let result : Str = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "productVersion"}) as Str;
        return result;
    }
    
    getVendorName() : Str{
        let result : Str = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "vendorName"}) as Str;
        return result;
    }
    
    getVendorUrl() : Uri {
        let result : Uri = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "vendorUrl"}) as Uri;
        return result;
    }
    
    getServerName() : Str {
        let result : Str = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "serverName"}) as Str;
        return result;
    }
}

export class Point extends Obj{
    static classType: string = 'point';
    type: string = Point.classType;
    is: Contract = new Contract( [ new Uri('obix:Point')]);
}

export class WritablePoint extends Obj{
    static classType: string = 'writablepoint';
    type: string = WritablePoint.classType;
    is: Contract = new Contract( [ new Uri('obix:WritablePoint')]);
}

export class MeasurePoint extends Obj{
    static classType: string = 'measurepoint';
    type: string = MeasurePoint.classType;
    is: Contract = new Contract( [ new Uri('ptoceti:MeasurePoint')]);
}

export class MonitoredPoint extends Obj{
    static classType: string = 'monitoredpoint';
    type: string = MonitoredPoint.classType;
    is: Contract = new Contract( [ new Uri('ptoceti:MonitoredPoint')]);
}

export class ReferencePoint extends Real{
    static classType: string = 'referencepoint';
    type: string = ReferencePoint.classType;
    is: Contract = new Contract( [ new Uri('ptoceti:ReferencePoint')]);
}

export class WatchService extends Obj{
    static classType: string = 'watchservice';
    type: string = WatchService.classType;
    is: Contract = new Contract( [ new Uri('obix:WatchService')]);
    
    getMakeOp() : Op{
        let result : Op = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "make"}) as Op;
        return result;
    }
    
}

export class Watch extends Obj{
    static classType: string = 'watch';
    type: string = Watch.classType;
    is: Contract = new Contract( [ new Uri('obix:Watch')]);
    
    getAddOp() : Op {
        let result : Op = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "add"}) as Op;
        return result;
    }

    getDeleteOp() : Op {
        let result : Op = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "delete"}) as Op;
        return result;
    }

    getPoolChangesOp() : Op {
        let result : Op = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "poolChanges"}) as Op;
        return result;
    }

    getPoolRefreshOp() : Op {
        let result : Op = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "poolRefresh"}) as Op;
        return result;
    }

    getRemoveOp() : Op {
        let result : Op = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "remove"}) as Op;
        return result;
    }

    getLease() : Reltime {
        let result : Reltime = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "lease"}) as Reltime;
        return result;
    }
    
}

export class WatchIn extends Obj {
    static classType: string = 'watchin';
    type: string = WatchIn.classType;
    is: Contract = new Contract( [ new Uri('obix:WatchIn')]);
    
    constructor(){
        super();
        
        let list : List = new List();
        list.name = 'href';
        list.of = new Contract([ new Uri('obix:href')])
        this.childrens.push(list);
    }

    getHrefList() : List {
        let result : List = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "href"}) as List;
        return result;
    }
}

export class WatchOut extends Obj{
    static classType: string = 'watchout';
    type: string = WatchOut.classType;
    is: Contract = new Contract( [ new Uri('obix:WatchOut')]);
    
    getValueList() : List {
        let result : List = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "values"}) as List;
        return result;
    }
}

export class WatchInItem extends Uri{
    static classType: string = 'watchinitem';
    type: string = WatchInItem.classType;
    is: Contract = new Contract( [ new Uri('obix:WatchInItem')]);
    
    constructor(val?: string){
        super(val);
    }

    getIn() : Obj{
        let result : Obj = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "in"}) as Obj;
        return result;
    }
}


export class HistoryService extends Obj{
    static classType: string = 'historyservice';
    type: string = HistoryService.classType;
    is: Contract = new Contract( [ new Uri('obix:HistoryService')]);
    
    getMakeOp() : Op {
        let result : Op = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "make"}) as Op;
        return result;
    }
    
}

export class History extends Obj{
    static classType: string = 'history';
    type: string = History.classType;
    is: Contract = new Contract( [ new Uri('obix:History')]);
    
    getQueryOp() : Op {
        let result : Op = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "query"}) as Op;
        return result;
    }
    
    getRollupOp() : Op {
        let result : Op = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "rollup"}) as Op;
        return result;
    }
    
    getStart() : Abstime {
        let result : Abstime = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "start"}) as Abstime;
        return result;
    }
    
    getEnd() : Abstime {
        let result : Abstime = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "end"}) as Abstime;
        return result;
    }
    
    getCount() : Int {
        let result : Int = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "count"}) as Int;
        return result;
    }
}

export class HistoryFilter extends Obj{
    static classType: string = 'historyfilter';
    type: string = HistoryFilter.classType;
    is: Contract = new Contract( [ new Uri('obix:HistoryFilter')]);
    
    
    getStart() : Abstime {
        let result : Abstime = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "start"}) as Abstime;
        return result;
    }
    
    setStart(start : Abstime) {
        let index = this.childrens.findIndex(function(this, value, index, obj) : boolean {return value.name == "start"});
        start.name = "start";
        if( index < 0) {
            this.childrens.push(start);
        } else {
            this.childrens[index] = start;
        }
    }
    
    getEnd() : Abstime {
        let result : Abstime = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "end"}) as Abstime;
        return result;
    }
    
    setEnd(end : Abstime) {
        let index = this.childrens.findIndex(function(this, value, index, obj) : boolean {return value.name == "end"});
        end.name = "end";
        if( index < 0) {
            this.childrens.push(end);
        } else {
            this.childrens[index] = end;
        }
    }
    
    getLimit() : Int {
        let result : Int = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "limit"}) as Int;
        return result;
    }
    
    setLimit(limit : Int) {
        let index = this.childrens.findIndex(function(this, value, index, obj) : boolean {return value.name == "limit"});
        limit.name = "limit";
        if( index < 0) {
            this.childrens.push(limit);
        } else {
            this.childrens[index] = limit;
        }
    }
}

export class HistoryRecord extends Obj{
    static classType: string = 'historyrecord';
    type: string = HistoryRecord.classType;
    is: Contract = new Contract( [ new Uri('obix:HistoryRecord')]);
    
    getTimeStamp() : Abstime {
        let result : Abstime = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "timestamp"}) as Abstime;
        return result;
    }
    
    getValue() : Obj {
        let result : Obj = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "value"}) as Obj;
        return result;
    }
}

export class HistoryQueryOut extends Obj{
    static classType: string = 'historyqueryout';
    type: string = HistoryQueryOut.classType;
    is: Contract = new Contract( [ new Uri('obix:HistoryQueryOut')]);
    
    getStart() : Abstime {
        let result : Abstime = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "start"}) as Abstime;
        return result;
    }
    
    getEnd() : Abstime {
        let result : Abstime = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "end"}) as Abstime;
        return result;
    }
    
    getCount() : Int {
        let result : Int = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "count"}) as Int;
        return result;
    }
    
    getDataList() : List {
        let result : List = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "data"}) as List;
        return result;
    }
}

export class HistoryRollupIn extends HistoryFilter{
    static classType: string = 'historyrollupin';
    type: string = HistoryRollupIn.classType;
    is: Contract = new Contract( [ new Uri('obix:HistoryRollupIn')]);
    
    getInterval() : Reltime {
        let result : Reltime = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "interval"}) as Reltime;
        return result;
    }
    
    setInterval(interval : Reltime) {
        let index = this.childrens.findIndex(function(this, value, index, obj) : boolean {return value.name == "interval"});
        interval.name = "interval";
        if( index < 0) {
            this.childrens.push(interval);
        } else {
            this.childrens[index] = interval;
        }
    }
}

export class HistoryRollupOut extends HistoryFilter{
    static classType: string = 'historyrollupout';
    type: string = HistoryRollupOut.classType;
    is: Contract = new Contract( [ new Uri('obix:HistoryRollupOut')]);
    
    getStart() : Abstime {
        let result : Abstime = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "start"}) as Abstime;
        return result;
    }
    
    getEnd() : Abstime {
        let result : Abstime = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "end"}) as Abstime;
        return result;
    }
    
    getCount() : Int {
        let result : Int = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "count"}) as Int;
        return result;
    }
    
    setCount(count : Int) {
        let index = this.childrens.findIndex(function(this, value, index, obj) : boolean {return value.name == "count"});
        count.name = "count";
        if( index < 0) {
            this.childrens.push(count);
        } else {
            this.childrens[index] = count;
        }
    }
    
    getDataList() : List {
        let result : List = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "data"}) as List;
        return result;
    }
}

export class HistoryRollupRecord extends Obj{
    static classType: string = 'historyrolluprecord';
    type: string = HistoryRollupRecord.classType;
    is: Contract = new Contract( [ new Uri('obix:HistoryRollupRecord')]);
    
    getStart() : Abstime {
        let result : Abstime = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "start"}) as Abstime;
        return result;
    }
    
    getEnd() : Abstime {
        let result : Abstime = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "end"}) as Abstime;
        return result;
    }
    
    getCount() : Int {
        let result : Int = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "count"}) as Int;
        return result;
    }
    
    getMin() : Real {
        let result : Real = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "min"}) as Real;
        return result;
    }
    
    getMax() : Real {
        let result : Real = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "max"}) as Real;
        return result;
    }
    
    getAvg() : Real {
        let result : Real = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "avg"}) as Real;
        return result;
    }
    
    getSum() : Real {
        let result : Real = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "sum"}) as Real;
        return result;
    }
}

export class AlarmService extends Obj{
    static classType: string = 'alarmservice';
    type: string = AlarmService.classType;
    is: Contract = new Contract( [ new Uri('ptoceti:AlarmService')]);
    
    getMakeOp() : Op {
        let result : Op = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "make"}) as Op;
        return result;
    }
    
}

export class Alarm extends Obj {
    static classType: string = 'alarm';
    type: string = Alarm.classType;
    is: Contract = new Contract( [ new Uri('obix:Alarm')]);
    
    getSource() : Ref {
        let result : Ref = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "source"}) as Ref;
        return result;
    }
    
    getTimeStamp() : Abstime {
        let result : Abstime = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "timestamp"}) as Abstime;
        return result;
    }
    
    getAckOp() : Op {
        let result : Op = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "ack"}) as Op;
        return result;
    }
    
    getAckTimestamp() : Abstime {
        let result : Abstime = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "ackTimestamp"}) as Abstime;
        return result;
    }
    
    getAckUser() : Str {
        let result : Str = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "ackUser"}) as Str;
        return result;
    }
    
    getNormalTimeStamp() : Abstime {
        let result : Abstime = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "normalTimeStamp"}) as Abstime;
        return result;
    }
    
    getAlarmValue() : Obj {
        let result : Obj = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "alarmValue"}) as Obj;
        return result;
    }
}

export class AckAlarm extends Alarm {
    static classType: string = 'ackalarm';
    type: string = AckAlarm.classType;
    is: Contract = new Contract( [ new Uri('obix:Alarm'), new Uri('obix:AckAlarm')]);
}

export class AlarmAckOut extends Obj {
    static classType: string = 'alarmackout';
    type: string = AlarmAckOut.classType;
    is: Contract = new Contract( [ new Uri('obix:AlarmAckOut')]);
    
    getAlarm() : Alarm {
        let result : Alarm = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "alarm"}) as Alarm;
        return result;
    }
}

export class AlarmAckIn extends Obj {
    static classType: string = 'alarmackin';
    type: string = AlarmAckIn.classType;
    is: Contract = new Contract( [ new Uri('obix:AlarmAckIn')]);
    
    getAckUser() : Str {
        let result : Str = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "ackUser"}) as Str;
        return result;
    }
}

export class StatefullAlarm extends Alarm {
    static classType: string = 'statefulalarm';
    type: string = StatefullAlarm.classType;
    is: Contract = new Contract( [ new Uri('obix:Alarm'), new Uri('obix:StatefullAlarm')]);
}

export class PointAlarm extends Alarm {
    static classType: string = 'pointalarm';
    type: string = PointAlarm.classType;
    is: Contract = new Contract( [ new Uri('obix:Alarm'), new Uri('obix:PointAlarm')]);
}

export class DigitAlarm extends Alarm {
    static classType: string = 'digitalarm';
    type: string = DigitAlarm.classType;
    is: Contract = new Contract( [ new Uri('obix:Alarm'), new Uri('obix:PointAlarm'), new Uri('obix:StatefulAlarm'),new Uri('obix:StatefulAlarm'), new Uri('obix:AckAlarm'), new Uri('ptoceti:DigitAlarm')]);

    getAlarmLevel() : Int {
        let result : Int = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "alarmLevel"}) as Int;
        return result;
    }
}

export class RangeAlarm extends Alarm {
    static classType: string = 'digitalarm';
    type: string = RangeAlarm.classType;
    is: Contract = new Contract( [ new Uri('obix:Alarm'), new Uri('obix:PointAlarm'), new Uri('obix:StatefulAlarm'),new Uri('obix:StatefulAlarm'), new Uri('obix:AckAlarm'), new Uri('ptoceti:RangeAlarm')]);

    getMaxValue() : Int {
        let result : Int = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "maxValue"}) as Int;
        return result;
    }
    
    getMinValue() : Int {
        let result : Int = null;
        result = this.childrens.find(function(this, value, index, obj) : boolean {return value.name == "minValue"}) as Int;
        return result;
    }
}



