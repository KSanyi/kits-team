package hu.kits.team.infrastructure.web.ui.view.match;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Optional;

import hu.kits.team.common.CollectionsUtil;
import hu.kits.team.domain.Mark;
import hu.kits.team.domain.Match;
import hu.kits.team.domain.Members;
import hu.kits.team.domain.Player;

class MemberStatementRow {

    final Player player;
    
    final Optional<Mark> mark;
    
    final int goals;

    MemberStatementRow(Player player, Optional<Mark> mark, int goals) {
        this.player = player;
        this.mark = mark;
        this.goals = goals;
    }
    
    static List<MemberStatementRow> createForMatch(Members members, Match match) {
        List<MemberStatementRow> rowsForMembers = members.entries().stream()
                .map(member -> new MemberStatementRow(member, match.statementFor(member).map(m -> m.mark), match.goalsBy(member)))
                .collect(toList());
        
        List<MemberStatementRow> rowsGuests = match.guests().stream()
                .map(guest -> new MemberStatementRow(guest, Optional.of(Mark.COMING), match.goalsBy(guest)))
                .collect(toList());
        
        return CollectionsUtil.concat(rowsForMembers, rowsGuests);
    }
    
}
