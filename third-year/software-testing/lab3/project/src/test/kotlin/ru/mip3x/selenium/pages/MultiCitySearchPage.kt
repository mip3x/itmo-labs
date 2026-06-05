package ru.mip3x.selenium.pages

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MultiCitySearchPage(
    driver: WebDriver,
    timeout: Duration,
) : BasePage(driver, timeout) {
    companion object {
        private const val AUTOCOMPLETE_POLLING_MILLIS = 100L
    }

    private val form = "//*[@data-test-id='multiway-form']"
    private val originInputs = "//*[@data-test-id='multiway-origin-input']"
    private val destinationInputs = "//*[@data-test-id='multiway-destination-input']"
    private val dateButtons = "//*[@data-test-id='multiway-date']"
    private val searchButton = "//*[@data-test-id='form-submit']"
    private val autocompleteOption = "//*[@role='option']"
    private val anyDateCell = "//*[starts-with(@data-test-id,'date-')]"

    fun isLoaded(): Boolean {
        return waitFor {
            driver.findElements(xpath(form)).any { it.isDisplayed }
        }
    }

    fun hasSegment(number: Int): Boolean {
        return waitFor {
            driver.findElements(xpath("//*[@data-test-id='multiway-direction-$number']"))
                .any { it.isDisplayed }
        }
    }

    fun setSegment(
        number: Int,
        from: String,
        to: String,
        date: LocalDate,
    ): MultiCitySearchPage {
        selectCity("($originInputs)[$number]", from)
        selectCity("($destinationInputs)[$number]", to)
        pickDate(number, date)
        return this
    }

    fun search(): FlightResultsPage {
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

    private fun pickDate(segmentNumber: Int, target: LocalDate) {
        openCalendar(segmentNumber)
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

    private fun openCalendar(segmentNumber: Int) {
        val dateButton = "($dateButtons)[$segmentNumber]"

        wait.until {
            runCatching {
                click(dateButton)
                driver.findElements(xpath(anyDateCell)).any { it.isDisplayed }
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
}
