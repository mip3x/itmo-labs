package ru.mip3x.selenium.tests

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Disabled
import ru.mip3x.selenium.config.Browser
import ru.mip3x.selenium.pages.HomePage

class SmokeTest : BaseSeleniumTest() {
    @Disabled
    @Test
    fun homePageOpens() {
        val homePage = HomePage(start(Browser.CHROME), config.timeout)
            .open(config.baseUrl)
        assertTrue(homePage.isOpened())
    }

    @Disabled
    @Test
    fun searchFormHasRouteInputs() {
        val homePage = HomePage(start(Browser.CHROME), config.timeout)
            .open(config.baseUrl)

        assertTrue(homePage.originValue().isNotBlank())
        assertTrue(homePage.destinationValue().isBlank())
    }

    @Disabled
    @Test
    fun destinationCanBeTyped() {
        val originCity = "Москва"
        val destinationCity = "Санкт-Петербург"
        
        val homePage = HomePage(start(Browser.CHROME), config.timeout)
            .open(config.baseUrl)
            .setOrigin(originCity)
            .setDestination(destinationCity)

        assertTrue(homePage.originValue().contains(originCity))
        assertTrue(homePage.destinationValue().contains(destinationCity))
    }
}
