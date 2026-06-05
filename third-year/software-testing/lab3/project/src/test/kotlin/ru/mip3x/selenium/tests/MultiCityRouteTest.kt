package ru.mip3x.selenium.tests

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.mip3x.selenium.pages.HomePage
import java.time.LocalDate

@Disabled
class MultiCityRouteTest : BaseSeleniumTest() {
    companion object {
        private val FLIGHT_DATE: LocalDate = LocalDate.of(2026, 12, 31)
    }

    @Test
    fun multiCityRouteSearchAndPurchase() {
        val multiCityPage = HomePage(start(), config.timeout)
            .open(config.baseUrl)
            .openMultiCitySearch()

        assertTrue(multiCityPage.isLoaded())
        assertTrue(multiCityPage.hasSegment(1))
        assertTrue(multiCityPage.hasSegment(2))

        val resultsPage = multiCityPage
            .setSegment(
                number = 1,
                from = "Москва",
                to = "Санкт-Петербург",
                date = FLIGHT_DATE,
            )
            .setSegment(
                number = 2,
                from = "Санкт-Петербург",
                to = "Сочи",
                date = FLIGHT_DATE,
            )
            .search()
            .waitForResults()

        assertTrue(resultsPage.areTicketsVisible())

        resultsPage.clickFirstTicket()
        assertTrue(resultsPage.isAirlineSiteOpened())
    }
}
