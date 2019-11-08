import java.awt.geom.Line2D
import kotlin.math.*

fun main() {
//    level1()
//    level2()
//    level3()
    level4()
}

fun level4() {
    val (rows, cols) = readLongs().map { it.toInt() }
    val n = readLong().toInt()

    repeat(n) {
        val beam = readLongs().map { it.toInt() }
        val linePointA = listOf(beam[0], beam[1])
        val linePointB = listOf(beam[0] + (rows + 1)*beam[2], beam[1] + (cols + 1)*beam[3])

        val touchedCells = mutableListOf<Cell>()
        for (y in 0 until rows) {
            for (x in 0 until cols) {
                val distance = distanceBetweenLineAndPoint(linePointA, linePointB, listOf(x, y))
                val distanceOfBox = euclideanDistance(linePointA, listOf(x, y))
                if (distance <= 0.5) touchedCells.add(Cell(-1, -1, y, x, distanceOfBox))
            }
        }

        touchedCells.sort()
        println(touchedCells.map { "${it.j} ${it.i}" }.joinToString(" "))
    }
}

fun distanceBetweenLineAndPoint(linePointA: List<Int>, linePointB: List<Int>, point: List<Int>): Double {
    val pointToA = euclideanDistance(linePointA, point)
    val ab = euclideanDistance(linePointA, linePointB)
    val pointToB = euclideanDistance(point, linePointB)

    // Find area
    val s = (pointToA + ab + pointToB) / 2
    val area = sqrt(s * (s - pointToA) * (s - ab) * (s - pointToB))

    return (2 * area) / ab
}

fun level3() {
    val (rows, _) = readLongs().map { it.toInt() }
    val grid = mutableListOf<MutableList<Cell>>()

    val idsToCells = mutableMapOf<Int, MutableList<Cell>>()

    repeat(rows) {
        val row = readLongs()
        grid.add(mutableListOf())
        for (i in row.indices step 2) {
            val cell = Cell(row[i], row[i + 1].toInt(), it, i / 2)
            grid[it].add(cell)
            if (idsToCells[cell.countryId] == null) idsToCells[cell.countryId] = mutableListOf()
            idsToCells[cell.countryId]?.add(cell)
        }
    }

    idsToCells.toSortedMap().forEach { (id, cells) ->
        val center = calculateCenterOfMass(cells)
        if (!isCoordInsideGrid(center[0], center[1], grid) || grid[center[0]][center[1]].countryId != id || grid[center[0]][center[1]].isBorder(grid))
            println(findCenter(center, id, grid).reversed().joinToString(" "))
        else println(center.reversed().joinToString(" "))
    }
}

fun findCenter(prevCenter: List<Int>, id: Int, grid: List<List<Cell>>): List<Int> {
    var minPoints = mutableListOf<Cell>()
    var minDistance = Double.MAX_VALUE

    grid.forEach { row ->
        row.forEach { cell ->
            if (cell.countryId == id && !cell.isBorder(grid)) {
                val distance = euclideanDistance(prevCenter, listOf(cell.i, cell.j))
                if (distance <= minDistance) {
                    if (distance == minDistance) minPoints.add(cell)
                    else minPoints = mutableListOf(cell)
                    minDistance = distance
                }
            }
        }
    }

    val minPoint = minPoints.min()!!
    return listOf(minPoint.i, minPoint.j)
}

fun manhattanDistance(cell1: List<Int>, cell2: List<Int>): Int {
    return abs(cell1[0] - cell2[0]) + abs(cell1[1] - cell2[1])
}

fun euclideanDistance(cell1: List<Int>, cell2: List<Int>): Double {
    return sqrt((cell1[0] - cell2[0]).toDouble().pow(2) + (cell1[1] - cell2[1]).toDouble().pow(2))
}

fun isCoordInsideGrid(i: Int, j: Int, grid: List<List<Cell>>): Boolean {
    return !(i < 0 || i >= grid.size || j < 0 || j >= grid[0].size)
}

fun calculateCenterOfMass(cells: List<Cell>): List<Int> {
    val center = mutableListOf(0, 0)
    cells.forEach {
        center[0] += it.i
        center[1] += it.j
    }

    center[0] /= cells.size
    center[1] /= cells.size
    return center
}

fun level2() {
    val (rows, _) = readLongs().map { it.toInt() }
    val grid = mutableListOf<MutableList<Cell>>()

    repeat(rows) {
        val row = readLongs()
        grid.add(mutableListOf())
        for (i in row.indices step 2) {
            grid[it].add(Cell(row[i], row[i + 1].toInt(), it, i / 2))
        }
    }

    val idsToBorders = mutableMapOf<Int, Int>()

    grid.forEachIndexed { _, row ->
        row.forEachIndexed { _, cell ->
            if (cell.isBorder(grid)) idsToBorders[cell.countryId] = idsToBorders[cell.countryId]?.plus(1) ?: 1
        }
    }

    idsToBorders.toSortedMap().forEach { (_, borders) -> println(borders) }
}

data class Cell(val altitude: Long, val countryId: Int, val i: Int, val j: Int, val distToPoint: Double = 0.0): Comparable<Cell> {

    fun isBorder(grid: List<List<Cell>>): Boolean {
        return i - 1 < 0 || i + 1 >= grid.size || j - 1 < 0 || j + 1 >= grid[0].size
                || grid[i - 1][j].countryId != countryId || grid[i + 1][j].countryId != countryId
                || grid[i][j - 1].countryId != countryId || grid[i][j + 1].countryId != countryId
    }

    fun compareToI(other: Cell): Int {
        if (i == other.i) return j - other.j
        return i - other.i
    }

    override fun compareTo(other: Cell): Int {
        if (distToPoint - other.distToPoint < 1e-6) return compareToI(other)
        if (distToPoint < other.distToPoint) return -1
        return 1
    }
}

fun level1() {
    val (rows, cols) = readLongs()
    val grid = mutableListOf<List<Long>>()
    repeat(rows.toInt()) { grid.add(readLongs()) }
    var minAlt = Long.MAX_VALUE
    var maxAlt = 0L
    var averageAlt = 0L

    grid.forEach { row ->
        minAlt = min(minAlt, row.min()!!)
        maxAlt = max(maxAlt, row.max()!!)
        averageAlt += row.sum()
    }

    println("$minAlt $maxAlt ${averageAlt / (rows * cols)}")
}

// Input Reader
private fun readLn() = readLine()!!

private fun readLong() = readLn().toLong()
private fun readStrings() = readLn().split(" ")
private fun readLongs() = readStrings().map { it.toLong() }
