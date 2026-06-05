package ru.mip3x.selenium.tests

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.mip3x.selenium.pages.FlightResultsPage
import ru.mip3x.selenium.pages.HomePage
import java.time.LocalDate

@Disabled
class FlightFiltersTest : BaseSeleniumTest() {
    companion object {
        private val DEPARTURE_DATE: LocalDate = LocalDate.of(2026, 12, 24)
        private val RETURN_DATE: LocalDate = LocalDate.of(2026, 12, 31)
    }

    @Test
    fun baggageAmountFilterShowsTwoCheckedBagsInEveryTicket() {
        val resultsPage = openResults()
            .enableBaggageFilter()
            .selectBaggageAmount(2)

        assertTrue(resultsPage.allTicketsHaveBaggageAmount(2))
    }

    @Test
    fun baggageWeightFilterIncreasesAverageTicketPrice() {
        val resultsPage = openResults()
            .enableBaggageFilter()
        val averagePriceBefore = resultsPage.averageTicketPrice()

        val averagePriceIncreased = resultsPage
            .selectBaggageWeight("30 кг")
            .averageTicketPriceIsGreaterThan(averagePriceBefore)

        assertTrue(averagePriceIncreased)
    }

    @Test
    fun daytimeDepartureFilterShowsOnlyDaytimeDepartures() {
        val resultsPage = openResults()
            .selectDaytimeDeparture()

        assertTrue(resultsPage.allDepartureTimesAreBetween(12, 18))
    }

    @Test
    fun priceSliderLowersMaximumTicketPrice() {
        val resultsPage = openResults()

        val ticketsFitMaxPrice = resultsPage
            .reduceMaxPriceWithSlider()
            .allVisibleTicketsWithinCurrentMaxPrice()

        assertTrue(ticketsFitMaxPrice)
    }

    private fun openResults(): FlightResultsPage {
        return HomePage(start(), config.timeout)
            .open(config.baseUrl)
            .searchRoundTrip(
                from = "Москва",
                to = "Санкт-Петербург",
                departureDate = DEPARTURE_DATE,
                returnDate = RETURN_DATE,
            )
            .waitForResults()
    }
}
