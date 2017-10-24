package com.benayn.constell.service.util;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;

import com.benayn.constell.service.exception.ServiceException;
import com.google.common.base.CharMatcher;
import org.springframework.http.HttpStatus;

public class Assets {

    public static int checkRecordDeleted(int result) throws ServiceException {
        checkRecordAlreadyExist(result > 0);
        return result;
    }

    public static int checkRecordSaved(int result) throws ServiceException {
        checkResults(result > 0, "{render.record.save.fail}");
        return result;
    }

    public static void checkRecordAlreadyExist(boolean expression) throws ServiceException {
        checkResults(expression, HttpStatus.NO_CONTENT.value(), "{render.record.none.exist}");
    }

    public static void checkRecordNoneExist(boolean expression, Object... errorMessageArgs) throws ServiceException {
        checkResults(expression, "{render.record.already.exist}", errorMessageArgs);
    }

    public static String checkNotBlank(String reference) throws ServiceException {
        return checkNotBlank(reference, "{assets.check.not.blank}", REFERENCE);
    }

    public static String checkNotBlank(String reference, String errorMessage, Object... errorMessageArgs) throws ServiceException {
        checkArgument(!CharMatcher.whitespace().matchesAllOf(nullToEmpty(reference)), errorMessage, errorMessageArgs);
        return reference;
    }

    public static String checkNotEmpty(String reference) throws ServiceException {
        return checkNotEmpty(reference, "{assets.check.not.empty}", REFERENCE);
    }

    public static String checkNotEmpty(String reference, String errorMessage, Object... errorMessageArgs) throws ServiceException {
        checkArgument(!isNullOrEmpty(reference), errorMessage, errorMessageArgs);
        return reference;
    }

    public static <T> T checkNotNull(T reference) throws ServiceException {
        return checkNotNull(reference, "{assets.check.not.null}", REFERENCE);
    }

    public static <T> T checkNotNull(T reference, String errorMessage, Object... errorMessageArgs) throws ServiceException {
        checkArgument(null != reference, errorMessage, errorMessageArgs);
        return reference;
    }

    public static void checkArgument(boolean expression, String errorMessage, Object... errorMessageArgs)
        throws ServiceException {
        checkArgument(expression, ServiceException.DEFAULT_CODE, errorMessage, errorMessageArgs);
    }

    public static void checkArgument(boolean expression, int code, String errorMessage, Object... errorMessageArgs)
        throws ServiceException {
        checkResults(expression, code, errorMessage, errorMessageArgs);
    }

    public static void checkResults(boolean expression, String errorMessage, Object... errorMessageArgs)
        throws ServiceException {
        checkResults(expression, ServiceException.DEFAULT_CODE, errorMessage, errorMessageArgs);
    }

    public static void checkResults(boolean expression, int code, String errorMessage, Object... errorMessageArgs)
        throws ServiceException {
        if (!expression) {
            throw new ServiceException(code, errorMessage, errorMessageArgs);
        }
    }

    private static final String REFERENCE = "reference";
}
