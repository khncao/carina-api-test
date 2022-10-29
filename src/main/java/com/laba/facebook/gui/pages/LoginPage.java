package com.laba.facebook.gui.pages;

import java.lang.invoke.MethodHandles;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;
import com.qaprosoft.carina.core.gui.AbstractPage;

public class LoginPage extends AbstractPage {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @FindBy(id = "email")
    private ExtendedWebElement emailTextInput;
    @FindBy(id = "pass")
    private ExtendedWebElement passwordTextInput;
    @FindBy(id = "loginbutton")
    private ExtendedWebElement loginBtn;

    public LoginPage(WebDriver driver) {
        super(driver);
        setPageURL("/login/");
        setUiLoadedMarker(loginBtn);
    }

    public void inputEmail(String email) {
        emailTextInput.type(email);
    }

    public void inputPassword(String password) {
        passwordTextInput.type(password);
    }

    public void submitLoginAttempt() {
        loginBtn.click();
    }

    public void login(String email, String password) {
        inputEmail(email);
        inputPassword(password);
        submitLoginAttempt();
    }
}
