package ru.mip3x.selenium.pages

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import java.time.Duration

class HotelsResultsPage(
    driver: WebDriver,
    timeout: Duration,
) : BasePage(driver, timeout) {
    private val hotelPreview = "//*[@data-test-id='hotel-preview']"
    private val filterGroup = "//*[starts-with(@data-test-id,'filter-group-')]"

    fun waitForResults(): HotelsResultsPage {
        waitFor { visibleHotels().isNotEmpty() }
        acceptCookiesIfPresent()
        return this
    }

    fun isResultsVisible(): Boolean {
        return waitFor { visibleHotels().isNotEmpty() }
    }

    fun hasResultsAndFilters(): Boolean {
        return waitFor {
            visibleHotels().isNotEmpty() &&
                driver.findElements(xpath(filterGroup)).any { it.isDisplayed }
        }
    }

    fun openFirstHotel(): HotelDetailsPage {
        val currentWindow = driver.windowHandle
        val firstHotel = wait.until { visibleHotels().firstOrNull() }
            ?: error("No visible hotel previews found")

        scrollTo(firstHotel)
        jsClick(firstHotel)
        wait.until { driver.windowHandles.size > 1 }
        driver.switchTo().window(driver.windowHandles.first { it != currentWindow })

        return HotelDetailsPage(driver, timeout)
    }

    private fun visibleHotels(): List<WebElement> {
        return driver.findElements(xpath(hotelPreview))
            .filter { runCatching { it.isDisplayed }.getOrDefault(false) }
    }
}
