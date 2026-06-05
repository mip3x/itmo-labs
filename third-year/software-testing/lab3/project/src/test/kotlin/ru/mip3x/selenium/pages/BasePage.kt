package ru.mip3x.selenium.pages

import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.time.Duration

abstract class BasePage(
    protected val driver: WebDriver,
    protected val timeout: Duration,
) {
    protected val wait = WebDriverWait(driver, timeout)
    private val acceptCookiesButton = "//*[@data-test-id='accept-cookies-button']"

    protected fun xpath(value: String): By = By.xpath(value)

    protected fun visible(xpath: String): WebElement =
        wait.until(ExpectedConditions.visibilityOfElementLocated(xpath(xpath)))

    protected fun clickable(xpath: String): WebElement =
        wait.until(ExpectedConditions.elementToBeClickable(xpath(xpath)))

    protected fun click(xpath: String) {
        clickable(xpath).click()
    }

    protected fun type(xpath: String, value: String) {
        val element = clickable(xpath)
        element.click()
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"))
        element.sendKeys(Keys.DELETE)
        element.sendKeys(value)
    }

    protected fun hasVisible(xpath: String): Boolean =
        runCatching { visible(xpath).isDisplayed }.getOrDefault(false)

    protected fun waitFor(condition: () -> Boolean): Boolean =
        runCatching { wait.until { condition() } }.getOrDefault(false)

    protected fun acceptCookiesIfPresent() {
        runCatching {
            val button = driver.findElements(xpath(acceptCookiesButton)).firstOrNull { it.isDisplayed }
                ?: return

            button.click()
        }
    }

    protected fun scrollTo(xpath: String) {
        val element = visible(xpath)
        scrollTo(element)
    }

    protected fun scrollTo(element: WebElement) {
        (driver as JavascriptExecutor).executeScript("arguments[0].scrollIntoView({block: 'center'});", element)
    }
}
