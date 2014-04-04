/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mycheapfriend;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *these are pulled right out of the second plan.  bad documentation right now, but this file sequentially follows the second plan doc.
 * @author michaelglass
 */
public class EmailInfoTest {

    public EmailInfoTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    //test fixture for testFromLogic
    private EmailInfo fromTestInstance()
    {
        EmailInfo instance = new EmailInfo();
        instance.setTo("new_account@mycheapfriend.com");
        instance.setContent("");
        return instance;
    }

    /**
     * 1.  Test of from parse logic, of class EmailInfo.
     */
    @Test
    public void testFromLogic() {
        System.out.println("testing from logic");
        EmailInfo instance = fromTestInstance();
        //valid from
        instance.setFrom("6462294051@mms.att.net");

        long expResult = 6462294051l;
        long result = instance.getPhone();
        assertEquals(expResult, result);
        assertEquals(TextMessage.NO_ERROR, instance.getErrorType());

        instance = fromTestInstance();
        //invalid from
        instance.setFrom("64622940521@mms.att.net");
        assertEquals(TextMessage.INVALID_SENDER, instance.getErrorType());

        instance = fromTestInstance();
        instance.setFrom("646229405a@gmail.com");
        assertEquals(TextMessage.INVALID_SENDER, instance.getErrorType());

        
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
    }

    //test fixture for testToLogic
    private EmailInfo toTestInstance()
    {
        EmailInfo instance = new EmailInfo();
        instance.setFrom("6462294051@mms.att.net");
        instance.setContent("report");
        return instance;
    }

    /**
     * test to:
     */
    @Test
    public void testToLogic() {
        System.out.println("testing to logic");
        EmailInfo instance = toTestInstance();
        instance.setTo("New_account@mycheapfriend.com");
        assertEquals(TextMessage.NO_ERROR, instance.getErrorType());

        instance = toTestInstance();
        instance.setTo("123abc@mycheapfriend.com");
        assertEquals(TextMessage.NO_ERROR, instance.getErrorType());
        assertEquals("123abc", instance.getPassword());
        instance = toTestInstance();
        instance.setTo("123@mycheapfriend.com");
        assertEquals(TextMessage.ERROR, instance.getType());
        instance = toTestInstance();
        instance.setTo("123abcd@mycheapfriend.com");
        assertEquals(TextMessage.ERROR, instance.getType());
        instance = toTestInstance();
        instance.setTo("123ab_@mycheapfriend.com");
        assertEquals(TextMessage.ERROR, instance.getType());
    }

    // test fixture
    private EmailInfo nonPasswordProtectedTestInstance()
    {
        EmailInfo instance = new EmailInfo();
        instance.setContent("13joifjl;;;;k");
        instance.setFrom("6462294051@mms.att.net");
        return instance;
    }
    /**
     * parsing body tests..
     */
    @Test
    public void testNonPasswordProtectedBody()
    {
        String[] to_addresses = {"new_account", "reset_pass", "unsubscribe", "resubscribe"};
        EmailInfo instance;
        for(String address : to_addresses)
        {
            instance = nonPasswordProtectedTestInstance();
            instance.setTo(address + "@mycheapfriend.com");
            assertEquals(TextMessage.NO_ERROR, instance.getErrorType());
        }

        instance = nonPasswordProtectedTestInstance();
        instance.setContent("yasf");
        instance.setTo("robot@mycheapfriend.com");
        assertEquals(TextMessage.ACCEPT_BILL, instance.getType());

        instance = nonPasswordProtectedTestInstance();
        instance.setContent("");
        instance.setTo("robot@mycheapfriend.com");
        assertEquals(TextMessage.ACCEPT_BILL, instance.getType());

        instance = nonPasswordProtectedTestInstance();
        instance.setContent("jfla;skjf;”");
        instance.setTo("robot@mycheapfriend.com");
        assertEquals(TextMessage.ERROR, instance.getType());
    }

    //fixture..
    private EmailInfo passwordInstance()
    {
        EmailInfo instance = new EmailInfo();
        instance.setTo("123abc@mycheapfriend.com");
        instance.setFrom("6462294051@mms.att.net");
        return instance;

    }

    /** these tests are for password protected actions */
    @Test
    public void testPasswordProtectedBody()
    {
        //from the docs...
        //1
        EmailInfo instance = passwordInstance();
        instance.setContent("report");
        assertEquals(TextMessage.REPORT_BILLS, instance.getType());
        //2
        instance = passwordInstance();
        instance.setContent("jon 6462294051");
        assertEquals(TextMessage.NEW_FRIEND, instance.getType());
        //3
        instance = passwordInstance();
        instance.setContent("60 robert Susie me");
        assertEquals(TextMessage.NEW_BILL, instance.getType());
        assertEquals(instance.getNumBills(), 2);
        //4
        instance = passwordInstance();
        instance.setContent("bob 60 60 susie 32 jon");
        assertEquals(TextMessage.NEW_BILL, instance.getType());
        assertEquals(instance.getNumBills(), 3);
        //5
        instance = passwordInstance();
        instance.setContent("me me 60");
        assertEquals(TextMessage.ERROR, instance.getType());
        //6
        instance = passwordInstance();
        instance.setContent("bob 60 65 bob me");
        assertEquals(TextMessage.ERROR, instance.getType());
        //7
        instance = passwordInstance();
        instance.setContent("bob 60 60 susie jon dough 75");
        assertEquals(TextMessage.ERROR, instance.getType());
        //8
        instance = passwordInstance();
        instance.setContent("me 6462294050 bob");
        assertEquals(TextMessage.ERROR, instance.getType());
        //9
        instance = passwordInstance();
        instance.setContent("#$25 bob");
        assertEquals(TextMessage.ERROR, instance.getType());
    }



}