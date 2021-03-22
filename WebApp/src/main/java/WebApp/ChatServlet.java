/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WebApp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author adamt
 */
@WebServlet(name = "ChatServlet", urlPatterns = {"/ChatServlet"})
public class ChatServlet extends HttpServlet {
    
    private static final int MESSAGE_COUNT_SPAN = 10;
    
    private static final String USERNAME_ARG = "username";
    private static final String MESSAGE_ARG = "message";
    private static final String REFRESH_ARG = "refresh";
    private static final String LOGOUT_ARG = "logout";
    
    private static final String CHAT_MESSAGES_ARG = "chatMessages";
    private static final String ACTIVE_USERS_ARG = "activeUsers";

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter(USERNAME_ARG) != null) {
            // it's login
            processLogin(request);
        } else if (request.getParameter(MESSAGE_ARG) != null) {
            // it's message
            processMessage(request);
        } else if (request.getParameter(REFRESH_ARG) != null) {
            // resend current page
        } else if (request.getParameter(LOGOUT_ARG) != null) {
            processLogout(request);
        }
        fillResponse(response, (String) request.getSession().getAttribute(USERNAME_ARG));
    }
    
    private void processLogin(HttpServletRequest request) {        
        String username = request.getParameter(USERNAME_ARG);
        List<String> activeUsers = getActiveUsers();
        
        if (activeUsers.contains(username)) {
            // username is taken
        } else {
            activeUsers.add(username);
            request.getSession().setAttribute(USERNAME_ARG, username);
        }
        
        saveActiveUsers(activeUsers);
    }
    
    private void processMessage(HttpServletRequest request) {
        String message = request.getParameter(MESSAGE_ARG);
        String username = (String) request.getSession().getAttribute(USERNAME_ARG);
        List<ChatMessage> chatMessages = getAllMessages();
        
        chatMessages.add(new ChatMessage(username,message));
        if (chatMessages.size() > MESSAGE_COUNT_SPAN) {
            chatMessages.remove(0);
        }
        
        saveAllMessages(chatMessages);
    }
    
    private void processLogout(HttpServletRequest request) {
        String username = (String) request.getSession().getAttribute(USERNAME_ARG);
        List<String> activeUsers = getActiveUsers();
        activeUsers.remove(username);
        saveActiveUsers(activeUsers);
        request.getSession().removeAttribute(USERNAME_ARG);
    }
    
    private void fillResponse(HttpServletResponse response, String username) throws IOException {
        List<ChatMessage> chatMessages = (List<ChatMessage>) getServletContext().getAttribute(CHAT_MESSAGES_ARG);
        
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ChatServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>VravServlet</h1>");
            
            if (username != null && username.length() > 0){
                // active users part
                out.println("Active users:");
                out.println("<ul>");
                getActiveUsers().forEach(user -> out.println("<li>"+user+"</li>"));
                out.println("</ul>");
                out.println("You are logged in as " + username + "<br><br>");
                
                //  messages part                
                out.println("<textarea name=\"chatArea\" rows=10 cols=40 readonly>");
                if (chatMessages != null) {
                    chatMessages.forEach(msg -> {
                        out.println(msg.getUsername() + ": " + msg.getText());
                    });
                }
                out.println("</textarea>");
                
                // action buttons part
                out.println("<form action=\"ChatServlet\" method=\"post\">Send message: <input type=\"text\" name=\"message\"><input type=\"submit\" value=\"send\"></form><br>");
                out.println("<form action=\"ChatServlet\" method=\"post\"><input type=\"submit\" value=\"refresh\"><input type=\"text\" name=\"refresh\" style=\"visibility:hidden;\"></form><br>");
                out.println("<form action=\"ChatServlet\" method=\"post\"><input type=\"submit\" value=\"logout\"><input type=\"text\" name=\"logout\" style=\"visibility:hidden;\"></form><br>");
            } else {
                out.println("There's a problem with your login process.");
                out.println("Please check following possibilities:");
                out.println("<ul><li>You are not signed in</li><li>Your username is empty</li><li>Your username is already taken by another user</li></ul>");
                out.println("<a href=\"index.html\">Login screen</a>");
            }
            out.println("</body>");
            out.println("</html>");
        }
    }
    
    private List<ChatMessage> getAllMessages() {
        List<ChatMessage> chatMessages = (List<ChatMessage>) getServletContext().getAttribute(CHAT_MESSAGES_ARG);
        chatMessages = chatMessages != null ? chatMessages : new ArrayList<>();
        return chatMessages;
    }
    
    private void saveAllMessages(List<ChatMessage> allMessages) {
        getServletContext().setAttribute(CHAT_MESSAGES_ARG,allMessages);
    }
    
    private List<String> getActiveUsers() {
        List<String> activeUsers = (List<String>) getServletContext().getAttribute(ACTIVE_USERS_ARG);
        activeUsers = activeUsers != null ? activeUsers : new ArrayList<>();
        return activeUsers;
    }
    
    private void saveActiveUsers(List<String> activeUsers) {
        getServletContext().setAttribute(ACTIVE_USERS_ARG, activeUsers);        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
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
     *
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
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
