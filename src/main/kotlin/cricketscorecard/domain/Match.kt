package cricketscorecard.domain

data class Match(
    val id: String,
    val status: MatchStatus,
    val teamSize: Int,
    val oversLimit: Int,
    val teamOne: Team,
    val teamTwo: Team,
    val firstInning: Inning? = null,
    val secondInning: Inning? = null,
    val winningTeamId: String? = null
)

data class Inning(
    val id: String,
    val teamId: String,
    val currentBatterId: String,
    val currentNonStrikeBatterId: String,
    val lastBatterPosition: Int,
    val overs: List<Over> = emptyList()
)

data class Over(
    val id: String,
    val inningId: String, // change to inning id
    val overNumber: Int,
    val ballCompleted: Int,
    val balls: List<Ball> = emptyList()
)

data class Ball(
    val id: String,
    val overId: String,
    val ballType: BallType,
    val playedBy: String,
    var run: Run? = null,
)

data class Run(
    val id: String,
    val ballId: String,
    val run: Int,
    val runType: RunType
)

enum class MatchStatus {
    LIVE,
    FINISHED
}

enum class BallType {
    NORMAL,
    WIDE,
    NO_BALL,
    WICKET
}

enum class RunType {
    NORMAL,
    FOUR,
    SIX,
    NO_BALL,
    WIDE_BALL,
    OVERTHROW
}


