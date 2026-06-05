package ru.mip3x.selenium.pages

import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.ExpectedConditions
import java.time.Duration

class JournalPage(
    driver: WebDriver,
    timeout: Duration,
) : BasePage(driver, timeout) {
    private val searchIconButton = "//button[contains(@class,'SearchButton_searchButton')]"
    private val searchInput = "//input[@placeholder='Поиск']"
    private val anyHeading = "//h1 | //h2 | //h3"
    private val articleLink = "//a[contains(@class,'ArticleCard_titleLink') and contains(@href,'/psgr/article/')]"

    private var lastQuery = ""
    private var selectedArticleTitle = ""

    fun isLoaded(): Boolean {
        return waitFor {
            driver.currentUrl.contains("/psgr") &&
                driver.findElements(xpath(anyHeading)).any { it.isDisplayed }
        }
    }

    fun search(query: String): JournalPage {
        lastQuery = query

        jsClick(searchIconButton)

        val input = wait.until(
            ExpectedConditions.visibilityOfElementLocated(xpath(searchInput))
        )

        input.clear()
        input.sendKeys(query)
        input.sendKeys(Keys.ENTER)

        return this
    }

    fun hasRelevantSearchResults(): Boolean {
        return waitFor {
            val inputValue = driver.findElements(xpath(searchInput))
                .firstOrNull { it.isDisplayed }
                ?.getAttribute("value")
                .orEmpty()

            inputValue.contains(lastQuery, ignoreCase = true) &&
                driver.findElements(xpath(articleLink)).any { it.isDisplayed }
        }
    }

    fun openFirstArticle(): ArticlePage {
        val originalWindows = driver.windowHandles
        val urlBefore = driver.currentUrl
        val article = wait.until {
            driver.findElements(xpath(articleLink))
                .firstOrNull { it.isDisplayed && articleTitle(it.text).isNotEmpty() }
        }!!

        selectedArticleTitle = articleTitle(article.text)
        jsClick(article)

        wait.until {
            driver.windowHandles.size > originalWindows.size || driver.currentUrl != urlBefore
        }

        val newWindow = driver.windowHandles.firstOrNull { it !in originalWindows }
        if (newWindow != null) {
            driver.switchTo().window(newWindow)
        }

        val articlePage = ArticlePage(driver, timeout)
        articlePage.hasTitle(selectedArticleTitle)
        return articlePage
    }

    fun selectedArticleTitle(): String {
        return selectedArticleTitle
    }

    private fun articleTitle(text: String): String {
        return text
            .lineSequence()
            .map { it.trim() }
            .firstOrNull { it.isNotEmpty() }
            .orEmpty()
    }
}
