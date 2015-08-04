/**
 *
 */

package com.mongodb.pinganpoc;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;

/**
 * MongoDB PoC client for Pingan
 *
 * See usageMessage() for command line usage
 * 
 * For direct invocation, 
 *
 *
 * @author TJ<jianfa.tang@mongodb.com>
 */
public class MongoPoc   {
    /**
    * @param account  Required. Account number, ONLY SUPPORT digits
    * @param from_date  Required. Date range, inclusive, must be in the form of YYYY-MM-DD, i.e., 2015-07-02
    * @param to_date Date Required. range inclusive
    * @param ccy  Optional. Currency, default to RMB
    * @param min_amount Optional. Minimum amount. must be number only, inclusive
    * @param max_amount Optional. Maximum transaction amount, must be number. Inclusive
    * @param trace Optional. HOST_TRACE must be number.
    */
    
    public static String runQuery(Long account, String from_date, String to_date,
        String ccy, Double min_amount, Double max_amount, Long trace) throws Exception     
    {
        
        int indx = ((int) (Math.random() * 100) ) % dbs.length;
        MongoDatabase db = dbs[indx];
        if(db == null){
            throw new RuntimeException("Null db at index " + indx);
        }
        String ret = "";
        if(hasToday(to_date)){
            MongoCollection todayColl = db.getCollection( colname+"0" );
            System.out.println("### Running query against both today and history collections ###");
            ret  += runQuery(account, from_date, to_date, ccy, min_amount, max_amount, trace, todayColl);   
            ret  += "\nDUALQUERY\n";
        }
        MongoCollection hisColl = db.getCollection( colname );
        ret +=runQuery(account, from_date, to_date, ccy, min_amount, max_amount, trace, hisColl);           
        return ret;
    }    

    private static String runQuery(Long account, String from_date, String to_date,
        String ccy, Double min_amount, Double max_amount, Long trace, MongoCollection coll) throws Exception     
    {
        // validations
        if(account == null || from_date == null || to_date ==null){
            System.out.println("Invalid parameters. Must provide account, from and to date");
            usageMessage();
            throw new Exception("Invalid parameters. Must provide account, from and to date");
        }
        if(ccy == null) ccy = "RMB";
        if(from_date.length()!=10 || to_date.length()!=10){
            System.out.println("Invalid date format, must be in yyyy-mm-dd: "+ from_date+" ]");
            usageMessage();
            throw new Exception("Invalid date format, must be in yyyy-mm-dd");
        }

        from_date += " 00:00:00";
        to_date += " 00:00:00";
                
        // build query object
        List ops = new ArrayList(10);
        ops.add( eq("ACC", account));
        ops.add(and(gte("ADT", from_date), lte("ADT", to_date)));
        ops.add(eq("CCY", ccy)); 
        if(trace !=null ) ops.add(eq("TRC",  trace));
        if(min_amount!=null) ops.add(gte("AMT",  min_amount )); 
        if(max_amount!=null) ops.add(lte("AMT",  max_amount ));         
        Bson query = and( ops ); 
        // return fields
        Bson projections = fields(include("ACC", "ADT", "TTM"), excludeId());
        
        Date start = new Date();
        MongoCursor cursor =  null;
        int count=0;    

        try {
            // execute the query
            cursor = coll.find(query)
                        .projection(projections )
                        .noCursorTimeout(true)
                        .limit(25000)
                        .iterator();
            while(cursor.hasNext()) {                    
               count++;   
               Object o = cursor.next();                                   
           }
                
        } catch(Exception e){
            System.err.println("Exception reading cursor "+e);            
        } finally {            
            cursor.close();
        }
        Date  now = new Date();      
        String s = "Q: "+ account +"|"+ from_date+"|"+ to_date+"|trc:"+ trace+"|min:"+min_amount+"|max:"+max_amount+"|COUNT:"+ count+"|MILLIS:"+ (now.getTime()-start.getTime()) ;
        System.out.println(s);
        return s;
    }

    private static boolean hasToday(String to_date){
        if(to_date == null) return false;
        Date today = new Date();
        String str =( today.getYear() + 1900) +"-";
        if(today.getMonth()+1 < 10) str+="0";
        str+= (today.getMonth()+1);
        str+="-";
        if(today.getDate() <10) str+="0";
        str+= (today.getDate());
        //System.out.println("----***** "+ str + " ***** ");
        return to_date.compareTo(str) >=0;
    }
    private static void usageMessage()
    {
         System.out.println("POC Loader");
         System.out.println("java -jar poc.jar com.mongodb.pinganpoc.MongoPoc [-p key=value]");
         System.out.println("Options:");
         System.out.println("  -p key=value: Specify a property value");
         System.out.println("  ");
         System.out.println("Supported keys: ");
         System.out.println("   account, currency, from_date, to_date, min_amount, max_amount, trace ");
         System.out.println("   host, dbname, collection");
         System.out.println();
    }
   
    public static void init(String url, String dbname, String colname){
        String[] urls = { url };
        init(urls, dbname, colname);
    }
    public static void init(String[] urls, String dbname, String cname){
        System.err.println("Initializing version 0.03");
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);
        
        if(dbname == null) dbname = "histxn";
        if(cname == null) cname = "tx";
        colname = cname;        
        dbs = new MongoDatabase[urls.length];
        for(int i=0;i<urls.length;i++){
            String url = urls[i];
            if(url == null) url = "mongodb://localhost:27017";
            
            MongoClient mongoClient = new MongoClient( new MongoClientURI(url));
            dbs[i] = mongoClient.getDatabase(dbname);
            System.out.println("Mongo URL: "+ url+"/"+dbname+"."+ colname +" "+ dbs[i]);       
        }        
        initialized = true;
    }    
    public static void runQuery(Map<String,String> props) throws Exception {
            
        if(!initialized ) 
            init(props.get("url"), props.get("db"), props.get("collection"));

        runQuery(props.get("account"), 
                 props.get("from_date"),
                 props.get("to_date"),
                 props.get("ccy"),
                 props.get("min_amount"),
                 props.get("max_amount"),
                 props.get("trace")
        );
        
    }
    public static String runQuery(String account, String from_date, String to_date, 
                    String ccy, 
                    String min_amount, String max_amount, String trace) throws Exception
    {
            if(account == null) { 
                usageMessage(); 
                throw new Exception("Account not specified");
            }
            Long acct = Long.parseLong(account);
            Double min= null;
            Double max = null;
            if(min_amount!=null) min = Double.parseDouble(min_amount);
            if(max_amount!=null) max = Double.parseDouble(max_amount);
            Long trc = null ;
            if(trace!=null) trc = Long.parseLong(trace);
            return runQuery(acct, from_date, to_date, ccy, min, max, trc);
    }

    
    private static MongoDatabase[] dbs = null;
    private  MongoCollection<Document> hisColl = null;
    private  MongoCollection<Document> todayColl = null;
    private static boolean initialized = false;
    private static String colname = null;

    public static void main(String[] args){
        System.out.println("POC client");
        Map<String,String> props = new HashMap<String,String>();
        try {            

            for(int argindex=0;argindex<args.length;argindex++){
                
                if(args[argindex]!=null && args[argindex].compareTo("-p")==0)
                {
                    argindex++;
                    if (argindex>=args.length)
                    {
                        usageMessage();
                        System.exit(0);
                    }
                    int eq=args[argindex].indexOf('=');
                   if (eq<0)
                   {
                        usageMessage();
                        System.exit(0);
                   }
                   
                    String name=args[argindex].substring(0,eq);
                    String value=args[argindex].substring(eq+1);
                    props.put(name,value);
                    //System.out.println("["+name+"]=["+value+"]");                    
                }
            }
            //if(props.get("account") ==null) throw new Exception("Account number must be digital only");
            //props.put( "account", Long.parseLong(""+props.get("account")));
            runQuery(props);
        } catch (Exception ex) {        
            System.out.println(ex);
            //ex.printStackTrace();            
        }   
    }
}
