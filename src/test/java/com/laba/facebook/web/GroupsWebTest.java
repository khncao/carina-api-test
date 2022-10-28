package com.laba.facebook.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.invoke.MethodHandles;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.io.Resources;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
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
        WebDriver driver = new ChromeDriver();
        // WebDriver driver = getDriver();
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open();

        Assert.assertTrue(loginPage.isPageOpened(), "Login page did not open as expected");
        loginPage.login(localProperties.getProperty("fb_email"),
                localProperties.getProperty("fb_pass"));

        HomePage homePage = new HomePage(driver);
        homePage.open();
        Assert.assertTrue(homePage.isPageOpened(), "Home page(banner) was not loaded after login as expected");

        sessionCookies = driver.manage().getCookies();
        // saveCookiesToFile(driver, SessionCookiePath);
    }

    @Test()
    @MethodOwner(owner = "khncao")
    public void testLoginWithInvalidCredentials() {
        WebDriver driver = new ChromeDriver();
        // WebDriver driver = getDriver();
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
        WebDriver driver = new ChromeDriver();
        // WebDriver driver = getDriver();
        GroupPage groupPage = openLoggedInGroupPage(driver);

        groupPage.pressPostComposeButton();
        CreatePostDialog createPostDialog = groupPage.getCreatePostDialog();
        Assert.assertTrue(createPostDialog.isUIObjectPresent(), "Group compose post dialog did not open as expected");
        String postTitle = "testCreateGroupPost title";
        String postMessage = "testCreateGroupPost message body test input.";
        createPostDialog.inputTitleText(postTitle);
        createPostDialog.inputMessageText(postMessage);
        createPostDialog.pressPostButton();
        groupPage.open();

        Post firstPost = groupPage.getGroupPostFeed().getFirstPost();
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(firstPost.getTitleLabel().getText(), postTitle, "First post title did not match expected test value");
        softAssert.assertEquals(firstPost.getBodyLabel().getText(), postMessage, "First post body did not match expected test value");
        softAssert.assertAll();
    }

    @Test(priority = 4)
    @MethodOwner(owner = "khncao")
    public void testAddCommentOnFirstGroupPost() {
        WebDriver driver = new ChromeDriver();
        // WebDriver driver = getDriver();
        GroupPage groupPage = openLoggedInGroupPage(driver);
        Post firstPost = groupPage.getGroupPostFeed().getFirstPost();
        int startCommentCount = firstPost.getCommentCount();
        String comment = "test comment abc";
        firstPost.typeAndSubmitCommentText(comment);
        groupPage.open();
        int commentCount = groupPage.getGroupPostFeed().getFirstPost().getCommentCount();
        Assert.assertEquals(commentCount, startCommentCount + 1, "Comment count did not increment by 1 as expected: " + commentCount + " : " + startCommentCount);
    }

    @Test(priority = 5)
    @MethodOwner(owner = "khncao")
    public void testDeleteFirstGroupPost() {
        WebDriver driver = new ChromeDriver();
        // WebDriver driver = getDriver();
        GroupPage groupPage = openLoggedInGroupPage(driver);
        Post firstPost = groupPage.getGroupPostFeed().getFirstPost();
        String firstPostBody = firstPost.getBodyLabel().getText();
        firstPost.pressPostActionsButton();
        firstPost.pressDeletePostButton();
        firstPost.pressDeleteConfirmButton();

        //TODO(khncao): check for post deleted alert instead
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            LOGGER.error(e.toString());
        }
        groupPage.open();
        //TODO(khncao): need better way to locate post body so doesnt rely on title; also warn if empty title
        String newFirstPostBody = groupPage.getGroupPostFeed().getFirstPost().getBodyLabel().getText();
        Assert.assertNotEquals(firstPostBody, newFirstPostBody, "Newly first post body should typically not be same as old first post's: " + firstPostBody + " : " + newFirstPostBody);
    }

    private GroupPage openLoggedInGroupPage(WebDriver driver) {
        GroupPage groupPage = new GroupPage(driver, localProperties.getProperty("group_id"));
        groupPage.open();
        injectCookiesAndRefresh(driver);
        Assert.assertTrue(groupPage.isPageOpened(), "Group page did not open as expected");
        return groupPage;
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
                System.out.println(c);
            }
        }
        driver.navigate().refresh();
    }
}
