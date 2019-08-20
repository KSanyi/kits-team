package hu.kits.team;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.LogManager;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.mysql.cj.jdbc.MysqlDataSource;

import hu.kits.team.domain.TeamService;
import hu.kits.team.domain.email.EmailSender;
import hu.kits.team.infrastructure.email.SendGridEmailSender;
import hu.kits.team.infrastructure.scheduler.MorningJob;
import hu.kits.team.infrastructure.scheduler.Scheduler;
import hu.kits.team.infrastructure.web.HttpServer;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public static final String URL = "https://luzer.herokuapp.com";
    
    static {
        LogManager.getLogManager().reset();
        SLF4JBridgeHandler.install();
        java.util.logging.Logger.getLogger("global").setLevel(Level.SEVERE);
    }
    
    public static final TeamService teamService;
    
    static {
        try {
            URI dbUri = getDatabaseUri();
            DataSource dataSource = createDataSource(dbUri);
            EmailSender emailSender = createEmaiSender();
            teamService = TeamServiceFactory.create(dataSource, emailSender);
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static void main(String[] args) throws Exception {
        int port = getPort();

        Scheduler scheduler = new Scheduler();
        scheduler.addJob(new MorningJob(teamService));
        
        HttpServer server = new HttpServer(port);
        server.start();
        server.join();
    }
    
    private static int getPort() {
        String port = System.getenv("PORT");
        if (port == null) {
            throw new IllegalArgumentException("System environment variable PORT is missing");
        }

        try {
            int portNumber = Integer.parseInt(port);
            logger.info("PORT: " + port);
            return portNumber;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Illegal system environment variable PORT: " + port);
        }
    }
    
    private static URI getDatabaseUri() throws URISyntaxException {
        String databaseUrl = System.getenv("CLEARDB_DATABASE_URL");
        if (databaseUrl == null) {
            throw new IllegalArgumentException("System environment variable CLEARDB_DATABASE_URL is missing");
        }
        
        return new URI(databaseUrl);
    }
    
    private static DataSource createDataSource(URI dbUri) throws URISyntaxException {
        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String jdbcUrl = "jdbc:mysql://" + dbUri.getHost() + dbUri.getPath() + "?" + dbUri.getQuery(); 
        
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(jdbcUrl);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        return dataSource;
    }
    
    private static EmailSender createEmaiSender() throws URISyntaxException {
        String sendGridUserName = System.getenv("SENDGRID_USERNAME");
        String sendGridPassword = System.getenv("SENDGRID_PASSWORD");
        
        return new SendGridEmailSender(sendGridUserName, sendGridPassword);
    }
    
}
