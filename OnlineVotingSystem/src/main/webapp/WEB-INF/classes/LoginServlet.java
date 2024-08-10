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

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	protected void DoPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Servlet code
        String username = request.getParameter("username");
        String password = request.getParameter("password");

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
                response.sendRedirect("vote.html");
            } else {
                PrintWriter out = response.getWriter();
                out.println("Invalid credentials");
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
