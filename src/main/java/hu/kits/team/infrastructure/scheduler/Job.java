package hu.kits.team.infrastructure.scheduler;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.kits.team.common.Clock;

public abstract class Job {

    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public final String name;
    
    public final Task task;

    public Job(String name, Task task) {
        this.name = name;
        this.task = task;
    }
    
    public abstract LocalDateTime nextExecution();
    
    public String execute() {
        String result = runTask(task);
        return result;
    }
    
    private static String runTask(Task task) {
        try {
            log.info("Running task {}", task.name());
            return task.run();
        } catch (Exception ex) {
            log.error("Error running task {}", task.name(), ex);
            return ex.getMessage();
        }
    }
    
    public String timeUntilNextExecution() {
        long hoursLeft = Clock.now().until(nextExecution(), ChronoUnit.HOURS);
        
        return (hoursLeft < 30 ? hoursLeft + " hours" : hoursLeft/24+1 + " days"); 
    }
    
    @Override
    public String toString() {
        return name;
    }
    
}
