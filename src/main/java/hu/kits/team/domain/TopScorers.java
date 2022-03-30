package hu.kits.team.domain;

import java.util.List;

public record TopScorers(List<PlayerScore> playersScores) {

    public static record PlayerScore(Player player, int goals) {}
}
