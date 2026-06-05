package ru.mip3x.selenium.pages

import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import java.time.Duration

class PriceMapPage(
    driver: WebDriver,
    timeout: Duration,
) : BasePage(driver, timeout) {
    companion object {
        private const val AUTOCOMPLETE_POLLING_MILLIS = 100L
    }

    private val heading = "//*[contains(normalize-space(.), 'Куда угодно')]"
    private val map = "//*[contains(@class, 'mapboxgl-map')]"
    private val originInput = "//*[@data-test-id='origin-input']"
    private val autocompleteOption = "//*[@role='option']"
    private val citiesCollection = "//*[@data-test-id='price-map-v2-cities-collection']"
    private val cityCard = ".//*[@data-test-id='city-card']"
    private val visibleCityCard = "//*[@data-test-id='city-card']"
    private val sortButton = "//*[@data-test-id='price-map-v2-sort-chip-button']"
    private val sortByPriceOption = "//*[@data-test-id='price']"

    fun isVisible(): Boolean {
        return waitFor {
            driver.findElements(xpath(heading)).any { it.isDisplayed } &&
                driver.findElements(xpath(originInput)).any { it.isDisplayed }
        }
    }

    fun selectOrigin(origin: String): PriceMapPage {
        type(originInput, origin)

        wait
            .pollingEvery(Duration.ofMillis(AUTOCOMPLETE_POLLING_MILLIS))
            .until {
                driver.findElements(xpath(autocompleteOption))
                    .firstOrNull { it.isDisplayed && it.text.contains(origin, ignoreCase = true) }
                    ?.let {
                        it.click()
                        true
                    } ?: false
            }

        wait.until {
            driver.findElements(xpath(originInput))
                .firstOrNull { it.isDisplayed }
                ?.getAttribute("value")
                ?.contains(origin, ignoreCase = true) == true
        }

        return this
    }

    fun hasDirectionsAndMap(): Boolean {
        return waitFor {
            driver.findElements(xpath(map)).any { it.isDisplayed } &&
                driver.findElements(xpath(visibleCityCard)).any { it.isDisplayed }
        }
    }

    fun sortByPrice(): PriceMapPage {
        hasDirectionsAndMap()
        click(sortButton)
        click(sortByPriceOption)
        return this
    }

    fun arePricesSortedAscending(): Boolean {
        return waitFor {
            val collections = driver.findElements(xpath(citiesCollection))
                .filter { it.isDisplayed }

            collections.isNotEmpty() && collections.all { collection ->
                val prices = extractPricesFromCollection(collection)

                prices.size < 2 || prices.zipWithNext().all { (left, right) -> left <= right }
            }
        }
    }

    private fun extractPricesFromCollection(collection: WebElement): List<Int> {
        return collection.findElements(xpath(cityCard))
            .mapNotNull { card ->
                card.text
                    .lineSequence()
                    .firstOrNull()
                    ?.replace(Regex("[^0-9]"), "")
                    ?.toIntOrNull()
            }
    }
}
