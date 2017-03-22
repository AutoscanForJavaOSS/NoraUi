package noraui.application.steps;

import static noraui.utils.Constants.ALERT_KEY;
import static noraui.utils.Constants.VALUE;

import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import noraui.application.page.IPage;
import noraui.application.page.Page;
import noraui.application.page.Page.PageElement;
import noraui.exception.FailureException;
import noraui.exception.Result;
import noraui.exception.TechnicalException;
import noraui.utils.Constants;
import noraui.utils.Context;
import noraui.utils.Messages;
import noraui.utils.Utilities;

public class Step implements IStep {

    private static volatile WebDriver webDriver = null;

    protected final Logger loggerStep = Logger.getLogger(Step.class.getClass());

    protected Step() {
        webDriver = Context.getDriver();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkStep(IPage page) {
        page.checkPage();
    }

    /**
     * @return a WebDriver
     */
    public static WebDriver getDriver() {
        return webDriver;
    }

    /**
     * Click on html element.
     *
     * @param toClick
     *            html element
     * @param args
     *            list of arguments to format the found selector with
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void clickOn(PageElement toClick, Object... args) throws TechnicalException, FailureException {
        displayMessageAtTheBeginningOfMethod("clickOn: %s in %s", toClick.toString(), toClick.getPage().getApplication());
        try {
            Context.waitUntil(ExpectedConditions.elementToBeClickable(Utilities.getLocator(toClick, args))).click();
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.format(Messages.FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK, toClick, toClick.getPage().getApplication()), true, toClick.getPage().getCallBack());
        }
    }

    /**
     * Click on html element by Javascript.
     *
     * @param toClick
     *            html element
     * @param args
     *            list of arguments to format the found selector with
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void clickOnByJs(PageElement toClick, Object... args) throws TechnicalException, FailureException {
        displayMessageAtTheBeginningOfMethod("clickOnByJs: %s in %s", toClick.toString(), toClick.getPage().getApplication());
        try {
            ((JavascriptExecutor) Step.getDriver())
                    .executeScript("document.evaluate(\"" + Utilities.getLocatorValue(toClick, args) + "\", document, null, XPathResult.UNORDERED_NODE_SNAPSHOT_TYPE, null).snapshotItem(0).click();");

        } catch (Exception e) {
            new Result.Failure<>(e, Messages.format(Messages.FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK, toClick, toClick.getPage().getApplication()), true, toClick.getPage().getCallBack());
        }
    }

    /**
     * Click on html element by Javascript.
     *
     * @param page
     *            page target application
     * @param xpath
     *            XPath of an element to evaluate
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_OPEN_ON_CLICK} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void clickOnByJs(Page page, String xpath) throws TechnicalException, FailureException {
        displayMessageAtTheBeginningOfMethod("clickOnByJs: %s in %s", xpath, page.getApplication());
        try {
            ((JavascriptExecutor) Step.getDriver()).executeScript("document.evaluate(\"" + xpath + "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue.click();");
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.format(Messages.FAIL_MESSAGE_UNABLE_TO_EVALUATE_XPATH, xpath, page.getApplication()), true, page.getCallBack());

        }
    }

    /**
     * Update a html input text with a text.
     *
     * @param pageElement
     *            Is target element
     * @param textOrKey
     *            Is the new data (text)
     * @param args
     *            list of arguments to format the found selector with
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_ERROR_ON_INPUT} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void updateText(PageElement pageElement, String textOrKey, Object... args) throws TechnicalException, FailureException {
        updateText(pageElement, textOrKey, null, args);
    }

    /**
     * Update a html input text with a text.
     *
     * @param pageElement
     *            Is target element
     * @param textOrKey
     *            Is the new data (text)
     * @param keysToSend
     *            character to send to the element after {@link org.openqa.selenium.WebElement#sendKeys(CharSequence...) sendKeys} with textOrKey
     * @param args
     *            list of arguments to format the found selector with
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_ERROR_ON_INPUT} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void updateText(PageElement pageElement, String textOrKey, CharSequence keysToSend, Object... args) throws TechnicalException, FailureException {
        if (textOrKey != null && !"".equals(textOrKey)) {
            try {
                String value = Context.getValue(textOrKey) != null ? Context.getValue(textOrKey) : textOrKey;
                WebElement element = Context.waitUntil(ExpectedConditions.elementToBeClickable(Utilities.getLocator(pageElement, args)));
                element.clear();
                element.sendKeys(value);
                if (keysToSend != null) {
                    element.sendKeys(keysToSend);
                }
            } catch (Exception e) {
                new Result.Failure<>(e, Messages.format(Messages.FAIL_MESSAGE_ERROR_ON_INPUT, pageElement, pageElement.getPage().getApplication()), true, pageElement.getPage().getCallBack());
            }
        } else {
            loggerStep.debug("Empty data provided. No need to update text.");
        }
    }

    /**
     * Update a html input text with "".
     *
     * @param pageElement
     *            Is target element
     * @param args
     *            list of arguments to format the found selector with
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_ERROR_CLEAR_ON_INPUT} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void clearText(PageElement pageElement, Object... args) throws TechnicalException, FailureException {
        clearText(pageElement, null, args);
    }

    /**
     * Update a html input text with "".
     *
     * @param pageElement
     *            Is target element
     * @param keysToSend
     *            character to send to the element after {@link org.openqa.selenium.WebElement#sendKeys(CharSequence...) sendKeys} with textOrKey
     * @param args
     *            list of arguments to format the found selector with
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_ERROR_CLEAR_ON_INPUT} message (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void clearText(PageElement pageElement, CharSequence keysToSend, Object... args) throws TechnicalException, FailureException {
        try {
            WebElement element = Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(pageElement, args)));
            element.clear();
            if (keysToSend != null) {
                element.sendKeys(keysToSend);
            }
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.format(Messages.FAIL_MESSAGE_ERROR_CLEAR_ON_INPUT, pageElement, pageElement.getPage().getApplication()), true, pageElement.getPage().getCallBack());
        }
    }

    /**
     * Checks if input text contains expected value.
     *
     * @param pageElement
     *            Is target element
     * @param value
     *            Is expected value in input text (value can be null).
     * @return true or false
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected boolean checkInputText(PageElement pageElement, String value) throws FailureException {
        WebElement inputText = null;
        try {
            inputText = Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(pageElement)));
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT, true, pageElement.getPage().getCallBack());
        }
        if (inputText == null || value == null || !value.equals(inputText.getAttribute(VALUE))) {
            return false;
        }
        return true;
    }

    /**
     * Check mandatory text field.
     *
     * @param pageElement
     *            Is concerned element
     * @return true or false
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected boolean checkMandatoryTextField(PageElement pageElement) throws FailureException {
        WebElement inputText = null;
        try {
            inputText = Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(pageElement)));
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT, true, pageElement.getPage().getCallBack());
        }
        if (inputText == null || "".equals(inputText.getAttribute(VALUE))) {
            return false;
        }
        return true;
    }

    protected String readValueTextField(PageElement pageElement) throws FailureException {
        try {
            return Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(pageElement))).getAttribute(VALUE);
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT, true, pageElement.getPage().getCallBack());
        }
        return null;
    }

    /**
     * Checks if HTML text contains expected value.
     *
     * @param pageElement
     *            Is target element
     * @param value
     *            Is expected value in input text (value can be null).
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_WRONG_EXPECTED_VALUE} message (with screenshot, with exception) or with
     *             {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT} message
     *             (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void checkText(PageElement pageElement, String value) throws TechnicalException, FailureException {
        WebElement inputText = null;
        if (value == null) {
            throw new TechnicalException(TechnicalException.TECHNICAL_ERROR_MESSAGE + "value ne peut être null.");
        }
        try {
            inputText = Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(pageElement)));
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT, true, pageElement.getPage().getCallBack());
        }
        if (inputText == null || !value.equals(inputText.getText())) {
            new Result.Failure<>(inputText == null ? null : inputText.getText(),
                    Messages.format(Messages.FAIL_MESSAGE_WRONG_EXPECTED_VALUE, pageElement, value, pageElement.getPage().getApplication()), true, pageElement.getPage().getCallBack());
        }
    }

    /**
     * Update a html select with a text value.
     *
     * @param pageElement
     *            Is target element
     * @param text
     *            Is the new data (text)
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_ERROR_ON_INPUT} message (with screenshot, no exception)
     *             or
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_VALUE_NOT_AVAILABLE_IN_THE_LIST} message (no screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void updateList(PageElement pageElement, String text) throws TechnicalException, FailureException {
        if (!"".equals(text)) {
            try {
                setDropDownValue(pageElement, text);
            } catch (Exception e) {
                new Result.Failure<>(e, Messages.format(Messages.FAIL_MESSAGE_ERROR_ON_INPUT, pageElement, pageElement.getPage().getApplication()), true, pageElement.getPage().getCallBack());
            }
        }
    }

    /**
     * Check text selected in list (html select option).
     *
     * @param pageElement
     *            Is target element
     * @param text
     *            is text searched
     * @return true or false
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected boolean checkTextSelectedInList(PageElement pageElement, String text) throws FailureException {
        WebElement select = Context.waitUntil(ExpectedConditions.elementToBeClickable(Utilities.getLocator(pageElement)));
        if (select != null) {
            Select dropDown = new Select(select);
            return text.equalsIgnoreCase(dropDown.getFirstSelectedOption().getText());
        } else {
            new Result.Failure<>(select, Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT, true, pageElement.getPage().getCallBack());
            return false;
        }
    }

    /**
     * Update a html input text value with a date.
     *
     * @param pageElement
     *            Is target element
     * @param dateType
     *            "any", "future", "today", "future_strict"
     * @param date
     *            Is the new data (date)
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_WRONG_DATE_FORMAT} message (no screenshot, no exception) or with
     *             {@value noraui.utils.Messages#FAIL_MESSAGE_UNEXPECTED_DATE} message
     *             (with screenshot, no exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void updateDateValidated(PageElement pageElement, String dateType, String date) throws TechnicalException, FailureException {
        loggerStep.debug(String.format("updateDateValidated with elementName=%s, dateType=%s and date=%s", pageElement.toString(), dateType, date));
        DateFormat formatter = new SimpleDateFormat(Constants.DATE_FORMAT);
        Date today = Calendar.getInstance().getTime();
        try {
            Date valideDate = formatter.parse(date);
            if ("any".equals(dateType)) {
                loggerStep.debug("update Date with any date: " + date);
                updateText(pageElement, date);
            } else if (formatter.format(today).equals(date) && ("future".equals(dateType) || "today".equals(dateType))) {
                loggerStep.debug("update Date with today");
                updateText(pageElement, date);
            } else if (valideDate.after(Calendar.getInstance().getTime()) && ("future".equals(dateType) || "future_strict".equals(dateType))) {
                loggerStep.debug("update Date with a date after today: " + date);
                updateText(pageElement, date);
            } else {
                new Result.Failure<>(date, Messages.format(Messages.FAIL_MESSAGE_UNEXPECTED_DATE, "supérieure ou égale à la date du jour"), true, pageElement.getPage().getCallBack());
            }
        } catch (ParseException e) {
            new Result.Failure<>(e, Messages.format(Messages.FAIL_MESSAGE_WRONG_DATE_FORMAT, pageElement, date), false, pageElement.getPage().getCallBack());
        }

    }

    /**
     * Save value in memory using default target key (Page key + field).
     *
     * @param field
     *            is name of the field to retrieve.
     * @param page
     *            is target page.
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT} message (with screenshot, with exception) or with
     *             {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_RETRIEVE_VALUE} message
     *             (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void saveValueInStep(String field, Page page) throws TechnicalException, FailureException {
        loggerStep.debug(String.format("saveValueInStep: %s in %s.", field, page.getApplication()));
        saveValueInStep(field, page.getPageKey() + field, page);
    }

    /**
     * Save value in memory.
     *
     * @param field
     *            is name of the field to retrieve.
     * @param targetKey
     *            is the key to save value to
     * @param page
     *            is target page.
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT} message (with screenshot, with exception) or with
     *             {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_RETRIEVE_VALUE} message
     *             (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void saveValueInStep(String field, String targetKey, Page page) throws TechnicalException, FailureException {
        loggerStep.debug(String.format("saveValueInStep: %s to %s in %s.", field, targetKey, page.getApplication()));
        String txt = "";
        try {
            txt = Utilities.findElement(page, field).getText();
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT, true, page.getCallBack());
        }
        try {
            Context.saveValue(targetKey, txt);
            Context.getCurrentScenario().write("SAVE " + targetKey + "=" + txt);
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.format(Messages.FAIL_MESSAGE_UNABLE_TO_RETRIEVE_VALUE, page.getPageElementByKey(field), page.getApplication()), true, page.getCallBack());
        }
    }

    /**
     * Update html radio button by value (value corresponding to key "index").
     *
     * @param pageElement
     *            Is concerned element
     * @param valueKey
     *            key printedValues
     * @param printedValues
     *            contain all possible value (order by key)
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Exception with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_SELECT_RADIO_BUTTON} message (with screenshot, with exception)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void updateRadioList(PageElement pageElement, String valueKey, Map<String, String> printedValues) throws TechnicalException, FailureException {
        try {
            List<WebElement> radioButtons = Context.waitUntil(ExpectedConditions.presenceOfAllElementsLocatedBy(Utilities.getLocator(pageElement)));
            String radioToSelect = printedValues.get(valueKey);
            if (radioToSelect == null) {
                radioToSelect = printedValues.get("Default");
            }
            for (WebElement button : radioButtons) {
                if (button.getAttribute(VALUE).equals(radioToSelect)) {
                    button.click();
                    break;
                }
            }
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.format(Messages.FAIL_MESSAGE_UNABLE_TO_SELECT_RADIO_BUTTON, pageElement), true, pageElement.getPage().getCallBack());
        }
    }

    /**
     * Checks that given value is matching the selected radio list button.
     *
     * @param pageElement
     *            The page element
     * @param value
     *            The value to check the selection from
     * @return true if the given value is selected, false otherwise.
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected boolean checkRadioList(PageElement pageElement, String value) throws FailureException {
        try {
            List<WebElement> radioButtons = Context.waitUntil(ExpectedConditions.presenceOfAllElementsLocatedBy(Utilities.getLocator(pageElement)));
            for (WebElement button : radioButtons) {
                if (button.getAttribute(VALUE).equalsIgnoreCase(value) && button.isSelected()) {
                    return true;
                }
            }
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.FAIL_MESSAGE_UNABLE_TO_FIND_ELEMENT, true, pageElement.getPage().getCallBack());
        }
        return false;
    }

    /**
     * Update html radio button by text "input".
     *
     * @param pageElement
     *            Is concerned element
     * @param value
     *            is the value use for selection
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Failure with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_SELECT_RADIO_BUTTON} message (with screenshot)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void updateRadioList(PageElement pageElement, String value) throws TechnicalException, FailureException {
        try {
            List<WebElement> radioButtons = Context.waitUntil(ExpectedConditions.presenceOfAllElementsLocatedBy(Utilities.getLocator(pageElement)));

            for (WebElement button : radioButtons) {
                if (button.getAttribute(VALUE).contains(value)) {
                    button.click();
                    break;
                }
            }
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.format(Messages.FAIL_MESSAGE_UNABLE_TO_SELECT_RADIO_BUTTON, pageElement.getLabel()), true, pageElement.getPage().getCallBack());
        }
    }

    /**
     * Passes over a specific page element triggering 'mouseover' js event.
     *
     * @param element
     *            Target page element
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Failure with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_PASS_OVER_ELEMENT} message (with screenshot)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void passOver(PageElement element) throws TechnicalException, FailureException {
        try {
            String javascript = "var evObj = document.createEvent('MouseEvents');" + "evObj.initMouseEvent(\"mouseover\",true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
                    + "arguments[0].dispatchEvent(evObj);";
            ((JavascriptExecutor) Step.getDriver()).executeScript(javascript, Context.waitUntil(ExpectedConditions.presenceOfElementLocated(Utilities.getLocator(element))));

        } catch (Exception e) {
            new Result.Failure<>(e, Messages.format(Messages.FAIL_MESSAGE_UNABLE_TO_PASS_OVER_ELEMENT, element, element.getPage().getApplication()), true, element.getPage().getCallBack());
        }

    }

    /**
     * Checks a checkbox type element.
     *
     * @param element
     *            Target page element
     * @param checked
     *            Final checkbox value
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Failure with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_CHECK_ELEMENT} message (with screenshot)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void selectCheckbox(PageElement element, boolean checked) throws TechnicalException, FailureException {
        try {
            WebElement webElement = Context.waitUntil(ExpectedConditions.elementToBeClickable(Utilities.getLocator(element)));
            if (webElement.isSelected() != checked) {
                webElement.click();
            }
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.format(Messages.FAIL_MESSAGE_UNABLE_TO_CHECK_ELEMENT, element, element.getPage().getApplication()), true, element.getPage().getCallBack());
        }
    }

    /**
     * Checks a checkbox type element (value corresponding to key "valueKey").
     *
     * @param element
     *            Target page element
     * @param valueKey
     *            Key to match in values map
     * @param values
     *            Values map
     * @throws TechnicalException
     *             is thrown if you have a technical error (format, configuration, datas, ...) in NoraUi.
     *             Failure with {@value noraui.utils.Messages#FAIL_MESSAGE_UNABLE_TO_CHECK_ELEMENT} message (with screenshot)
     * @throws FailureException
     *             if the scenario encounters a functional error
     */
    protected void selectCheckbox(PageElement element, String valueKey, Map<String, Boolean> values) throws TechnicalException, FailureException {
        try {
            WebElement webElement = Context.waitUntil(ExpectedConditions.elementToBeClickable(Utilities.getLocator(element)));
            Boolean checkboxValue = values.get(valueKey);
            if (checkboxValue == null) {
                checkboxValue = values.get("Default");
            }
            if (webElement.isSelected() != checkboxValue.booleanValue()) {
                webElement.click();
            }
        } catch (Exception e) {
            new Result.Failure<>(e, Messages.format(Messages.FAIL_MESSAGE_UNABLE_TO_CHECK_ELEMENT, element, element.getPage().getApplication()), true, element.getPage().getCallBack());
        }
    }

    /**
     * Displays message (concerned activity and list of authorized activities) at the beginning of method in logs.
     *
     * @param methodName
     *            is name of java method
     * @param act
     *            is name of activity
     * @param concernedActivity
     *            is concerned activity
     * @param concernedActivities
     *            is a list of authorized activities
     */
    protected void displayMessageAtTheBeginningOfMethod(String methodName, String act, String concernedActivity, List<String> concernedActivities) {
        loggerStep.debug(String.format("%s %s: %s with %d concernedActivity(ies)", act, methodName, concernedActivity, concernedActivities.size()));
        int i = 0;
        for (String activity : concernedActivities) {
            i++;
            loggerStep.debug(String.format("  activity N°%d=%s", i, activity));
        }
    }

    /**
     * Display message (list of authorized activities) at the beginning of method in logs.
     *
     * @param methodName
     *            is name of java method
     * @param act
     *            is name of activity
     * @param concernedActivities
     *            is a list of authorized activities
     */
    protected void displayMessageAtTheBeginningOfMethod(String methodName, String act, List<String> concernedActivities) {
        loggerStep.debug(String.format("%s: %s with %d concernedActivity(ies)", act, methodName, concernedActivities.size()));
        int i = 0;
        for (String activity : concernedActivities) {
            i++;
            loggerStep.debug(String.format("  activity N°%d=%s", i, activity));
        }
    }

    /**
     * Display message (list of elements) at the beginning of method in logs.
     *
     * @param methodName
     *            is name of java method
     * @param concernedElements
     *            is a list of concerned elements (example: authorized activities)
     */
    protected void displayConcernedElementsAtTheBeginningOfMethod(String methodName, List<String> concernedElements) {
        loggerStep.debug(String.format("%s: with %d concernedElements", methodName, concernedElements.size()));
        int i = 0;
        for (String element : concernedElements) {
            i++;
            loggerStep.debug(String.format("  element N°%d=%s", i, element));
        }
    }

    private void displayMessageAtTheBeginningOfMethod(String message, String element, String application) throws TechnicalException {
        try {
            loggerStep.debug(Messages.format(message, element, application));
        } catch (Exception te) {
            loggerStep.error(TechnicalException.TECHNICAL_ERROR_MESSAGE + te.getMessage(), te);
            throw new TechnicalException("Technical problem in the code Messages.formatMessage(String templateMessage, String... args) in NoraUi.", te);
        }
    }

    private void setDropDownValue(PageElement element, String text) throws TechnicalException, FailureException {
        WebElement select = Context.waitUntil(ExpectedConditions.elementToBeClickable(Utilities.getLocator(element)));
        Select dropDown = new Select(select);
        int index = findOptionByIgnoreCaseText(text, dropDown);
        if (index != -1) {
            dropDown.selectByIndex(index);
        } else {
            new Result.Failure<>(text, Messages.format(Messages.FAIL_MESSAGE_VALUE_NOT_AVAILABLE_IN_THE_LIST, element, element.getPage().getApplication()), false, element.getPage().getCallBack());
        }

    }

    protected int findOptionByIgnoreCaseText(String text, Select dropDown) {
        int index = 0;
        for (WebElement option : dropDown.getOptions()) {
            if (Normalizer.normalize(option.getText(), Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").trim()
                    .equalsIgnoreCase(Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").trim())) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     * @return a String with the message of Alert, return null if no alert message.
     */
    protected String getLastConsoleAlertMessage() {
        LogEntries logEntries = Step.getDriver().manage().logs().get(LogType.BROWSER);
        List<LogEntry> l = logEntries.getAll();
        for (int i = l.size() - 1; i >= 0; i--) {
            if (l.get(i).getMessage().contains(ALERT_KEY)) {
                return l.get(i).getMessage().replace(ALERT_KEY, "").replace(" (:)", "");
            }
        }
        return null;
    }
}
