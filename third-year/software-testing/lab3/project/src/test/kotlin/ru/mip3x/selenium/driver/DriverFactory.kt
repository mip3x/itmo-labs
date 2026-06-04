package ru.mip3x.selenium.driver

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import ru.mip3x.selenium.config.Browser

object DriverFactory {
    fun create(browser: Browser, headless: Boolean): WebDriver =
        when (browser) {
            Browser.CHROME -> createChrome(headless)
            Browser.FIREFOX -> createFirefox(headless)
        }

    private fun createChrome(headless: Boolean): WebDriver {
        WebDriverManager.chromedriver()
            .browserVersion("148")
            .setup()

        val options = ChromeOptions().apply {
            setBinary("/usr/bin/chromium")

            if (headless) {
                addArguments("--headless=new")
            }

            addArguments(
                "--disable-notifications",
                "--disable-popup-blocking",
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--remote-allow-origins=*",
            )
        }

        return ChromeDriver(options)
    }

    private fun createFirefox(headless: Boolean): WebDriver {
        WebDriverManager.firefoxdriver().setup()

        val options = FirefoxOptions().apply {
            if (headless) {
                addArguments("-headless")
            }
        }

        return FirefoxDriver(options)
    }
}
