import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String adminUsername = request.getParameter("adminUsername");
        String adminPassword = request.getParameter("adminPassword");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/votingsystem", "root", "Msvinayak@2002");

            // Verify admin credentials
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM admin WHERE username=? AND password=?");
            pst.setString(1, adminUsername);
            pst.setString(2, adminPassword);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                // If credentials are correct, forward to Admin Panel
                request.getRequestDispatcher("Adminn.html").forward(request, response);
            } else {
                // If credentials are incorrect, redirect back to login with error message
                response.sendRedirect("Admin.html?error=1");
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String message = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/votingsystem", "root", "Msvinayak@2002");

            if ("addCandidate".equals(action)) {
                String candidateName = request.getParameter("candidateName");

                // Insert candidate into the database
                PreparedStatement pst = conn.prepareStatement("INSERT INTO candidates (name) VALUES (?)");
                pst.setString(1, candidateName);
                int rowCount = pst.executeUpdate();

                if (rowCount > 0) {
                    message = "Candidate added successfully!";
                } else {
                    message = "Failed to add candidate.";
                }
            } else if ("removeCandidate".equals(action)) {
                String candidatename = request.getParameter("candidatename");

                // Remove candidate from the database
                PreparedStatement pst = conn.prepareStatement("DELETE FROM candidates WHERE name = ?");
                pst.setString(1, candidatename);
                int rowCount = pst.executeUpdate();

                if (rowCount > 0) {
                    message = "Candidate removed successfully!";
                } else {
                    message = "Failed to remove candidate.";
                }
            } else if ("addVoter".equals(action)) {
                String voterName = request.getParameter("voterName");
                String voterPassword = request.getParameter("voterPassword");

                // Insert voter into the database
                PreparedStatement pst = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
                pst.setString(1, voterName);
                pst.setString(2, voterPassword);
                int rowCount = pst.executeUpdate();

                if (rowCount > 0) {
                    message = "Voter added successfully!";
                } else {
                    message = "Failed to add voter.";
                }
            } else if ("removeVoter".equals(action)) {
                String votername = request.getParameter("votername");

                // Remove voter from the database
                PreparedStatement pst = conn.prepareStatement("DELETE FROM users WHERE username = ?");
                pst.setString(1, votername);
                int rowCount = pst.executeUpdate();

                if (rowCount > 0) {
                    message = "Voter removed successfully!";
                } else {
                    message = "Failed to remove voter.";
                }
            }

            // Redirect to the same page with a message as a query parameter
            response.sendRedirect("Adminn.html?message=" + URLEncoder.encode(message, "UTF-8"));

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("Adminn.html?message=" + URLEncoder.encode("Error: " + e.getMessage(), "UTF-8"));
        }
    }
}
