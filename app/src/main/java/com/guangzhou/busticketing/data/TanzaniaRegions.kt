package com.guangzhou.busticketing.data

object TanzaniaRegions {
    val regionMap = mapOf(
        "Dar es Salaam" to "Coastal",
        "Morogoro" to "Central",
        "Dodoma" to "Central",
        "Arusha" to "Northern",
        "Moshi" to "Northern",
        "Mbeya" to "Southern Highlands",
        "Iringa" to "Southern Highlands",
        "Songea" to "Southern",
        "Tanga" to "Coastal",
        "Zanzibar" to "Zanzibar",
        "Mwanza" to "Lake",
        "Kigoma" to "Lake",
        "Bukoba" to "Lake",
        "Tabora" to "Central",
        "Shinyanga" to "Lake",
        "Lindi" to "Coastal"
    )

    fun regionFor(city: String): String = regionMap[city] ?: "National"
}
