package com.benayn.constell.service.util;

import com.benayn.constell.service.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class Assets {

    public static void checkRecordAlreadyExist(boolean expression, Object... errorMessageArgs) throws ServiceException {
        checkResults(expression, "{render.record.already.exist}", errorMessageArgs);
    }

    public static int checkRecordNoneExist(int result) throws ServiceException {
        checkResults(result < 1, HttpStatus.NO_CONTENT.value(), "{render.record.none.exist}");
        return result;
    }

    public static int checkRecordNoneSaved(int result) throws ServiceException {
        checkResults(result < 1, "{render.record.save.fail}");
        return result;
    }

    public static void checkResults(boolean expression, String errorMessage, Object... errorMessageArgs)
        throws ServiceException {
        checkResults(expression, 0, errorMessage, errorMessageArgs);
    }

    public static void checkResults(boolean expression, int code, String errorMessage, Object... errorMessageArgs)
        throws ServiceException {
        if (expression) {
            throw new ServiceException(code, errorMessage, errorMessageArgs);
        }
    }

}
