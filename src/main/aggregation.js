
var started = new Date();
var hdb = db.getSiblingDB("his");
var sdb = db.getSiblingDB("shis");
sdb.tx.drop();
var count=0;
var bulk = sdb.tx.initializeOrderedBulkOp();
hdb.tx.find().sort({ACC:1, ADT:1, CCY:1, SEQ:1}).forEach(function(item){
    bulk.insert(item);
    count++;
    if(count%1000 == 0){
        bulk.execute();        
        bulk = sdb.tx.initializeOrderedBulkOp();        
        elapsed = Math.round( (new Date().getTime() - started.getTime())/1000);
        print("Inserted "+ count + " Elapsed(s) "+ elapsed );
    }       
});
bulk.execute();
sdb.tx.ensureIndex({ACC:1, ADT:1, CCY:1, SEQ:1});



