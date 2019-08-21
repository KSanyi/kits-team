package hu.kits.team.infrastructure.scheduler;

import java.util.List;

import hu.kits.team.domain.TeamService;

public class MorningJob extends DailyJob {

    public MorningJob(TeamService teamService) {
        super("Morning job", List.of(DailyTime.T_05_00), new MatchReminder(teamService));
    }
    
    private static class MatchReminder implements Task {

        private final TeamService teamService;
        
        public MatchReminder(TeamService teamService) {
            this.teamService = teamService;
        }

        @Override
        public String name() {
            return "Match remainder email";
        }

        @Override
        public String run() {
            int count = teamService.sendReminders();
            return "Sent " + count + " reminders";
        }
        
    }
}
