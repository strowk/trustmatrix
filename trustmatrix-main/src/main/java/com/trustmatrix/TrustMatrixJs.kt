/*-
 * #%L
 * trustmatrix-main
 * %%
 * Copyright (C) 2017 Timur Sultanaev
 * %%
 * Licensed under the MIT license.
 * See LICENSE file in the root of project for details.
 * #L%
 */
package com.trustmatrix

import com.trustmatrix.Player.Companion.DEFAULT_MUTATIONS
import kotlin.js.Math
import kotlin.reflect.KClass

class Random {
    fun nextDouble(): Double {
        return Math.random().toDouble();
    }

    fun nextInt(): Int {
        return (Math.random() * (Int.MAX_VALUE - 1)).toInt()
    }

    fun nextInt(max: Int): Int {
        return (Math.random() * max).toInt()
    }
}

class LoggerFactory {
    companion object {
        fun getLogger(clazz: KClass<dynamic>): Logger {
            return Logger()
        }
    }

}

class Logger {

    fun error(message: String) {
        console.error(message)
    }

    fun info(message: String) {
        console.info(message)
    }

    fun debug(message: String) {
        console.log("DEBUG: ${message}")
    }

    fun trace(message: String) {
//        console.log("TRACE: ${message}")
    }
}

enum class Color {
    WHITE, BLACK, BLUE, YELLOW
}


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
        val smartOne = Strategy(
                {
                    if (it.gameHistory.currentRound == 0) {
                        GameChoice.CHEAT
                    } else {
                        if (it.gameHistory.currentRound == 1) {
                            GameChoice.COOPERATE
                        } else {
                            val firstOpponentChoice = it.results[0].opponentChoice
                            val secondOpponentChoice = it.results[1].opponentChoice
                            if (firstOpponentChoice == GameChoice.CHEAT && secondOpponentChoice == GameChoice.CHEAT) {
                                //we play with always cheat - let's not be tricked anymore
                                GameChoice.CHEAT
                            }
                            if (firstOpponentChoice == GameChoice.COOPERATE && secondOpponentChoice == GameChoice.CHEAT) {
                                //we play with eye for an eye - let's be good
                                GameChoice.COOPERATE
                            }
                            if (firstOpponentChoice == GameChoice.CHEAT && secondOpponentChoice == GameChoice.COOPERATE) {
                                //we play with ourselves - let's be good
                                GameChoice.COOPERATE
                            }
                            if (firstOpponentChoice == GameChoice.COOPERATE && secondOpponentChoice == GameChoice.COOPERATE) {
                                //we play with always cooperate - we can be bad finally
                                GameChoice.CHEAT
                            }
                            //this we should not reach but however let's be good for a change
                            GameChoice.COOPERATE
                        }

                    }
                }, Color.YELLOW, "smartOne")
    }

    override fun toString(): String {
        return name
    }
}

abstract class PlayerMutation {
    abstract fun mutate(player: Player): Player?;
}

class SimpleStrongestNeighbourMutation(val random: Random = Random(), val distortion: Double = 0.5) : PlayerMutation() {
    companion object {
        val log: Logger = LoggerFactory.getLogger(SimpleStrongestNeighbourMutation::class)
    }

    override fun mutate(player: Player): Player? {
        //TODO add generic probability
        if (random.nextDouble() > 1.0 - distortion) {
            return null
        }
        val gamePosition = player.gamePosition
        if (gamePosition == null) {
            log.error("Strange things happen: player without position plays");
            throw RuntimeException("Player without position cannot play");
        }
        val strongestNeighbour = gamePosition.neighborPositions
                .map { it.player }
                // we need shuffle here because in another case we would get some particular pattern in players distribution
                .sortedWith(getShuffleComparator(Player::class, random))
                .maxWith(kotlin.Comparator { player1, player2 -> player1.generationIncomeToShow.compareTo(player2.generationIncomeToShow) })
                ?: throw RuntimeException("")  /*Strategy.alwaysCheat*/
        log.debug("Strongest neighbour is in ${strongestNeighbour.gamePosition?.coordinateText()} " +
                "and have ${strongestNeighbour.strategy} strategy " +
                "with ${strongestNeighbour.generationIncomeToShow} generation income")
        if (player.strategy != strongestNeighbour.strategy && player.generationIncomeToShow < strongestNeighbour.generationIncomeToShow) {
            val mutateTo = strongestNeighbour.strategy
            return Player(mutateTo)
        }
        return null
    }
}

class SpawnMutation(val spawnStrategy: Strategy, val probability: Double = 0.001) : PlayerMutation() {
    companion object {
        val log: Logger = LoggerFactory.getLogger(SpawnMutation::class)
    }

    override fun mutate(player: Player): Player? {
        if (player.strategy != spawnStrategy && Random().nextDouble() > 1.0 - probability) {
            log.debug("${player} spontaneously become ${spawnStrategy}")
            return Player(spawnStrategy)
        }
        return null
    }
}

class SpawnMutationUniform(val spawnStrategies: Set<Strategy>,
                           val probability: Double = 0.001 * spawnStrategies.size,
                           val random: Random = Random()) : PlayerMutation() {
    override fun mutate(player: Player): Player? {
        val spawnHappened = random.nextDouble() > 1.0 - probability
        if (!spawnHappened) return null

        val spawnAllowedStrategies = spawnStrategies.filter { it != player.strategy }
        return Player(spawnAllowedStrategies[random.nextInt(spawnAllowedStrategies.size)])
    }
}

class SpawnStrategyControl(val spawnStrategy: Strategy, val probabilityOnGeneration: (Generation) -> Double = { 0.001 }) {

}

class Generation(val previous: Generation?, val number: Long = if (previous == null) 0 else (previous.number + 1),
                 val mutations: List<PlayerMutation> = if (previous == null) DEFAULT_MUTATIONS else previous.mutations) {
}

class SpawnMutationConfigurable(val spawnStrategies: Set<SpawnStrategyControl>,
                                val random: Random = Random()) : PlayerMutation() {
    override fun mutate(player: Player): Player? {
        //TODO should check if overhead
        val coin = random.nextDouble()
        var spawnPosition: Double = 0.0
        spawnStrategies.forEach {
            spawnPosition += it.probabilityOnGeneration(player.generation);
            if (spawnPosition > coin) {
                return Player(it.spawnStrategy)
            }
        }
        return null
    }
}

class Player(val strategy: Strategy,
             val random: Random = Random(),
             var generation: Generation = Generation(null)) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(Player::class)
        val DEFAULT_MUTATIONS = listOf(
                //SpawnMutation(Strategy.alwaysCheat),
                //SpawnMutation(Strategy.alwaysCooperate),
                //SpawnMutation(Strategy.anEyeForAnEye)
                SimpleStrongestNeighbourMutation(),
                SpawnMutationUniform(setOf(
                        Strategy.alwaysCheat
                        , Strategy.alwaysCooperate
                        , Strategy.anEyeForAnEye

                ))
        )
    }

    var generationIncomeToShow: Int = 0
    var generationOpponentsMet: Int = 0
    var generationIncome: Int = 0
    var gamePosition: GamePosition? = null


    override fun toString() = "${strategy} on ${gamePosition}"

    var color: Color = strategy.color
    fun mutate(mutations: List<PlayerMutation> = this.generation.mutations): Player {
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

        mutations.forEach {
            it.mutate(this).apply {
                if (this != null) {
                    return this
                }
            }
        }

        log.debug("No mutations applied, ${strategy} stays on ${gamePosition}")
        return this
    }
}

class GamePosition(val i: Int, val j: Int, var player: Player, val determineNeighbors: List<() -> GamePosition>) {
    var nextPlayer: Player? = null

    fun placePlayer(player: Player) {
        this.player = player
        player.gamePosition = this
    }

    init {
        player.gamePosition = this
    }

    val neighborPositions by lazy { determineNeighbors.map { it() }.toList() }
    fun prepareForGame(generation: Generation) {
        player = nextPlayer ?: player
        player.generation = generation
    }

    public operator fun compareTo(gamePosition: GamePosition): Int {
        if (this.i == gamePosition.i) return this.j.compareTo(gamePosition.j)
        return this.i.compareTo(gamePosition.i)
    }

    fun mutate(mutations: List<PlayerMutation>) {
        val mutatePlayer = player.mutate(mutations)
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
            //            leftPosition.prepareForGame()
//            rightPosition.prepareForGame()
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

fun <T> getShuffleComparator(clazz: KClass<dynamic>, random: Random): Comparator<T> = kotlin.Comparator { o1, o2 -> random.nextInt(2) - 1 }

enum class InitialDistribution(val player: (i: Int, j: Int) -> Player) {
    ALL_ALWAYS_COOPERATE({ _, _ -> Player(Strategy.alwaysCooperate) }),
    ALL_ALWAYS_CHEAT({ _, _ -> Player(Strategy.alwaysCheat) })
}

class TrustMatrix(val xDimension: Int, val yDimension: Int,
                  val initialDistribution: (i: Int, j: Int) -> Player = ALL_ALWAYS_COOPERATE_DISTR,
                  val roundsNumber: Number = 20,
                  val game: Game = TrustMatrix.DEFAULT_DILEMMA_GAME,
                  val mutations: List<PlayerMutation> = Player.DEFAULT_MUTATIONS,
                  val random: Random = Random()) {
    var generation = Generation(null, mutations = mutations)

    companion object defaults {
        val ALL_ALWAYS_COOPERATE_DISTR = InitialDistribution.ALL_ALWAYS_COOPERATE.player
        val ALL_ALWAYS_CHEAT_DISTR = InitialDistribution.ALL_ALWAYS_CHEAT.player
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

    val log: Logger = LoggerFactory.getLogger(TrustMatrix::class)

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

    fun play() {
        neighbourhood.forEach {
            it.playGame(roundsNumber, game)
        }
    }

    fun mutate() {
        positionMatrix.forEach { it.forEach { it.mutate(mutations) } }
    }

    fun generate() {
        play()
        mutate()
        generation = Generation(generation)
        positionMatrix.forEach { it.forEach { it.prepareForGame(generation) } }

    }

    val neighbourhood: Collection<Neighbourhood>


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
