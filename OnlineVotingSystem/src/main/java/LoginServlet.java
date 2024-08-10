import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
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

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null) {
            PrintWriter out = response.getWriter();
            out.println("Missing username or password");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/votingsystem", "root", "Msvinayak@2002");
            PreparedStatement pst = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                HttpSession session = request.getSession();
                session.setAttribute("user_id", rs.getInt("id"));
                response.sendRedirect("VoteServlet");
            } else {
                // Read the content of login.html
                String htmlContent = new String(Files.readAllBytes(Paths.get(getServletContext().getRealPath("loginn.html"))));
                
                // Insert the error message
                String errorMessage = "<p class=\"error-message\">Invalid credentials</p>";
                htmlContent = htmlContent.replace("<p class=\"error-message\" id=\"error-message\"></p>", errorMessage);
                
                // Send the response
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println(htmlContent);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
