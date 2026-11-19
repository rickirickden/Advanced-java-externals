import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 1. NON-PERSISTENT COOKIE (Session Cookie - RAM only)
        Cookie nonPersistentCookie = new Cookie("userRole", "Student");
        nonPersistentCookie.setMaxAge(-1); // deleted on browser close
        response.addCookie(nonPersistentCookie);

        // 2. PERSISTENT COOKIE (Stored on disk for 24 hours)
        Cookie persistentCookie = new Cookie("username", "AdminUser");
        persistentCookie.setMaxAge(24 * 60 * 60); // 86400 seconds
        response.addCookie(persistentCookie);

        // Dynamic HTML response (No separate HTML file needed)
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>Cookie Demonstration</title>");
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 40px; background-color: #f4f6f9; }");
        out.println(".card { background: white; padding: 25px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); max-width: 650px; margin: auto; }");
        out.println("h2 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }");
        out.println("table { width: 100%; border-collapse: collapse; margin-top: 15px; }");
        out.println("th, td { border: 1px solid #ddd; padding: 10px; text-align: left; }");
        out.println("th { background-color: #3498db; color: white; }");
        out.println(".badge-np { color: #d35400; font-weight: bold; }");
        out.println(".badge-p { color: #27ae60; font-weight: bold; }");
        out.println(".btn { display: inline-block; margin-top: 20px; padding: 10px 18px; background-color: #3498db; color: white; text-decoration: none; border-radius: 5px; font-weight: bold; }");
        out.println("</style></head><body>");

        out.println("<div class='card'>");
        out.println("<h2>Cookies Written to Client Successfully</h2>");
        out.println("<p>The following cookies have been sent to the browser via HTTP response:</p>");

        out.println("<table>");
        out.println("<tr><th>Cookie Name</th><th>Value</th><th>Type</th><th>Expiry (MaxAge)</th></tr>");
        out.println("<tr><td>userRole</td><td>Student</td><td class='badge-np'>Non-Persistent</td><td>Session (Deleted on browser exit)</td></tr>");
        out.println("<tr><td>username</td><td>AdminUser</td><td class='badge-p'>Persistent</td><td>24 Hours (86,400 seconds)</td></tr>");
        out.println("</table>");

        // Reading cookies received from client
        out.println("<h3 style='margin-top: 25px;'>Cookies Currently Received from Client:</h3>");
        Cookie[] cookies = request.getCookies();

        if (cookies != null && cookies.length > 0) {
            out.println("<table>");
            out.println("<tr><th>Received Name</th><th>Received Value</th></tr>");
            for (Cookie c : cookies) {
                out.println("<tr><td>" + c.getName() + "</td><td>" + c.getValue() + "</td></tr>");
            }
            out.println("</table>");
        } else {
            out.println("<p><i>No cookies received on first request. Click the button below to refresh and read them!</i></p>");
        }

        out.println("<a href='CookieServlet' class='btn'>Refresh Page to Read Cookies</a>");
        out.println("</div></body></html>");
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
