package com.damtoy.rewear.model

enum class FabricCategory {
    NATURAL, SYNTHETIC, DENIM_BLENDED
}

/**
 * LCA (Life Cycle Assessment) estimates per 1 kg of fabric.
 */
enum class FabricType(
    val displayName: String,
    val co2PerKg: Double,
    val waterPerKg: Double,
    val category: FabricCategory
) {
    // Natural
    COTTON("Katun (Kaos / Kemeja)", 20.0, 10000.0, FabricCategory.NATURAL),
    LINEN("Linen (Kemeja Halus)", 10.0, 2500.0, FabricCategory.NATURAL),
    WOOL("Wol (Rajut / Tebal)", 14.0, 500.0, FabricCategory.NATURAL),
    SILK("Sutra (Halus / Mengkilap)", 25.0, 800.0, FabricCategory.NATURAL),
    HEMP("Rami (Kasar / Kuat)", 9.0, 300.0, FabricCategory.NATURAL),
    BAMBOO("Serat Bambu", 11.0, 500.0, FabricCategory.NATURAL),

    // Synthetic
    POLYESTER("Sintetis (Baju Olahraga / Jaket)", 21.0, 71.0, FabricCategory.SYNTHETIC),
    NYLON("Nilon (Parasut / Jas Hujan)", 30.0, 150.0, FabricCategory.SYNTHETIC),
    ACRYLIC("Akrilik (Rajut Sintetis)", 38.0, 200.0, FabricCategory.SYNTHETIC),
    SPANDEX("Spandeks (Ketat / Melar)", 22.0, 100.0, FabricCategory.SYNTHETIC),
    FLEECE("Fleece (Jaket Musim Dingin)", 24.0, 150.0, FabricCategory.SYNTHETIC),

    // Denim & Blended
    DENIM("Denim / Jeans", 23.0, 8000.0, FabricCategory.DENIM_BLENDED),
    VISCOSE("Viscose / Rayon", 15.0, 3000.0, FabricCategory.DENIM_BLENDED),
    LEATHER("Kulit", 50.0, 17000.0, FabricCategory.DENIM_BLENDED),
    BLENDED("Campuran", 25.0, 5000.0, FabricCategory.DENIM_BLENDED)
}