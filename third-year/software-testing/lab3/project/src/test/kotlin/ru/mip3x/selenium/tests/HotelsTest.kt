package ru.mip3x.selenium.tests

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.mip3x.selenium.pages.HomePage
import java.time.LocalDate

class HotelsTest : BaseSeleniumTest() {
    companion object {
        private val CHECK_IN_DATE: LocalDate = LocalDate.of(2026, 12, 24)
        private val CHECK_OUT_DATE: LocalDate = LocalDate.of(2026, 12, 31)
    }

    @Test
    fun hotelBookingRedirectsToPartnerSite() {
        val hotelsPage = HomePage(start(), config.timeout)
            .open(config.baseUrl)
            .openHotels()
        assertTrue(hotelsPage.isLoaded())

        val resultsPage = hotelsPage
            .search(
                city = "Санкт-Петербург",
                checkIn = CHECK_IN_DATE,
                checkOut = CHECK_OUT_DATE,
            )
            .waitForResults()
        assertTrue(resultsPage.hasResultsAndFilters())

        val hotelDetailsPage = resultsPage.openFirstHotel()
        assertTrue(hotelDetailsPage.isLoaded())

        hotelDetailsPage.chooseRoom()
        assertTrue(hotelDetailsPage.areRoomsVisible())

        hotelDetailsPage.bookFirstAvailableRoom()
        assertTrue(hotelDetailsPage.isRedirectedToPartnerSite())
    }
}
