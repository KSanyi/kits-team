package hu.kits.team.domain;

import java.util.List;

public record TopPlayers(List<PlayerGames> playersScores) {

    public static record PlayerGames(Player player, int games) {}
}
