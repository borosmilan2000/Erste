package hu.robertszujo.seleniumproject.pages;

import com.aventstack.extentreports.ExtentTest;
import hu.robertszujo.seleniumproject.utils.ElementActions;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.ArrayList;
import java.util.List;

public class LoanCalculatorPage extends BasePageObject {

    public LoanCalculatorPage(WebDriver driver, ExtentTest reporter) {
        super(driver, reporter);
    }

    // *** Elements ***

    @FindBy(css = "div[class='content_hitelmaximum']")
    private WebElement calculatorForm;

    // Age input field - based on HTML
    @FindBy(css = "input#meletkor")
    private WebElement ageInputField;

    // Application button
    @FindBy(css = "input.btn.btn-orange.mennyit_kaphatok")
    private WebElement calculateLoanButton;

    // Error message for age (visible when age < 18)
    @FindBy(css = "#eletkor_error")
    private WebElement ageErrorMessage;

    // Results section that appears after calculation
    @FindBy(css = "#max_eredmeny")
    private WebElement resultsSection;

    // Property value input
    @FindBy(css = "input#ingatlan_erteke")
    private WebElement propertyValueInput;

    // Income input
    @FindBy(css = "input#mjovedelem")
    private WebElement incomeInput;

    // Existing loan repayment input
    @FindBy(css = "input#meglevo_torleszto")
    private WebElement existingLoanRepaymentInput;

    // Bank account credit checkbox
    @FindBy(css = "input#kedvezmeny_jovairasm")
    private WebElement bankCreditCheckbox;

    // Baby loan checkbox
    @FindBy(css = "input#kedvezmeny_babavarom")
    private WebElement babyLoanCheckbox;

    // Insurance checkbox
    @FindBy(css = "input#kedvezmeny_biztositasm")
    private WebElement insuranceCheckbox;

    // "Sorry we couldn't calculate" section (appears when loan not available)
    @FindBy(css = "#nem_tudunk_kalkulalni")
    private WebElement cannotCalculateSection;

    // "Try again" button (appears after calculation)
    @FindBy(css = ".ujrakalkulal")
    private WebElement tryAgainButton;

    // First loan offer box
    @FindBy(css = "#box_1")
    private WebElement firstLoanOffer;

    // "érdekel" button in offer box
    @FindBy(css = "#box_1 .js-erdekel")
    private WebElement interestedInOfferButton;

    // *** Element methods ***

    public void waitForCalculatorFormToBeDisplayed() {
        reporter.info("Waiting for calculator form to be displayed");
        ElementActions.waitForElementToBeDisplayed(calculatorForm, driver);
    }

    public boolean isCalculatorFormDisplayedAfterWaiting() {
        try {
            waitForCalculatorFormToBeDisplayed();
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    // *** Form input methods ***

    public boolean isAgeErrorVisible() {
        try {
            WebElement errorElement = driver.findElement(By.id("eletkor_error"));
            // Check if error is actually visible
            // Some error messages might be in DOM but hidden with display: none
            return errorElement.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void setCustomerAge(int age) {
        reporter.info("Setting customer age to: " + age);

        // Wait for the age input field to be interactable
        ElementActions.waitForElementToBeDisplayed(ageInputField, driver);

        // Clear existing value and set new age
        ageInputField.clear();
        ageInputField.sendKeys(String.valueOf(age));

        // Trigger blur event if needed
        executeJavaScript("arguments[0].blur();", ageInputField);

        // Small wait for validation to run
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        reporter.pass("Customer age set to: " + age);
    }

    /**
     * Sets the property value
     * @param value Property value in HUF
     */
    public void setPropertyValue(int value) {
        reporter.info("Setting property value to: " + value);

        ElementActions.waitForElementToBeDisplayed(propertyValueInput, driver);
        propertyValueInput.clear();
        propertyValueInput.sendKeys(String.valueOf(value));

        executeJavaScript("arguments[0].blur();", propertyValueInput);
        reporter.pass("Property value set to: " + value);
    }

    /**
     * Sets the monthly income
     * @param income Monthly income in HUF
     */
    public void setMonthlyIncome(int income) {
        reporter.info("Setting monthly income to: " + income);

        ElementActions.waitForElementToBeDisplayed(incomeInput, driver);
        incomeInput.clear();
        incomeInput.sendKeys(String.valueOf(income));

        executeJavaScript("arguments[0].blur();", incomeInput);
        reporter.pass("Monthly income set to: " + income);
    }

    /**
     * Sets existing loan repayment amount
     * @param amount Monthly repayment amount in HUF
     */
    public void setExistingLoanRepayment(int amount) {
        reporter.info("Setting existing loan repayment to: " + amount);

        ElementActions.waitForElementToBeDisplayed(existingLoanRepaymentInput, driver);
        existingLoanRepaymentInput.clear();
        existingLoanRepaymentInput.sendKeys(String.valueOf(amount));

        executeJavaScript("arguments[0].blur();", existingLoanRepaymentInput);
        reporter.pass("Existing loan repayment set to: " + amount);
    }

    /**
     * Selects "alone" or "multiple earners" in household
     * @param alone true for "Egyedül keresek", false for "Legalább ketten keresünk"
     */
    public void selectHouseholdType(boolean alone) {
        reporter.info("Selecting household type: " + (alone ? "egyedül" : "többen"));

        String radioId = alone ? "egyedul" : "tobben";
        WebElement radioButton = driver.findElement(org.openqa.selenium.By.id(radioId));
        ElementActions.waitForElementToBeDisplayed(radioButton, driver);

        if (!radioButton.isSelected()) {
            radioButton.click();
        }

        reporter.pass("Household type selected: " + (alone ? "Alone" : "Multiple earners"));
    }

    /**
     * Checks or unchecks the bank account credit option
     * @param check true to check, false to uncheck
     */
    public void setBankAccountCreditOption(boolean check) {
        reporter.info("Setting bank account credit option to: " + check);

        ElementActions.waitForElementToBeDisplayed(bankCreditCheckbox, driver);
        if (check != bankCreditCheckbox.isSelected()) {
            bankCreditCheckbox.click();
        }

        reporter.pass("Bank account credit option set to: " + check);
    }

    /**
     * Checks or unchecks the baby loan option
     * @param check true to check, false to uncheck
     */
    public void setBabyLoanOption(boolean check) {
        reporter.info("Setting baby loan option to: " + check);

        ElementActions.waitForElementToBeDisplayed(babyLoanCheckbox, driver);
        if (check != babyLoanCheckbox.isSelected()) {
            babyLoanCheckbox.click();
        }

        reporter.pass("Baby loan option set to: " + check);
    }

    /**
     * Checks or unchecks the insurance option
     * @param check true to check, false to uncheck
     */
    public void setInsuranceOption(boolean check) {
        reporter.info("Setting insurance option to: " + check);

        ElementActions.waitForElementToBeDisplayed(insuranceCheckbox, driver);
        if (check != insuranceCheckbox.isSelected()) {
            insuranceCheckbox.click();
        }

        reporter.pass("Insurance option set to: " + check);
    }

    // *** Calculation methods ***

    /**
     * Clicks the "Calculate loan amount" button
     */
    public void clickCalculateLoanButton() {
        reporter.info("Clicking on 'Mennyi lakáshitelt kaphatok?' button");

        ElementActions.waitForElementToBeDisplayed(calculateLoanButton, driver);
        calculateLoanButton.click();

        // Wait for calculation to complete (either results or error)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        reporter.pass("Clicked calculate loan button");
    }

    public void waitForResults() {
        reporter.info("Waiting for calculation results");

        try {
            ElementActions.waitForElementToBeDisplayed(resultsSection, driver);
            reporter.pass("Calculation results displayed");
        } catch (TimeoutException e) {
            reporter.warning("Results section not displayed within timeout");
        }
    }

    public boolean isAgeErrorMessageDisplayed() {
        try {
            // Check if error message is visible and contains text
            ElementActions.waitForElementToBeDisplayed(ageErrorMessage, driver);
            String errorText = ageErrorMessage.getText();
            boolean hasError = errorText != null && !errorText.isEmpty() &&
                    errorText.contains("Hitelt kizárólag 18. életévüket betöltött személyek igényelhetnek");

            reporter.info("Age error message displayed: " + hasError);
            return hasError;

        } catch (TimeoutException e) {
            // Error message not displayed within timeout
            reporter.info("Age error message not displayed");
            return false;
        }
    }


    public boolean isLoanApplicationAvailable() {
        reporter.info("Checking if loan application is available");

        // First check if age error is displayed (age < 18)
        if (isAgeErrorMessageDisplayed()) {
            reporter.info("Loan application NOT available - age restriction error");
            return false;
        }

        // Click calculate to see if loan is available
        clickCalculateLoanButton();

        // Check if "cannot calculate" section appears
        try {
            ElementActions.waitForElementToBeDisplayed(cannotCalculateSection, driver);
            if (cannotCalculateSection.isDisplayed()) {
                reporter.info("Loan application NOT available - cannot calculate section displayed");
                return false;
            }
        } catch (TimeoutException e) {
            // "Cannot calculate" section not displayed, check for results
        }

        // Check if results section appears (loan offers)
        try {
            ElementActions.waitForElementToBeDisplayed(resultsSection, driver);
            if (resultsSection.isDisplayed()) {
                // Check if at least one loan offer is displayed
                boolean hasLoanOffers = isElementDisplayed("#box_1") || isElementDisplayed("#box_2");

                if (hasLoanOffers) {
                    reporter.info("Loan application IS available - loan offers displayed");
                    return true;
                }
            }
        } catch (TimeoutException e) {
            // Results not displayed
        }

        reporter.info("Loan application NOT available - no results or offers found");
        return false;
    }

    /**
     * Helper method to check if an element is displayed by CSS selector
     */
    private boolean isElementDisplayed(String cssSelector) {
        try {
            WebElement element = driver.findElement(org.openqa.selenium.By.cssSelector(cssSelector));
            return element.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the loan amount from the first offer box
     * @return Loan amount as string, or empty string if not available
     */
    public String getLoanAmountFromFirstOffer() {
        try {
            if (firstLoanOffer.isDisplayed()) {
                WebElement amountElement = driver.findElement(org.openqa.selenium.By.cssSelector("#box_1_max_desktop"));
                return amountElement.getText();
            }
        } catch (Exception e) {
            // Element not found or not displayed
        }
        return "";
    }

    /**
     * Gets the monthly repayment from the first offer box
     * @return Monthly repayment as string, or empty string if not available
     */
    public String getMonthlyRepaymentFromFirstOffer() {
        try {
            if (firstLoanOffer.isDisplayed()) {
                WebElement repaymentElement = driver.findElement(org.openqa.selenium.By.cssSelector("#box_1_torleszto"));
                return repaymentElement.getText();
            }
        } catch (Exception e) {
            // Element not found or not displayed
        }
        return "";
    }

    /**
     * Fills the calculator form with minimum required data for testing
     * @param age Customer age
     */
    public void fillCalculatorFormWithMinimumData(int age) {
        reporter.info("Filling calculator form with minimum data for age: " + age);

        // Set minimum required values
        setPropertyValue(50000000);
        setCustomerAge(age);
        selectHouseholdType(true);
        setMonthlyIncome(500000);
        setExistingLoanRepayment(0);

        reporter.pass("Calculator form filled with minimum data");
    }

    /**
     * Checks if the "I'm interested" button is clickable in the offer
     * @return true if button is enabled and clickable, false otherwise
     */
    public boolean isInterestedButtonClickable() {
        try {
            ElementActions.waitForElementToBeDisplayed(interestedInOfferButton, driver);
            return interestedInOfferButton.isEnabled() && interestedInOfferButton.isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Clicks the "I'm interested" button in the offer
     */
    public void clickInterestedInOffer() {
        reporter.info("Clicking 'I'm interested' button in offer");

        if (isInterestedButtonClickable()) {
            interestedInOfferButton.click();
            reporter.pass("Clicked 'I'm interested' button");
        } else {
            reporter.warning("'I'm interested' button not clickable");
        }
    }

    /**
     * Executes JavaScript on an element
     */
    private void executeJavaScript(String script, WebElement element) {
        try {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(script, element);
        } catch (Exception e) {
            // JavaScript execution failed
        }
    }

    // *** Error checking methods ***

    /**
     * Checks if age error message is displayed
     * Based on HTML pattern: id="eletkor_error"
     */
    public boolean isAgeErrorDisplayed() {
        try {
            WebElement errorElement = driver.findElement(By.id("eletkor_error"));
            return errorElement.isDisplayed() &&
                    !errorElement.getText().trim().isEmpty();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Checks if property value error message is displayed
     * Based on HTML pattern: id="ingatlan_erteke_error"
     */
    public boolean isPropertyValueErrorDisplayed() {
        try {
            WebElement errorElement = driver.findElement(By.id("ingatlan_erteke_error"));
            return errorElement.isDisplayed() &&
                    !errorElement.getText().trim().isEmpty();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Checks if monthly income error message is displayed
     * Based on HTML pattern: id="mjovedelem_error"
     */
    public boolean isMonthlyIncomeErrorDisplayed() {
        try {
            WebElement errorElement = driver.findElement(By.id("mjovedelem_error"));
            return errorElement.isDisplayed() &&
                    !errorElement.getText().trim().isEmpty();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Checks if existing loan repayment error message is displayed
     * Based on HTML pattern: id="meglevo_torleszto_error"
     */
    public boolean isExistingLoanRepaymentErrorDisplayed() {
        try {
            WebElement errorElement = driver.findElement(By.id("meglevo_torleszto_error"));
            return errorElement.isDisplayed() &&
                    !errorElement.getText().trim().isEmpty();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // *** Additional methods for checking specific error content ***

    /**
     * Gets the text of age error message
     */
    public String getAgeErrorText() {
        try {
            WebElement errorElement = driver.findElement(By.id("eletkor_error"));
            return errorElement.isDisplayed() ? errorElement.getText().trim() : "";
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    /**
     * Gets the text of property value error message
     */
    public String getPropertyValueErrorText() {
        try {
            WebElement errorElement = driver.findElement(By.id("ingatlan_erteke_error"));
            return errorElement.isDisplayed() ? errorElement.getText().trim() : "";
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    /**
     * Gets the text of monthly income error message
     */
    public String getMonthlyIncomeErrorText() {
        try {
            WebElement errorElement = driver.findElement(By.id("mjovedelem_error"));
            return errorElement.isDisplayed() ? errorElement.getText().trim() : "";
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    /**
     * Gets the text of existing loan repayment error message
     */
    public String getExistingLoanRepaymentErrorText() {
        try {
            WebElement errorElement = driver.findElement(By.id("meglevo_torleszto_error"));
            return errorElement.isDisplayed() ? errorElement.getText().trim() : "";
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    // *** Helper method to check if any form error is visible ***

    /**
     * Checks if any form input error is currently visible
     * @return true if at least one error message is displayed
     */
    public boolean isAnyFormErrorVisible() {
        return isAgeErrorDisplayed() ||
                isPropertyValueErrorDisplayed() ||
                isMonthlyIncomeErrorDisplayed() ||
                isExistingLoanRepaymentErrorDisplayed();
    }

    /**
     * Gets a list of all currently visible error messages
     * @return List of error messages that are currently displayed
     */
    public List<String> getAllVisibleErrorMessages() {
        List<String> errors = new ArrayList<>();

        if (isAgeErrorDisplayed()) {
            errors.add("Age error: " + getAgeErrorText());
        }
        if (isPropertyValueErrorDisplayed()) {
            errors.add("Property value error: " + getPropertyValueErrorText());
        }
        if (isMonthlyIncomeErrorDisplayed()) {
            errors.add("Monthly income error: " + getMonthlyIncomeErrorText());
        }
        if (isExistingLoanRepaymentErrorDisplayed()) {
            errors.add("Existing loan repayment error: " + getExistingLoanRepaymentErrorText());
        }

        return errors;
    }
}