package com.aws.outposts;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("")
public class AwsMovieTheatreServlet extends HttpServlet {

        private static final BuildInfo BUILD = BuildInfo.load();

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.setContentType("text/html");
                PrintWriter out = resp.getWriter();

                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<meta charset='UTF-8'>");
                out.println("<title>AWS Movie Theatre</title>");
                out.println("<style>");
                out.println("body { font-family: Arial, sans-serif; background: #1a1a2e; color: #c9c9c9; margin: 0; padding: 20px; }");
                out.println(".container { max-width: 1400px; margin: 0 auto; }");
                out.println(".header { text-align: center; padding: 40px 0; background: linear-gradient(180deg, #0f0f1e 0%, #1a1a2e 100%); border-bottom: 3px solid #e74c3c; margin-bottom: 40px; }");
                out.println("h1 { font-size: 3.5em; margin: 0; color: #e74c3c; text-shadow: 2px 2px 4px rgba(0,0,0,0.5); }");
                out.println(".subtitle { font-size: 1.3em; margin-top: 10px; color: #c9c9c9; }");
                out.println(".movies { display: grid; grid-template-columns: repeat(auto-fit, minmax(350px, 1fr)); gap: 30px; margin-bottom: 60px; }");
                out.println(".movie { background: #16213e; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.3); transition: transform 0.3s; }");
                out.println(".movie:hover { transform: translateY(-5px); box-shadow: 0 8px 25px rgba(231,76,60,0.3); }");
                out.println(".movie img { width: 100%; height: auto; display: block; background: #0f0f1e; }");
                out.println(".movie-content { padding: 20px; }");
                out.println(".movie h3 { margin: 0 0 10px 0; color: #e74c3c; font-size: 1.5em; }");
                out.println(".movie p { line-height: 1.6; margin: 10px 0; color: #c9c9c9; }");
                out.println(".rating { color: #e74c3c; font-weight: bold; font-size: 1.1em; margin-top: 15px; }");
                out.println(".disclaimer { text-align: center; padding: 20px; color: #888; font-size: 0.85em; border-top: 1px solid #333; margin-top: 40px; }");
                out.println("</style>");
                out.println("</head>");
                out.println("<body>");
                out.println("<div class='container'>");
                out.println("<div class='header'>");
                out.println("<h1>AWS Movie Theatre</h1>");
                out.println("<p class='subtitle'>Now Showing on AWS Outposts</p>");
                out.println("</div>");
                out.println("<div class='movies'>");

                out.println("<div class='movie'>");
                out.println("<img src='images/toaster.png' alt='The Brave Little Toaster Returns'>");
                out.println("<div class='movie-content'>");
                out.println("<h3>The Brave Little Toaster Returns</h3>");
                out.println("<p>A shiny appliance embarks on a heartwarming journey through the modern kitchen.</p>");
                out.println("<p class='rating'>Rating: 5/5</p>");
                out.println("</div>");
                out.println("</div>");

                out.println("<div class='movie'>");
                out.println("<img src='images/potato.png' alt='The Great Potato Heist'>");
                out.println("<div class='movie-content'>");
                out.println("<h3>The Great Potato Heist</h3>");
                out.println("<p>A team of clever spuds plans the ultimate underground vegetable caper.</p>");
                out.println("<p class='rating'>Rating: 4/5</p>");
                out.println("</div>");
                out.println("</div>");

                out.println("<div class='movie'>");
                out.println("<img src='images/calculator.png' alt='Calculator Warriors'>");
                out.println("<div class='movie-content'>");
                out.println("<h3>Calculator Warriors</h3>");
                out.println("<p>Office supplies come alive in an epic mathematical adventure of numbers and spreadsheets.</p>");
                out.println("<p class='rating'>Rating: 5/5</p>");
                out.println("</div>");
                out.println("</div>");

                out.println("<div class='movie'>");
                out.println("<img src='images/ducks.png' alt='The Rubber Duck Chronicles'>");
                out.println("<div class='movie-content'>");
                out.println("<h3>The Rubber Duck Chronicles</h3>");
                out.println("<p>A cheerful bath toy discovers the magic hidden in everyday bubbles.</p>");
                out.println("<p class='rating'>Rating: 4/5</p>");
                out.println("</div>");
                out.println("</div>");

                out.println("<div class='movie'>");
                out.println("<img src='images/llamas.png' alt='Space Llamas from Mars'>");
                out.println("<div class='movie-content'>");
                out.println("<h3>Space Llamas from Mars</h3>");
                out.println("<p>Intergalactic camelids journey across the cosmos seeking the perfect grazing grounds.</p>");
                out.println("<p class='rating'>Rating: 5/5</p>");
                out.println("</div>");
                out.println("</div>");

                out.println("<div class='movie'>");
                out.println("<img src='images/stapler.png' alt='The Mysterious Stapler'>");
                out.println("<div class='movie-content'>");
                out.println("<h3>The Mysterious Stapler</h3>");
                out.println("<p>An enchanted office tool holds the key to solving workplace mysteries.</p>");
                out.println("<p class='rating'>Rating: 4/5</p>");
                out.println("</div>");
                out.println("</div>");

                out.println("</div>");
                out.println("<div class='disclaimer'>");
                out.println("<p>Movie posters generated with Amazon Bedrock. AWS Movie Theatre is not a real service.</p>");
                out.println("<p>v" + BUILD.version() + " (" + BUILD.revision() + ")</p>");
                out.println("</div>");
                out.println("</div>");
                out.println("</body>");
                out.println("</html>");
        }
}
