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

fun main() {
    val gameBoard = List<MutableList<String>>(8) { rank -> MutableList<String>(8) {
        file ->
        var token: String = " "
        if (rank == 1) token = "B"
        if (rank == 6) token = "W"
        token
    } }
    playGame(gameBoard)

}

fun playGame(board: List<MutableList<String>>) {
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
fun printGameBoard(board: List<MutableList<String>>) {

    board.forEachIndexed(){ i, rank -> println(rank.joinToString(prefix = "  +---+---+---+---+---+---+---+---+\n${rank.size - i} | ", separator = " | ", postfix = " |"))}
    println("  +---+---+---+---+---+---+---+---+")
    println(('a'..'h').joinToString(prefix = "    ", separator = "   ", postfix = "  "))
}