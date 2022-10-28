package com.laba.facebook.gui.components;

import java.lang.invoke.MethodHandles;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;
import com.qaprosoft.carina.core.gui.AbstractUIObject;

public class CreatePostDialog extends AbstractUIObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @FindBy(xpath = "//textarea[contains(@placeholder,'title?')]")
    private ExtendedWebElement titleTextInput;
    @FindBy(xpath = "//div[@role='textbox' and contains(@aria-label,'Write something')]")
    private ExtendedWebElement messageTextInput;
    @FindBy(xpath = "//div[@aria-label='Post' and @role='button']")
    private ExtendedWebElement postBtn;

    public CreatePostDialog(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
        setUiLoadedMarker(postBtn);
    }

    public void inputTitleText(String titleText) {
        titleTextInput.type(titleText);
    }

    public void inputMessageText(String message) {
        messageTextInput.type(message);
    }

    public void pressPostButton() {
        postBtn.click();
    }
}
