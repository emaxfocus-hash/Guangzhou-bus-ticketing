package com.guangzhou.busticketing.data

object TanzaniaRoutes {
    val cities = listOf(
        "Dar es Salaam",
        "Morogoro",
        "Dodoma",
        "Arusha",
        "Moshi",
        "Mbeya",
        "Tanga",
        "Zanzibar",
        "Mwanza",
        "Kigoma",
        "Iringa",
        "Songea",
        "Tabora",
        "Bukoba",
        "Shinyanga",
        "Lindi"
    )

    val routeCatalog = listOf(
        Route("Dar es Salaam", "Morogoro", "Dala 94 Express", "08:00 AM", 32000, 12),
        Route("Dar es Salaam", "Morogoro", "Gani Bus", "10:30 AM", 35000, 7),
        Route("Dar es Salaam", "Dodoma", "Northern Link", "07:15 AM", 28000, 16),
        Route("Dar es Salaam", "Arusha", "Safari Coach", "09:15 AM", 42000, 10),
        Route("Dar es Salaam", "Mbeya", "Top Route", "18:30 PM", 61000, 9),
        Route("Dar es Salaam", "Zanzibar", "Coastal Star", "06:45 AM", 25000, 20),
        Route("Morogoro", "Dodoma", "Hillway Express", "06:00 AM", 22000, 14),
        Route("Morogoro", "Arusha", "Mountain Movers", "11:00 AM", 39000, 11),
        Route("Dodoma", "Mbeya", "Central Transit", "08:45 AM", 43000, 13),
        Route("Arusha", "Moshi", "Kilimanjaro Line", "07:35 AM", 17000, 21),
        Route("Arusha", "Mwanza", "Lake Route", "15:15 PM", 52000, 8),
        Route("Mwanza", "Kigoma", "Lake Express", "06:30 AM", 33000, 12),
        Route("Mbeya", "Iringa", "Southern Hills", "09:40 AM", 18000, 25),
        Route("Mbeya", "Songea", "Southern Link", "13:00 PM", 24000, 18),
        Route("Tanga", "Dar es Salaam", "Coastal Express", "08:10 AM", 26000, 19),
        Route("Zanzibar", "Dar es Salaam", "Bora Ferry Coach", "07:00 AM", 23000, 22),
        Route("Tabora", "Dodoma", "Central Shuttle", "09:05 AM", 25500, 14),
        Route("Bukoba", "Mwanza", "Lake Transit", "10:25 AM", 21000, 17),
        Route("Lindi", "Dar es Salaam", "Coastway", "06:50 AM", 29000, 15)
    )

    data class Route(
        val from: String,
        val to: String,
        val operator: String,
        val departure: String,
        val price: Int,
        val seats: Int
    )

    fun getRoutesFor(from: String, to: String): List<Route> {
        return routeCatalog.filter {
            it.from.equals(from, ignoreCase = true) && it.to.equals(to, ignoreCase = true)
        }
    }

    fun getPopularRoutes(): List<Route> = listOf(
        routeCatalog[0],
        routeCatalog[2],
        routeCatalog[3],
        routeCatalog[4],
        routeCatalog[5]
    )
}
