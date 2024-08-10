import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/UpdateServlet")
public class UpdateServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String newpassword = request.getParameter("NewPassword");

        System.out.println("Received username: " + username);
        System.out.println("Received password: " + password);
        System.out.println("Received new password: " + newpassword);

        if (username == null || password == null || newpassword == null) {
            PrintWriter out = response.getWriter();
            out.println("Missing username, password, or new password");
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
                PreparedStatement pst1 = conn.prepareStatement("UPDATE users SET password=? WHERE username=? AND password=?");
                pst1.setString(1, newpassword);
                pst1.setString(2, username);
                pst1.setString(3, password);
                pst1.executeUpdate();
                
                response.sendRedirect("login1.html");

            } else {
                request.setAttribute("errorMessage", "Password or Username is Wrong!!");
                RequestDispatcher dispatcher = request.getRequestDispatcher("update1.html");
                dispatcher.forward(request, response);
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
