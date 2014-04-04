/*
 * Handles the login process.
 */

package mycheapfriend;

import java.io.IOException;
import java.io.PrintWriter;
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
public class LoginHandler extends HttpServlet {

    @EJB (mappedName="ejb.AdminLoginBean")
    AdminLoginRemote loginSession;

    @EJB (mappedName="ejb.UserObjFacade")
    UserObjFacadeRemote userFacade;

   
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
 
         String phone = request.getParameter("Phone");
         String password = request.getParameter("Password");
        long test_phone;
                 
        try {
            if((phone.length() == 0) || (password.length() == 0)){
            out.println("<html>");
            out.println("<head>");
            out.println("<title>LoginHandler</title>");  
            out.println("</head>");
            out.println("<body>");
            out.println("<center>");
            out.println("MyCheapFriend Administrator Console");
            out.println("<br>");
            out.println("<b><font color=red>Please Enter valid Phone number and Password in the Appropriate Fields</font></b> ");
            out.println("<br>");
            out.println("<form name=\"login\" action=\"LoginHandler\" method=\"POST\">");
            out.println("Enter your Phone Number: ");
            out.println("<input type=\"text\" name=\"Phone\" MAXLENGTH = \"10\" size=\"10\">");
            out.println("<br>Enter your Password:");
            out.println("<input type=\"password\" name=\"Password\" MAXLENGTH = \"6\" size=\"6\">");
            out.println("<br>");
            out.println("<input type=\"submit\" value=\"Submit\">");
            out.println("</form>");
            out.println("</center>");
            out.println("</body>");
            out.println("</html>");
            }
            else {
                try{
                test_phone = Long.valueOf(phone);
                }
                catch(NumberFormatException e){
                    test_phone = 0L;
                }
                
               
                Boolean answer;
                long David = 9175200211L;
                long Waseem = 6467034172L;
                long Michael = 6462294050L;
                long Shaoqing = 3472446971L;
             
                    
                    if((test_phone == David) || (test_phone == Waseem) 
                        || (test_phone == Michael) || (test_phone == Shaoqing)){
                        answer = loginSession.check_password(test_phone, password);
                        if(answer == Boolean.TRUE){
                                HttpSession session = request.getSession(true);
                                
                                session.setAttribute("phone", test_phone);
                                session.setAttribute("password", password);
                                out.println("<html>");
                                out.println("<head>");
                                out.println("<title>LoginHandler</title>");
                                out.println("</head>");
                                out.println("<body onLoad=\"parent.location='Administrator'\">");
                                out.println("</html>");
                        }
                        else if(answer == Boolean.FALSE){
                            out.println("<html>");
                            out.println("<head>");
                            out.println("<title>LoginHandler</title>");
                            out.println("</head>");
                            out.println("<body>");
                            out.println("<center>");
                            out.println("MyCheapFriend Administrator Console");
                            out.println("<br>");
                            out.println("<b><font color=red>Invalid Password. Try Again!</font></b> ");
                            out.println("<br>");
                            out.println("<form name=\"login\" action=\"LoginHandler\" method=\"POST\">");
                            out.println("Enter your Phone Number: ");
                            out.println("<input type=\"text\" name=\"Phone\" MAXLENGTH = \"10\" size=\"10\">");
                            out.println("<br>Enter your Password:");
                            out.println("<input type=\"password\" name=\"Password\" MAXLENGTH = \"6\" size=\"6\">");
                            out.println("<br>");
                            out.println("<input type=\"submit\" value=\"Submit\">");
                            out.println("</form>");
                            out.println("</center>");
                            out.println("</body>");
                            out.println("</html>");
                        }
                    }
                    else{
                        out.println("<html>");
                        out.println("<head>");
                        out.println("<title>LoginHandler</title>");
                        out.println("</head>");
                        out.println("<body>");
                        out.println("<center>");
                        out.println("MyCheapFriend Administrator Console");
                        out.println("<br>");
                        out.println("<b><font color=red>Invalid PhoneNumber. Try Again!</font></b> ");
                        out.println("<br>");
                        out.println("<form name=\"login\" action=\"LoginHandler\" method=\"POST\">");
                        out.println("Enter your Phone Number: ");
                        out.println("<input type=\"text\" name=\"Phone\" MAXLENGTH = \"10\" size=\"10\">");
                        out.println("<br>Enter your Password:");
                        out.println("<input type=\"password\" name=\"Password\" MAXLENGTH = \"6\" size=\"6\">");
                        out.println("<br>");
                        out.println("<input type=\"submit\" value=\"Submit\">");
                        out.println("</form>");
                        out.println("</center>");
                        out.println("</body>");
                        out.println("</html>");
                    }
                }           
            
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
