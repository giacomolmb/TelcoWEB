package it.polimi.db69.telco.telcoweb.controllers;

import it.polimi.db69.telco.telcoejb.entities.ServicePackage;
import it.polimi.db69.telco.telcoejb.entities.User;
import it.polimi.db69.telco.telcoejb.services.UserService;
import it.polimi.db69.telco.telcoweb.exceptions.InputException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.ejb.EJB;
import javax.persistence.NonUniqueResultException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Pattern;

@WebServlet(name = "SigninServlet", value = "/signin")
public class SigninServlet extends HttpServlet {
    private TemplateEngine templateEngine;

    @EJB(name = "it.polimi.db69.telco.telcoejb.services/UserService")
    private UserService userService;

    private String path = "/WEB-INF/index.html";

    @Override
    public void init() throws ServletException {
        ServletContext servletContext = getServletContext();
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        this.templateEngine = new TemplateEngine();
        this.templateEngine.setTemplateResolver(templateResolver);
        templateResolver.setSuffix(".html");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        ServletContext servletContext = getServletContext();
        WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
        path = "/WEB-INF/login.html";

        if(request.getSession().getAttribute("successMessage") != null){
            ctx.setVariable("successMessage", request.getSession().getAttribute("successMessage"));
            request.getSession().removeAttribute("successMessage");
        }

        templateEngine.process(path, ctx, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User user;
        try {
            user = userService.checkCredentials(username, password);
        } catch (NonUniqueResultException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not check credentials.");
            return;
        }

        if (user == null) {
            response.setContentType("text/html");

            ServletContext servletContext = getServletContext();
            final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
            ctx.setVariable("signinErrorMessage", "Incorrect username or password.");

            templateEngine.process(path, ctx, response.getWriter());
        } else {
            userService.addLog(user.getId());
            request.getSession().setAttribute("user", user);
            if(request.getSession().getAttribute("origin") == null){
                response.sendRedirect(getServletContext().getContextPath() + "/homepage");
            } else {
                response.sendRedirect(getServletContext().getContextPath() + request.getSession().getAttribute("origin"));
            }
        }
    }
}
