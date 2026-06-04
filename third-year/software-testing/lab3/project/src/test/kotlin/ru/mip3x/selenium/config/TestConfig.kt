package ru.mip3x.selenium.config

import java.time.Duration

data class TestConfig(
    val baseUrl: String = System.getProperty("baseUrl", "https://www.aviasales.ru"),
    val browsers: List<Browser> = System.getProperty("browsers", "chrome")
        .split(",")
        .filter { it.isNotBlank() }
        .map(Browser::from),
    val headless: Boolean = System.getProperty("headless", "false").toBoolean(),
    val timeout: Duration = Duration.ofSeconds(System.getProperty("timeoutSeconds", "20").toLong()),
)
