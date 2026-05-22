package com.damtoy.rewear.repository

import com.damtoy.rewear.model.ClothingCategory
import com.damtoy.rewear.model.Yayasan
import kotlin.math.*

class YayasanRepository {

    // MVP hardcoded list of 10 donation partners in Indonesia with real GPS coordinates
    private val yayasanList = listOf(
        Yayasan(
            id = "Y01", name = "Rumah Zakat", city = "Jakarta Selatan",
            address = "Jl. Turangga No. 33, Bukit Duri, Jakarta Selatan",
            phone = "081234567890", waLink = "https://wa.me/6281234567890",
            acceptedCategories = ClothingCategory.values().toList(),
            latitude = -6.2441, longitude = 106.8469,
            description = "Lembaga amil zakat nasional yang aktif mengelola donasi pakaian untuk dhuafa di seluruh Indonesia.",
            website = "https://www.rumahzakat.org"
        ),
        Yayasan(
            id = "Y02", name = "Dompet Dhuafa", city = "Tangerang Selatan",
            address = "Philanthropy Building, Jl. Warung Jati Barat No. 18, Tangerang Selatan",
            phone = "08111222333", waLink = "https://wa.me/628111222333",
            acceptedCategories = listOf(ClothingCategory.TOPS, ClothingCategory.BOTTOMS, ClothingCategory.OUTERWEAR),
            latitude = -6.2874, longitude = 106.7330,
            description = "Yayasan filantropi terkemuka yang menyalurkan pakaian ke masyarakat kurang mampu di pelosok negeri.",
            website = "https://www.dompetdhuafa.org"
        ),
        Yayasan(
            id = "Y03", name = "Yatim Mandiri", city = "Surabaya",
            address = "Jl. Jambangan Kebon Agung No. 4, Jambangan, Surabaya",
            phone = "082233445566", waLink = "https://wa.me/6282233445566",
            acceptedCategories = listOf(ClothingCategory.TOPS, ClothingCategory.BOTTOMS),
            latitude = -7.3411, longitude = 112.7199,
            description = "Fokus pada pemberdayaan anak-anak yatim dan dhuafa, menerima pakaian layak pakai untuk anak-anak.",
            website = "https://www.yatimmandiri.org"
        ),
        Yayasan(
            id = "Y04", name = "BAZNAS", city = "Jakarta Pusat",
            address = "Gedung BAZNAS, Jl. Matraman Raya No. 134, Matraman, Jakarta Pusat",
            phone = "085566778899", waLink = "https://wa.me/6285566778899",
            acceptedCategories = ClothingCategory.values().toList(),
            latitude = -6.2073, longitude = 106.8611,
            description = "Badan Amil Zakat Nasional resmi milik pemerintah yang mengelola distribusi zakat, termasuk pakaian.",
            website = "https://baznas.go.id"
        ),
        Yayasan(
            id = "Y05", name = "Yayasan Sayap Ibu", city = "Yogyakarta",
            address = "Jl. Rajawali No. 3, Pringwulung, Condongcatur, Sleman",
            phone = "087788990011", waLink = "https://wa.me/6287788990011",
            acceptedCategories = listOf(ClothingCategory.TOPS, ClothingCategory.DRESSES),
            latitude = -7.7593, longitude = 110.4028,
            description = "Yayasan yang berdedikasi untuk melindungi dan merawat anak terlantar serta ibu yang membutuhkan.",
            website = "https://yayasansayapibu.or.id"
        ),
        Yayasan(
            id = "Y06", name = "Pita Kuning", city = "Jakarta Selatan",
            address = "Jl. Kemang Timur No. 11, Bangka, Mampang Prapatan, Jakarta Selatan",
            phone = "081999888777", waLink = "https://wa.me/6281999888777",
            acceptedCategories = listOf(ClothingCategory.ACCESSORIES, ClothingCategory.TOPS),
            latitude = -6.2631, longitude = 106.8175,
            description = "Komunitas peduli kanker yang mengumpulkan pakaian untuk mendukung pasien kanker dan keluarganya.",
            website = null
        ),
        Yayasan(
            id = "Y07", name = "Lazismu", city = "Bandung",
            address = "Jl. Pelajar Pejuang 45 No. 40, Turangga, Lengkong, Bandung",
            phone = "081333444555", waLink = "https://wa.me/6281333444555",
            acceptedCategories = ClothingCategory.values().toList(),
            latitude = -6.9342, longitude = 107.6232,
            description = "Lembaga amil zakat Muhammadiyah yang aktif mendistribusikan pakaian ke masyarakat prasejahtera.",
            website = "https://lazismu.org"
        ),
        Yayasan(
            id = "Y08", name = "Habitat for Humanity", city = "Surabaya",
            address = "Ruko Klampis Jaya Blok A-37, Klampis Ngasem, Sukolilo, Surabaya",
            phone = "085544332211", waLink = "https://wa.me/6285544332211",
            acceptedCategories = listOf(ClothingCategory.SHOES, ClothingCategory.OUTERWEAR),
            latitude = -7.2932, longitude = 112.7777,
            description = "Organisasi global yang membantu keluarga prasejahtera, menerima pakaian untuk keluarga yang membutuhkan tempat tinggal.",
            website = "https://www.habitatindonesia.org"
        ),
        Yayasan(
            id = "Y09", name = "Wahana Visi Indonesia", city = "Tangerang",
            address = "Jl. Bintaro Utama 3A, Pesanggrahan, Bintaro, Pesanggrahan, Jakarta Selatan",
            phone = "082111222333", waLink = "https://wa.me/6282111222333",
            acceptedCategories = ClothingCategory.values().toList(),
            latitude = -6.2673, longitude = 106.7563,
            description = "Mitra World Vision Indonesia yang berfokus pada pemberdayaan anak-anak di daerah terpencil.",
            website = "https://www.wvi.or.id"
        ),
        Yayasan(
            id = "Y10", name = "Yayasan Buddha Tzu Chi", city = "Jakarta Utara",
            address = "Tzu Chi Center, Pantai Indah Kapuk Boulevard, Penjaringan, Jakarta Utara",
            phone = "081222333444", waLink = "https://wa.me/6281222333444",
            acceptedCategories = ClothingCategory.values().toList(),
            latitude = -6.1083, longitude = 106.7475,
            description = "Yayasan kemanusiaan internasional yang aktif mendistribusikan pakaian dan kebutuhan dasar bagi masyarakat.",
            website = "https://www.tzuchi.or.id"
        )
    )

    fun getAllYayasan(): List<Yayasan> = yayasanList

    /**
     * Returns foundations sorted by distance from user's GPS location (nearest first).
     * Uses Haversine formula to calculate distance in kilometers.
     * Falls back to default order if location is null.
     */
    fun getSortedByDistance(userLat: Double?, userLng: Double?): List<Yayasan> {
        if (userLat == null || userLng == null) return yayasanList
        return yayasanList.sortedBy { haversineDistanceKm(userLat, userLng, it.latitude, it.longitude) }
    }

    /**
     * Calculates straight-line distance in kilometers between two GPS coordinates.
     * Haversine formula — handles curvature of the Earth.
     */
    fun distanceKm(userLat: Double, userLng: Double, yayasan: Yayasan): Double {
        return haversineDistanceKm(userLat, userLng, yayasan.latitude, yayasan.longitude)
    }

    fun filterByCity(city: String): List<Yayasan> =
        yayasanList.filter { it.city.contains(city, ignoreCase = true) }

    fun getById(id: String): Yayasan? = yayasanList.find { it.id == id }

    private fun haversineDistanceKm(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val R = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }
}