package txreadonly;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@WebServlet(urlPatterns = "/txreadonly")
public class TxReadonlyServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(TxReadonlyServlet.class.getName());

    private @Inject Service svc;
    private @Inject WriteAllowedCheck check;

    @Override
    public final void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String action = request.getParameter("action");
        if (action != null && !action.isEmpty()) {
            PrintWriter w = response.getWriter();
            w.println("Action: " + action + " " + new Date());
            if ("read".equals(action)) {
                List<LogEntry> all = svc.findAll();
                w.println(all.size());
            } else if ("write".equals(action)) {
                try {
                    String name = Integer.toString(Thread.currentThread().hashCode());
                    w.print("Wrote " + svc.createLog(name).getId());
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    response.sendError(500, e.getMessage());
                }
            }
        }

        String ro = request.getParameter("readonly");
        if (ro != null && ro.length() > 0) {
            final boolean roEnabled;
            roEnabled = Boolean.valueOf(ro);
            check.setReadonlyEnabled(roEnabled);
        }
    }

}
