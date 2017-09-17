package com.trustmatrix

import javafx.scene.paint.Color
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Math.random
import java.util.stream.Collectors

enum class GameChoice {
    COOPERATE, CHEAT
}

data class GameResult(val leftIncome: Int, val rightIncome: Int, val leftChoice: GameChoice, val rightChoice: GameChoice)

data class PersonalGameResult(val myIncome: Int, val opponentIncome: Int, val myChoice: GameChoice, val opponentChoice: GameChoice) {
    constructor(ind: GameResult, left: Boolean) : this(
            if (left) ind.leftIncome else ind.rightIncome,
            if (left) ind.rightIncome else ind.leftIncome,
            if (left) ind.leftChoice else ind.rightChoice,
            if (left) ind.rightChoice else ind.leftChoice)
}

class Game(val rule: (leftChoice: GameChoice, rightChoice: GameChoice) -> GameResult)

class GameHistory(val game: Game) {
    var currentRound = 0
    val gameResults: MutableList<GameResult> = ArrayList<GameResult>()
}

class PersonalGameHistory(val gameHistory: GameHistory, val left: Boolean) {
    val results
        get() = gameHistory.gameResults.map { PersonalGameResult(it, left) }

}

class Strategy(val play: (PersonalGameHistory) -> GameChoice, val color: Color, val name: String = "default") {
    companion object defaults {
        val alwaysCooperate = Strategy({ GameChoice.COOPERATE }, Color.WHITE, "alwaysCooperate")
        val alwaysCheat = Strategy({ GameChoice.CHEAT }, Color.BLACK, "alwaysCheat")
        val anEyeForAnEye = Strategy(
                {
                    if (it.gameHistory.currentRound == 0) {
                        GameChoice.COOPERATE
                    } else {
                        it.results[it.gameHistory.currentRound - 1].opponentChoice
                    }
                }, Color.BLUE, "anEyeForAnEye")
    }

    override fun toString(): String {
        return name
    }
}

class Player(val strategy: Strategy) {
    var generationIncomeToShow: Int = 0
    var generationOpponentsMet: Int = 0
    var generationIncome: Int = 0
    var gamePosition: GamePosition? = null

    companion object {
        val log: Logger = LoggerFactory.getLogger(Player::class.java)
    }

    var color: Color = strategy.color
    fun mutate(): Player {
        val mutateIncomeModifier = generationIncome
        val gamePosition = gamePosition
        if (gamePosition == null) {
            log.error("Strange things happen: player without position plays");
            throw RuntimeException("Player without position cannot play");
        }
        generationIncome = 0
        log.debug("Mutate player on (${gamePosition.i}, ${gamePosition.j}) of ${strategy} with modifier ${mutateIncomeModifier} " +
                "and opponents met in this generation ${generationOpponentsMet}")
        generationOpponentsMet = 0

        val strongestNeighbour = gamePosition.neighborPositions.stream()
                .map { it.player }.max({ player1, player2 -> player1.generationIncomeToShow.compareTo(player2.generationIncomeToShow) })
                .orElseThrow { RuntimeException("") } /*Strategy.alwaysCheat*/
        log.debug("Strongest neighbour is in ${strongestNeighbour.gamePosition?.coordinateText()} " +
                "and have ${strongestNeighbour.strategy} strategy " +
                "with ${strongestNeighbour.generationIncomeToShow} generation income")

        if (strategy != strongestNeighbour.strategy && mutateIncomeModifier < strongestNeighbour.generationIncomeToShow) {
            val mutateTo = strongestNeighbour.strategy
            return Player(mutateTo)
        }

        if (strategy != Strategy.alwaysCheat && random() > 0.999) {
            log.debug("Spontaneously become always cheat")
            return Player(Strategy.alwaysCheat)
        }
        if (strategy != Strategy.alwaysCooperate && random() > 0.999) {
            log.debug("Spontaneously become always cooperate")
            return Player(Strategy.alwaysCooperate)
        }
        if (strategy != Strategy.anEyeForAnEye && random() > 0.999) {
            log.debug("Spontaneously become eye for an eye")
            return Player(Strategy.anEyeForAnEye)
        }
        return this
    }
}

class GamePosition(val i: Int, val j: Int, var player: Player, val determineNeighbors: List<() -> GamePosition>) {
    var nextPlayer: Player? = null

    init {
        player.gamePosition = this
    }

    val neighborPositions by lazy { determineNeighbors.stream().map { it() }.collect(Collectors.toList()) }
    fun prepareForGame() {
        player = nextPlayer ?: player
    }

    public operator fun compareTo(gamePosition: GamePosition): Int {
        if (this.i == gamePosition.i) return this.j.compareTo(gamePosition.j)
        return this.i.compareTo(gamePosition.i)
    }

    fun mutate() {
        val mutatePlayer = player.mutate()
        if (player === mutatePlayer) {
            nextPlayer = null
        } else {
            mutatePlayer.gamePosition = this
            nextPlayer = mutatePlayer
        }
    }

    fun color() = player.color
    fun coordinateText() = "(${i}, ${j})"

    public override fun toString(): String {
        return coordinateText()
    }
}

data class Neighbourhood(var leftPosition: GamePosition, var rightPosition: GamePosition) {
    init {
        if (leftPosition > rightPosition) {
            leftPosition = rightPosition.also { rightPosition = leftPosition }
        }
    }

    fun playGame(roundsNumber: Number, game: Game) {
        val gameHistory = GameHistory(game)
        val leftGameHistory = PersonalGameHistory(gameHistory, true)
        val rightGameHistory = PersonalGameHistory(gameHistory, false)

        List(roundsNumber.toInt(), {
            leftPosition.prepareForGame()
            rightPosition.prepareForGame()
            val leftChoice = leftPosition.player.strategy.play(leftGameHistory)
            val rightChoice = rightPosition.player.strategy.play(rightGameHistory)
            val gameResult = game.rule(leftChoice, rightChoice)
            gameHistory.gameResults.add(gameResult)
            leftPosition.player.generationIncome += gameResult.leftIncome
            rightPosition.player.generationIncome += gameResult.rightIncome
            gameHistory.currentRound++
        })
        leftPosition.player.generationOpponentsMet++
        rightPosition.player.generationOpponentsMet++
        leftPosition.player.generationIncomeToShow = leftPosition.player.generationIncome
        rightPosition.player.generationIncomeToShow = rightPosition.player.generationIncome
    }
}


class TrustMatrix(val xDimension: Int, val yDimension: Int,
                  val initialDistribution: (i: Int, j: Int) -> Player = TrustMatrix.ALL_ALWAYS_CHEAT_DISTR,
                  val roundsNumber: Number = 20,
                  val game: Game = TrustMatrix.DEFAULT_DILEMMA_GAME) {
    companion object defaults {
        val ALL_ALWAYS_COOPERATE_DISTR: (i: Int, j: Int) -> Player = { _, _ -> Player(Strategy.alwaysCooperate) }
        val ALL_ALWAYS_CHEAT_DISTR: (i: Int, j: Int) -> Player = { _, _ -> Player(Strategy.alwaysCheat) }
        val DEFAULT_DILEMMA_GAME: Game = Game({ leftChoice, rightChoice ->
            GameResult(
                    if (leftChoice == rightChoice) {
                        if (leftChoice == GameChoice.COOPERATE) 2 else 0
                    } else {
                        if (leftChoice == GameChoice.COOPERATE) -1 else 3
                    }
                    ,
                    if (leftChoice == rightChoice) {
                        if (leftChoice == GameChoice.COOPERATE) 2 else 0
                    } else {
                        if (leftChoice == GameChoice.COOPERATE) 3 else -1
                    }, leftChoice, rightChoice)
        })
    }

    val log: Logger = LoggerFactory.getLogger(TrustMatrix::class.java)

    val positionMatrix = Array(yDimension, { i ->
        Array(xDimension, { j ->
            GamePosition(i, j, initialDistribution(i, j), arrayListOf(
                    { getGamePositionByCoordinates(i - 1, j - 1) },
                    { getGamePositionByCoordinates(i - 0, j - 1) },
                    { getGamePositionByCoordinates(i - 1, j - 0) },
                    { getGamePositionByCoordinates(i + 1, j + 1) },
                    { getGamePositionByCoordinates(i + 1, j + 0) },
                    { getGamePositionByCoordinates(i + 0, j + 1) },
                    { getGamePositionByCoordinates(i - 1, j + 1) },
                    { getGamePositionByCoordinates(i + 1, j - 1) })
            )
        })
    })

    fun getGamePositionByCoordinates(i: Int, j: Int): GamePosition {
        //THIS IS TOR
        val iOffset = i % yDimension
        val jOffset = j % xDimension
        val realI = if (iOffset >= 0) iOffset else yDimension + iOffset
        val realJ = if (jOffset >= 0) jOffset else xDimension + jOffset
        return positionMatrix[realI][realJ]
    }

    fun game() {
        neighbourhood.forEach {
            it.playGame(roundsNumber, game)
        }
    }

    fun mutate() {
        positionMatrix.forEach { it.forEach { it.mutate() } }
        //positionMatrix[round(random() * yDimension).toInt()][round(random() * xDimension).toInt()].mutate()
    }

    fun generate() {
        game()
        mutate()
    }

    val neighbourhood: Set<Neighbourhood>


    init {
        val builtNeighbourhood: MutableSet<Neighbourhood> = HashSet<Neighbourhood>()

        log.info("Prepare neighbourhood")
        positionMatrix.iterator().forEach {
            it.iterator().forEach {
                val position = it
                it.neighborPositions.forEach {
                    log.trace("Prepare neighbourhood for ${position} and ${it}")
                    builtNeighbourhood.add(Neighbourhood(position, it))
                }
            }
        }
        log.info("Prepared ${builtNeighbourhood.size} neighbourhoods")
        neighbourhood = builtNeighbourhood
    }
}
