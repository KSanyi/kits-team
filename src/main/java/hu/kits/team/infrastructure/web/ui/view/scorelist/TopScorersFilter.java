package hu.kits.team.infrastructure.web.ui.view.scorelist;

import java.time.LocalDate;

import hu.kits.team.domain.AllGoals;
import hu.kits.team.domain.Championship;
import hu.kits.team.domain.TopScorers;

public interface TopScorersFilter {

    String label();
    
    TopScorers apply(AllGoals allGoals);
    
    static record ChampionshipFilter(Championship championship) implements TopScorersFilter {

        @Override
        public String label() {
            return championship.name();
        }

        @Override
        public TopScorers apply(AllGoals allGoals) {
            return allGoals.topScorersOfChampionship(championship);
        }
        
    }
    
    static record DateFilter(LocalDate date, String label) implements TopScorersFilter {

        @Override
        public TopScorers apply(AllGoals allGoals) {
            return allGoals.topScorersSince(date);
        }
        
    }
    
}
