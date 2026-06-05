package ru.mip3x.selenium.tests

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.mip3x.selenium.pages.HomePage
import java.time.LocalDate

class FlightSearchTest : BaseSeleniumTest() {
    companion object {
        private val DEPARTURE_DATE: LocalDate = LocalDate.of(2026, 12, 24)
        private val RETURN_DATE: LocalDate = LocalDate.of(2026, 12, 31)
    }

    @Test
    fun searchRoundTripTicketAndProceedToPurchase() {
        val resultsPage = HomePage(start(), config.timeout)
            .open(config.baseUrl)
            .searchRoundTrip(
                from = "Москва",
                to = "Санкт-Петербург",
                departureDate = DEPARTURE_DATE,
                returnDate = RETURN_DATE,
            )
            .waitForResults()

        assertTrue(resultsPage.areRoundTripTicketsVisible())

        resultsPage.clickFirstTicket()
        assertTrue(resultsPage.isAirlineSiteOpened())
    }

    @Test
    fun closedAirportRouteShowsWarning() {
        val resultsPage = HomePage(start(), config.timeout)
            .open(config.baseUrl)
            .searchRoundTrip(
                from = "Москва",
                to = "Симферополь",
                departureDate = DEPARTURE_DATE,
                returnDate = RETURN_DATE,
            )

        assertTrue(resultsPage.hasClosedAirportWarning())
    }
}
