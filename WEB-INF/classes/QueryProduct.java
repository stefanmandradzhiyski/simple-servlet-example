import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

// Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
@WebServlet("/queryProduct")
public class QueryProduct extends HttpServlet {

   // The doGet() runs once per HTTP GET request to this servlet.
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response)
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
      ) {
         // Execute a SQL SELECT query
         String sqlStr = "select * from products where barcode = "
               + "'" + request.getParameter("product_barcode") + "'";

         ResultSet rset = stmt.executeQuery(sqlStr);  // Send the query to the server

         // Process the query result set
         int count = 0;
         while(rset.next()) {
            // Print a paragraph <p>...</p> for each record
            out.println("<b>---Product: </b>" + rset.getString("barcode") + "---<br>" 
                  + "<b>Product Name:</b> " + rset.getString("name") + "<br>"
                  + "<b>Product Short Description:</b> " + rset.getString("short_description") + "<br>"
                  + "<b>Product Weight:</b> " + rset.getString("weight") + "kg " + "<br>"
                  + "<b>Product Quantity:</b> " + rset.getString("quantity") + "<br>"
                  + "<b>Product Price:</b> " + rset.getDouble("price") + "lv. <br><br>");
            count++;
         }

         out.println("<a href='index.html'><button>Go back to main page</button></a>");
      } catch(Exception ex) {
         out.println("<p>Error: " + ex.getMessage() + "</p>");
         out.println("<p>Check Tomcat console for details.</p>");
         ex.printStackTrace();
      }

      // Close conn and stmt - Can be done automatically by try-with-resources (JDK 7)
      out.println("</body></html>");
      out.close();
   }
}