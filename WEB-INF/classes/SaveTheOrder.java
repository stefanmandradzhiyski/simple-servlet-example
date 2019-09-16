import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

// Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
@WebServlet("/saveTheOrder")
public class SaveTheOrder extends HttpServlet {

   // The doGet() runs once per HTTP GET request to this servlet.
   @Override
   public void doPost(HttpServletRequest request, HttpServletResponse response)
               throws ServletException, IOException {
      // Set the MIME type for the response message
      response.setContentType("text/html");
      // Get a output writer to write the response message into the network socket
      PrintWriter out = response.getWriter();

      // Print an HTML page as the output of the query
      out.println("<html>");
      out.println("<head><title>Query Response</title></head>");
      out.println("<body>");

      try (
         // Allocate a database 'Connection' object
         Connection conn = DriverManager.getConnection(
               "jdbc:mysql://localhost:3306/store?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC",
               "***", "***");   // For MySQL
               // The format is: "jdbc:mysql://hostname:port/databaseName", "username", "password"

         // Allocate a 'Statement' object in the Connection
         Statement stmt = conn.createStatement();
         CallableStatement myStmt = conn.prepareCall("{call sp_insertsalesdetails(?, ?, ?, ?)}");
      ) {
         myStmt.setString(1, "321321321");
         myStmt.setString(2, request.getParameter("companies"));
         myStmt.setString(3, request.getParameter("products"));
         myStmt.setInt(4, Integer.parseInt(request.getParameter("order_quantity")));
         myStmt.execute();
      } catch(Exception ex) {
         out.println("<p>Error: " + ex.getMessage() + "</p>");
         out.println("<p>Check Tomcat console for details.</p>");
         ex.printStackTrace();
      }

      // Close conn and stmt - Done automatically by try-with-resources (JDK 7)
      out.println("</body></html>");
      out.close();
   }
}