package com.example.mejorprecio

enum class UnitCategory { MASS, VOLUME, UNIT }

enum class ProductUnit(val symbol: String, val category: UnitCategory, val baseFactor: Double) {
    G("g", UnitCategory.MASS, 1.0),
    KG("kg", UnitCategory.MASS, 1000.0),
    ML("ml", UnitCategory.VOLUME, 1.0),
    L("l", UnitCategory.VOLUME, 1000.0),
    UNIDAD("unidad", UnitCategory.UNIT, 1.0)
}

data class ComparisonRow(
    val id: Int,
    val price: String = "",
    val quantity: String = "",
    val unit: ProductUnit = ProductUnit.G,
    val isValid: Boolean = true
)

data class CalculationResult(
    val index: Int,
    val comparativePrice: Double,
    val label: String
)

object ComparisonEngine {
    fun calculate(rows: List<ComparisonRow>): Pair<List<CalculationResult?>, Int?> {
        val activeRows = rows.filter { it.price.isNotEmpty() || it.quantity.isNotEmpty() }
        
        // Validation: All active must have both values
        val allValid = activeRows.all { it.price.toDoubleOrNull() != null && it.quantity.toDoubleOrNull() != null }
        if (!allValid || activeRows.size < 2) return List(rows.size) { null } to null

        val parsedRows = activeRows.map { row ->
            val p = row.price.toDoubleOrNull() ?: 0.0
            val q = row.quantity.toDoubleOrNull() ?: 0.0
            row to Triple(p, q, row.unit)
        }

        // Check unique category
        val categories = parsedRows.map { it.first.unit.category }.toSet()
        if (categories.size > 1) return List(rows.size) { null } to null

        val category = categories.first()
        val unitsUsed = parsedRows.map { it.first.unit }.toSet()

        val basis = when (category) {
            UnitCategory.MASS -> if (unitsUsed.contains(ProductUnit.KG)) Basis(1000.0, "kg") else Basis(100.0, "100 g")
            UnitCategory.VOLUME -> if (unitsUsed.contains(ProductUnit.L)) Basis(1000.0, "l") else Basis(100.0, "100 ml")
            UnitCategory.UNIT -> Basis(1.0, "unidad")
        }

        val results = mutableListOf<CalculationResult?>()
        val computed = parsedRows.map { (row, values) ->
            val (p, q, u) = values
            val amountInBase = q * u.baseFactor
            val comparative = (p / amountInBase) * basis.divisor
            CalculationResult(row.id, comparative, basis.label)
        }

        val winner = computed.minByOrNull { it.comparativePrice }
        
        val finalResults = Array<CalculationResult?>(rows.size) { null }
        computed.forEach { finalResults[it.index] = it }

        return finalResults.toList() to winner?.index
    }

    private data class Basis(val divisor: Double, val label: String)
}
