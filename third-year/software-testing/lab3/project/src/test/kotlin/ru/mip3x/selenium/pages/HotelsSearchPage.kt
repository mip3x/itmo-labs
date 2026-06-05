package ru.mip3x.selenium.pages

import org.openqa.selenium.WebDriver
import org.openqa.selenium.interactions.Actions
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HotelsSearchPage(
    driver: WebDriver,
    timeout: Duration,
) : BasePage(driver, timeout) {
    private val hotelForm = "//*[@data-test-id='hotel-form']"
    private val cityInput = "//*[@data-test-id='hotel-autocomplete-input']"
    private val autocompleteOption = "//*[@role='option']"
    private val checkInField = "//*[@data-test-id='start-date-field']"
    private val checkOutField = "//*[@data-test-id='end-date-field']"
    private val checkInValue = "//*[@data-test-id='start-date-value']"
    private val checkOutValue = "//*[@data-test-id='end-date-value']"
    private val searchButton = "//*[@data-test-id='form-submit']"
    private val anyDateCell = "//*[starts-with(@data-test-id,'date-')]"
    private val popupClose = "//*[@data-test-id='hotels-informer-modal-close-button']"

    fun isLoaded(): Boolean {
        val loaded = waitFor {
            driver.findElements(xpath(hotelForm)).any { it.isDisplayed } ||
                driver.findElements(xpath(cityInput)).any { it.isDisplayed }
        }

        closePopupIfPresent()
        return loaded
    }

    fun setCity(city: String): HotelsSearchPage {
        type(cityInput, city)

        wait.until {
            driver.findElements(xpath(autocompleteOption))
                .firstOrNull {
                    it.isDisplayed &&
                        it.text.lineSequence().firstOrNull()?.startsWith(city, ignoreCase = true) == true
                }
                ?.let {
                    it.click()
                    true
                } ?: false
        }

        return this
    }

    fun selectDates(checkIn: LocalDate, checkOut: LocalDate): HotelsSearchPage {
        openCalendar(checkInField)
        pickDay(checkIn, checkInValue)

        openCalendar(checkOutField)
        pickDay(checkOut, checkOutValue)

        return this
    }

    fun search(): HotelsResultsPage {
        val originalWindow = driver.windowHandle
        val urlBefore = driver.currentUrl

        jsClick(searchButton)

        wait.until {
            driver.currentUrl != urlBefore || driver.windowHandles.size > 1
        }

        if (driver.windowHandles.size > 1) {
            driver.switchTo().window(driver.windowHandles.first { it != originalWindow })
        }

        closePopupIfPresent()
        return HotelsResultsPage(driver, timeout)
    }

    fun search(city: String, checkIn: LocalDate, checkOut: LocalDate): HotelsResultsPage {
        return setCity(city)
            .selectDates(checkIn, checkOut)
            .search()
    }

    private fun openCalendar(fieldXpath: String) {
        val field = clickable(fieldXpath)
        Actions(driver).moveToElement(field).pause(Duration.ofMillis(150)).click().perform()
        wait.until { driver.findElements(xpath(anyDateCell)).any { it.isDisplayed } }
    }

    private fun pickDay(target: LocalDate, selectedValueXpath: String) {
        selectMonthIfPresent(target)

        val dateId = "date-${target.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}"
        val day = "//*[@data-test-id='$dateId']"

        wait.until {
            runCatching {
                val element = driver.findElements(xpath(day))
                    .firstOrNull { it.isDisplayed && it.isEnabled }
                    ?: return@runCatching false

                scrollTo(element)
                jsClick(element)
                isDateSelected(selectedValueXpath, target)
            }.getOrDefault(false)
        }
    }

    private fun isDateSelected(valueXpath: String, target: LocalDate): Boolean {
        val text = driver.findElements(xpath(valueXpath))
            .firstOrNull { it.isDisplayed }
            ?.text
            ?: return false

        return text.contains(target.dayOfMonth.toString()) &&
            text.contains(russianMonthName(target), ignoreCase = true)
    }

    private fun russianMonthName(date: LocalDate): String {
        return when (date.monthValue) {
            1 -> "января"
            2 -> "февраля"
            3 -> "марта"
            4 -> "апреля"
            5 -> "мая"
            6 -> "июня"
            7 -> "июля"
            8 -> "августа"
            9 -> "сентября"
            10 -> "октября"
            11 -> "ноября"
            else -> "декабря"
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

    private fun closePopupIfPresent() {
        runCatching {
            driver.findElements(xpath(popupClose))
                .firstOrNull { it.isDisplayed }
                ?.let { jsClick(it) }
        }
    }
}
