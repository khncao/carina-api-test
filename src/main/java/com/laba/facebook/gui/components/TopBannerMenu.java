package com.laba.facebook.gui.components;

import java.lang.invoke.MethodHandles;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;
import com.qaprosoft.carina.core.gui.AbstractUIObject;

public class TopBannerMenu extends AbstractUIObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @FindBy(xpath = "//a[@aria-label='Home']")
    private ExtendedWebElement homeBtn;
    @FindBy(xpath = "//a[@aria-label='Friends']")
    private ExtendedWebElement friendsBtn;

    public TopBannerMenu(WebDriver driver, SearchContext searchContext) {
        super(driver, searchContext);
        setUiLoadedMarker(homeBtn);
    }

    public ExtendedWebElement getHomeButton() {
        return homeBtn;
    }
}
