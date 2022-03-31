package hu.kits.team.infrastructure.web.ui.view.cominglist;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.data.renderer.ComponentRenderer;

import hu.kits.team.domain.Member;
import hu.kits.team.domain.Player;
import hu.kits.team.domain.TopPlayers.PlayerGames;
import hu.kits.team.infrastructure.web.ui.component.Initials;
import hu.kits.team.infrastructure.web.ui.component.ListItem;
import hu.kits.team.infrastructure.web.ui.component.util.LumoStyles.Color;
import hu.kits.team.infrastructure.web.ui.component.util.UIUtils;

class TopPlayersGrid extends Grid<PlayerGames> {

    TopPlayersGrid() {
        
        addColumn(new ComponentRenderer<>(this::createMemberInfo))
            .setWidth(UIUtils.COLUMN_WIDTH_XL);

        addColumn(PlayerGames::games)
            .setTextAlign(ColumnTextAlign.CENTER)
            .setWidth(UIUtils.COLUMN_WIDTH_XS);
        
        setSelectionMode(SelectionMode.NONE);
    }

    private Component createMemberInfo(PlayerGames row) {
        Player player = row.player();
        
        if(player instanceof Member) {
            Member member = (Member)player;
            Image avatar = new Image(UIUtils.IMG_PATH + member.id + ".png", member.getInitials());
            avatar.setWidth("50px");
            avatar.setHeight("50px");
            return new ListItem(avatar, member.nickName());
        } else {
            return new ListItem(new Initials("V", Color.Primary._50), player.name);
        }
    }

}
