package ru.mip3x.selenium.pages

import org.openqa.selenium.WebDriver
import java.time.Duration

class HotelDetailsPage(
    driver: WebDriver,
    timeout: Duration,
) : BasePage(driver, timeout) {
    private val chooseRoomButton = "//*[self::button or self::a][contains(normalize-space(.), 'Выбрать номер')]"
    private val firstRoomTariff = "(//*[@data-test-id='room-tariff'])[1]"
    private val roomTariffs = "//*[@data-test-id='room-tariff']"

    fun isLoaded(): Boolean {
        return waitFor {
            driver.findElements(xpath(roomTariffs)).any { it.isDisplayed } ||
                driver.findElements(xpath(chooseRoomButton)).any { it.isDisplayed }
        }
    }

    fun chooseRoom(): HotelDetailsPage {
        runCatching {
            click(chooseRoomButton)
        }

        scrollTo(firstRoomTariff)
        waitFor { driver.findElements(xpath(roomTariffs)).any { it.isDisplayed } }
        return this
    }

    fun areRoomsVisible(): Boolean {
        return waitFor { driver.findElements(xpath(roomTariffs)).any { it.isDisplayed } }
    }

    fun bookFirstAvailableRoom(): HotelDetailsPage {
        val windowsBefore = driver.windowHandles.toSet()

        scrollTo(firstRoomTariff)
        click(firstRoomTariff)
        wait.until { driver.windowHandles.size > windowsBefore.size }
        driver.switchTo().window(driver.windowHandles.first { it !in windowsBefore })

        return this
    }

    fun isRedirectedToPartnerSite(): Boolean {
        return waitFor {
            val currentUrl = driver.currentUrl.orEmpty()

            !currentUrl.contains("aviasales.ru", ignoreCase = true) &&
                !currentUrl.contains("/hotels/", ignoreCase = true)
        }
    }
}
