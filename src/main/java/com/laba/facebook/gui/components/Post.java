package com.laba.facebook.gui.components;

import java.lang.invoke.MethodHandles;
import java.util.NoSuchElementException;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;
import com.qaprosoft.carina.core.gui.AbstractUIObject;

public class Post extends AbstractUIObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String titleXpath = "//h4//b";
    private static final String postDeleteSuccessAlertXpath = "//div[@role='alert']//*[contains(text(),'deleted')]";
    private static final String commentCountRelXPath = "//*[contains(text(),'Comment')]";

    @FindBy(xpath = "//div[@role='textbox']")
    private ExtendedWebElement commentTextInput;
    // @FindBy(xpath = "//*[contains(text(),'Comments')]")
    private ExtendedWebElement commentCountLabel;
    @FindBy(xpath = "//div[@aria-label='Actions for this post']")
    private ExtendedWebElement actionsBtn;
    @FindBy(xpath = "//span[contains(text(),'Delete post')]")
    private ExtendedWebElement deletePostBtn;
    @FindBy(xpath = "//div[@aria-label='Delete Post?']//span[text()='Delete']")
    private ExtendedWebElement deleteConfirmBtn;

    private WebElement titleLabel;
    private WebElement bodyLabel;
    private WebElement commentsLabel;

    public Post(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
        setUiLoadedMarker(actionsBtn);
    }

    public ExtendedWebElement getActionsButton() {
        return actionsBtn;
    }

    public WebElement getTitleLabel() {
        try {
            titleLabel = rootElement.findElement(By.xpath(titleXpath));
        } catch (NoSuchElementException e) {
            LOGGER.error(e.toString());
            return null;
        }
        return titleLabel;
    }

    public WebElement getBodyLabel() {
        if(bodyLabel == null) {
            bodyLabel = rootElement.findElement(By.xpath(getPostBodyXpath()));
        }
        return bodyLabel;
    }

    public int getCommentCount() {
        commentsLabel = rootElement.findElement(By.xpath(commentCountRelXPath));
        return Integer.parseInt(commentsLabel.getText().substring(0, 1));
    }

    public void typeAndSubmitCommentText(String commentText) {
        commentTextInput.type(commentText);
        commentTextInput.sendKeys(Keys.ENTER);
    }

    public void pressPostActionsButton() {
        actionsBtn.click();
    }

    public void pressDeletePostButton() {
        deletePostBtn.click();
    }

    public void pressDeleteConfirmButton() {
        deleteConfirmBtn.click();
    }

    public boolean waitUntilPostDeletedAlert(long timeOutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(postDeleteSuccessAlertXpath)));
        return element != null;
    }

    private final String getPostBodyXpath() {
        String classes = rootElement.getAttribute("aria-describedby");
        String[] split = classes.split(" ");
        return "//div[@id='" + split[1] + "']/*/div";
    }
}
