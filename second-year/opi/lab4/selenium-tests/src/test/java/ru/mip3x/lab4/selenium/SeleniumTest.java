package ru.mip3x.lab4.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SeleniumTest {
    private WebDriver driver;
    private WebDriverWait wait;
    private final String baseUrl = "http://localhost:8081";

    private String generateRandomUsername() {
        return "seleniumTestUser_" + System.currentTimeMillis();
    }

    @BeforeEach
    void beforeEach(TestInfo info) {
        driver.manage().deleteAllCookies();

        driver.get(baseUrl + "/");
        ((JavascriptExecutor) driver)
            .executeScript("window.localStorage.removeItem('authToken');");
        driver.get(baseUrl + "/");

        wait.until(ExpectedConditions.or(
            ExpectedConditions.elementToBeClickable(By.id("login-submit")),
            ExpectedConditions.elementToBeClickable(By.id("register-submit"))
        ));

        if (info.getTags().contains("needsAuth")) {
            driver.findElement(By.id("login-username")).sendKeys("seleniumUser");
            driver.findElement(By.id("login-password")).sendKeys("secret123");
            driver.findElement(By.id("login-submit")).click();
            wait.until(ExpectedConditions.urlToBe(baseUrl + "/main"));
        }
    }

    @AfterEach
    void pauseBeforeTest() throws InterruptedException {
        Thread.sleep(100);
    }

    @BeforeAll
    void setup() {
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--no-sandbox","--disable-gpu", "--remote-allow-origins=*");
        driver = new ChromeDriver(opts);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    void teardown() {
        if (driver != null) driver.quit();
    }

    @Test @DisplayName("Point Inside Area")
    @Tag("needsAuth")
    void pointInside() {
        driver.get(baseUrl + "/main");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("plot-submit")));

        int initialRowCount = driver.findElements(By.cssSelector(".results-table tbody tr")).size();

        driver.findElement(By.cssSelector("#x-buttons [data-test='x-btn-1']")).click();
        driver.findElement(By.id("plot-y")).sendKeys("0.5");
        driver.findElement(By.cssSelector("#r-buttons [data-test='r-btn-1']")).click();

        driver.findElement(By.id("plot-submit")).click();

        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".results-table tbody tr"), initialRowCount));

        var rows = driver.findElements(By.cssSelector(".results-table tbody tr"));
        WebElement lastRow = rows.get(rows.size() - 1);

        String resultSymbol = lastRow.findElements(By.tagName("td")).get(3).getText();
        assertEquals("✔", resultSymbol);
    }

    @Test @DisplayName("Point Outside Area")
    @Tag("needsAuth")
    void pointOutside() {
        driver.get(baseUrl + "/main");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("plot-submit")));

        int initialRowCount = driver.findElements(By.cssSelector(".results-table tbody tr")).size();

        driver.findElement(By.cssSelector("#x-buttons [data-test='x-btn--2']")).click();

        WebElement yInput = driver.findElement(By.id("plot-y"));
        yInput.clear();
        yInput.sendKeys("-2");

        driver.findElement(By.cssSelector("#r-buttons [data-test='r-btn-2']")).click();

        driver.findElement(By.id("plot-submit")).click();

        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".results-table tbody tr"), initialRowCount));

        var rows = driver.findElements(By.cssSelector(".results-table tbody tr"));
        WebElement lastRow = rows.get(rows.size() - 1);

        String resultSymbol = lastRow.findElements(By.tagName("td")).get(3).getText();

        var tds = lastRow.findElements(By.tagName("td"));
        //System.out.printf("X=%s, Y=%s, R=%s, Result=%s%n",tds.get(0).getText(), tds.get(1).getText(), tds.get(2).getText(), tds.get(3).getText());

        assertEquals("❌", resultSymbol);
    }

    @Test
    @DisplayName("Point on Border")
    @Tag("needsAuth")
    void pointOnBorder() {
        driver.get(baseUrl + "/main");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("plot-submit")));

        int initialRowCount = driver.findElements(By.cssSelector(".results-table tbody tr")).size();

        driver.findElement(By.cssSelector("#x-buttons [data-test='x-btn-0']")).click();
        WebElement yInput = driver.findElement(By.id("plot-y"));
        yInput.clear();
        yInput.sendKeys("-2");
        driver.findElement(By.cssSelector("#r-buttons [data-test='r-btn-2']")).click();
        driver.findElement(By.id("plot-submit")).click();

        wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".results-table tbody tr"), initialRowCount));

        var rows = driver.findElements(By.cssSelector(".results-table tbody tr"));
        WebElement lastRow = rows.get(rows.size() - 1);

        String resultSymbol = lastRow.findElements(By.tagName("td")).get(3).getText();

        assertEquals("✔", resultSymbol);
    }

    @Test
    @DisplayName("Register New User Successfully")
    void registerNewUser() {
        String username = generateRandomUsername();

        driver.get(baseUrl + "/");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("register-submit")));

        driver.findElement(By.id("login-username")).sendKeys(username);
        driver.findElement(By.id("login-password")).sendKeys("testpass");
        driver.findElement(By.id("register-submit")).click();

        wait.until(ExpectedConditions.urlToBe(baseUrl + "/main"));

        assertEquals(baseUrl + "/main", driver.getCurrentUrl());
    }

    @Test
    @DisplayName("Register Existing User Fails")
    void registerExistingUserFails() {
        String username = "seleniumUser";

        driver.get(baseUrl + "/");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("register-submit")));

        driver.findElement(By.id("login-username")).sendKeys(username);
        driver.findElement(By.id("login-password")).sendKeys("secret123");
        driver.findElement(By.id("register-submit")).click();

        WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("toast")));
        String message = toast.getText();

        assertTrue(message.contains("Пользователь уже существует"));
    }

    @Test
    @DisplayName("Login Success")
    void loginSuccess() {
        driver.get(baseUrl + "/");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("login-submit")));

        driver.findElement(By.id("login-username")).sendKeys("seleniumUser");
        driver.findElement(By.id("login-password")).sendKeys("secret123");
        driver.findElement(By.id("login-submit")).click();

        wait.until(ExpectedConditions.urlToBe(baseUrl + "/main"));
        assertEquals(baseUrl + "/main", driver.getCurrentUrl());
    }

    @Test
    @DisplayName("Login with Wrong Password Fails")
    void loginWrongPasswordFails() {
        driver.get(baseUrl + "/");
        wait.until(ExpectedConditions.elementToBeClickable(By.id("login-submit")));

        driver.findElement(By.id("login-username")).sendKeys("seleniumUser");
        driver.findElement(By.id("login-password")).sendKeys("wrongpass");
        driver.findElement(By.id("login-submit")).click();

        WebElement toast = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("toast")));
        assertTrue(toast.getText().contains("Ошибка входа"));
    }
}
