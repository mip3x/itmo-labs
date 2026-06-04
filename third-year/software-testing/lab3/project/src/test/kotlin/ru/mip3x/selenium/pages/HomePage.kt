package ru.mip3x.selenium.pages

import org.openqa.selenium.WebDriver
import java.time.Duration

class HomePage(
    driver: WebDriver,
    timeout: Duration,
) : BasePage(driver, timeout) {
    private val fromInput = "//input[@data-test-id='origin-input']"
    private val toInput = "//input[@data-test-id='destination-input']"

    fun isOpened(): Boolean {
        return hasVisible(fromInput)
    }

    fun fromValue(): String {
        return visible(fromInput).getAttribute("value") ?: ""
    }

    fun toValue(): String {
        return visible(toInput).getAttribute("value") ?: ""
    }

    fun open(baseUrl: String): HomePage {
        driver.get(baseUrl)
        visible(fromInput)
        return this
    }

    fun openHotels(): HotelsSearchPage {
        click("//*[self::a or self::button][contains(normalize-space(.), 'Отели')]")
        return HotelsSearchPage(driver, timeout)
    }

    fun openJournal(): JournalPage {
        click("//*[self::a or self::button][contains(normalize-space(.), 'Журнал')]")
        switchToLastWindow()
        return JournalPage(driver, timeout)
    }

    fun openMultiCitySearch(): MultiCitySearchPage {
        click("//*[self::a or self::button][contains(normalize-space(.), 'сложный маршрут') or contains(normalize-space(.), 'Сложный маршрут')]")
        return MultiCitySearchPage(driver, timeout)
    }

    fun openPriceMap(): PriceMapPage {
        click("//*[contains(normalize-space(.), 'Карта цен')]/ancestor-or-self::*[self::a or self::button][1]")
        return PriceMapPage(driver, timeout)
    }

    fun searchRoundTrip(from: String, to: String, departureDateLabel: String, returnDateLabel: String): FlightResultsPage {
        type("(//input[not(@type='hidden')])[1]", from)
        click("//*[contains(normalize-space(.), '$from')][1]")
        type("(//input[not(@type='hidden')])[2]", to)
        click("//*[contains(normalize-space(.), '$to')][1]")
        click("//*[contains(@aria-label, '$departureDateLabel') or contains(normalize-space(.), '$departureDateLabel')][1]")
        click("//*[contains(@aria-label, '$returnDateLabel') or contains(normalize-space(.), '$returnDateLabel')][1]")
        click("//*[self::button or self::a][contains(normalize-space(.), 'Найти')]")
        return FlightResultsPage(driver, timeout)
    }

    private fun switchToLastWindow() {
        val currentWindows = driver.windowHandles
        driver.switchTo().window(currentWindows.last())
    }
}
