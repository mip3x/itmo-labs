package ru.mip3x.selenium.tests

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.provider.Arguments
import org.openqa.selenium.WebDriver
import ru.mip3x.selenium.config.Browser
import ru.mip3x.selenium.config.TestConfig
import ru.mip3x.selenium.driver.DriverFactory
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
abstract class BaseSeleniumTest {
    protected val config = TestConfig()
    protected lateinit var driver: WebDriver

    @AfterEach
    fun tearDown() {
        if (::driver.isInitialized) {
            driver.quit()
        }
    }

    protected fun start(browser: Browser): WebDriver {
        driver = DriverFactory.create(browser, config.headless)
        return driver
    }

    companion object {
        @JvmStatic
        fun browsers(): Stream<Arguments> =
            TestConfig().browsers.map { Arguments.of(it) }.stream()
    }
}
