package cricketscorecard.service

import cricketscorecard.repo.MatchRepository
import cricketscorecard.domain.Ball
import cricketscorecard.domain.BallType
import cricketscorecard.domain.BattingOrder
import cricketscorecard.domain.Inning
import cricketscorecard.domain.Match
import cricketscorecard.domain.MatchStatus
import cricketscorecard.domain.Over
import cricketscorecard.domain.Player
import cricketscorecard.domain.PlayerStat
import cricketscorecard.domain.Run
import cricketscorecard.domain.RunType
import cricketscorecard.domain.ScoreCard
import cricketscorecard.domain.Team
import cricketscorecard.domain.TeamStat
import java.util.*

class MatchService {
    private val matchRepository = MatchRepository()

    fun createMatch(matchCreateRequest: MatchCreateRequest): Match {
        val matchId = UUID.randomUUID().toString()
        val match = Match(
            id = matchId,
            status = MatchStatus.LIVE,
            teamSize = matchCreateRequest.playerSize,
            teamOne = Team(
                matchId = matchId,
                id = UUID.randomUUID().toString(),
            ),
            teamTwo = Team(
                matchId = matchId,
                id = UUID.randomUUID().toString(),
            ),
            oversLimit = matchCreateRequest.overs
        )
        return matchRepository.addOrUpdateMatch(matchId, match)
    }

    fun addPlayers(playerIds: List<String>, matchId: String, teamId: String): Match {
        val match = matchRepository.getMatch(matchId) ?: error("Match not found")
        if (match.teamOne.id == teamId) {
            return matchRepository.addOrUpdateMatch(
                matchId, match.copy(
                    teamOne = match.teamOne.copy(
                        players = playerIds.map { Player(it) },
                        battingOrder = BattingOrder(teamId = teamId, playerIdsOrder = playerIds)
                    )
                )
            )
        }
        return matchRepository.addOrUpdateMatch(
            matchId, match.copy(
                teamTwo = match.teamTwo.copy(
                    players = playerIds.map { Player(it) },
                    battingOrder = BattingOrder(teamId = teamId, playerIdsOrder = playerIds)
                )
            )
        )
    }

    fun getScoreCard(matchId: String): ScoreCard {
        val match = matchRepository.getMatch(matchId)!!
        val currentInning = match.secondInning ?: match.firstInning
        val playerIdBallsMap =
            currentInning!!.overs.flatMap { it.balls }
                .filter { it.ballType == BallType.NORMAL || it.ballType == BallType.WICKET }.groupBy { it.playedBy }
        val currentTeam = if (match.teamOne.id == currentInning.teamId) match.teamOne else match.teamTwo
        val playerStats = currentTeam.players.map { it.id }.map { playerId ->
            PlayerStat(
                playerId = playerId,
                isCurrentPlayer = currentInning.currentBatterId == playerId || currentInning.currentNonStrikeBatterId == playerId,
                runs = playerIdBallsMap[playerId]?.sumBy { it.run?.run ?: 0 } ?: 0,
                balls = playerIdBallsMap[playerId]?.size ?: 0,
                fours = playerIdBallsMap[playerId]?.filter { it.run?.runType == RunType.FOUR }?.size ?: 0,
                sixes = playerIdBallsMap[playerId]?.filter { it.run?.runType == RunType.SIX }?.size ?: 0
            )
        }
        val overs =
            (currentInning.overs.sumBy { it.ballCompleted } / 6).toString() + "." + (currentInning.overs.sumBy { it.ballCompleted } % 6).toString()
        val teamStat = TeamStat(
            teamId = currentInning.teamId,
            totalScore = currentInning.overs.flatMap { it.balls }.sumBy { it.run?.run ?: 0 },
            totalWickets = currentInning.overs.flatMap { it.balls }.filter { it.ballType == BallType.WICKET }.size,
            totalOvers = overs
        )
        return ScoreCard(
            matchId = matchId,
            playerStats = playerStats,
            teamStats = teamStat,
            winningTeamId = match.winningTeamId
        )
    }

    fun addBall(matchId: String, ballRequest: String): Match {
        val match = matchRepository.getMatch(matchId) ?: error("Match not found")
        require(match.status != MatchStatus.FINISHED) {
            "match is finished"
        }
        var currentInning = match.secondInning ?: match.firstInning
        if (currentInning == null || isCurrentInningComplete(match)) {
            currentInning = getNewInning(getNewBattingTeam(match))
        }
        val isSecondInning = currentInning.teamId != match.teamOne.id
        var currentTeam = match.teamOne
        if (currentInning.teamId == match.teamTwo.id)
            currentTeam = match.teamTwo
        var currentOver = currentInning.overs.lastOrNull()
        var isNewOver = false
        if (currentOver == null || isCurrentOverComplete(currentOver)) {
            isNewOver = true
            currentOver = getNewOver(currentInning.id, currentInning.overs.size + 1)
        }
        val newBall = getNewBall(getBallType(ballRequest), currentInning, currentOver, ballRequest)
        currentOver = getUpdatedOver(currentOver, newBall)
        currentInning = getUpdatedInning(currentInning, newBall, currentOver, isNewOver, currentTeam)
        return matchRepository.addOrUpdateMatch(
            matchId,
            getUpdatedMatch(match, currentInning, isSecondInning)
        )
    }

    fun isCurrentInningComplete(match: Match): Boolean {
        val currentInning = match.secondInning ?: match.firstInning ?: return false
        if (match.winningTeamId != null) {
            return true
        }
        val overComplete =
            currentInning.overs.size == match.oversLimit && currentInning.overs.lastOrNull()?.ballCompleted == 6
        val battingComplete =
            currentInning.overs.flatMap { it.balls }
                .filter { it.ballType == BallType.WICKET }.size == (match.teamSize - 1)
        return overComplete || battingComplete
    }

    private fun getBallType(ballRequest: String) = when (ballRequest) {
        "W" -> BallType.WICKET
        "Wd" -> BallType.WIDE
        "Nb" -> BallType.NO_BALL
        else -> BallType.NORMAL
    }

    private fun getRunType(ballRequest: String) = when (ballRequest) {
        "4" -> RunType.FOUR
        "6" -> RunType.SIX
        "Wd" -> RunType.WIDE_BALL
        "Nb" -> RunType.NO_BALL
        else -> RunType.NORMAL
    }

    private fun getNewBall(ballType: BallType, currentInning: Inning, currentOver: Over, ballRequest: String): Ball {
        val newBall = Ball(
            id = UUID.randomUUID().toString(),
            ballType = ballType,
            playedBy = currentInning.currentBatterId,
            overId = currentOver.id
        )
        if (ballType == BallType.NORMAL) {
            require(ballRequest.toInt() in 0..6) {
                "Runs should be between 0 and 6"
            }
            newBall.run = Run(
                id = UUID.randomUUID().toString(),
                ballId = newBall.id,
                run = ballRequest.toInt(),
                runType = getRunType(ballRequest)
            )
        } else if (ballType in listOf(BallType.WIDE, BallType.NO_BALL)) {
            newBall.run = Run(
                id = UUID.randomUUID().toString(),
                ballId = newBall.id,
                run = 1,
                runType = getRunType(ballRequest)
            )
        }
        return newBall
    }

    private fun getUpdatedOver(currentOver: Over, newBall: Ball): Over {
        return currentOver.copy(
            balls = currentOver.balls + newBall,
            ballCompleted = if (newBall.ballType in listOf(
                    BallType.WIDE,
                    BallType.NO_BALL
                )
            )
                currentOver.ballCompleted
            else
                currentOver.ballCompleted + 1
        )
    }

    private fun getUpdatedMatch(
        match: Match,
        currentInning: Inning,
        isSecondInning: Boolean
    ): Match {
        var updatedMatch = match
        updatedMatch = if (isSecondInning) {
            updatedMatch.copy(secondInning = currentInning)
        } else {
            updatedMatch.copy(firstInning = currentInning)
        }
        return updatedMatch.copy(
            status = getMatchStatus(updatedMatch),
            winningTeamId = getWinningTeam(updatedMatch)
        )
    }

    private fun getUpdatedInning(inning: Inning, ball: Ball, over: Over, isNewOver: Boolean, team: Team): Inning {
        var batterId = inning.currentBatterId
        var nonStrikerBatterId = inning.currentNonStrikeBatterId
        var lastBatterPosition = inning.lastBatterPosition
        if (ball.ballType == BallType.WICKET) {
            if (inning.lastBatterPosition < team.players.size - 1) {
                batterId = team.battingOrder!!.playerIdsOrder[inning.lastBatterPosition + 1]
            }
            lastBatterPosition += 1
        }
        if (ball.ballType == BallType.NORMAL && ball.run!!.run % 2 != 0) {
            batterId = nonStrikerBatterId.also { nonStrikerBatterId = batterId }
        }
        if (over.ballCompleted == 6) {
            batterId = nonStrikerBatterId.also { nonStrikerBatterId = batterId }
        }
        var updatedInning = inning.copy(
            currentBatterId = batterId,
            currentNonStrikeBatterId = nonStrikerBatterId,
            lastBatterPosition = lastBatterPosition,
        )
        updatedInning = if (isNewOver) {
            updatedInning.copy(overs = updatedInning.overs + over)
        } else {
            updatedInning.copy(overs = updatedInning.overs.dropLast(1) + over)
        }
        return updatedInning
    }

    private fun isCurrentOverComplete(over: Over) = over.ballCompleted == 6

    private fun getNewInning(team: Team) = Inning(
        id = UUID.randomUUID().toString(),
        teamId = team.id,
        currentBatterId = team.battingOrder!!.playerIdsOrder.first(),
        currentNonStrikeBatterId = team.battingOrder.playerIdsOrder[1],
        lastBatterPosition = 1
    )

    private fun getNewOver(inningId: String, overNumber: Int) = Over(
        id = UUID.randomUUID().toString(),
        inningId = inningId,
        overNumber = overNumber,
        ballCompleted = 0
    )

    private fun getNewBattingTeam(match: Match): Team {
        if (match.firstInning == null) {
            return match.teamOne
        }
        return match.teamTwo
    }

    private fun getMatchStatus(match: Match): MatchStatus {
        if (match.firstInning == null || match.secondInning == null) {
            return MatchStatus.LIVE
        }
        if (match.winningTeamId != null) {
            return MatchStatus.FINISHED
        }
        val currentInning = match.secondInning
        if (currentInning.overs.size == match.oversLimit && currentInning.overs.lastOrNull()?.ballCompleted == 6) {
            return MatchStatus.FINISHED
        }
        return MatchStatus.LIVE
    }

    private fun getWinningTeam(match: Match): String? {
        val firstTeamRun = match.firstInning?.overs?.flatMap { it.balls }?.sumBy { it.run?.run ?: 0 }
        val secondTeamRun = match.secondInning?.overs?.flatMap { it.balls }?.sumBy { it.run?.run ?: 0 }
        if (secondTeamRun == null || firstTeamRun == null) {
            return null
        }
        if (secondTeamRun > firstTeamRun) return match.teamTwo.id
        if (isCurrentInningComplete(match) && firstTeamRun > secondTeamRun) return match.teamOne.id
        return null
    }
}

data class MatchCreateRequest(
    val playerSize: Int,
    val overs: Int
)
