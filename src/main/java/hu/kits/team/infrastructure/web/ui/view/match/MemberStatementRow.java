package hu.kits.team.infrastructure.web.ui.view.match;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import hu.kits.team.domain.Mark;
import hu.kits.team.domain.Match;
import hu.kits.team.domain.Members;
import hu.kits.team.domain.Player;

record MemberStatementRow(Player player, Optional<Mark> mark, int goals) implements Comparable<MemberStatementRow> {

    static List<MemberStatementRow> createForMatch(Members members, Match match) {
        Stream<MemberStatementRow> rowsForMembers = members.entries().stream()
                .map(member -> new MemberStatementRow(member, match.statementFor(member).map(m -> m.mark()), match.goalsBy(member)));
        
        Stream<MemberStatementRow> rowsGuests = match.guests().stream()
                .map(guest -> new MemberStatementRow(guest, Optional.of(Mark.COMING), match.goalsBy(guest)));
        
        return Stream.concat(rowsForMembers, rowsGuests)
                .sorted()
                .collect(toList());
    }

    @Override
    public int compareTo(MemberStatementRow other) {
        return other.createScore() - createScore();
    }
    
    private int createScore() {
        if(mark.isPresent()) {
            if(mark.get() == Mark.COMING) {
                return 10 + goals;
            } else {
                return 5;
            }
        } else {
            return 0;    
        }
    }
    
}
