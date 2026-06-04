package ru.mip3x.selenium.pages

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

class HomePage(
    driver: WebDriver,
    timeout: Duration,
) : BasePage(driver, timeout) {
    companion object {
        private const val AUTOCOMPLETE_POLLING_MILLIS = 100L
    }

    private val originInput = "//input[@data-test-id='origin-input']"
    private val destinationInput = "//input[@data-test-id='destination-input']"
    private val autocompleteOption = "//*[@role='option']"

    private fun selectCity(input: String, city: String) {
        type(input, city)

        runCatching {
                wait
                .pollingEvery(Duration.ofMillis(AUTOCOMPLETE_POLLING_MILLIS))
                .until {
                    driver.findElements(xpath(autocompleteOption))
                        .firstOrNull { it.isDisplayed && it.text.contains(city, ignoreCase = true) }
                        ?.let {
                            it.click()
                            true
                        } ?: false
                }
        }
    }

    fun isOpened(): Boolean {
        return hasVisible(originInput)
    }

    fun originValue(): String {
        return visible(originInput).getAttribute("value") ?: ""
    }

    fun destinationValue(): String {
        return visible(destinationInput).getAttribute("value") ?: ""
    }

    fun setOrigin(city: String): HomePage {
        selectCity(originInput, city)
        return this
    }

    fun setDestination(city: String): HomePage {
        selectCity(destinationInput, city)
        return this
    }

    fun open(baseUrl: String): HomePage {
        driver.get(baseUrl)
        visible(originInput)
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
