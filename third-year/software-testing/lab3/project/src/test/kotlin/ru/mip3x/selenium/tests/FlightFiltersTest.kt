package ru.mip3x.selenium.tests

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.mip3x.selenium.pages.FlightResultsPage
import ru.mip3x.selenium.pages.HomePage
import java.time.LocalDate

class FlightFiltersTest : BaseSeleniumTest() {
    companion object {
        private val DEPARTURE_DATE: LocalDate = LocalDate.of(2026, 12, 24)
        private val RETURN_DATE: LocalDate = LocalDate.of(2026, 12, 31)
    }

    @Disabled
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

    @Disabled
    @Test
    fun daytimeDepartureFilterShowsOnlyDaytimeDepartures() {
        val resultsPage = openResults()
            .selectDepartureTime("Днём")

        assertTrue(resultsPage.allDepartureTimesAreBetween(12, 18))
    }

    @Disabled
    @Test
    fun priceSliderLowersMaximumTicketPrice() {
        val resultsPage = openResults()
        val maxPriceBefore = resultsPage.maxTicketPrice()

        val maxPriceDecreased = resultsPage
            .reduceMaxPriceWithSlider()
            .maxTicketPriceIsLessThan(maxPriceBefore)

        assertTrue(maxPriceDecreased)
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
