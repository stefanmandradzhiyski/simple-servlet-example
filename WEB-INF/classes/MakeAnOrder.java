import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

// Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
@WebServlet("/makeAnOrder")
public class MakeAnOrder extends HttpServlet {

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

         // All needed lists
         List<String> companiesName = new ArrayList<String>();
         List<String> companiesUI = new ArrayList<String>();
         List<String> productsName = new ArrayList<>();
         List<String> productsID = new ArrayList<>();

         String sqlStr = "select name, unique_identifier from companies";

         // Send the query to the server
         ResultSet rset = stmt.executeQuery(sqlStr);

         // Process the query result set
         while(rset.next()) {
            companiesName.add(rset.getString("name"));
            companiesUI.add(rset.getString("unique_identifier"));
         }

         // Filling the companies option field
         StringBuilder stringBuilder = new StringBuilder();
         for (int i = 0; i < companiesUI.size(); i++){
            stringBuilder.append("<option value='" + companiesUI.get(i) + "'>" + companiesName.get(i) + "</option>");
         }

         out.println("<form method='post' action='http://localhost:9999/store/saveTheOrder'><select name='companies'>"
                     + stringBuilder.toString()
                     + "</select><br><br>");

         String sqlStrProduct = "select id, name from products";
         ResultSet rsetp = stmt.executeQuery(sqlStrProduct);

         while(rsetp.next()) {
            productsName.add(rsetp.getString("name"));
            productsID.add(rsetp.getString("id"));
         }

         // Fillin the products option field
         StringBuilder stringBuilderP = new StringBuilder();
         for (int i = 0; i < productsID.size(); i++){
            stringBuilderP.append("<option value='" + productsID.get(i) + "'>" + productsName.get(i) + "</option>");
         }

         out.println("<select name='products'>"
                     + stringBuilderP.toString()
                     + "</select><br><br>" 
                     + "Quantity: <input type='number' name='order_quantity' min='1' max='100'><br>"
                     + "<input type='submit' value='Buy'></form>");

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