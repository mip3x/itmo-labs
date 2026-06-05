package ru.mip3x.selenium.pages

import org.openqa.selenium.WebDriver
import java.time.Duration

class ArticlePage(
    driver: WebDriver,
    timeout: Duration,
) : BasePage(driver, timeout) {
    fun title(): String = visible("//h1").text

    fun hasTitle(expectedTitle: String): Boolean {
        return waitFor {
            val actualTitle = title()

            actualTitle.contains(expectedTitle, ignoreCase = true) ||
                expectedTitle.contains(actualTitle, ignoreCase = true)
        }
    }
}
