package com.damtoy.rewear.repository

import com.damtoy.rewear.model.ClothingCategory
import com.damtoy.rewear.model.Yayasan

class YayasanRepository {

    // MVP Hardcoded list of 10 donation partners in Indonesia
    private val yayasanList = listOf(
        Yayasan("Y01", "Rumah Zakat", "Jakarta", "Jl. Turangga No. 33, Jakarta Selatan", "081234567890", "https://wa.me/6281234567890", ClothingCategory.values().toList()),
        Yayasan("Y02", "Dompet Dhuafa", "Tangerang", "Philanthropy Building, Jl. Warung Jati Barat", "08111222333", "https://wa.me/628111222333", listOf(ClothingCategory.TOPS, ClothingCategory.BOTTOMS, ClothingCategory.OUTERWEAR)),
        Yayasan("Y03", "Yatim Mandiri", "Surabaya", "Jl. Jambangan Kebon Agung No. 4", "082233445566", "https://wa.me/6282233445566", listOf(ClothingCategory.TOPS, ClothingCategory.BOTTOMS)),
        Yayasan("Y04", "BAZNAS", "Jakarta", "Gedung BAZNAS, Jl. Matraman Raya", "085566778899", "https://wa.me/6285566778899", ClothingCategory.values().toList()),
        Yayasan("Y05", "Yayasan Sayap Ibu", "Yogyakarta", "Jl. Rajawali No. 3, Pringwulung", "087788990011", "https://wa.me/6287788990011", listOf(ClothingCategory.TOPS, ClothingCategory.DRESSES)),
        Yayasan("Y06", "Pita Kuning", "Jakarta", "Jl. Kemang Timur No. 11", "081999888777", "https://wa.me/6281999888777", listOf(ClothingCategory.ACCESSORIES, ClothingCategory.TOPS)),
        Yayasan("Y07", "Lazismu", "Bandung", "Jl. Pelajar Pejuang 45 No. 40", "081333444555", "https://wa.me/6281333444555", ClothingCategory.values().toList()),
        Yayasan("Y08", "Habitat for Humanity", "Surabaya", "Ruko Klampis Jaya No. 37", "085544332211", "https://wa.me/6285544332211", listOf(ClothingCategory.SHOES, ClothingCategory.OUTERWEAR)),
        Yayasan("Y09", "Wahana Visi Indonesia", "Tangerang", "Jl. Bintaro Utama 3A", "082111222333", "https://wa.me/6282111222333", ClothingCategory.values().toList()),
        Yayasan("Y10", "Yayasan Buddha Tzu Chi", "Jakarta", "Tzu Chi Center, Pantai Indah Kapuk", "081222333444", "https://wa.me/6281222333444", ClothingCategory.values().toList())
    )

    fun getAllYayasan(): List<Yayasan> = yayasanList

    fun filterByCity(city: String): List<Yayasan> {
        return yayasanList.filter { it.city.contains(city, ignoreCase = true) }
    }

    fun getById(id: String): Yayasan? {
        return yayasanList.find { it.id == id }
    }
}