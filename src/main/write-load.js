var sample = {    
    "TDT" : "2015-07-01 00:00:00",
    "TTM" : 131237,
    "ACC" : NumberLong("12100184624"),
    "ADT" : "2015-07-01 00:00:00",
    "TRC" : 377638,
    "F6" : 587580007,
    "F7" : "",
    "F8" : "",
    "F9" : 2101,
    "F10" : 58758,
    "F11" : "",
    "F12" : 2101,
    "F13" : "平安银行深圳分行营业部",
    "F14" : 2101,
    "F15" : NumberLong("12100184624"),
    "F16" : "广东格威律师事务所",
    "F17" : "",
    "F18" : "",
    "F19" : "",
    "F20" : "",
    "F21" : "",
    "AMT" : 70000,
    "F23" : 0,
    "F24" : 0,
    "CCY" : "RMB",
    "F26" : "",
    "F27" : "D",
    "F28" : "",
    "F29" : 1200,
    "F30" : "差旅费",
    "F31" : 866962.05,
    "F32" : 0,
    "F33" : "405920        CSWRMB  99101010200000",
    "F34" : "",
    "F35" : "",
    "F36" : "",
    "F37" : "B",
    "F38" : "WDR",
    "F39" : 0,
    "SEQ" : 1,
    "F41" : "N",
    "F42" : "",
    "F43" : "",
    "F44" : "2015-07-01 00:00:00",
    "F45" : 866962.05,
    "F46" : 866962.05,
    "F47" : "Y",
    "F48" : 1,
    "F49" : "",
    "F50" : "",
    "F51" : "",
    "F52" : "",
    "F53" : "CSW",
    "F54" : "",
    "F55" : "CD",
    "F56" : 0,
    "F57" : "",
    "F58" : "",
    "F59" : "",
    "F60" : "",
    "F61" : 0,
    "F62" : 0,
    "F63" : 0,
    "F64" : 11,
    "F65" : "",
    "F66" : "",
    "F67" : "",
    "F68" : 11,
    "F69" : NumberLong("12100184624"),
    "F70" : "N"
}

var insertion_rate = 150;  // number of inserts per second
var count=0;
var hdb = db.getSiblingDB("his");
hdb.tx0.ensureIndex({ACC:1, ADT:1, CCY:1, SEQ:1});
while(true){
    var start = new Date();
    var docs = [];
    for(var i=0;i<insertion_rate;i++){
        var obj = {};
        for(var k in sample){
            obj[k] = sample[k];
        }
        obj.ADT = today();
        obj.ACC = random(1000000)
        obj.SEQ = random(100);
        docs.push(obj);
    }
    hdb.tx0.insert(docs);
    count+= docs.length;
    print( count );
    var now = new Date();
    var elapsed = now.getTime() - start.getTime();
    if(elapsed < 1000){
        sleep( 1000 - elapsed );
    }

}
function today() {
    var d = new Date();
    var str = (d.getYear() +1900 )+"-";
    if(d.getMonth()+1 < 10) str+="0";
    str+=(d.getMonth()+1);
    str+="-";
    if(d.getDate()  < 10) str+="0";
    str+= d.getDate();
    return str +" 00:00:00";
}
function random(max){ return Math.round(Math.random()* max) }; 


