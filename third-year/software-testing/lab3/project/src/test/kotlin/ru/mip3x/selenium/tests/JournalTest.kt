package ru.mip3x.selenium.tests

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.mip3x.selenium.pages.HomePage

@Disabled
class JournalTest : BaseSeleniumTest() {
    @Test
    fun journalSearchByBrestOpensFirstArticle() {
        val journal = HomePage(start(), config.timeout)
            .open(config.baseUrl)
            .openJournal()

        assertTrue(journal.isLoaded())

        journal.search("Брест")
        assertTrue(journal.hasRelevantSearchResults())

        val articlePage = journal.openFirstArticle()
        assertTrue(articlePage.hasTitle(journal.selectedArticleTitle()))
    }
}
