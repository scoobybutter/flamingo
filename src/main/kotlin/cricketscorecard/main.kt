package cricketscorecard

import cricketscorecard.service.MatchCreateRequest
import cricketscorecard.service.MatchService

fun main() {
    val matchService = MatchService()
    println("No. of players for each team:")
    val playerSize = readLine()!!.toInt()
    println("No. of overs:")
    val overs = readLine()!!.toInt()
    var match = matchService.createMatch(MatchCreateRequest(playerSize = playerSize, overs = overs))
    val teamOnePlayers = mutableListOf<String>()
    for (i in 1..playerSize) {
        println("Batting Order for team 1:")
        val playerId = readLine()!!
        teamOnePlayers.add(playerId)
    }
    match = matchService.addPlayers(teamOnePlayers, match.id, match.teamOne.id)
    while (!matchService.isCurrentInningComplete(match)) {
        val ballRequest = readLine()!!
        match = matchService.addBall(matchId = match.id, ballRequest)
        if (match.firstInning!!.overs.last().ballCompleted == 6) {
            println(matchService.getScoreCard(matchId = match.id).playerStats)
            println(matchService.getScoreCard(matchId = match.id).teamStats)
        }
    }
    println(matchService.getScoreCard(matchId = match.id))
    val teamTwoPlayers = mutableListOf<String>()
    for (i in 1..playerSize) {
        println("Batting Order for team 2:")
        val playerId = readLine()!!
        teamTwoPlayers.add(playerId)
    }
    match = matchService.addPlayers(teamTwoPlayers, match.id, match.teamTwo.id)
    do {
        val ballRequest = readLine()!!
        match = matchService.addBall(matchId = match.id, ballRequest)
        println(matchService.getScoreCard(matchId = match.id).playerStats)
        println(matchService.getScoreCard(matchId = match.id).teamStats)
    } while (!matchService.isCurrentInningComplete(match))
    println(matchService.getScoreCard(matchId = match.id))
}
