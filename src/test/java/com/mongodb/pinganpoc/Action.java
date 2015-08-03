import lrapi.lr;
import com.mongodb.pinganpoc.MongoPoc;
public class Actions
{ 
    public int init() throws Throwable {
        MongoPoc.init("mongodb://localhost:27017", "histxn", "tx");
        return 0;
    }//end of init
    public int action() throws Throwable {
        lr.start_transaction("query");
        //todo: randomly choose values based on the scenario
        String res = MongoPoc.runQuery(
            new Long("272100088551"),       // account
            "2015-07-01",                   // from date
            "2015-07-01",                   // to date
            "RMB",                          // ccy
            new Double(100000),             // min amount
            new Double(200000),             // max amount
            null                            // host trace
        );         
        lr.end_transaction("query", lr.AUTO);
        lr.message("RESULT: "+ res);
        return 0;
    }//end of action
    public int end() throws Throwable {
        return 0;
    }//end of end
}
