package com.laba.facebook.gui.components;

import java.lang.invoke.MethodHandles;

import org.openqa.selenium.Keys;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;
import com.qaprosoft.carina.core.gui.AbstractUIObject;

public class Post extends AbstractUIObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @FindBy(xpath = "//h4//b")
    private ExtendedWebElement titleLabel;
    @FindBy(xpath = "//h4//b//ancestor::h4//following-sibling::div//span")
    private ExtendedWebElement bodyLabel;
    @FindBy(xpath = "//div[@role='textbox']")
    private ExtendedWebElement commentTextInput;
    @FindBy(xpath = "//h4[contains(text(),'Comments')]")
    private ExtendedWebElement commentCountLabel;
    @FindBy(xpath = "//div[@aria-label='Actions for this post']")
    private ExtendedWebElement actionsBtn;
    @FindBy(xpath = "//span[contains(text(),'Delete post')]")
    private ExtendedWebElement deletePostBtn;
    @FindBy(xpath = "//div[@aria-label='Delete Post?']//span[text()='Delete']")
    private ExtendedWebElement deleteConfirmBtn;

    public Post(WebDriver driver, SearchContext searchContext) {
        super(driver);
        setUiLoadedMarker(bodyLabel);
    }

    public ExtendedWebElement getTitleLabel() {
        return titleLabel;
    }

    public ExtendedWebElement getBodyLabel() {
        return bodyLabel;
    }

    public int getCommentCount() {
        return Integer.parseInt(commentCountLabel.getText().substring(0, 1));
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
}
