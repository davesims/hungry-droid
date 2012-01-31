package com.hungrymachine.hungrydroid.api;

import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ApiResultTest {
    @Test
    public void resultConstructorShouldReturnSuccess() {
        ApiResult result = new ApiResult(new Object());
        assertThat(result.success(), equalTo(true));
    }

    @Test
    public void exceptionConstructorShouldNotReturnSuccess() {
        ApiResult result = new ApiResult(new TestApiException());
        assertThat(result.success(), equalTo(false));
    }

    @Test
    public void exceptionResultShouldReturnUserMessage() {
        ApiResult result = new ApiResult(new TestApiException());
        assertThat(result.getErrorMessageForUser(), equalTo("boom"));
    }

    @Test
    public void shouldReturnTheResultObject() {
        Object resultObject = new Object();
        ApiResult result = new ApiResult(resultObject);
        assertThat(result.getResultObject(), equalTo(resultObject));
    }

    @Test
    public void authenticationRejectedShouldBeTrueForAuthenticationExceptions() {
        ApiResult result = new ApiResult(new TestApiException());
        assertThat(result.authenticationRejected(), equalTo(true));
    }

    @Test
    public void authenticationRejectedShouldBeFalseForNonAuthenticationExceptions() {
        ApiResult result = new ApiResult(new ApiException(){
            public String getErrorMessageForUser(){ return "";}
        });
        assertThat(result.authenticationRejected(), equalTo(false));
    }


    class TestApiException extends ApiException {
        @Override
        public String getErrorMessageForUser() {
            return "boom";
        }

        @Override
        public boolean isAuthenticationException() {
            return true;
        }
    }
}
