package com.laba.facebook.gui.pages;

import java.lang.invoke.MethodHandles;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.laba.facebook.gui.components.TopBannerMenu;
import com.qaprosoft.carina.core.gui.AbstractPage;

public class HomePage extends AbstractPage {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @FindBy(xpath = "//div[@role='banner']")
    private TopBannerMenu topBannerMenu;
    
    public HomePage(WebDriver driver) {
        super(driver);
        setUiLoadedMarker(topBannerMenu.getHomeButton());
    }

    public TopBannerMenu getTopBannerMenu() {
        return topBannerMenu;
    }
}
