package org.quickocm.matchers;

import org.quickocm.exception.UploadException;
import org.hamcrest.Matcher;
import org.mockito.ArgumentMatcher;

public class ExceptionMatcher {

    public static Matcher<UploadException> uploadExceptionMatcher(final String code, final String... params) {
        return new ArgumentMatcher<UploadException>() {
            @Override
            public boolean matches(Object argument) {
                UploadException uploadException = (UploadException) argument;
                if (!uploadException.getCode().equals(code)) return false;
                for (int index = 0; index < params.length; index++) {
                    if (!uploadException.getParams()[index].equals(params[index])) return false;
                }
                return true;
            }
        };
    }

}
