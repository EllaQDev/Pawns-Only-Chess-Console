//objective: print game title and starting board:
//  Pawns-Only Chess
//  +---+---+---+---+---+---+---+---+
//8 |   |   |   |   |   |   |   |   |
//  +---+---+---+---+---+---+---+---+
//7 | B | B | B | B | B | B | B | B |
//  +---+---+---+---+---+---+---+---+
//6 |   |   |   |   |   |   |   |   |
//  +---+---+---+---+---+---+---+---+
//5 |   |   |   |   |   |   |   |   |
//  +---+---+---+---+---+---+---+---+
//4 |   |   |   |   |   |   |   |   |
//  +---+---+---+---+---+---+---+---+
//3 |   |   |   |   |   |   |   |   |
//  +---+---+---+---+---+---+---+---+
//2 | W | W | W | W | W | W | W | W |
//  +---+---+---+---+---+---+---+---+
//1 |   |   |   |   |   |   |   |   |
//  +---+---+---+---+---+---+---+---+
//    a   b   c   d   e   f   g   h
const val NUM_SQUARES_PER_SIDE = 8
fun main() {
    val gameBoard = List<MutableList<Cell>>(8) { rank -> MutableList<Cell>(8) {
        file ->
        var token = " "
        var currCell : Cell = Cell(NUM_SQUARES_PER_SIDE - rank, (('a' + file).toString()))
        if (rank == 1) token = "B"
        if (rank == 6) token = "W"
        if (token == "B") {
            currCell.piece = Piece(PlayerColor.BLACK)
        }
        if (token == "W") {
            currCell.piece = Piece(PlayerColor.WHITE)
        }
        currCell
    } }
    playGame(gameBoard)
}

fun playGame(board: List<MutableList<Cell>>) {
    printTitle()
    val firstPlayer = promptPlayer("First")
    val secondPlayer = promptPlayer("Second")
    printGameBoard(board)
    var count = 0
    outer@while (true) {
        if (count % 2 == 0) {
            while (true) {
                println("$firstPlayer's turn:")
                val response = readln()
                if (response == "exit") {
                    println("Bye!")
                    break@outer
                }
                val valid = checkValidMove(response)
                if (!valid) println("Invalid Input") else break
            }
        } else {
            while (true) {
                println("$secondPlayer's turn:")
                val response = readln()
                if (response == "exit") {
                    println("Bye!")
                    break@outer
                }
                val valid = checkValidMove(response)
                if (!valid) println("Invalid Input") else break
            }
        }

        count++
    }

}
fun checkValidMove(move: String): Boolean {
    val validSquaresRegex = "[a-h][1-8][a-h][1-8]".toRegex()
    return validSquaresRegex.matches(move)
}
fun printTitle() {
    println("Pawns-Only Chess")
}
fun promptPlayer(label: String) : String{
    println("$label Player's name:")
    return readln()
}
fun printGameBoard(board: List<MutableList<Cell>>) {

    board.forEachIndexed(){ i, rank -> println(rank.joinToString(prefix = "  +---+---+---+---+---+---+---+---+\n${rank.size - i} | ", separator = " | ", postfix = " |"))}
    println("  +---+---+---+---+---+---+---+---+")
    println(('a'..'h').joinToString(prefix = "    ", separator = "   ", postfix = "  "))
}

class Cell(val rank : Int, val file: String, var piece: Piece? = null) {
    override fun toString(): String {
        //return "$rank, $file"
        return piece?.color?.symbol ?: " "
    }
}

class Piece(val color: PlayerColor)

enum class PlayerColor(val symbol: String) {
    BLACK("B"),
    WHITE("W")
}