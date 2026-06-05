package ru.mip3x.selenium.tests

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.mip3x.selenium.pages.HomePage

class PriceMapTest : BaseSeleniumTest() {
    @Test
    fun priceMapSortsDirectionsByPrice() {
        val priceMap = HomePage(start(), config.timeout)
            .open(config.baseUrl)
            .openPriceMap()

        assertTrue(priceMap.isVisible())

        priceMap.selectOrigin("Москва")
        assertTrue(priceMap.hasDirectionsAndMap())

        priceMap.sortByPrice()
        assertTrue(priceMap.arePricesSortedAscending())
    }
}
