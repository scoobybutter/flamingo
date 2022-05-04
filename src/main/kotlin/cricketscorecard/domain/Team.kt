package cricketscorecard.domain

data class Team(
    val id: String,
    val matchId: String,
    val players: List<Player> = emptyList(),
    val battingOrder: BattingOrder? = null
)

data class BattingOrder(
    val teamId: String,
    val playerIdsOrder: List<String>
)
