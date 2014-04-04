/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author David
 */
import java.util.Date;

import java.util.Hashtable;
import java.util.Properties;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;


public class Main {
@EJB
private static EmployeeServiceRemote service;
  public static void main(String[] a) throws Exception {


    //Context ctx;
    

      //  Properties props=new Properties();
//props.setProperty("java.naming.factory.initial","com.sun.enterprise.naming.SerialInitContextFactory");

//props.put(Context.PROVIDER_URL,"localhost:3700");
       // ctx = new InitialContext(props);
InitialContext ic = new InitialContext();
        System.out.println("runing!!!!!!!!!!!");
    // Context compEnv = (Context) new InitialContext().lookup("java:comp/env");

    // service = (HelloService)new InitialContext().lookup("java:comp/env/ejb/HelloService");
   service = (EmployeeServiceRemote) ic.lookup("ejb.employee");
service.doAction();
System.out.println("runing!!!!!!!!!!!");



    service.doAction();
    //while(true);


  }

}

