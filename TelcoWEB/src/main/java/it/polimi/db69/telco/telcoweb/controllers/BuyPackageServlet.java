package it.polimi.db69.telco.telcoweb.controllers;

import it.polimi.db69.telco.telcoejb.entities.*;
import it.polimi.db69.telco.telcoejb.services.ProductService;
import it.polimi.db69.telco.telcoejb.services.ServicePackageService;
import it.polimi.db69.telco.telcoejb.services.ValidityPeriodService;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.ejb.EJB;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

@WebServlet(name = "BuyPackageServlet", value = "/buypackage")
public class BuyPackageServlet extends HttpServlet {
    private TemplateEngine templateEngine;

    @EJB(name = "it.polimi.db69.telco.telcoejb.services/ServicePackageService")
    private ServicePackageService packageService;

    @EJB(name = "it.polimi.db69.telco.telcoejb.services/ProductService")
    private ProductService productService;

    @EJB(name = "it.polimi.db69.telco.telcoejb.services/ValidityPeriodService")
    private ValidityPeriodService vpService;

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
        String path = "/WEB-INF/buypackage.html";

        int packageid = Integer.parseInt(request.getParameter("id"));
        ServicePackage selectedPackage = packageService.findPackageById(packageid);

        request.getSession().setAttribute("origin", "/buypackage?id=" + packageid);

        if(request.getSession().getAttribute("user") != null){
            User user = (User) request.getSession().getAttribute("user");
            ctx.setVariable("user", user.getUsername());
        }
        ctx.setVariable("package", selectedPackage);

        templateEngine.process(path, ctx, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        java.util.Date startDate;
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("startDate"));


            Date startSQLDate =  new java.sql.Date(startDate.getTime());

            int vpId = Integer.parseInt(request.getParameter("vp"));
            ValidityPeriod validityPeriod = vpService.findById(vpId);

            Subscription subscription = new Subscription(startSQLDate, validityPeriod);

            ArrayList<Product> products = new ArrayList<>();

            if(request.getParameter("optionalProducts") != null){
                for(String optProd : request.getParameterValues("optionalProducts")){
                    Product product = productService.findProductById(Integer.parseInt(optProd));
                    products.add(product);
                }
                subscription.setSubscriptionProducts(products);
            }

            request.getSession().setAttribute("subscription",subscription);

            response.sendRedirect(getServletContext().getContextPath() + "/confirmation");

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
