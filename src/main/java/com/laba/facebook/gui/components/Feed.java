package com.laba.facebook.gui.components;

import java.lang.invoke.MethodHandles;
import java.util.List;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qaprosoft.carina.core.gui.AbstractUIObject;

public class Feed extends AbstractUIObject {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @FindBy(xpath = "//div[@aria-posinset=1]")
    private Post firstPost;
    @FindBy(xpath = "//div[@aria-posinset]")
    private List<Post> posts;

    public Feed(WebDriver driver, SearchContext searchContext) {
        super(driver);
        setUiLoadedMarker(firstPost.getTitleLabel());
    }

    public Post getFirstPost() {
        return firstPost;
    }

    public List<Post> getPosts() {
        return posts;
    }
}
