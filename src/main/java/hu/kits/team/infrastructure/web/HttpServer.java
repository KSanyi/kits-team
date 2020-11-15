package hu.kits.team.infrastructure.web;

import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.websocket.server.ServerContainer;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.server.startup.ServletContextListeners;

import hu.kits.team.Main;

public class HttpServer extends Server {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public HttpServer(int port) {
        super(port);
        setHandler(createHandler());
        logger.info("Server initialized on port {}", port);
    }
    
    private static Handler createHandler() {
        
        WebAppContext context = new WebAppContext();
        context.setBaseResource(createBaseResource());
        context.setContextPath("/");
        context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern", ".*");
        context.setConfigurationDiscovered(true);
        context.setConfigurations(new Configuration[]{
          new AnnotationConfiguration(),
          new WebInfConfiguration(),
          new WebXmlConfiguration(),
          new MetaInfConfiguration()
        });
        context.getServletContext().setExtendedListenerTypes(true);
        
        try {
            // to supress this: java.lang.IllegalStateException: Unable to configure jsr356 at that stage. ServerContainer is null 
            context.getServletContext().setAttribute(ServerContainer.class.getName(), WebSocketServerContainerInitializer.configureContext(context));
        } catch(ServletException ex) {
            logger.error(ex.getMessage());
        }
        context.addEventListener(new ServletContextListeners());
        
        return context;
    }
    
    private static Resource createBaseResource() {
        URL webRootLocation = Main.class.getResource("/webapp/");
        try {
            URI webRootUri = webRootLocation.toURI();
            return Resource.newResource(webRootUri);
        } catch (URISyntaxException | MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}