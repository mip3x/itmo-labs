package ru.mip3x.selenium.pages

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import java.net.URI
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomePage(
    driver: WebDriver,
    timeout: Duration,
) : BasePage(driver, timeout) {
    companion object {
        private const val AUTOCOMPLETE_POLLING_MILLIS = 100L
    }

    private val originInput = "//*[@data-test-id='origin-input']"
    private val destinationInput = "//*[@data-test-id='destination-input']"
    private val autocompleteOption = "//*[@role='option']"
    private val departureDateButton = "//*[@data-test-id='start-date-field']"
    private val returnDateButton = "//*[@data-test-id='end-date-field']"
    private val searchButton = "//*[@data-test-id='form-submit']"
    private val anyDateCell = "//*[starts-with(@data-test-id,'date-')]"
    private val calendarActionButton = "//*[@data-test-id='calendar-action-button']"

    fun open(baseUrl: String): HomePage {
        driver.get(baseUrl)
        visible(originInput)
        acceptCookiesIfPresent()
        return this
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

    fun setRoute(origin: String, destination: String): HomePage {
        setOrigin(origin)
        setDestination(destination)
        return this
    }

    fun selectRoundTripDates(departureDate: LocalDate, returnDate: LocalDate): HomePage {
        openCalendar(departureDateButton)
        pickDay(departureDate)
        click(returnDateButton)
        pickDay(returnDate)
        confirmCalendar()
        return this
    }

    fun searchFlights(): FlightResultsPage {
        val originalWindow = driver.windowHandle
        val urlBefore = driver.currentUrl

        click(searchButton)

        wait.until {
            driver.windowHandles.size > 1 || driver.currentUrl != urlBefore
        }

        if (driver.windowHandles.size > 1) {
            driver.switchTo().window(driver.windowHandles.first { it != originalWindow })
        }

        return FlightResultsPage(driver, timeout)
    }

    fun searchRoundTrip(
        from: String,
        to: String,
        departureDate: LocalDate,
        returnDate: LocalDate,
    ): FlightResultsPage {
        return setRoute(from, to)
            .selectRoundTripDates(departureDate, returnDate)
            .searchFlights()
    }

    fun openHotels(): HotelsSearchPage {
        val currentUri = URI(driver.currentUrl)
        driver.navigate().to("${currentUri.scheme}://${currentUri.host}/hotels")
        return HotelsSearchPage(driver, timeout).also { it.isLoaded() }
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

    private fun openCalendar(button: String) {
        wait.until {
            runCatching {
                click(button)
                driver.findElements(xpath(anyDateCell)).any { it.isDisplayed }
            }.getOrDefault(false)
        }
    }

    private fun pickDay(target: LocalDate) {
        selectMonthIfPresent(target)

        val dateId = "date-${target.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}"
        val dayButton = "//*[@data-test-id='$dateId']/ancestor::button[1]"

        wait.until {
            runCatching {
                val element = wait.until(ExpectedConditions.elementToBeClickable(xpath(dayButton)))

                scrollTo(element)
                element.click()
                true
            }.getOrDefault(false)
        }
    }

    private fun selectMonthIfPresent(target: LocalDate) {
        driver.findElements(xpath("//*[@data-test-id='select-month']"))
            .firstOrNull()
            ?.let {
                runCatching {
                    Select(it).selectByValue(target.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                }
            }
    }

    private fun confirmCalendar() {
        runCatching {
            wait.until(ExpectedConditions.elementToBeClickable(xpath(calendarActionButton)))
                .click()
        }
    }

    private fun switchToLastWindow() {
        val currentWindows = driver.windowHandles
        driver.switchTo().window(currentWindows.last())
    }
}
