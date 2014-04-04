/*
 * The main Servlet, that lists all the users and
 * provides the option for disabling/enabling a user and
 * also starting/stoping the server.
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
import javax.servlet.http.HttpSession;

/**
 *
 * @author Waseem Ilahi
 */
public class ListUsers extends HttpServlet {

    UserObjFacadeRemote userFacade;
    InitialContext context;
    @EJB(mappedName="ejb.PollerBean")
    private PollerRemote poller;
    @EJB (mappedName="ejb.AdminLoginBean")
    AdminLoginRemote loginSession;
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
            
        } catch (NamingException ex) {
            Logger.getLogger(ListUsers.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            HttpSession session = request.getSession(false);
            if(session == null){
                out.println("<body onLoad=\"parent.location='LoginUser'\">");
                return;
            }
            if(!loginSession.check_password((Long)session.getAttribute("phone"),(String)session.getAttribute("password"))){
                out.println("<body onLoad=\"parent.location='LoginUser'\">");
                return;
            }
            if(request.getParameter("id") == null) {
                out.println("<html>");
                out.println("<head>");
                out.println("<title>List Users</title>");
                out.println("</head>");
                out.println("<body>");
                out.println("<h1>List User Info.</h1>");

                List<UserObj> users = userFacade.findAll();

                out.println("<h3>" + users.size() + " Users:</h3>");

                out.println("<table border='1' cellpadding='5'>");
                out.println("<tr>");
                out.println("<th align='center'>Phone#</th>");
                out.println("<th align='center'>Password</th>");
                out.println("<th align='center'>Domain</th>");
                out.println("<th align='center'>Active</th>");
                out.println("<th align='center'>Subscribed</th>");
                out.println("<th align='center'>Disabled</th>");
                out.println("</tr>");

                for(UserObj u : users)
                {
                    out.println("<tr>");
                    out.println("<td align='center'> <a href=\"ListUsers?id=" + u.getPhone() + "\">" + u.getPhone() + "</a></td>");
                    out.println("<td align='center'>" + u.getPassword() + "</td>");
                    out.println("<td align='center'>" + u.getEmail_domain() + "</td>");
                    out.println("<td align='center'>" + u.isActive() + "</td>");
                    out.println("<td align='center'>" + !u.isUnsubscribe() + "</td>");
                    if(u.isDisabled())
                        out.println("<td align='center'>true <a href=\"enable?id=" + u.getPhone() + "\">[enable]</a></td>");
                    else
                        out.println("<td align='center'>false <a href=\"disable?id=" + u.getPhone() + "\">[disable]</a></td>");
                    out.println("</tr>");
                }

                out.println("</table><br>");

                out.println("<a href='ListUsers'>Refresh</a>");

            }
            else {
                UserObj user = userFacade.find(Long.parseLong(request.getParameter("id")));
                if(user == null)
                    out.println("<body onLoad=\"parent.location='ListUsers'\">");
                else {
                        out.println("<html>");
                        out.println("<head>");
                        out.println("<title>User Info</title>");
                        out.println("</head>");
                        out.println("<body>");
                        out.println("<h1>Detailed Info of " + request.getParameter("id") + ".</h1>");

                        out.println("<h3>Basic Info:</h3>");
                        out.println("<table border='1' cellpadding='5'>");
                        out.println("<tr>");
                        out.println("<th align='center'>Phone#</th>");
                        out.println("<th align='center'>Password</th>");
                        out.println("<th align='center'>Domain</th>");
                        out.println("<th align='center'>Active</th>");
                        out.println("<th align='center'>Subscribed</th>");
                        out.println("<th align='center'>Disabled</th>");
                        out.println("</tr>");

                        out.println("<tr>");
                        out.println("<td align='center'>" + user.getPhone() + "</td>");
                        out.println("<td align='center'>" + user.getPassword() + "</td>");
                        out.println("<td align='center'>" + user.getEmail_domain() + "</td>");
                        out.println("<td align='center'>" + user.isActive() + "</td>");
                        out.println("<td align='center'>" + !user.isUnsubscribe() + "</td>");
                        if(user.isDisabled())
                            out.println("<td align='center'>true <a href=\"enable?id=" + user.getPhone() + "\">[enable]</a></td>");
                        else
                            out.println("<td align='center'>false <a href=\"disable?id=" + user.getPhone() + "\">[disable]</a></td>");
                        out.println("</tr>");
                        out.println("</table>");

                        out.println("<h3>Friends:</h3>");
                        out.println("<table border='1' cellpadding='5'>");
                        out.println("<tr>");
                        out.println("<th align='center'>Phone#</th>");
                        out.println("<th align='center'>Nickname</th>");
                        out.println("</tr>");

                        List<Friend> fs = user.getFriends();

                        if(fs != null && !fs.isEmpty()) {
                             for(Friend f : fs) {
                                out.println("<tr>");
                                out.println("<td align='center'>" + f.getFriend().getPhone()+"</td>");
                                out.println("<td align='center'>" + f.getNickname()+"</td>");
                                out.println("</tr>");
                            }
                        }

                        out.println("</table><br>");

                        out.println("<h3>Debts:</h3>");
                        out.println("<table border='1' cellpadding='5'>");
                        out.println("<tr>");
                        out.println("<th align='center'>Lender</th>");
                        out.println("<th align='center'>Amount</th>");
                        out.println("<th align='center'>Approved</th>");
                        out.println("<th align='center'>Paid</th>");
                        out.println("<th align='center'>Date</th>");
                        out.println("</tr>");

                        List<Bill> debts = user.getDebts();

                        if(debts != null && !debts.isEmpty())
                        {
                             for(Bill f : debts)
                            {
                                out.println("<tr>");
                                out.println("<td align='center'>" + f.getLender().getPhone() + "</td>");
                                out.println("<td align='center'>" + f.getAmount()/100.0 + "</td>");
                                out.println("<td align='center'>" + f.getApproved() + "</td>");
                                out.println("<td align='center'>" + f.getPaid() + "</td>");
                                out.println("<td align='center'>" + f.getTimeCreated() + "</td>");
                                out.println("</tr>");
                            }
                        }

                        out.println("</table><br>");

                        out.println("<h3>Assets:</h3>");
                        out.println("<table border='1' cellpadding='5'>");
                        out.println("<tr>");
                        out.println("<th align='center'>Borrower</th>");
                        out.println("<th align='center'>Amount</th>");
                        out.println("<th align='center'>Approved</th>");
                        out.println("<th align='center'>Paid</th>");
                        out.println("<th align='center'>Date</th>");
                        out.println("</tr>");

                        List<Bill> assets = user.getAssets();

                        if(assets != null && !assets.isEmpty())
                        {

                             for(Bill f : assets)
                            {
                                out.println("<tr>");
                                out.println("<td align='center'>" + f.getBorrower().getPhone() + "</td>");
                                out.println("<td align='center'>" + f.getAmount()/100.0 + "</td>");
                                out.println("<td align='center'>" + f.getApproved() + "</td>");
                                out.println("<td align='center'>" + f.getPaid() + "</td>");
                                out.println("<td align='center'>" + f.getTimeCreated() + "</td>");
                                out.println("</tr>");
                            }
                        }
                        out.println("</table><br>");
                        out.println("<a href='ListUsers?id="+user.getPhone()+"'>Refresh</a>");
                        out.println("<a href='ListUsers'>Show All</a>");
                }
            }
            if(poller.testStarted()) {
                out.print("<br>The server is running.");
                out.println("<a href='StopService' onclick=\"System.out.println(\"click\");\">[stop]</a>");
            }
            else {
                out.print("<br>The server is stopped.");
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
