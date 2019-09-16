import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

// Configure the request URL for this servlet (Tomcat 7/Servlet 3.0 upwards)
@WebServlet("/querySalesHistory")
public class QuerySalesHistory extends HttpServlet {

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

         String sqlStr =   "select s.invoice_number as 'Invoice number', p.name as 'Product name', p.barcode as 'Product barcode', sd.product_unit_price as 'Product price', " +
                           "sd.product_quantity as 'Bought quantity', SUM(sd.product_quantity * sd.product_unit_price) as 'Total price' from sales s " +
                           "inner join sales_details sd on sd.sale_id = s.id " +
                           "inner join products p on p.id = sd.product_id " +
                           "where s.invoice_number = '" + request.getParameter("invoice_number") + "' group by p.barcode";

         // Send the query to the server
         ResultSet rset = stmt.executeQuery(sqlStr);

         // Process the query result set
         int count = 0;
         while(rset.next()) {
            // Print a paragraph <p>...</p> for each record
            out.println("<b>Invoice number: </b>" + rset.getString("Invoice number") + "<br>"
                  + "<b>Product barcode: </b>" + rset.getString("Product barcode") + "<br>" 
                  + "<b>Product name:</b> " + rset.getString("Product name") + "<br>"
                  + "<b>Product unit price:</b> " + rset.getString("Product price") + "<br>"
                  + "<b>Product bought quantity:</b> " + rset.getString("Bought quantity") + "<br>"
                  + "<b>Product total price:</b> " + rset.getString("Total price") + "<br>");
            count++;
         }

         out.println("<a href='index.html'><button>Go back to main page</button></a>");
      } catch(Exception ex) {
         out.println("<p>Error: " + ex.getMessage() + "</p>");
         out.println("<p>Check Tomcat console for details.</p>");
         ex.printStackTrace();
      }

      //Close conn and stmt - Can be done automatically by try-with-resources (JDK 7)
      out.println("</body></html>");
      out.close();
   }
}