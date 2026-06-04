package ru.mip3x.selenium.tests;

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.mip3x.selenium.config.Browser
import ru.mip3x.selenium.pages.HomePage

class SmokeTest : BaseSeleniumTest() {
    @Test
    fun homePageOpens() {
        val homePage = HomePage(start(Browser.CHROME), config.timeout)
            .open(config.baseUrl)
        assertTrue(homePage.isOpened())
    }
}