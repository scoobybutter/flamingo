package cricketscorecard.repo

import cricketscorecard.domain.Match

class MatchRepository {
    private val matchIdMatchMap = emptyMap<String, Match>().toMutableMap()

    fun addOrUpdateMatch(id: String, match: Match): Match {
        matchIdMatchMap[id] = match
        return match
    }

    fun getMatch(id: String) = matchIdMatchMap[id]
}
