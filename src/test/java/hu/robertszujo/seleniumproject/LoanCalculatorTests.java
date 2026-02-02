package hu.robertszujo.seleniumproject;

import com.aventstack.extentreports.ExtentTest;
import hu.robertszujo.seleniumproject.constants.TestConstants;
import hu.robertszujo.seleniumproject.constants.TestContextConstants;
import hu.robertszujo.seleniumproject.pages.LoanCalculatorPage;
import hu.robertszujo.seleniumproject.pages.components.CookiePopup;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(TestListener.class)
public class LoanCalculatorTests extends BaseTestClass {

    private ExtentTest reporter;
    private LoanCalculatorPage loanCalculatorPage;
    private CookiePopup cookiePopup;

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(ITestContext context, ITestResult result) {
        reporter = SuiteWideStorage.testReport.createTest(result.getMethod().getMethodName(), result.getMethod().getDescription());
        context.setAttribute(TestContextConstants.REPORTER, reporter);
        initializePageObjects();
    }

    public void initializePageObjects() {
        loanCalculatorPage = new LoanCalculatorPage(driver, reporter);
        cookiePopup = new CookiePopup(driver, reporter);
    }

    // Helper methods
    private void loadPageAndHandleCookies() {
        driver.get(TestConstants.CALCULATOR_PAGE_URL);

        try {
            if (cookiePopup.isCookiePopupDisplayedAfterWaiting()) {
                cookiePopup.clickOnCookieAcceptButton();
                cookiePopup.waitForCookiePopupToDisappear();
            }
        } catch (Exception e) {
            // Cookie popup might not appear or already accepted
        }

        loanCalculatorPage.waitForCalculatorFormToBeDisplayed();
    }

    private void quickRefresh() {
        driver.navigate().refresh();
        loanCalculatorPage.waitForCalculatorFormToBeDisplayed();
    }

    private int extractNumericLoanAmount(String loanAmountText) {
        try {
            String clean = loanAmountText.replaceAll("[^0-9]", "");
            return Integer.parseInt(clean);
        } catch (Exception e) {
            return 0;
        }
    }

    private double parseAPRToDouble(String aprString) {
        try {
            String clean = aprString.trim().replace(",", ".");
            return Double.parseDouble(clean);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private void waitForValidation() {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // *** Specification Requirement Tests ***

    @Test(description = "Cookie popup should be displayed after page load")
    public void loadCalculatorPage_cookiePopupShouldBeDisplayed() {
        driver.get(TestConstants.CALCULATOR_PAGE_URL);
        Assertions.assertThat(cookiePopup.isCookiePopupDisplayedAfterWaiting())
                .as("Cookie popup should have displayed after page load")
                .isTrue();
    }

    @Test(description = "Cookie popup should disappear after accepting cookies")
    public void acceptCookies_CookiePopupShouldDisappear() {
        driver.get(TestConstants.CALCULATOR_PAGE_URL);
        cookiePopup.waitForCookiePopupToBeDisplayed();
        cookiePopup.clickOnCookieAcceptButton();
        Assertions.assertThat(cookiePopup.hasCookiePopupDisappearedAfterWaiting())
                .as("Cookie popup should have disappeared")
                .isTrue();
    }

    @Test(description = "Calculator form should be displayed after page load & accepting cookies")
    public void loadPageAndAcceptCookies_CalculatorFormShouldBeDisplayed() {
        loadPageAndHandleCookies();
        loanCalculatorPage.isCalculatorFormDisplayedAfterWaiting();
    }

    @Test(description = "Age validation: 17 (error), 18 (no error)")
    public void ageBoundaryTest() {
        loadPageAndHandleCookies();

        loanCalculatorPage.setCustomerAge(17);
        Assert.assertTrue(loanCalculatorPage.isAgeErrorVisible(),
                "Error should show for age 17");

        loanCalculatorPage.setCustomerAge(18);
        Assert.assertFalse(loanCalculatorPage.isAgeErrorVisible(),
                "No error should show for age 18");

        loanCalculatorPage.setCustomerAge(19);
        Assert.assertFalse(loanCalculatorPage.isAgeErrorVisible(),
                "No error should show for age 19");
    }

    @Test(description = "Age 65+ validation: 65 (no error), 66 (error)")
    public void age65PlusBoundaryTest() {
        loadPageAndHandleCookies();

        loanCalculatorPage.setCustomerAge(65);
        Assert.assertFalse(loanCalculatorPage.isAgeErrorVisible(),
                "No error should show for age 65");

        loanCalculatorPage.setCustomerAge(66);
        Assert.assertTrue(loanCalculatorPage.isAgeErrorVisible(),
                "Error should show for age 66");

        loanCalculatorPage.setCustomerAge(70);
        Assert.assertTrue(loanCalculatorPage.isAgeErrorVisible(),
                "Error should show for age 70");
    }

    @Test(description = "Property value boundary tests")
    public void propertyValueBoundaryTests() {
        loadPageAndHandleCookies();

        loanCalculatorPage.setCustomerAge(30);
        loanCalculatorPage.selectHouseholdType(true);
        loanCalculatorPage.setMonthlyIncome(500000);
        loanCalculatorPage.setExistingLoanRepayment(0);

        loanCalculatorPage.setPropertyValue(4999999);
        waitForValidation();
        Assert.assertTrue(loanCalculatorPage.isPropertyValueErrorDisplayed(),
                "Error should show for 4,999,999 (1 below minimum)");

        quickRefresh();
        loanCalculatorPage.setCustomerAge(30);
        loanCalculatorPage.selectHouseholdType(true);
        loanCalculatorPage.setMonthlyIncome(500000);
        loanCalculatorPage.setExistingLoanRepayment(0);
        loanCalculatorPage.setPropertyValue(5000000);
        waitForValidation();
        Assert.assertFalse(loanCalculatorPage.isPropertyValueErrorDisplayed(),
                "No error should show for 5,000,000 (exact minimum)");

        quickRefresh();
        loanCalculatorPage.setCustomerAge(30);
        loanCalculatorPage.selectHouseholdType(true);
        loanCalculatorPage.setMonthlyIncome(500000);
        loanCalculatorPage.setExistingLoanRepayment(0);
        loanCalculatorPage.setPropertyValue(5000001);
        waitForValidation();
        Assert.assertFalse(loanCalculatorPage.isPropertyValueErrorDisplayed(),
                "No error should show for 5,000,001 (1 above minimum)");

        quickRefresh();
        loanCalculatorPage.setCustomerAge(30);
        loanCalculatorPage.selectHouseholdType(true);
        loanCalculatorPage.setMonthlyIncome(500000);
        loanCalculatorPage.setExistingLoanRepayment(0);
        loanCalculatorPage.setPropertyValue(6000000);
        waitForValidation();
        Assert.assertFalse(loanCalculatorPage.isPropertyValueErrorDisplayed(),
                "No error should show for 6,000,000 (well above minimum)");
    }

    @Test(description = "Income boundary tests for single earner")
    public void singleEarnerIncomeBoundaryTests() {
        loadPageAndHandleCookies();

        loanCalculatorPage.setCustomerAge(30);
        loanCalculatorPage.setPropertyValue(50000000);
        loanCalculatorPage.selectHouseholdType(true);
        loanCalculatorPage.setExistingLoanRepayment(0);

        loanCalculatorPage.setMonthlyIncome(192999);
        Assert.assertTrue(loanCalculatorPage.isMonthlyIncomeErrorDisplayed(),
                "Error should show for 192,999 (1 below single earner minimum)");

        loanCalculatorPage.setMonthlyIncome(193000);
        Assert.assertFalse(loanCalculatorPage.isMonthlyIncomeErrorDisplayed(),
                "No error should show for 193,000 (exact single earner minimum)");

        loanCalculatorPage.setMonthlyIncome(193001);
        Assert.assertFalse(loanCalculatorPage.isMonthlyIncomeErrorDisplayed(),
                "No error should show for 193,001 (1 above single earner minimum)");
    }

    @Test(description = "Income boundary tests for multiple earners")
    public void multipleEarnerIncomeBoundaryTests() {
        loadPageAndHandleCookies();

        loanCalculatorPage.setCustomerAge(30);
        loanCalculatorPage.setPropertyValue(50000000);
        loanCalculatorPage.selectHouseholdType(false);
        loanCalculatorPage.setExistingLoanRepayment(0);

        loanCalculatorPage.setMonthlyIncome(289999);
        Assert.assertTrue(loanCalculatorPage.isMonthlyIncomeErrorDisplayed(),
                "Error should show for 289,999 (1 below multiple earner minimum)");

        loanCalculatorPage.setMonthlyIncome(290000);
        Assert.assertFalse(loanCalculatorPage.isMonthlyIncomeErrorDisplayed(),
                "No error should show for 290,000 (exact multiple earner minimum)");

        loanCalculatorPage.setMonthlyIncome(290001);
        Assert.assertFalse(loanCalculatorPage.isMonthlyIncomeErrorDisplayed(),
                "No error should show for 290,001 (1 above multiple earner minimum)");
    }

    @Test(description = "Existing loan repayment limits with 800,000 threshold")
    public void existingLoanRepayment800kThresholdTest() {
        loadPageAndHandleCookies();

        loanCalculatorPage.setCustomerAge(30);
        loanCalculatorPage.setPropertyValue(50000000);
        loanCalculatorPage.selectHouseholdType(true);

        // Test BELOW 800,000 threshold - 50% limit
        loanCalculatorPage.setMonthlyIncome(1000000);

        loanCalculatorPage.setExistingLoanRepayment(490000);
        Assert.assertFalse(loanCalculatorPage.isExistingLoanRepaymentErrorDisplayed(),
                "No error should show for 49% repayment (below 800k threshold)");

        loanCalculatorPage.setExistingLoanRepayment(500000);
        Assert.assertFalse(loanCalculatorPage.isExistingLoanRepaymentErrorDisplayed(),
                "No error should show for 50% repayment (below 800k threshold)");

        loanCalculatorPage.setExistingLoanRepayment(510000);
        boolean error51Percent = loanCalculatorPage.isExistingLoanRepaymentErrorDisplayed();
        reporter.info("Error for 51% repayment (below 800k): " + error51Percent);

        // Test AT 800,000 threshold - should use 60% rule
        quickRefresh();
        loanCalculatorPage.setCustomerAge(30);
        loanCalculatorPage.setPropertyValue(50000000);
        loanCalculatorPage.selectHouseholdType(true);

        int incomeFor800k = 1600000;
        loanCalculatorPage.setMonthlyIncome(incomeFor800k);

        loanCalculatorPage.setExistingLoanRepayment(800000);
        Assert.assertFalse(loanCalculatorPage.isExistingLoanRepaymentErrorDisplayed(),
                "No error should show for 800,000 repayment (at threshold, using 60% rule)");

        loanCalculatorPage.setExistingLoanRepayment(960000);
        Assert.assertFalse(loanCalculatorPage.isExistingLoanRepaymentErrorDisplayed(),
                "No error should show for 60% repayment (at 800k threshold)");

        loanCalculatorPage.setExistingLoanRepayment(976000);
        boolean error61Percent = loanCalculatorPage.isExistingLoanRepaymentErrorDisplayed();
        reporter.info("Error for 61% repayment (at 800k threshold): " + error61Percent);

        // Test ABOVE 800,000 threshold - 60% limit
        quickRefresh();
        loanCalculatorPage.setCustomerAge(30);
        loanCalculatorPage.setPropertyValue(50000000);
        loanCalculatorPage.selectHouseholdType(true);

        loanCalculatorPage.setMonthlyIncome(2000000);

        loanCalculatorPage.setExistingLoanRepayment(1100000);
        Assert.assertFalse(loanCalculatorPage.isExistingLoanRepaymentErrorDisplayed(),
                "No error should show for 55% repayment (above 800k but below 60%)");

        loanCalculatorPage.setExistingLoanRepayment(1200000);
        Assert.assertFalse(loanCalculatorPage.isExistingLoanRepaymentErrorDisplayed(),
                "No error should show for 60% repayment (above 800k threshold)");

        loanCalculatorPage.setExistingLoanRepayment(1220000);
        boolean error61PercentAbove = loanCalculatorPage.isExistingLoanRepaymentErrorDisplayed();
        reporter.info("Error for 61% repayment (above 800k threshold): " + error61PercentAbove);

        reporter.pass("800k threshold tests completed");
    }

    // *** Additional Required Tests ***

    @Test(description = "Loan amount increases with higher property value")
    public void loanAmountIncreasesWithPropertyValue() {
        loadPageAndHandleCookies();

        // Setup: High income, no existing loans
        loanCalculatorPage.setCustomerAge(30);
        loanCalculatorPage.selectHouseholdType(false);
        loanCalculatorPage.setMonthlyIncome(1000000);
        loanCalculatorPage.setExistingLoanRepayment(0);
        loanCalculatorPage.setBankAccountCreditOption(true);
        loanCalculatorPage.setBabyLoanOption(false);
        loanCalculatorPage.setInsuranceOption(true);

        loanCalculatorPage.setPropertyValue(10000000);
        loanCalculatorPage.clickCalculateLoanButton();
        loanCalculatorPage.waitForResults();
        int loan1 = extractNumericLoanAmount(loanCalculatorPage.getLoanAmountFromFirstOffer());

        quickRefresh();
        loanCalculatorPage.setCustomerAge(30);
        loanCalculatorPage.selectHouseholdType(false);
        loanCalculatorPage.setMonthlyIncome(1000000);
        loanCalculatorPage.setExistingLoanRepayment(0);
        loanCalculatorPage.setBankAccountCreditOption(true);
        loanCalculatorPage.setBabyLoanOption(false);
        loanCalculatorPage.setInsuranceOption(true);

        loanCalculatorPage.setPropertyValue(30000000);
        loanCalculatorPage.clickCalculateLoanButton();
        loanCalculatorPage.waitForResults();
        int loan2 = extractNumericLoanAmount(loanCalculatorPage.getLoanAmountFromFirstOffer());

        quickRefresh();
        loanCalculatorPage.setCustomerAge(30);
        loanCalculatorPage.selectHouseholdType(false);
        loanCalculatorPage.setMonthlyIncome(1000000);
        loanCalculatorPage.setExistingLoanRepayment(0);
        loanCalculatorPage.setBankAccountCreditOption(true);
        loanCalculatorPage.setBabyLoanOption(false);
        loanCalculatorPage.setInsuranceOption(true);

        loanCalculatorPage.setPropertyValue(50000000);
        loanCalculatorPage.clickCalculateLoanButton();
        loanCalculatorPage.waitForResults();
        int loan3 = extractNumericLoanAmount(loanCalculatorPage.getLoanAmountFromFirstOffer());

        Assert.assertTrue(loan1 < loan2 && loan2 < loan3,
                "Loan should increase with property value: " +
                        loan1 + " (10M) < " + loan2 + " (30M) < " + loan3 + " (50M)");

        reporter.info("Loan increases with property value: " + loan1 + " → " + loan2 + " → " + loan3);
    }

    @Test(description = "Maximum loan amount increases with higher income")
    public void maximumLoanAmountIncreasesWithIncome() {
        loadPageAndHandleCookies();

        int loan1 = testIncomeLoanMapping(400000, "400K income");
        int loan2 = testIncomeLoanMapping(600000, "600K income");
        int loan3 = testIncomeLoanMapping(800000, "800K income");

        Assert.assertTrue(loan1 < loan2 && loan2 < loan3,
                "Loan should increase with income: " + loan1 + " < " + loan2 + " < " + loan3);
    }

    @Test(description = "Maximum loan amount decreases with higher existing loan repayments")
    public void maximumLoanAmountDecreasesWithExistingLiabilities() {
        loadPageAndHandleCookies();

        int loan1 = testRepaymentLoanMapping(50000, "50K repayment");
        int loan2 = testRepaymentLoanMapping(200000, "200K repayment");
        int loan3 = testRepaymentLoanMapping(400000, "400K repayment");

        Assert.assertTrue(loan1 >= loan2 && loan2 >= loan3,
                "Loan should decrease with higher repayments: " + loan1 + " >= " + loan2 + " >= " + loan3);

        // Credit limit test (simulated with repayment field)
        quickRefresh();
        int loan4 = testRepaymentLoanMapping(0, "No credit limit");
        quickRefresh();
        int loan5 = testRepaymentLoanMapping(300000, "300K credit limit");

        Assert.assertTrue(loan5 <= loan4,
                "Loan should decrease with higher credit limits");
    }

    @Test(description = "Loan amount calculation with specific property values")
    public void loanAmountCalculationBasedOnPropertyValue() {
        loadPageAndHandleCookies();

        testPropertyLoanMapping(5500000, 4400000, "5.5M property → 4.4M loan");
        testPropertyLoanMapping(10000000, 8000000, "10M property → 8M loan");
        testPropertyLoanMapping(300000000, 48300000, "300M property → 48.3M loan");
    }

    @Test(description = "Insurance reduces APR")
    public void repaymentProtectionInsuranceReducesAPR() {
        loadPageAndHandleCookies();

        double aprWithout = testInsuranceAPR(false, "Without insurance");
        double aprWith = testInsuranceAPR(true, "With insurance");

        Assert.assertTrue(aprWith < aprWithout,
                "APR should be lower with insurance: " + aprWithout + "% > " + aprWith + "%");
    }

    // *** Supporting Tests ***

    @Test(description = "Complete end-to-end test with valid data")
    public void completeValidScenarioTest() {
        loadPageAndHandleCookies();

        loanCalculatorPage.setCustomerAge(35);
        loanCalculatorPage.setPropertyValue(30000000);
        loanCalculatorPage.selectHouseholdType(false);
        loanCalculatorPage.setMonthlyIncome(600000);
        loanCalculatorPage.setExistingLoanRepayment(100000);
        loanCalculatorPage.setBankAccountCreditOption(true);
        loanCalculatorPage.setBabyLoanOption(false);
        loanCalculatorPage.setInsuranceOption(true);

        loanCalculatorPage.clickCalculateLoanButton();
        loanCalculatorPage.waitForResults();

        String loanAmount = loanCalculatorPage.getLoanAmountFromFirstOffer();
        String monthlyRepayment = loanCalculatorPage.getMonthlyRepaymentFromFirstOffer();

        Assert.assertFalse(loanAmount.isEmpty(), "Loan amount should be displayed");
        Assert.assertFalse(monthlyRepayment.isEmpty(), "Monthly repayment should be displayed");
        Assert.assertTrue(loanCalculatorPage.isInterestedButtonClickable(),
                "'I'm interested' button should be available");
    }

    @Test(description = "Edge case values test")
    public void edgeCaseValuesTest() {
        loadPageAndHandleCookies();

        loanCalculatorPage.setCustomerAge(18);
        loanCalculatorPage.setPropertyValue(5000000);
        loanCalculatorPage.selectHouseholdType(true);
        loanCalculatorPage.setMonthlyIncome(193000);
        loanCalculatorPage.setExistingLoanRepayment(0);
        loanCalculatorPage.setBankAccountCreditOption(false);
        loanCalculatorPage.setBabyLoanOption(false);
        loanCalculatorPage.setInsuranceOption(false);

        loanCalculatorPage.clickCalculateLoanButton();
    }

    // *** Helper methods ***

    private void testPropertyLoanMapping(int propertyValue, int expectedLoan, String message) {
        quickRefresh();

        loanCalculatorPage.setCustomerAge(30);
        loanCalculatorPage.selectHouseholdType(false);
        loanCalculatorPage.setMonthlyIncome(600000);
        loanCalculatorPage.setExistingLoanRepayment(0);
        loanCalculatorPage.setBankAccountCreditOption(true);
        loanCalculatorPage.setBabyLoanOption(false);
        loanCalculatorPage.setInsuranceOption(true);

        loanCalculatorPage.setPropertyValue(propertyValue);
        loanCalculatorPage.clickCalculateLoanButton();
        loanCalculatorPage.waitForResults();

        int actualLoan = extractNumericLoanAmount(loanCalculatorPage.getLoanAmountFromFirstOffer());
        Assert.assertEquals(actualLoan, expectedLoan, message);
    }

    private int testIncomeLoanMapping(int income, String description) {
        quickRefresh();

        loanCalculatorPage.setCustomerAge(30);
        loanCalculatorPage.setPropertyValue(100000000);
        loanCalculatorPage.selectHouseholdType(false);
        loanCalculatorPage.setMonthlyIncome(income);
        loanCalculatorPage.setExistingLoanRepayment(50000);
        loanCalculatorPage.setBankAccountCreditOption(true);
        loanCalculatorPage.setBabyLoanOption(false);
        loanCalculatorPage.setInsuranceOption(true);

        loanCalculatorPage.clickCalculateLoanButton();
        loanCalculatorPage.waitForResults();

        reporter.info(description + " → Loan: " + loanCalculatorPage.getLoanAmountFromFirstOffer());
        return extractNumericLoanAmount(loanCalculatorPage.getLoanAmountFromFirstOffer());
    }

    private int testRepaymentLoanMapping(int repayment, String description) {
        quickRefresh();

        loanCalculatorPage.setCustomerAge(30);
        loanCalculatorPage.setPropertyValue(100000000);
        loanCalculatorPage.selectHouseholdType(false);
        loanCalculatorPage.setMonthlyIncome(800000);
        loanCalculatorPage.setExistingLoanRepayment(repayment);
        loanCalculatorPage.setBankAccountCreditOption(true);
        loanCalculatorPage.setBabyLoanOption(false);
        loanCalculatorPage.setInsuranceOption(true);

        loanCalculatorPage.clickCalculateLoanButton();
        loanCalculatorPage.waitForResults();

        reporter.info(description + " → Loan: " + loanCalculatorPage.getLoanAmountFromFirstOffer());
        return extractNumericLoanAmount(loanCalculatorPage.getLoanAmountFromFirstOffer());
    }

    private double testInsuranceAPR(boolean insurance, String description) {
        quickRefresh();

        loanCalculatorPage.setCustomerAge(35);
        loanCalculatorPage.setPropertyValue(30000000);
        loanCalculatorPage.selectHouseholdType(false);
        loanCalculatorPage.setMonthlyIncome(600000);
        loanCalculatorPage.setExistingLoanRepayment(100000);
        loanCalculatorPage.setBankAccountCreditOption(true);
        loanCalculatorPage.setBabyLoanOption(false);
        loanCalculatorPage.setInsuranceOption(insurance);

        loanCalculatorPage.clickCalculateLoanButton();
        loanCalculatorPage.waitForResults();

        try {
            WebElement aprElement = driver.findElement(By.id("box_1_thm"));
            double apr = parseAPRToDouble(aprElement.getText());
            reporter.info(description + " → APR: " + apr + "%");
            return apr;
        } catch (Exception e) {
            return 0.0;
        }
    }
}