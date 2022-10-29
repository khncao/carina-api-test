package com.laba.facebook.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;
import java.util.Set;

import org.apache.ibatis.io.Resources;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.laba.facebook.gui.components.CreatePostDialog;
import com.laba.facebook.gui.components.Post;
import com.laba.facebook.gui.pages.GroupPage;
import com.laba.facebook.gui.pages.HomePage;
import com.laba.facebook.gui.pages.LoginPage;
import com.qaprosoft.carina.core.foundation.IAbstractTest;
import com.qaprosoft.carina.core.foundation.utils.Configuration;
import com.qaprosoft.carina.core.foundation.utils.ownership.MethodOwner;

public class GroupsWebTest implements IAbstractTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String SessionCookiePath = "src/main/resources/local/facebookSessionCookies.ser";
    private static Properties localProperties;
    private static Set<Cookie> sessionCookies;

    @BeforeClass
    public void beforeClass() {
        try {
            localProperties = Resources.getResourceAsProperties("local.properties");
        } catch (IOException e) {
            LOGGER.error(e.toString());
        }
        sessionCookies = loadCookiesFromFile(SessionCookiePath);
    }

    @Test()
    @MethodOwner(owner = "khncao")
    public void testLogin() {
        WebDriver driver = getDriverOrFallback();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();

        Assert.assertTrue(loginPage.isPageOpened(), "Login page did not open as expected");
        loginPage.login(localProperties.getProperty("fb_email"),
                localProperties.getProperty("fb_pass"));

        HomePage homePage = new HomePage(driver);
        homePage.open();
        Assert.assertTrue(homePage.isPageOpened(), "Home page(banner) was not loaded after login as expected");

        sessionCookies = driver.manage().getCookies();
    }

    @Test()
    @MethodOwner(owner = "khncao")
    public void testLoginWithInvalidCredentials() {
        WebDriver driver = getDriverOrFallback();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();

        Assert.assertTrue(loginPage.isPageOpened(), "Login page did not open as expected");
        loginPage.login("invalid@test.com", "test1");

        loginPage.open();
        Assert.assertTrue(loginPage.isPageOpened(), "Login page did not open despite expected failed login");
    }

    @Test(priority = 3)
    @MethodOwner(owner = "khncao")
    public void testCreateGroupPost() {
        WebDriver driver = getDriverOrFallback();
        GroupPage groupPage = openLoggedInGroupPage(driver);

        groupPage.pressPostComposeButton();
        CreatePostDialog createPostDialog = groupPage.getCreatePostDialog();
        Assert.assertTrue(createPostDialog.isUIObjectPresent(), "Group compose post dialog did not open as expected");
        String postTitle = "testCreateGroupPost title";
        String postMessage = "testCreateGroupPost message body test input.";
        createPostDialog.inputTitleText(postTitle);
        createPostDialog.inputMessageText(postMessage);
        createPostDialog.pressPostButton();

        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(createPostDialog.getRootBy()));
        refreshAndWaitUntilDone(driver);
        wait.until(ExpectedConditions.visibilityOf(groupPage.getGroupPostFeed().getRootElement()));
        Post firstPost = groupPage.getGroupPostFeed().getFirstPost();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(firstPost.getTitleLabel().getText(), postTitle, "First post title did not match expected test value");
        softAssert.assertEquals(firstPost.getBodyLabel().getText(), postMessage, "First post body did not match expected test value");
        softAssert.assertAll();
    }

    @Test(priority = 4)
    @MethodOwner(owner = "khncao")
    public void testAddCommentOnFirstGroupPost() {
        WebDriver driver = getDriverOrFallback();
        GroupPage groupPage = openLoggedInGroupPage(driver);
        Post firstPost = groupPage.getGroupPostFeed().getFirstPost();
        int startCommentCount = firstPost.getCommentCount();
        String comment = "test comment abc";
        firstPost.typeAndSubmitCommentText(comment);
        refreshAndWaitUntilDone(driver);
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOf(groupPage.getGroupPostFeed().getFirstPost().getRootElement()));
        int commentCount = groupPage.getGroupPostFeed().getFirstPost().getCommentCount();
        Assert.assertEquals(commentCount, startCommentCount + 1, "Comment count did not increment by 1 as expected: " + commentCount + " : " + startCommentCount);
    }

    @Test(priority = 5)
    @MethodOwner(owner = "khncao")
    public void testDeleteFirstGroupPost() {
        WebDriver driver = getDriverOrFallback();
        GroupPage groupPage = openLoggedInGroupPage(driver);
        Post firstPost = groupPage.getGroupPostFeed().getFirstPost();
        firstPost.pressPostActionsButton();
        firstPost.pressDeletePostButton();
        firstPost.pressDeleteConfirmButton();
        Assert.assertTrue(firstPost.waitUntilPostDeletedAlert(5));
    }

    private GroupPage openLoggedInGroupPage(WebDriver driver) {
        GroupPage groupPage = new GroupPage(driver, localProperties.getProperty("group_id"));
        groupPage.open();
        injectCookiesAndRefresh(driver);
        Assert.assertTrue(groupPage.isPageOpened(), "Group page did not open as expected");
        return groupPage;
    }

    // TODO(khncao): to some kind of util or parent
    private WebDriver getDriverOrFallback() {
        WebDriver driver = null;
        try {
            URI statusUri = new URI(Configuration.getSeleniumUrl() + "/status");
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder(statusUri).GET().timeout(Duration.ofSeconds(2)).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 200) {
                driver = getDriver();
            }
        } catch (MalformedURLException e) {
            LOGGER.error(e.toString());
        } catch (IOException e) {
            LOGGER.error(e.toString());
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.toString());
        } catch (InterruptedException e) {
            LOGGER.error(e.toString());
        } catch (URISyntaxException e) {
            LOGGER.error(e.toString());
        } finally {
            if(driver == null) {
                String browser = Configuration.getBrowser();
                if(browser == null || browser.isEmpty()) {
                    LOGGER.error("Selenium browser not configured");
                }
                switch(browser) {
                    case "chrome": {
                        driver = new ChromeDriver();
                        break;
                    }
                    case "firefox": {
                        driver = new FirefoxDriver();
                        break;
                    }
                    default: {
                        LOGGER.error(browser + " not supported");
                    }
                }
            }
        }
        return driver;
    }

    // TODO(khncao): move to some sort of cookie session manager util
    private void saveCookiesToFile(WebDriver driver, String path) {
        Set<Cookie> cookies = driver.manage().getCookies();
        try (FileOutputStream fileOut = new FileOutputStream(new File(path));
                ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {
            objectOut.writeObject(cookies);
        } catch (IOException e) {
            LOGGER.error(e.toString());
        }
    }

    private Set<Cookie> loadCookiesFromFile(String path) {
        Set<Cookie> cookies = null;
        try (FileInputStream fileIn = new FileInputStream(new File(SessionCookiePath));
                ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {
            cookies = (Set<Cookie>) objectIn.readObject();
        } catch (IOException e) {
            LOGGER.error(e.toString());
        } catch (ClassNotFoundException e) {
            LOGGER.error(e.toString());
        }
        return cookies;
    }

    private void injectCookiesAndRefresh(WebDriver driver) {
        if (sessionCookies != null) {
            for (Cookie c : sessionCookies) {
                driver.manage().addCookie(c);
            }
        }
        refreshAndWaitUntilDone(driver);
    }

    private void refreshAndWaitUntilDone(WebDriver driver) {
        WebElement anyEle = driver.findElement(By.xpath("*"));
        WebDriverWait wait = new WebDriverWait(driver, 5);
        driver.navigate().refresh();
        wait.until(ExpectedConditions.stalenessOf(anyEle));
    }
}
