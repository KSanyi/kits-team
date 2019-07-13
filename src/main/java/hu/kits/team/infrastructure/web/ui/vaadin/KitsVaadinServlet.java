package hu.kits.team.infrastructure.web.ui.vaadin;

import java.lang.invoke.MethodHandles;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.server.SessionDestroyEvent;
import com.vaadin.flow.server.SessionDestroyListener;
import com.vaadin.flow.server.VaadinServlet;

@WebServlet(value = "/*")
public class KitsVaadinServlet extends VaadinServlet implements SessionDestroyListener {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    @Override
    protected void servletInitialized() throws ServletException {
        super.servletInitialized();
        getService().addSessionDestroyListener(this);
    }

    @Override
    public void sessionDestroy(SessionDestroyEvent event) {
        logger.debug("Session destroyed for {}", Session.currentMember());
    }
}