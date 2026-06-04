package ru.mip3x.selenium.pages

import org.openqa.selenium.WebDriver
import java.time.Duration

class FlightResultsPage(
    driver: WebDriver,
    timeout: Duration,
) : BasePage(driver, timeout) {
    private val ticketPreview = "//*[@data-test-id='ticket-preview']"
    private val ticketBadge = "//*[starts-with(@data-test-id,'ticket-preview-badge')]"
    private val buyButton = "//*[starts-with(@data-test-id,'proposal-') and contains(@data-test-id,'-button')]"
    private val closedAirportWarningText = "//*[@data-test-id='text' and contains(., 'Симферополя') and contains(., 'закрыты')]"

    fun waitForResults(): FlightResultsPage {
        waitFor { ticketsVisible() }
        acceptCookiesIfPresent()
        return this
    }

    fun areRoundTripTicketsVisible(): Boolean {
        return waitFor {
            val firstTicket = driver.findElements(xpath(ticketPreview)).firstOrNull()
            firstTicket != null && Regex("в пути").findAll(firstTicket.text).count() >= 2
        }
    }

    fun clickFirstTicket(): FlightResultsPage {
        click(ticketBadge)

        wait.until {
            runCatching {
                val button = driver.findElements(xpath(buyButton)).firstOrNull { it.isDisplayed }
                    ?: return@runCatching false

                button.click()
                true
            }.getOrDefault(false)
        }

        return this
    }

    fun isAirlineSiteOpened(): Boolean {
        val urlBefore = driver.currentUrl
        val opened = waitFor { driver.windowHandles.size > 1 || driver.currentUrl != urlBefore }

        if (opened && driver.windowHandles.size > 1) {
            driver.switchTo().window(driver.windowHandles.last())
        }

        return opened
    }

    fun hasClosedAirportWarning(): Boolean {
        return hasVisible(closedAirportWarningText)
    }

    fun enableBaggageFilter(): FlightResultsPage {
        click("//*[self::label or self::button][contains(normalize-space(.), 'багаж') or contains(normalize-space(.), 'Багаж')]")
        return this
    }

    fun selectBaggageAmount(label: String): FlightResultsPage {
        click("//*[self::label or self::button][contains(normalize-space(.), '$label')]")
        return this
    }

    fun selectDepartureTime(label: String): FlightResultsPage {
        click("//*[self::label or self::button][contains(normalize-space(.), '$label')]")
        return this
    }

    fun hasBaggageInfo(): Boolean {
        return hasVisible("//*[contains(normalize-space(.), 'багаж') or contains(normalize-space(.), 'Багаж') or contains(normalize-space(.), 'кг')]")
    }

    private fun ticketsVisible(): Boolean {
        return driver.findElements(xpath(ticketPreview)).any { it.isDisplayed }
    }
}
