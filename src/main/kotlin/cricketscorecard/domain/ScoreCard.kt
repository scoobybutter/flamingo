package cricketscorecard.domain

data class ScoreCard(
    val matchId: String,
    val playerStats: List<PlayerStat>,
    val teamStats: TeamStat,
    val winningTeamId: String? = null
)

data class PlayerStat(
    val playerId: String,
    val isCurrentPlayer: Boolean,
    val runs: Int,
    val balls: Int,
    val fours: Int,
    val sixes: Int
)

data class TeamStat(
    val teamId: String,
    val totalScore: Int,
    val totalWickets: Int,
    val totalOvers: String
)
