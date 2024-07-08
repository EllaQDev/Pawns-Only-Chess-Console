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
var lastMove = ""
fun main() {
    val gameBoard = List<MutableList<Cell>>(8) { rank -> MutableList<Cell>(8) {
        file ->
        var token = " "
        val currCell = Cell(NUM_SQUARES_PER_SIDE - rank, (('a' + file).toString()))
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
                val allowedStartingMoves = "$startFile[${startRank + 1}${startRank + 2}]"
                //println(allowedStartingMovesRegex.toString())
                //println(move.substring(2,3))
                val diagMoves = getPatternForDiagCaptures(move, board, PlayerColor.WHITE)
                var combinedAllowedMoves : String = allowedStartingMoves
                if (diagMoves != null){
                    combinedAllowedMoves += "|$diagMoves"
                }
                val combinedAllowedMoveRegex = combinedAllowedMoves.toRegex()
                val validMove = combinedAllowedMoveRegex.matches(move.substring(2,4))
                if (validMove == true) {
                    val moveMade = makeMove(move, cellOnBoard, pieceAtStart, board, PlayerColor.WHITE, false)
                    return moveMade
                } else {
                    return false
                }
            } else if (cellOnBoard.rank != 1){
                //cover all other ranks to move just one, except rank 1
                val allowedMove = "$startFile${startRank + 1}"
                val diagMoves = getPatternForDiagCaptures(move, board, PlayerColor.WHITE)
                var combinedAllowedMoves : String? = null
                combinedAllowedMoves = allowedMove
                if (diagMoves != null){
                    combinedAllowedMoves += "|$diagMoves"
                }
                //TODO (add if rank == 5)
                var enPassantFlag = false
                if (cellOnBoard.rank == 5){
                    val adjFiles = if (startFile != 'a' && startFile != 'h') listOf(startFile + 1, startFile - 1)
                    else if (startFile == 'a') listOf(startFile + 1) else listOf(startFile - 1)
                    val mutListEnpassantCandidates = mutableListOf<String>()
                    for (file in adjFiles) {
                        if (lastMove == "${file}7${file}5") {
                            mutListEnpassantCandidates.add("${startFile}5${file}6")
                        }
                    }
                    if (mutListEnpassantCandidates.isNotEmpty()){
                        combinedAllowedMoves +="|${mutListEnpassantCandidates[0]}"
                        enPassantFlag = true
                    }
                }

                val combinedAllowedMoveRegex = combinedAllowedMoves.toRegex()
                //println(combinedAllowedMoves)
                val validMove = combinedAllowedMoveRegex.matches(move.substring(2,4))
                val enPassantValid = if (enPassantFlag) combinedAllowedMoveRegex.matches(move) else false
                //val validMove = allowedMoveRegex.matches(move.substring(2,4))
                if (validMove || enPassantValid) {
                    val moveMade = makeMove(move, cellOnBoard, pieceAtStart, board, PlayerColor.WHITE, enPassantValid)
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
                val allowedStartingMoves = "$startFile[${startRank - 2}${startRank - 1}]"
//                println(allowedStartingMovesRegex.toString())
//                println(move.substring(2,3))
                val diagMoves = getPatternForDiagCaptures(move, board, PlayerColor.BLACK)
                var combinedAllowedMoves : String? = null
                combinedAllowedMoves = allowedStartingMoves
                if (diagMoves != null){
                    combinedAllowedMoves += "|$diagMoves"
                }
                val combinedAllowedMoveRegex = combinedAllowedMoves.toRegex()
                val validMove = combinedAllowedMoveRegex.matches(move.substring(2,4))
                if (validMove) {
                    val moveMade = makeMove(move, cellOnBoard, pieceAtStart, board, PlayerColor.BLACK, false)
                    return moveMade
                } else {
                    return false
                }
            } else if (cellOnBoard.rank != 8) {
                //cover all other ranks to move just one
                val allowedMove = "$startFile${startRank - 1}"
                val diagMoves = getPatternForDiagCaptures(move, board, PlayerColor.BLACK)
                var combinedAllowedMoves : String? = null
                combinedAllowedMoves = allowedMove
                if (diagMoves != null){
                    combinedAllowedMoves += "|$diagMoves"
                }
                //TODO ( add if rank == 4)
                var enPassantFlag = false
                if (cellOnBoard.rank == 4){
                    val adjFiles = if (startFile != 'a' && startFile != 'h') listOf(startFile + 1, startFile - 1)
                    else if (startFile == 'a') listOf(startFile + 1) else listOf(startFile - 1)
                    val mutListEnpassantCandidates = mutableListOf<String>()
                    for (file in adjFiles) {
                        if (lastMove == "${file}2${file}4") {
                            mutListEnpassantCandidates.add("${startFile}4${file}3")
                        }
                    }
                    if (mutListEnpassantCandidates.isNotEmpty()){
                        combinedAllowedMoves +="|${mutListEnpassantCandidates[0]}"
                        enPassantFlag = true
                    }
                }
                val combinedAllowedMoveRegex = combinedAllowedMoves.toRegex()

                val validMove = combinedAllowedMoveRegex.matches(move.substring(2,4))
                val enPassantValid = if (enPassantFlag) combinedAllowedMoveRegex.matches(move) else false
                if (validMove || enPassantValid) {
                    val moveMade = makeMove(move, cellOnBoard, pieceAtStart, board, PlayerColor.BLACK, enPassantValid)
                    //println(moveMade)
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
fun makeMove(move: String, cell: Cell, piece: Piece, board: List<MutableList<Cell>>, playerColor: PlayerColor, enPassantValid: Boolean) : Boolean{
    // get target cell
    val targetCell = board.flatten().first { it.file == move[2].toString() && it.rank == move[3].digitToInt() }
    // reject move made onto occupied cell if cell is in same file (move[0] == move[2])
    if (targetCell.piece?.color == PlayerColor.WHITE && move[0] == move[2] || targetCell.piece?.color == PlayerColor.BLACK && move[0] == move[2]) {
        return false
    }
    cell.piece = null
    //change target cell piece
    targetCell.piece = Piece(playerColor)
    if (enPassantValid && move[0] != move[2]) {
        val capturedCell = board.flatten().first { it.file == move[2].toString() && if (playerColor == PlayerColor.WHITE) it.rank == 5 else it.rank == 4 }
        capturedCell.piece = null
    }
    lastMove = move
    return true
}
fun getPatternForDiagCaptures(move: String, board: List<MutableList<Cell>>, playerColor: PlayerColor): String? {
    val file = move[0]
    val rank = move[1].digitToInt()
    val adjFiles = if (file != 'a' && file != 'h') listOf(file + 1, file - 1)
    else if (file == 'a') listOf(file + 1) else listOf(file - 1)
    val possibleCellsForCapture = mutableListOf<Cell>()
    for (opt in adjFiles) {
        //println(opt)
        possibleCellsForCapture.addAll(board.flatten().filter { it.file == opt.toString() &&
                if (playerColor == PlayerColor.WHITE) it.rank == rank + 1  else it.rank == rank - 1 })
    }
    val availableCellsForCapture = mutableListOf<Cell>()
    for (cell in possibleCellsForCapture) {
        if (playerColor == PlayerColor.WHITE) {
            if (cell.piece?.color == PlayerColor.BLACK ) {
                availableCellsForCapture.add(cell)
            }
        } else {
            if (cell.piece?.color == PlayerColor.WHITE) {
                availableCellsForCapture.add(cell)
            }
        }
    }
    val listCellCoords = buildString {
        append(availableCellsForCapture.map { it.file + it.rank.toString() + "|"}.joinToString(separator = ""))
    }
    //println(listCellCoords)
    var finalCellCoordsPattern : String? = null
    if (listCellCoords.isEmpty()) return null
    if (listCellCoords[listCellCoords.lastIndex] == '|') finalCellCoordsPattern = listCellCoords.substring(0, listCellCoords.lastIndex)
    //println(finalCellCoordsPattern)
    return finalCellCoordsPattern
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