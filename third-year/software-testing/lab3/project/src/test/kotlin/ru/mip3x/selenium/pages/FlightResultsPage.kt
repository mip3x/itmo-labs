package ru.mip3x.selenium.pages

import org.openqa.selenium.WebDriver
import org.openqa.selenium.Keys
import java.time.Duration

class FlightResultsPage(
    driver: WebDriver,
    timeout: Duration,
) : BasePage(driver, timeout) {
    private val ticketPreview = "//*[@data-test-id='ticket-preview']"
    private val ticketBadge = "//*[starts-with(@data-test-id,'ticket-preview-badge')]"
    private val buyButton = "//*[starts-with(@data-test-id,'proposal-') and contains(@data-test-id,'-button')]"
    private val baggageFilter = "//*[@data-test-id='boolean-filter-baggage']"
    private val priceFilterGroup = "//*[@data-test-id='filter-group-price_side_group']"
    private val priceSlider = "(//*[@data-test-id='dynamic-filter-instance-price']//*[@role='slider'])[last()]"
    private val ticketPrice = ".//*[@data-test-id='price']"
    private val closedAirportWarningText = "//*[@data-test-id='text' and contains(., 'Симферополя') and contains(., 'закрыты')]"
    private val ticketBaggageInfo = "//*[@data-test-id='ticket-preview']//*[contains(normalize-space(.), 'багаж') or contains(normalize-space(.), 'Багаж') or contains(normalize-space(.), 'кг')]"
    private val baggageGroup = "//*[@data-test-id='boolean-filter-baggage']/ancestor::*[contains(normalize-space(.), 'Багаж')][1]"
    private val baggageAmountPlusButton = "$baggageGroup//*[@data-test-id='increase-button']"
    private val baggageAmountValue = "$baggageGroup//*[@data-test-id='stepper-value']"

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
        click(baggageFilter)
        waitFor { ticketsVisible() }
        return this
    }

    fun selectBaggageWeight(label: String): FlightResultsPage {
        clickBaggageFilterRow(label)
        waitFor { ticketsVisible() }
        return this
    }

    fun selectBaggageAmount(amount: Int): FlightResultsPage {
        repeat(amount - 1) {
            click(baggageAmountPlusButton)
        }

        waitFor {
            driver.findElements(xpath(baggageAmountValue))
                .any { it.isDisplayed && it.text.contains("$amount шт") }
        }
        waitFor { ticketsVisible() }
        return this
    }

    fun selectDepartureTime(label: String): FlightResultsPage {
        clickFilter(label)
        waitFor { ticketsVisible() }
        return this
    }

    fun selectDaytimeDeparture(): FlightResultsPage {
        clickFilter("Днём")

        waitFor {
            val tickets = visibleTicketTexts()

            allTicketTextsHaveDepartureTimeBetween(tickets, 12, 18)
        }

        return this
    }

    fun hasBaggageInfo(): Boolean {
        return waitFor {
            driver.findElements(xpath(ticketBaggageInfo)).any { it.isDisplayed }
        }
    }

    fun areTicketsVisible(): Boolean {
        return waitFor { ticketsVisible() }
    }

    fun allTicketsHaveBaggageAmount(amount: Int): Boolean {
        return waitFor {
            val tickets = visibleTicketTexts()

            tickets.isNotEmpty() && tickets.all { ticket ->
                val baggageAmounts = Regex("""(\d+)\s*шт""")
                    .findAll(ticket)
                    .mapNotNull { it.groupValues[1].toIntOrNull() }
                    .toList()

                ticket.contains("багаж", ignoreCase = true) &&
                    baggageAmounts.any { it >= amount }
            }
        }
    }

    fun averageTicketPrice(): Double {
        val prices = wait.until {
            val currentPrices = visibleTicketPrices()

            currentPrices.ifEmpty { null }
        }

        return prices.orEmpty().average()
    }

    fun averageTicketPriceIsGreaterThan(price: Double): Boolean {
        return waitFor {
            val currentPrice = averageTicketPrice()

            !currentPrice.isNaN() && currentPrice > price
        }
    }

    fun allTicketsHaveBaggageWeightAtLeast(weight: Int): Boolean {
        return waitFor {
            val tickets = visibleTicketTexts()

            tickets.isNotEmpty() && tickets.all { ticket ->
                val baggageWeights = Regex("""Багаж\s+(\d+)\s*кг""", RegexOption.IGNORE_CASE)
                    .findAll(ticket)
                    .mapNotNull { it.groupValues[1].toIntOrNull() }
                    .toList()

                baggageWeights.any { it >= weight }
            }
        }
    }

    fun allDepartureTimesAreBetween(startHour: Int, endHour: Int): Boolean {
        return waitFor {
            val tickets = visibleTicketTexts()

            allTicketTextsHaveDepartureTimeBetween(tickets, startHour, endHour)
        }
    }

    fun reduceMaxPriceWithSlider(): FlightResultsPage {
        val maxPriceBefore = currentMaxPriceFilterValue()

        wait.until {
            runCatching {
                scrollTo(priceFilterGroup)
                click(priceFilterGroup)
                val slider = clickable(priceSlider)

                slider.click()
                repeat(10) {
                    slider.sendKeys(Keys.ARROW_LEFT)
                }

                currentMaxPriceFilterValue() < maxPriceBefore
            }.getOrDefault(false)
        }

        waitFor { ticketsVisible() }
        return this
    }

    fun maxTicketPrice(): Int {
        return visibleTicketPrices().maxOrNull() ?: 0
    }

    fun maxTicketPriceIsLessThan(price: Int): Boolean {
        return waitFor {
            val currentPrice = maxTicketPrice()

            currentPrice > 0 && currentPrice < price
        }
    }

    fun allVisibleTicketsWithinCurrentMaxPrice(): Boolean {
        return waitFor {
            val maxPrice = currentMaxPriceFilterValue()
            val ticketPrices = visibleTicketPrices()

            ticketPrices.isNotEmpty() && ticketPrices.all { it <= maxPrice }
        }
    }

    private fun ticketsVisible(): Boolean {
        return driver.findElements(xpath(ticketPreview)).any { it.isDisplayed }
    }

    private fun visibleTicketTexts(): List<String> {
        return driver.findElements(xpath(ticketPreview))
            .mapNotNull { ticket ->
                runCatching {
                    if (ticket.isDisplayed && isInViewport(ticket)) ticket.text else null
                }.getOrNull()
            }
    }

    private fun allTicketTextsHaveDepartureTimeBetween(
        tickets: List<String>,
        startHour: Int,
        endHour: Int,
    ): Boolean {
        return tickets.isNotEmpty() && tickets.all { ticketText ->
            val departureHour = firstDepartureHour(ticketText)

            departureHour != null && departureHour in startHour until endHour
        }
    }

    private fun visibleTicketPrices(): List<Int> {
        return driver.findElements(xpath(ticketPreview))
            .mapNotNull { ticket ->
                runCatching {
                    if (!ticket.isDisplayed) {
                        return@runCatching null
                    }

                    ticket.findElement(xpath(ticketPrice))
                        .text
                        .replace(Regex("[^\\d]"), "")
                        .toIntOrNull()
                }.getOrNull()
            }
    }

    private fun currentMaxPriceFilterValue(): Int {
        return driver.findElement(xpath(priceSlider))
            .getAttribute("aria-valuenow")
            .orEmpty()
            .toInt()
    }

    private fun firstDepartureHour(ticketText: String): Int? {
        return Regex("""\b(\d{2}):(\d{2})\b""")
            .find(ticketText)
            ?.groupValues
            ?.get(1)
            ?.toIntOrNull()
    }

    private fun clickBaggageFilterRow(label: String) {
        val labels = listOf(label, label.removePrefix("от "), label.removePrefix("От ")).distinct()

        wait.until {
            runCatching {
                val element = labels.firstNotNullOfOrNull { text ->
                    val row =
                        "$baggageGroup//*[contains(normalize-space(.), '$text')]/ancestor-or-self::*[self::label or self::button or @role='button' or @role='radio'][1]"
                    val textElement = "$baggageGroup//*[contains(normalize-space(.), '$text')]"

                    driver.findElements(xpath(row)).firstOrNull { it.isDisplayed }
                        ?: driver.findElements(xpath(textElement)).firstOrNull { it.isDisplayed }
                } ?: return@runCatching false

                element.click()
                true
            }.getOrDefault(false)
        }
    }

    private fun clickFilter(label: String) {
        val labels = listOf(label, label.removePrefix("от "), label.removePrefix("От ")).distinct()

        wait.until {
            runCatching {
                val element = labels.firstNotNullOfOrNull { text ->
                    val interactiveElement =
                        "//*[contains(normalize-space(.), '$text') and not(ancestor::*[@data-test-id='ticket-preview']) and not(ancestor::a)]/ancestor-or-self::*[self::label or self::button or @role='button' or @role='checkbox'][1]"

                    driver.findElements(xpath(interactiveElement)).firstOrNull { it.isDisplayed }
                }
                    ?: return@runCatching false

                element.click()
                true
            }.getOrDefault(false)
        }
    }
}
