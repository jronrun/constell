package com.benayn.constell.service.server.respond;

public enum InputType {
    /*
        button: A push button with no default behavior.
        checkbox: A check box allowing single values to be selected/deselected.
        color: HTML5 A control for specifying a color. A color picker's UI has no required features other than accepting simple colors as text (more info).
        date: HTML5 A control for entering a date (year, month, and day, with no time).
        email: HTML5 A field for editing an e-mail address.
        file: A control that lets the user select a file. Use the accept attribute to define the types of files that the control can select.
        hidden: A control that is not displayed but whose value is submitted to the server.
        image: A graphical submit button. You must use the src attribute to define the source of the image and the alt attribute to define alternative text. You can use the height and width attributes to define the size of the image in pixels.
        month: HTML5 A control for entering a month and year, with no time zone.
        number: HTML5 A control for entering a number.
        password: A single-line text field whose value is obscured. Use the maxlength attribute to specify the maximum length of the value that can be entered.
        radio: A radio button, allowing a single value to be selected out of multiple choices.
        range: HTML5 A control for entering a number whose exact value is not important.
        reset: A button that resets the contents of the form to default values.
        search: HTML5 A single-line text field for entering search strings. Line-breaks are automatically removed from the input value.
        submit: A button that submits the form.
        tel: HTML5 A control for entering a telephone number. Line-breaks are automatically removed from the input value, but no other syntax is enforced. You can use attributes such as pattern and maxlength to restrict values entered in the control. The :valid and :invalid CSS pseudo-classes are applied as appropriate.
        text: A single-line text field. Line-breaks are automatically removed from the input value.
        time: HTML5 A control for entering a time value with no time zone.
        url: HTML5 A field for editing a URL. The input value is validated to contain either the empty string or a valid absolute URL before submitting. You can use attributes such as pattern and maxlength to restrict values entered in the control. The :valid and :invalid CSS pseudo-classes are applied as appropriate.
        week: HTML5 A control for entering a date consisting of a week-year number and a week number with no time zone.
     */

    BUTTON, CHECKBOX, COLOR, DATE, EMAIL, FILE, HIDDEN,
    IMAGE, MONTH, NUMBER, PASSWORD, RADIO, RANGE,
    RESET, SEARCH, SUBMIT, TEL, TEXT, TIME, URL, WEEK,
    UNDEFINED
}
