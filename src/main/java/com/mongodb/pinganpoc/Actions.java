package com.mongodb.pinganpoc;
import lrapi.lr;
import com.mongodb.pinganpoc.MongoPoc;
import java.util.Date;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import com.mongodb.util.JSON;
import com.mongodb.DBObject;

public class Actions
{ 
    private static String dataPath = "E:\\sequoiadb\\loadrunner\\querydata";
    
    private static int counter = 0;
    private static Test[] tests = null;
    private static class Test {
        Long account;
        String from_date;
        String to_date;
        String ccy;
        Double min_amount;
        Double max_amount;
        Long trace;
    }
    public int init() throws Throwable {
        String[] urls = {"mongodb://10.14.200.74:25017","mongodb://10.14.200.78:25017","mongodb://10.14.200.79:25017","mongodb://10.14.200.80:25017","mongodb://10.14.200.81:25017","mongodb://10.14.200.82:25017"};

        MongoPoc.init(urls, "histxn", "tx");
        String year = "2014"; // hardcode to 2014 for now

        // get list of query documents
        List<Map<String,String>> queryDocs = createQueryMap(year, dataPath);  

        // initialize 100 accounts
        //String account_str= "11000253321703,11000253321701,9262105010143,9262105009182,7512100041471,7042100011631,7042100011563,7042100011266,7042100010635,7012100093602,7012100093423,11000253349501,7012100093404,7012100093263,7012100093196,7012100093091,7012100091081,7012100090865,7012100087493,5512100076426,5012100056642,5012100055951,4512100000242,4012100006111,2000016222425,2000015892313,2000015513233,2000015422384,2000014886481,2000014871506,2000014612333,2000013736581,2000013721514,2000012563833,2000009214932,2000009210381,2000009147105,2000009079641,2000009022921,2000008850484,2000008817685,2000008810455,2000008764494,2000008603885,2000008594015,2000008405605,2000008392002,2000008323595,2000008323563,2000008323301,2000008323023,2000008322715,2000008317681,2000008316431,2000008267781,2000001018235,2000003561934,2000001774511,2000001585735,2000001421606,2000001411116,2000000764502,2000000754076,2000000711476,512100061211,512100061032,512100060916,512100060823,512100060775,512100060513,512100058113,2000008603444,502100259744,502100175427,502100072090,462100351751,462100351601,462100351585,462100349295,462100348856,462100348706,462100348601,462100348521,462100348514,462100345454,422100039069,462100249631,462100218458,452100266621,452100266615,452100265156,452100262606,452100063058,432100322120,432100316901,432100315962,422100048813,422100048483,422100047316,412100104128";
        String start_date = year+ "-01-01";
        String[] enddates = {year+"-01-31", year+"-03-31", year+"-12-31"}; // 1, 3, 12 month        
        int[] ratio = {6,3,1}; // 60% of 1 month, 30% of 3 month, 10% of 12 month

        // String[] accounts_ids = account_str.split(",");
        // List<Long> accounts = new ArrayList<Long>(queryDocs.size());
        // for(int i=0;i<accounts_ids.length;i++)
        //     accounts.add( new Long(accounts_ids[i]));
        System.err.println("Loaded "+ queryDocs.size()+" queries for year "+year);
        int totalQueries = queryDocs.size();
        tests = new Test[totalQueries * 3];
        for(int indx=0;indx< totalQueries*3 ; indx++){
                Map<String, String> doc = queryDocs.get(indx % totalQueries);
                Test test = new Test();
                test.account = new Long(  doc.get("host_acc"));
                test.from_date = start_date;
                test.ccy = doc.get("ccy");

                if(indx%10 < ratio[0] ) 
                    test.to_date = enddates[0]; 
                else if(indx%10 < ratio[0]+ ratio[1] )
                    test.to_date = enddates[1]; 
                else
                    test.to_date = enddates[2]; 
                if(doc.get("trace")!=null && doc.get("trace").trim().length()>0)
                    test.trace = new Long(doc.get("trace"));
                if(doc.get("minamount")!=null) // must be number
                    test.min_amount = new Double(doc.get("minamount"));
                if(doc.get("maxamount")!=null)
                    test.min_amount = new Double(doc.get("maxamount"));

                tests[indx] = test; 
        }
        return 0;
    }//end of init

    public int action() throws Throwable {
        int testno = 0;
        synchronized(this.getClass()) {
            testno = counter++;
        }
        testno = testno % tests.length ;

        Test test = tests[testno];        
        if(test == null) return 0;
        lr.start_transaction("query");
        String res  = MongoPoc.runQuery(
            test.account,
            test.from_date,
            test.to_date,
            test.ccy,
            test.min_amount,
            test.max_amount,
            test.trace
        );
        //System.out.println(res);
        // String res = MongoPoc.runQuery(
        //     new Long("272100088551"),       // account
        //     "2015-07-01",                   // from date
        //     "2015-07-01",                   // to date
        //     "RMB",                          // ccy
        //     new Double(100000),             // min amount
        //     new Double(200000),             // max amount
        //     null                            // host trace
        // );         
        lr.end_transaction("query", lr.AUTO);
        lr.message("RESULT: "+ res);
        return 0;
    }//end of action
    public int end() throws Throwable {
        return 0;
    }//end of end

 
    public static List<Map<String, String>> createQueryMap (String year, String dataPath) throws IOException{
        
        //List<BSONObject> bsonList = new ArrayList<BSONObject>();
        
        List<Map<String,String>> queryDocs = new ArrayList<Map<String,String>>();

        String fileName = dataPath + "/query_" + year;
        File file = new File(fileName);
        FileReader fileReader = new FileReader(file);
        BufferedReader br = new BufferedReader(fileReader);
        String temp = null;

        do{
            temp = br.readLine ();
            try{
                if (temp != null){
                    if (! temp.equals("")){
                        Object record = JSON.parse(temp);
                        //bsonList.add(record);
                        queryDocs.add((Map<String, String>)((DBObject)record).toMap());
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            
        }while(temp != null);
        
        fileReader.close();
        br.close();
        
        
        // // random a num
        // int i = (int) (Math.random() * bsonList.size());
        // BSONObject record = bsonList.get(i);
        
        return queryDocs;
    }
    
    public static void main(String[] args) throws Throwable{
        if(args!=null && args.length>0)
            dataPath = args[0];
        Actions action = new Actions();
        action.init();
        Date start = new Date();
        for(int i=0;i<1000;i++)
            action.action();
        Date end = new Date();
        System.err.println("Time used: "+ (end.getTime()- start.getTime()) +"ms");
    }
}
