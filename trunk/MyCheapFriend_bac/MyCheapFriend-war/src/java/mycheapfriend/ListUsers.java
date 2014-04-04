/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mycheapfriend;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Waseem Ilahi
 */
public class ListUsers extends HttpServlet {

    UserObjFacadeRemote userFacade;
    InitialContext context;
    @EJB(mappedName="ejb.PollerBean")
    private PollerRemote poller;
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        System.setProperty("org.omg.CORBA.ORBInitialHost", "localhost");
        try {
            context = new InitialContext();
            userFacade = (UserObjFacadeRemote) context.lookup("ejb.UserObjFacade");
            //poller.startTimer();
        } catch (NamingException ex) {
            Logger.getLogger(ListUsers.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            
            out.println("<html>");
            out.println("<head>");
            out.println("<title>List Users</title>");
            out.println("</head>");
            out.println("<body>");
            /*out.println("<a href='CreateUser'>Create a User.</a>");
            out.println("<br>");
            out.println("<a href='DeleteUsers'>Delete all Users.</a>");*/
            out.println("<h1>List User Info.</h1>");

            List<UserObj> users = userFacade.findAll();

            out.println("<h3>" + users.size() + " Users:</h3>");
            out.println("<ul>");
            for(UserObj u : users)
            {
                out.println("<li> Phone#" + u.getPhone() + "</br>");
                out.print("Disabled: ");
                if(u.isDisabled())
                    out.println("true <a href=\"enable?id="+u.getPhone()+"\">[enable]</a></br>");
                else
                    out.println("false <a href=\"disable?id="+u.getPhone()+"\">[disable]</a></br>");

                //out.println("<br>" + u.getSalt() + "</br>");
                out.println("<br>Password: " + u.getPassword() + "</br>");
                out.println("<br>Domain: " + u.getEmail_domain() + "</li><br>");
                
                List<Friend> fs = u.getFriends();

                if(fs != null)
                {

                    if(!fs.isEmpty())
                        out.println("<br>Friends:<br>");

                    out.println("<ul>");
                     for(Friend f : fs)
                    {
                        out.println("<li>"+f.getNickname()+" => "+f.getFriend().getPhone()+"</li>");
                    }
                    out.println("</ul>");
                }

                 List<Bill> debts = u.getDebts();

                if(debts != null)
                {
                    if(!debts.isEmpty())
                        out.println("Debts:<br>");

                    out.println("<ul>");
                     for(Bill f : debts)
                    {
                        out.println("<li>"+f.getAmount()/100.0 +" => "+f.getLender().getPhone()+"</li>");
                    }
                    out.println("</ul>");
                }    

                 List<Bill> assets = u.getAssets();

                if(assets != null)
                {
                    if(!assets.isEmpty())
                        out.println("Assets:<br>");

                    out.println("<ul>");
                     for(Bill f : assets)
                    {
                        out.println("<li>"+f.getAmount()/100.0 +" => "+f.getBorrower().getPhone()+"</li>");
                    }
                    out.println("</ul><br>");
                }

            }
            out.println("</ul>");
            out.println("<br>");




            out.println("<a href='ListUsers'>Refresh</a>");
            out.println("<br>");
            if(poller.testStarted()) {
                out.print("The server is running.");
                out.println("<a href='StopService' onclick=\"System.out.println(\"click\");\">[stop]</a>");
            }
            else {
                out.print("The server is stopped.");
                out.println("<a href='StartService'>[start]</a>");
            }
            out.println("<br>");
            out.println("<a href='LoginUser'>Logout</a>");
            out.println("<br>");
            out.println("</body>");
            out.println("</html>");
            
        } finally { 
            out.close();
        }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
