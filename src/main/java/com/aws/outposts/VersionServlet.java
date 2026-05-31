package com.aws.outposts;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Machine-readable build identity, so tooling can read the version without
 * scraping the HTML page. Serves /version and /healthz.
 */
@WebServlet({"/version", "/healthz"})
public class VersionServlet extends HttpServlet {

    private static final BuildInfo BUILD = BuildInfo.load();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        out.print("{\"version\":\"" + BUILD.version()
                + "\",\"revision\":\"" + BUILD.revision()
                + "\",\"status\":\"ok\"}");
    }
}
