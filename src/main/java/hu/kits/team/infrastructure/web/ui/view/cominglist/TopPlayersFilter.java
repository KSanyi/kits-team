package hu.kits.team.infrastructure.web.ui.view.cominglist;

import java.time.LocalDate;

import hu.kits.team.domain.Championship;
import hu.kits.team.domain.Matches;
import hu.kits.team.domain.TopPlayers;

public interface TopPlayersFilter {

    String label();
    
    TopPlayers apply(Matches matches);
    
    static record ChampionshipFilter(Championship championship) implements TopPlayersFilter {

        @Override
        public String label() {
            return championship.name();
        }

        @Override
        public TopPlayers apply(Matches matches) {
            
            return  matches.topPlayersOfChampionship(championship);
        }
        
    }
    
    static record DateFilter(LocalDate date, String label) implements TopPlayersFilter {

        @Override
        public TopPlayers apply(Matches matches) {
            return  matches.topPlayersSince(date);
        }
        
    }
    
}
