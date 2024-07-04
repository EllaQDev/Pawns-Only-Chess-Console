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
var noPawnTrigger = false
fun main() {
    val gameBoard = List<MutableList<Cell>>(8) { rank -> MutableList<Cell>(8) {
        file ->
        var token = " "
        val currCell : Cell = Cell(NUM_SQUARES_PER_SIDE - rank, (('a' + file).toString()))
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
                noPawnTrigger = false
                val valid = checkValidMove(response, board, count)
                if (!valid && !noPawnTrigger) {
                    println("Invalid Input")
                } else if (!valid) {

                } else {
                    printGameBoard(board)
                    break
                }
            }
        } else {
            while (true) {
                println("$secondPlayer's turn:")
                val response = readln()
                if (response == "exit") {
                    println("Bye!")
                    break@outer
                }
                noPawnTrigger = false
                val valid = checkValidMove(response, board, count)
                if (!valid && !noPawnTrigger) {
                    println("Invalid Input")

                } else if (!valid) {

                } else {
                    printGameBoard(board)
                    break
                }
            }
        }

        count++
    }

}
fun checkValidMove(move: String, board: List<MutableList<Cell>>, count: Int): Boolean {

    val allValidSquaresRegex = "[a-h][1-8][a-h][1-8]".toRegex()
    if (!allValidSquaresRegex.matches(move)) return false
    val startFile = move[0]
    val startRank = move[1]
    //println(startFile.toString() + startRank)
    val cellOnBoard = board.flatten().filter {it.file == startFile.toString() && it.rank == startRank.digitToInt() }[0]
    //println("tried to find $startFile$startRank:${cellOnBoard[0].file}${cellOnBoard[0].rank}")
    val pieceAtStart = cellOnBoard.piece
    if (count % 2 == 0) {
        if (pieceAtStart != null) {
            if(pieceAtStart.color != PlayerColor.WHITE) {
                println("No white pawn at $startFile$startRank")
                noPawnTrigger = true
                return false
            }
            if (cellOnBoard.rank == 2) {
                val allowedStartingMovesRegex = "$startFile[${startRank + 1}${startRank + 2}]".toRegex()
                //println(allowedStartingMovesRegex.toString())
                //println(move.substring(2,3))
                val validMove = allowedStartingMovesRegex.matches(move.substring(2,4))
                if (validMove) {
                    val moveMade = makeMove(move, cellOnBoard, pieceAtStart, board, PlayerColor.WHITE)
                    return moveMade
                } else {
                    return false
                }
            } else if (cellOnBoard.rank != 1){
                //cover all other ranks to move just one, except rank 1
                val allowedMoveRegex = "$startFile${startRank + 1}".toRegex()
                val validMove = allowedMoveRegex.matches(move.substring(2,4))
                if (validMove) {
                    val moveMade = makeMove(move, cellOnBoard, pieceAtStart, board, PlayerColor.WHITE)
                    return moveMade
                } else {
                    return false
                }
            } else {
                //rank 1 move
                return false
            }
        } else {
            println("No white pawn at $startFile$startRank")
            noPawnTrigger = true
            return false
        }
    } else {
        if (pieceAtStart != null) {
            if(pieceAtStart.color != PlayerColor.BLACK) {
                println("No black pawn at $startFile$startRank")
                noPawnTrigger = true
                return false
            }
            if (cellOnBoard.rank == 7) {
                val allowedStartingMovesRegex = "$startFile[${startRank - 2}${startRank - 1}]".toRegex()
//                println(allowedStartingMovesRegex.toString())
//                println(move.substring(2,3))
                val validMove = allowedStartingMovesRegex.matches(move.substring(2,4))
                if (validMove) {
                    val moveMade = makeMove(move, cellOnBoard, pieceAtStart, board, PlayerColor.BLACK)
                    return moveMade
                } else {
                    return false
                }
            } else if (cellOnBoard.rank != 8) {
                //cover all other ranks to move just one
                val allowedMoveRegex = "$startFile${startRank - 1}".toRegex()
                val validMove = allowedMoveRegex.matches(move.substring(2,4))
                if (validMove) {
                    val moveMade = makeMove(move, cellOnBoard, pieceAtStart, board, PlayerColor.BLACK)
                    return moveMade
                } else {
                    return false
                }
            } else {
                //rank 8 move
                return false
            }
        } else {
            println("No black pawn at $startFile$startRank")
            noPawnTrigger = true
            return false
        }
    }
    return true
}
fun makeMove(move: String, cell: Cell, piece: Piece, board: List<MutableList<Cell>>, playerColor: PlayerColor) : Boolean{
    // get target cell
    val targetCell = board.flatten().first { it.file == move[2].toString() && it.rank == move[3].digitToInt() }
    // reject move made onto occupied cell
    if (targetCell.piece?.color == PlayerColor.WHITE || targetCell.piece?.color == PlayerColor.BLACK) {
        return false
    }
    cell.piece = null
    //change target cell piece
    targetCell.piece = Piece(playerColor)
    return true
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