package main.java.com.aostrovskiy

typealias Board = CharArray
typealias Position = Int
typealias RowSize = Int
typealias ColumnSize = Int
typealias Piece = Char

const val none: Piece = ' '
const val attacked: Piece = '+'
const val king: Piece = 'k'
const val queen: Piece = 'q'
const val castle: Piece = 'r'
const val bishop: Piece = 'b'
const val knight: Piece = 'n'

@Suppress("NOTHING_TO_INLINE")
class ChessBoardEvaluator(
    val m: RowSize,
    val n: ColumnSize
) {

    fun initBoard(): Board = CharArray(m * n) { none }

    operator fun <T> invoke(runnable: ChessBoardEvaluator.() -> T): T {
        return this.runnable()
    }

    //mark и check для максимального быстродействия дублированы
    private  fun Board.checkBishop(pos: Position): Boolean {

        val col = pos % n

        var p = pos
        var c = col
        while (p < size && c < n) {
            if (this[p] != none && this[p] != attacked) {
                return false
            }
            p += n + 1
            c += 1
        }

        p = pos
        c = col
        while (p < size && c >= 0) {
            if (this[p] != none && this[p] != attacked) {
                return false
            }
            p += n - 1
            c -= 1
        }

        p = pos
        c = col
        while (p >= 0 && c >= 0) {
            if (this[p] != none && this[p] != attacked) {
                return false
            }
            p -= n + 1
            c -= 1
        }

        p = pos
        c = col
        while (p >= 0 && c < n) {
            if (this[p] != none && this[p] != attacked) {
                return false
            }
            p -= n - 1
            c += 1
        }

        return true
    }

    internal  fun Board.markBishop(pos: Position): Board {

        val col = pos % n

        var p = pos
        var c = col
        while (p < size && c < n) {
            this[p] = attacked
            p += n + 1
            c += 1
        }

        p = pos
        c = col
        while (p < size && c >= 0) {
            this[p] = attacked
            p += n - 1
            c -= 1
        }

        p = pos
        c = col
        while (p >= 0 && c >= 0) {
            this[p] = attacked
            p -= n + 1
            c -= 1
        }

        p = pos
        c = col
        while (p >= 0 && c < n) {
            this[p] = attacked
            p -= n - 1
            c += 1
        }

        return this
    }

    private  fun Board.checkKnight(pos: Int): Boolean {
        val row = pos / n
        val col = pos % n

        if (col + 1 < n && row - 2 >= 0) {
            if (this[pos - n - n + 1].hits()) return false
        }

        if (col + 2 < n && row - 1 >= 0) {
            if (this[pos - n + 2].hits()) return false
        }

        if (col + 2 < n && row + 1 < m) {
            if (this[pos + n + 2].hits()) return false
        }

        if (col + 1 < n && row + 2 < m && pos + n + n + 1 < size) {
            if (this[pos + n + n + 1].hits()) return false
        }
//
        if (col - 1 >= 0 && row + 2 < m) {
            if (this[pos + n + n - 1].hits()) return false
        }

        if (col - 2 >= 0 && row + 1 < m) {
            if (this[pos + n - 2].hits()) return false
        }

        if (col - 2 >= 0 && row - 1 >= 0) {
            if (this[pos - n - 2].hits()) return false
        }

        if (col - 1 >= 0 && row - 2 >= 0) {
            if (this[pos - n - n - 1].hits()) return false
        }

        return true
    }

    internal  fun Board.markKnight(pos: Int): Board {
        val row = pos / n
        val col = pos % n

        //обходим по часовой стрелке начиная с вернего левого
        if (col + 1 < n && row - 2 >= 0) {
            this[pos - n - n + 1] = attacked
        }

        if (col + 2 < n && row - 1 >= 0) {
            this[pos - n + 2] = attacked
        }

        if (col + 2 < n && row + 1 < m) {
            this[pos + n + 2] = attacked
        }

        if (col + 1 < n && row + 2 < m) {
            this[pos + n + n + 1] = attacked
        }

        if (col - 1 >= 0 && row + 2 < m) {
            this[pos + n + n - 1] = attacked
        }

        if (col - 2 >= 0 && row + 1 < m) {
            this[pos + n - 2] = attacked
        }

        if (col - 2 >= 0 && row - 1 >= 0) {
            this[pos - n - 2] = attacked
        }

        if (col - 1 >= 0 && row - 2 >= 0) {
            this[pos - n - n - 1] = attacked
        }

        return this
    }

    private  fun Board.checkQueen(pos: Int): Boolean = checkBishop(pos) && checkCastle(pos)

    internal  fun Board.markQueen(pos: Position): Board = markBishop(pos).markCastle(pos)

    private  fun Board.checkCastle(pos: Int): Boolean {
        val col = pos % n

        for (i in pos - col until pos - col + n) {
            if (this[i] != none && this[i] != attacked) {
                return false
            }
        }

        var c = col
        for (i in 0 until m) {
            if (this[c] != none && this[c] != attacked) {
                return false
            }

            c += n
        }

        return true
    }

    internal  fun Board.markCastle(pos: Position): Board {

        val col = pos % n

        for (i in pos - col until pos - col + n) {
            this[i] = attacked
        }

        var c = col
        for (i in 0 until m) {
            this[c] = attacked
            c += n
        }

        return this
    }

    private  fun Board.checkKing(pos: Int): Boolean {
        val row = pos / n
        val col = pos % n

        if (col + 1 < n) {
            if (this[pos + 1].hits()) {
                return false
            }
        }

        if (col > 0) {
            if (this[pos - 1].hits()) {
                return false
            }
        }

        if (row > 0) {
            if (this[pos - n].hits()) {
                return false
            }

            if (col > 0 && this[pos - n - 1].hits()) {
                return false
            }

            if (col + 1 < n && this[pos - n + 1].hits()) {
                return false
            }
        }

        if (row + 1 < m) {
            if (this[pos + n].hits()) {
                return false
            }

            if (col > 0 && this[pos + n - 1].hits()) {
                return false
            }

            if (col + 1 < n && this[pos + n + 1].hits()) {
                return false
            }
        }

        return true
    }

    internal  fun Board.markKing(pos: Position): Board {
        val row = pos / n
        val col = pos % n

        if (col + 1 < n) {
            this[pos + 1] = attacked
        }

        if (col > 0) {
            this[pos - 1] = attacked
        }

        if (row > 0) {
            this[pos - n] = attacked

            if (col > 0) {
                this[pos - n - 1] = attacked
            }
            if (col + 1 < n) {
                this[pos - n + 1] = attacked
            }
        }

        if (row + 1 < m) {
            this[pos + n] = attacked

            if (col > 0) {
                this[pos + n - 1] = attacked
            }

            if (col + 1 < n) {
                this[pos + n + 1] = attacked
            }
        }

        return this
    }

    //проверяем что под ударом фигура а не пустое поле/поле под ударом
    private  fun Piece.hits() = this != none && this != attacked


    //проверяет, допустимо ли поставить
    private fun check(board: Board, current: Piece, currPos: Position): Boolean {
        if (board[currPos] != none) {
            return false
        }

        return when (current) {
            king -> board.checkKing(currPos)
            queen -> board.checkQueen(currPos)
            castle -> board.checkCastle(currPos)
            bishop -> board.checkBishop(currPos)
            knight -> board.checkKnight(currPos)
            else -> true
        }
    }

    fun initStart(box: CharArray) = start(CharArray(m * n) { none }, 0, box)

    private fun start(board: Board, last: Position, box: CharArray): Long {

        var count: Long = 0

        if (box.isEmpty()) {
//            board.debug()
            return 1
        }

        val current = box[0]

        //берем первую фигуру из коробки с оставшимися фирурами
        //повторяем начиная от последней провереной позиции и до конца доски
        @Suppress("LoopToCallChain")
        for (i in last until board.size) {

            if (check(board, current, i)) {

                count += start(
                    cloneAndMark(board, i, current),
                    i + 1,
                    box.slice(1 until box.size).toCharArray()
                )
            }
        }

        return count
    }

    private fun cloneAndMark(board: Board, position: Position, current: Piece): Board =
        charArrayOf(*board).apply {
            when (current) {
                queen -> markQueen(position)
                king -> markKing(position)
                bishop -> markBishop(position)
                castle -> markCastle(position)
                knight -> markKnight(position)
            }
            this[position] = current
        }

    fun Board.debug() {
        var k = 0
        println("---------")
        for (i in 0 until m) {
            print("|")
            for (j in 0 until n) {
                print(this[k++])
            }
            println("|")
        }
        println("---------")
    }
}

fun combinations(pieces: CharArray): List<CharArray> {
    val result = mutableSetOf<String>()

    fun comb(list: MutableSet<String>, _in: CharArray, _out: CharArray) {
        if (_in.isEmpty()) {
            list.add(String(_out))
        }

        for (i in 0 until _in.size) {
            val out = charArrayOf(_in[i], *_out)

            val newIn = charArrayOf(
                *_in.sliceArray(0 until i),
                *_in.sliceArray(i + 1 until _in.size))

            comb(list, newIn, out)
        }
    }

    comb(result, charArrayOf(*pieces), charArrayOf())

    return result.map { it.toCharArray() }
}