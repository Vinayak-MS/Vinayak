import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import jakarta.servlet.http.HttpSession;

@WebServlet("/VoteServlet")
public class VoteServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/votingsystem";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Msvinayak@2002";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String message = request.getParameter("message"); // Get message parameter if available
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM candidates");
             ResultSet rs = pst.executeQuery();
             PrintWriter out = response.getWriter()) {
            
            out.println("<html><head><title>Vote</title>"
                    + "<style>"
                    + "body {"
                    + "    font-family: Arial, sans-serif;"
                    + "    margin: 0;"
                    + "    padding: 0;"
                    + "    background-color: #000;"
                    + "    color: #fff;"
                    + "    display: flex;"
                    + "    justify-content: center;"
                    + "    align-items: center;"
                    + "    height: 100vh;"
                    + "}"
                    + ".container {"
                    + "    background-color: #222;"
                    + "    border-radius: 8px;"
                    + "    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.4);"
                    + "    width: 300px;"
                    + "    padding: 20px;"
                    + "    text-align: center;"
                    + "}"
                    + "strong {"
                    + "    display: block;"
                    + "    margin-bottom: 15px;"
                    + "    font-size: 18px;"
                    + "    color: #f00;" /* Red color */
                    + "}"
                    + ".candidates-box {"
                    + "    background-color: #333;"
                    + "    border-radius: 4px;"
                    + "    padding: 10px;"
                    + "    text-align: left;"
                    + "    margin-bottom: 15px;"
                    + "    max-height: 200px;" /* Set max height */
                    + "    overflow-y: auto;" /* Enable vertical scrolling */
                    + "}"
                    + ".candidates-box input[type='radio'] {"
                    + "    margin-right: 10px;"
                    + "}"
                    + "input[type='submit'] {"
                    + "    background-color: #f00;" /* Red color */
                    + "    color: #fff;"
                    + "    border: none;"
                    + "    border-radius: 4px;"
                    + "    padding: 10px 20px;"
                    + "    font-size: 16px;"
                    + "    cursor: pointer;"
                    + "    transition: background-color 0.3s;"
                    + "}"
                    + "input[type='submit']:hover {"
                    + "    background-color: #c00;" /* Darker red for hover effect */
                    + "}"
                    + ".message {"
                    + "    color: #f00;" /* Red color for messages */
                    + "    font-size: 14px;"
                    + "    margin-top: 15px;"
                    + "}"
                    + "</style></head><body>");
            out.println("<div class=\"container\">");
            if (message != null && !message.isEmpty()) {
                out.println("<p class='message'>" + message + "</p>"); // Display the message with class
            }
            out.println("<strong>VOTE FOR YOUR FAVOURITE CANDIDATE</strong>");
            out.println("<form action='VoteServlet' method='post'>");

            // Box for candidates
            out.println("<div class='candidates-box'>");
            
            // Generate radio buttons for candidates
            while (rs.next()) {
                out.println("<input type='radio' name='candidate' value='" + rs.getInt("id") + "'> " + rs.getString("name") + "<br>");
            }
            
            out.println("</div>"); // Close candidates box
            
            out.println("<input type='submit' value='Vote'>");
            out.println("</form>");
            out.println("</div></body></html>");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int candidateId = Integer.parseInt(request.getParameter("candidate"));
        HttpSession session = request.getSession();
        int userId = (Integer) session.getAttribute("user_id");
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            
            // Check if the user has already voted
            PreparedStatement checkVote = conn.prepareStatement("SELECT * FROM votes WHERE user_id=?");
            checkVote.setInt(1, userId);
            ResultSet rs = checkVote.executeQuery();
            
            if (rs.next()) {
                // Redirect to the same page with a message
                response.sendRedirect("vote.html");
            } else {
                // Record the vote
                try (PreparedStatement pst = conn.prepareStatement("INSERT INTO votes (user_id, candidate_id) VALUES (?, ?)")) {
                    pst.setInt(1, userId);
                    pst.setInt(2, candidateId);
                    pst.executeUpdate();
                }
                
                // Update the candidate's vote count
                try (PreparedStatement updateVotes = conn.prepareStatement("UPDATE candidates SET votes = votes + 1 WHERE id=?")) {
                    updateVotes.setInt(1, candidateId);
                    updateVotes.executeUpdate();
                }
                
                response.sendRedirect("Thankyou.html");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
