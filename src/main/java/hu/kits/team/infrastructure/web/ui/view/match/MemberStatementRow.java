package hu.kits.team.infrastructure.web.ui.view.match;

import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.stream.Collectors.toList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import hu.kits.team.common.Clock;
import hu.kits.team.domain.Mark;
import hu.kits.team.domain.Match;
import hu.kits.team.domain.Members;
import hu.kits.team.domain.Player;

record MemberStatementRow(Player player, Optional<Mark> mark, Optional<LocalDateTime> timeOfStatement, int goals) implements Comparable<MemberStatementRow> {

    static List<MemberStatementRow> createForMatch(Members members, Match match) {
        Stream<MemberStatementRow> rowsForMembers = members.entries().stream()
                .filter(member -> !member.isTempMember() || match.statementFor(member).map(s -> s.mark() == Mark.COMING).orElse(false))
                .map(member -> new MemberStatementRow(member, match.statementFor(member).map(m -> m.mark()), match.statementFor(member).map(m -> m.time()), match.goalsBy(member)));
        
        Stream<MemberStatementRow> rowsGuests = match.guests().stream()
                .map(guest -> new MemberStatementRow(guest, Optional.of(Mark.COMING), Optional.empty(), 0));
        
        return Stream.concat(rowsForMembers, rowsGuests)
                .sorted()
                .collect(toList());
    }

    @Override
    public int compareTo(MemberStatementRow other) {
        return (int)(other.createScore() - createScore());
    }
    
    private long createScore() {
        if(mark.isPresent()) {
            if(mark.get() == Mark.COMING) {
                return 10L + timeOfStatement.map(time -> SECONDS.between(time, Clock.now())).orElse(0L);
            } else {
                return 5;
            }
        } else {
            return 0;    
        }
    }
    
}
