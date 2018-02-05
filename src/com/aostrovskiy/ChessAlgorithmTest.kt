@file:Suppress("RemoveEmptyParenthesesFromLambdaCall")

package com.aostrovskiy

fun main(args: Array<String>) {

    //прогнать регресс тесты будет не лишним
    regress()
    regressCombinations()

    val (m, n, init) = parseParams(args)
    fullStart(m, n, *init)
}

fun fullStart(m: RowSize, n: ColumnSize, vararg pieces: Char) {
    val combinations = combinations(charArrayOf(*pieces))
    var total: Long = 0

    println("total combinations: ${combinations.size}")

    val time = System.currentTimeMillis()

    ChessBoardEvaluator(m, n)() {

        for (comb in combinations) {
            val count = initStart(comb)
            println("For combination ${String(comb)}: $count")
            total += count
        }

        println("Total $total")

    }

    println("Process ended in ${(System.currentTimeMillis() - time) / 1000 }s")

}

//parsing something like 6 9 -q 1 -r 1 -b 1 -k 2 -n 1
fun parseParams(args: Array<String>): Triple<Int, Int, CharArray> {
    try {
        val m = args[0].toInt()
        val n = args[1].toInt()

        val slice = args.slice(2 until args.size)
        val init = slice
            .filterIndexed { index, _ ->
                index % 2 == 0
            }
            .mapIndexed { index, s ->
                s[1] to slice[index * 2 + 1].toInt()
            }.flatMap { (piece, count) ->
                (0 until count).map { piece }
            }.toCharArray()

        return Triple(m, n, init)
    } catch (e: Exception) {
        println("correct format is <m> <n> [< -type num >], working on defaults 6 9 -q 1 -r 1 -b 1 -k 2 -n 1")
        return parseParams("6 9 -q 1 -r 1 -b 1 -k 2 -n 1".split(" ").toTypedArray())
    }
}

fun regressCombinations() {
    check(combinations(charArrayOf(king)).size == 1)
    check(combinations(charArrayOf(king, queen)).size == 2)
    check(combinations(charArrayOf(king, king, queen)).size == 3)
    check(combinations(charArrayOf(king, queen, bishop)).size == 6)
    check(combinations(charArrayOf(king, queen, bishop, bishop)).size == 12)
    check(combinations(charArrayOf(king, queen, bishop, castle)).size == 24)
    check(combinations(charArrayOf(king, queen, bishop, castle, knight)).size == 120)
}

private fun regress() {

    fun check(m: Int, n: Int, expected: Long, vararg box: Char) = check(
        ChessBoardEvaluator(m, n)
            .initStart(charArrayOf(*box))
            .also {
                print("count = $it")
            } == expected

    )

    check(3, 2, 13, knight, knight)
    check(2, 2, 6, knight, knight)
    check(2, 2, 4, knight)

    check(3, 3, 8, king, king, king)

    check(2, 2, 0, king, king)
    check(2, 2, 4, king)

    check(2, 3, 2, queen, queen)

    check(2, 2, 0, bishop, castle)
    check(2, 2, 0, castle, bishop)

    check(3, 2, 11, bishop, bishop)
    check(2, 2, 4, bishop, bishop)

    check(2, 3, 0, queen, queen, queen)

    check(2, 2, 4, castle)
    check(2, 2, 2, castle, castle)
    check(3, 3, 6, castle, castle, castle)

    check(2, 1, 0, castle, castle)
}

private fun debugAttack() {
    ChessBoardEvaluator(5, 5)() {
        initBoard()
            .markKnight(12)
            .debug()

        initBoard()
            .markKnight(0)
            .markKnight(4)
            .markKnight(24)
            .markKnight(20)
            .debug()

        initBoard()
            .markKing(0)
            .markKing(6)
            .debug()
        initBoard()
            .markCastle(0)
            .markCastle(4)
            .markCastle(14)
            .markCastle(20)
            .markCastle(24)
            .debug()

        initBoard()
            .markBishop(0)
            .markBishop(1)
            .debug()

        initBoard()
            .markBishop(12)
            .debug()

        initBoard()
            .markBishop(13)
            .markBishop(14)
            .debug()

        initBoard()
            .markBishop(24)
            .markBishop(23)
            .debug()

        initBoard()
            .markQueen(11)
            .debug()
    }
}