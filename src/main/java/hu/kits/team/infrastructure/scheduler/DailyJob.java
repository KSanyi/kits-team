package hu.kits.team.infrastructure.scheduler;

import java.time.LocalDateTime;
import java.util.List;

public class DailyJob extends Job {

    public final List<DailyTime> scheduledAt;
    
    public DailyJob(String name, List<DailyTime> scheduledAt, Task task) {
        super(name, task);
        this.scheduledAt = scheduledAt;
    }
    
    @Override
    public LocalDateTime nextExecution() {
        return scheduledAt.stream().map(DailyTime::next).min(LocalDateTime::compareTo).get();
    }
    
    @Override
    public String toString() {
        return name + " " + scheduledAt;
    }
    
}
