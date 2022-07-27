
import kotlin.random.Random

enum class Enums(val value: String) {
    BOMB("X "),
    DOT(". "),
    FREE("/ "),
    GUESS("* ")
}

fun playingField(rows: Int, cols: Int): MutableList<MutableList<String>> {
    val playingField = MutableList(rows + 3) { MutableList(cols + 3) { Enums.DOT.value } }
    playingField[0][0] = "  "

    // Creating grid

    // vertical numbers
    for (i in 1..rows) playingField[i + 1][0] = "$i "
    // horizontal numbers
    for (i in 1..cols) playingField[0][i + 1] = "$i "
    // '-'
    for (i in 0..cols + 2) {
        playingField[1][i] = "--"
        playingField[rows + 2][i] = "--"
    }
    // '|'
    for (i in 0..rows + 2) {
        playingField[i][1] = "|"
        playingField[i][cols + 2] = "|"
    }

    return playingField
}

fun mineSetter(rows: Int, cols: Int, mines: Int, playingField: MutableList<MutableList<String>>, x: Int, y: Int):  MutableList<MutableList<String>> {
    val rand = Random(System.nanoTime())
    val mineList = mutableListOf<Pair<Int, Int>>()
    val list = mutableListOf<Pair<Int, Int>>()
    var index = 0
    // Creating linear list with all pairs of coordinates
    for (i in 1..rows) {
        for (j in 1..cols) {
            list.add(i to j)
            index++
        }
    }
    // Deleting 9 pairs of coordinates(9 cells), that surround the [x, y] cell (and of course [x, y] cell)
    for (i in x - 1..x + 1) {
        for (j in y - 1..y + 1) {
            list.remove(i to j)
        }
    }
    // Randomly setting mines
    for (i in 0 until mines) {
        val ran = rand.nextInt(0, list.size)
        mineList.add(list[ran])
        list.removeAt(ran)
    }
    // Setting mines to Playing field
    for (i in 0 until mines) {
        playingField[mineList[i].first + 1][mineList[i].second + 1] = Enums.BOMB.value
    }

    return playingField
}

fun hiddenField(rows: Int, cols: Int, playingField: MutableList<MutableList<String>>): MutableList<MutableList<String>> {
    val hiddenField = MutableList(rows) { MutableList(cols) { "  " } }
    for (i in 0 until rows) {
        for (j in 0 until cols) {
            if (playingField[i + 2][j + 2] == Enums.GUESS.value && hiddenField[i][j] != Enums.BOMB.value) hiddenField[i][j] = Enums.FREE.value
            hiddenField[i][j] = playingField[i + 2][j + 2]
        }
    }
    // Making mines invisible for player on the Playing field
    for (i in 0 until rows + 2) {
        for (j in 0 until cols + 2) {
            if (playingField[i][j] == Enums.BOMB.value) playingField[i][j] = Enums.DOT.value
        }
    }
    return hiddenField
}

fun mineChecker(rows: Int, cols: Int, hiddenFieldChecked: MutableList<MutableList<String>>): MutableList<MutableList<String>>   {
    var count = 0
    // Counting mines around each cell
    for (i in 0 until rows) {
        for (j in 0 until cols) {
            if (hiddenFieldChecked[i][j] == Enums.DOT.value) {
                for (k in -1..1) {
                    for (l in -1..1) {
                        if (hiddenFieldChecked.getOrNull(i + k)?.getOrNull(j + l) == Enums.BOMB.value) count++
                    }
                }
                if (count > 0) hiddenFieldChecked[i][j] = "$count "
                count = 0
            }
        }
    }
    return hiddenFieldChecked
}

fun floodFill(x: Int, y: Int, rows: Int, cols: Int, playingField: MutableList<MutableList<String>>, hiddenField: MutableList<MutableList<String>>) {
    if (hiddenField[x - 1][y - 1] == Enums.FREE.value) return
    // Calling recursion function
    floodFillRecur(x, y, rows, cols, playingField, hiddenField)
}

fun floodFillRecur(x: Int, y: Int, rows: Int, cols: Int, playingField: MutableList<MutableList<String>>, hiddenField: MutableList<MutableList<String>>) {
    if (x < 1 || x > rows || y < 1 || y > cols) return
    if (hiddenField[x - 1][y - 1] != Enums.DOT.value) return
    hiddenField[x - 1][y - 1] = Enums.FREE.value
    playingField[x + 1][y + 1] = Enums.FREE.value

    for (k in -1..1) {
        for (l in -1..1) {
            if (hiddenField.getOrNull(x - 1 + k)?.getOrNull(y - 1 + l) != Enums.FREE.value &&
                hiddenField.getOrNull(x - 1 + k)?.getOrNull(y - 1 + l) != Enums.DOT.value &&
                x + k in 1..rows && y + l in 1..cols)  {
                playingField[x + 1 + k][y + 1 + l] = hiddenField[x - 1 + k][y - 1 + l]
            }
        }
    }
    // South, north, east, west
    floodFillRecur(x + 1, y, rows, cols, playingField, hiddenField)
    floodFillRecur(x - 1, y, rows, cols, playingField, hiddenField)
    floodFillRecur(x, y + 1, rows, cols, playingField, hiddenField)
    floodFillRecur(x, y - 1, rows, cols, playingField, hiddenField)
    // North-west, north-east, south-west, south-east,
    floodFillRecur(x - 1, y - 1, rows, cols, playingField, hiddenField)
    floodFillRecur(x - 1, y + 1, rows, cols, playingField, hiddenField)
    floodFillRecur(x + 1, y - 1, rows, cols, playingField, hiddenField)
    floodFillRecur(x + 1, y + 1, rows, cols, playingField, hiddenField)
}

fun game(rows: Int, cols: Int, mines: Int) {
    var n = 0
    val playingField = playingField(rows, cols)
    val guessingField = playingField(rows, cols)
    var hiddenField = hiddenField(rows, cols, playingField)
    var y: Int
    var x: Int
    var command: String

    while (true) {
        playingField.forEach { println(it.joinToString("")) }
        // todo remove printing the hiddenField
        hiddenField.forEach { println(it.joinToString("")) }
        print("Set/unset mine marks or claim a cell as free: > ")
        val inp = readLine()!!
        y = inp.split(" ")[0].toInt()
        x = inp.split(" ")[1].toInt()
        command = inp.split(" ")[2]

        // Checking if [x] in [0..cols] and [y] in [0..rows] and if cell[x][y] isn't a number or free sign
        while (x !in 0..rows || y !in 0..cols || playingField[x + 1][y + 1] != Enums.GUESS.value && playingField[x + 1][y + 1] != Enums.DOT.value) {
            println("Choose another cell!")
            print("Set/unset mine marks or claim a cell as free: > ")
            val input = readLine()!!
            val y1 = input.split(" ")[0].toInt()
            val x1 = input.split(" ")[1].toInt()
            val command1 = input.split(" ")[2]
            y = y1; x = x1; command = command1
        }

        // Variants of the commands
        when (command) {
            "free" ->
                // First [x] and [y] are special due to setting mines considering [x] and [y], so the first opened cell will be free
                if (n == 0) {
                    if (playingField[x + 1][y + 1] == Enums.DOT.value) {
                        // Clearing playing field because player can already set guesses, but needed clear field for setting mines
                        for (i in 2..rows + 1) {
                            for (j in 2..cols + 1) {
                                playingField[i][j] = Enums.DOT.value
                            }
                        }
                        mineSetter(rows, cols, mines, playingField, x, y)
                        hiddenField = hiddenField(rows, cols, playingField)
                        mineChecker(rows, cols, hiddenField)
                        floodFill(x, y, rows, cols, playingField, hiddenField)
                        // Returning guesses
                        for (i in 2..rows + 1) {
                            for (j in 2..cols + 1) {
                                if (guessingField[i][j] == Enums.GUESS.value)
                                    playingField[i][j] = guessingField[i][j]
                            }
                        }
                        n++
                    }
                } else {
                    if (playingField[x + 1][y + 1] == Enums.DOT.value) {
                        if (hiddenField[x - 1][y - 1] == Enums.DOT.value) {
                            floodFillRecur(x, y, rows, cols, playingField, hiddenField)
                        } else if (hiddenField[x - 1][y - 1] == Enums.BOMB.value) {
                            for (i in 0 until rows) {
                                for (j in 0 until cols) {
                                    if (hiddenField[i][j] == Enums.BOMB.value) playingField[i + 2][j + 2] =
                                        Enums.BOMB.value
                                }
                            }
                            playingField.forEach { println(it.joinToString("")) }
                            println("You stepped on a mine and failed!")
                            break
                        } else {
                            playingField[x + 1][y + 1] = hiddenField[x - 1][y - 1]
                        }
                    }
                }
            "mine" ->
                if (playingField[x + 1][y + 1] == Enums.DOT.value) {
                    playingField[x + 1][y + 1] = Enums.GUESS.value
                    guessingField[x + 1][y + 1] = Enums.GUESS.value
                } else if (playingField[x + 1][y + 1] == Enums.GUESS.value) {
                    playingField[x + 1][y + 1] = Enums.DOT.value
                    guessingField[x + 1][y + 1] = Enums.DOT.value
                }
            else -> println("Wrong command!")
        }

        // Condition of the winning
        var win1 = 0
        var win2 = 0
        for (i in 2..rows + 1) {
            for (j in 2..cols + 1) {
                // Counting numbers and free signs on the playing field
                if (playingField[i][j] != Enums.GUESS.value && playingField[i][j] != Enums.DOT.value) win1++
                // Counting numbers and free signs on the hidden field
                if (hiddenField[i - 2][j - 2] != Enums.GUESS.value && hiddenField[i - 2][j - 2] != Enums.DOT.value && hiddenField[i - 2][j - 2] != Enums.BOMB.value) win2++
            }
        }

        if (win1 == win2) {
            if (n != 0) {
                playingField.forEach { println(it.joinToString("")) }
                println("Congratulations! You found all the mines!")
                break
            }
        }
        // todo remove
        println("k = $win1")
        println("l = $win2")

    }
}

fun main() {
    println("Welcome to the game Minesweeper! Be careful and good luck! Have a nice game!")
    println()
    print("How many rows? > ")
    val rows = readLine()!!.toInt()
    print("How many columns? > ")
    val cols = readLine()!!.toInt()
    print("How many mines do you want on the field? > ")
    var mines = readLine()!!.toInt()
    while (mines !in 1..rows * cols - 9) {
        println("Incorrect numbers of the mines")
        print("How many mines do you want on the field? > ")
        val input = readLine()!!.toInt()
        mines = input
    }
    game(rows, cols, mines)
}

