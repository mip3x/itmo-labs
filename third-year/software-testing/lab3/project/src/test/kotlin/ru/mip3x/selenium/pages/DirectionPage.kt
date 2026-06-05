package ru.mip3x.selenium.pages

import org.openqa.selenium.WebDriver
import java.time.Duration

class DirectionPage(
    driver: WebDriver,
    timeout: Duration,
) : BasePage(driver, timeout) {
    fun hasRouteInfo(from: String, to: String): Boolean =
        hasVisible("//*[contains(normalize-space(.), '$from') and contains(normalize-space(.), '$to')]") &&
            hasVisible("//*[contains(normalize-space(.), 'Самые дешёвые') or contains(normalize-space(.), 'График цен')]")

    fun hasRouteCategories(): Boolean {
        return waitFor {
            val pageText = driver.findElement(xpath("//*")).text

            listOf(
                "Правила въезда",
                "Самые дешёвые билеты",
                "График цен",
                "Расписание прямых рейсов",
                "Пригодится в пути",
            ).all { pageText.contains(it, ignoreCase = true) }
        }
    }

    fun hasMainRouteBlocks(): Boolean {
        return waitFor {
            val pageText = driver.findElement(xpath("//*")).text

            pageText.contains("Самые дешёвые билеты", ignoreCase = true) &&
                pageText.contains("График цен", ignoreCase = true)
        }
    }
}
