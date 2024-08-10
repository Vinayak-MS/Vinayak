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
import jakarta.servlet.http.HttpSession;

@WebServlet("/VoteServlet")
public class VoteServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/votingsystem", "root", "Msvinayak@2002");
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM candidates");
            ResultSet rs = pst.executeQuery();

            PrintWriter out = response.getWriter();
            out.println("<html><body><form action='VoteServlet' method='post'>");

            while (rs.next()) {
                out.println("<input type='radio' name='candidate' value='" + rs.getInt("id") + "'> " + rs.getString("name") + "<br>");
            }

            out.println("<input type='submit' value='Vote'>");
            out.println("</form></body></html>");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int candidateId = Integer.parseInt(request.getParameter("candidate"));
        HttpSession session = request.getSession();
        int userId = (Integer) session.getAttribute("user_id");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/votingsystem", "root", "Msvinayak@2002");

            // Check if the user has already voted
            PreparedStatement checkVote = conn.prepareStatement("SELECT * FROM votes WHERE user_id=?");
            checkVote.setInt(1, userId);
            ResultSet rs = checkVote.executeQuery();

            if (rs.next()) {
                PrintWriter out = response.getWriter();
                out.println("You have already voted!");
            } else {
                // Record the vote
                PreparedStatement pst = conn.prepareStatement("INSERT INTO votes (user_id, candidate_id) VALUES (?, ?)");
                pst.setInt(1, userId);
                pst.setInt(2, candidateId);
                pst.executeUpdate();

                // Update the candidate's vote count
                PreparedStatement updateVotes = conn.prepareStatement("UPDATE candidates SET votes = votes + 1 WHERE id=?");
                updateVotes.setInt(1, candidateId);
                updateVotes.executeUpdate();

                response.sendRedirect("results.html");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
