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

    MemberStatementRow(Player player, Optional<Mark> mark) {
        this.player = player;
        this.mark = mark;
    }
    
    static List<MemberStatementRow> createForMatch(Members members, Match match) {
        List<MemberStatementRow> rowsForMembers = members.entries().stream()
                .map(member -> new MemberStatementRow(member, match.statementFor(member).map(m -> m.mark)))
                .collect(toList());
        
        List<MemberStatementRow> rowsGuests = match.guests().stream()
                .map(guest -> new MemberStatementRow(guest, Optional.of(Mark.COMING)))
                .collect(toList());
        
        return CollectionsUtil.concat(rowsForMembers, rowsGuests);
    }
    
}
