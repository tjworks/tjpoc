package com.mongodb.pinganpoc;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class MongoPocTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MongoPocTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        MongoPoc.init("mongodb://localhost:27017",null,null);
        return new TestSuite( MongoPocTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testBasic() throws Exception
    {    
        String res = MongoPoc.runQuery(
            Long.parseLong("11000253321701"),
            "2015-07-01",
            "2015-07-01",
            "RMB",
            null,
            null,
            null
        );    
        assertTrue( "Count should be 9 but got "+res, res!=null && res.indexOf("COUNT:9|")>0 );            
    }
    /**
     * Rigourous Test :-)
     */
    public void testTrace() throws Exception
    {    
        String res = MongoPoc.runQuery(
            Long.parseLong("11000253321701"),
            "2015-07-01",
            "2015-07-01",
            "RMB",
            null,
            null,
            new Long(159960)
        );    
        assertTrue( "Count should be 1 but got "+res, res!=null && res.indexOf("COUNT:1|")>0 );            
    }

     /**
     * Test query with amount condition
     */
    public void testAmount() throws Exception
    {    
        String res = MongoPoc.runQuery(
            new Long("272100088551"),
            "2015-07-01",
            "2015-07-01",
            "RMB",
            new Double(100000),
            new Double(200000),
            null
        );    
        assertTrue( "Count should be 84 but got "+res, res!=null && res.indexOf("COUNT:84|")>0 );            
    }


    public void testToday() throws Exception
    {    
        String res = MongoPoc.runQuery(
            new Long("272100088551"),
            "2015-07-01",
            "2015-08-30",
            "RMB",
            new Double(100000),
            new Double(200000),
            null
        );    
        //System.out.println(res);
        assertTrue( "Count should be 84 but got "+res, res!=null && res.indexOf("COUNT:84|")>0 );            
        assertTrue( "Expecting 2 queries: "+res, res!=null && res.indexOf("DUALQUERY")>0 );            
    }
}
