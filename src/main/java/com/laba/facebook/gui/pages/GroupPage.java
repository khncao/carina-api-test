package com.laba.facebook.gui.pages;

import java.lang.invoke.MethodHandles;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.laba.facebook.gui.components.CreatePostDialog;
import com.laba.facebook.gui.components.Feed;
import com.laba.facebook.gui.components.TopBannerMenu;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;
import com.qaprosoft.carina.core.gui.AbstractPage;

public class GroupPage extends AbstractPage {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @FindBy(xpath = "//div[@role='banner']")
    private TopBannerMenu topBannerMenu;
    @FindBy(xpath = "//span[contains(text(),'Write something')]//ancestor::div[2 and @role='button']")
    private ExtendedWebElement postComposeBtn;
    @FindBy(xpath = "//div[@data-pagelet='GroupInlineComposer']")
    private CreatePostDialog createPostDialog;
    @FindBy(xpath = "//div[@role='feed']")
    private Feed groupPostFeed;
    
    public GroupPage(WebDriver driver, String groupId) {
        super(driver);
        setPageURL("/groups/" + groupId);
        setUiLoadedMarker(postComposeBtn);
    }

    public TopBannerMenu getTopBannerMenu() {
        return topBannerMenu;
    }

    public CreatePostDialog getCreatePostDialog() {
        return createPostDialog;
    }

    public Feed getGroupPostFeed() {
        return groupPostFeed;
    }

    public void pressPostComposeButton() {
        postComposeBtn.click();
    }
}
