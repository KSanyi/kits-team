package hu.kits.team.domain;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toList;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import hu.kits.team.domain.TopScorers.PlayerScore;

public record AllGoals(List<GoalData> goals) {

    public static record GoalData(Player player, LocalDate date, Championship championship, int goals) {}
    
    public TopScorers topScorersOfChampionship(Championship championship) {
        
        List<GoalData> relevantGoals = goals.stream()
            .filter(goal -> goal.championship.equals(championship))
            .collect(toList());
        
        return topScorers(relevantGoals);
    }
    
    public TopScorers topScorersInYear(int year) {
        LocalDate start = LocalDate.of(year, 1, 1).minusDays(1);
        LocalDate end = LocalDate.of(year+1, 1, 1);
        List<GoalData> relevantGoals = goals.stream()
                .filter(goal -> goal.date.isAfter(start) && goal.date.isBefore(end))
                .collect(toList());
            
            return topScorers(relevantGoals);
    }

    private static TopScorers topScorers(List<GoalData> relevantGoals) {
        
        Map<Player, Integer> goals = relevantGoals.stream()
                .collect(groupingBy(GoalData::player, summingInt(GoalData::goals)));
        
        List<PlayerScore> playerScores = goals.entrySet().stream()
            .map(e -> new PlayerScore(e.getKey(), e.getValue()))
            .sorted(Comparator.comparing(PlayerScore::goals).reversed())
            .collect(toList());
        
        return new TopScorers(playerScores);
    }

    public TopScorers topScorers() {
        return topScorers(goals);
    }

}
