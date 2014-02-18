package com.hungrymachine.hungrydroid.api;

import android.content.Context;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(RobolectricTestRunner.class)
public class ApiResultUIProcessorTest {

    Context mockContext;
    ApiResultUIProcessor resultProcessor;
    ApiException apiException;
    ApiResult result;
    ApiResultProcessor spiedProcessor;

    @Before
    public void setup() {
        mockContext = mock(Context.class);
        resultProcessor = new ApiResultUIProcessorTest.TestResultUIProcessor();
        spiedProcessor = spy(resultProcessor);
        apiException = mock(ApiException.class);
        result = mock(ApiResult.class);
    }

     @Test
    public void shouldRequestResultObjectForSuccessResult() {
//        when(result.success()).thenReturn(true);
//        resultProcessor.processResult(result, mockContext);
//        verify(result).getResultObject();
    }

    public class TestResultUIProcessor extends ApiResultUIProcessor {
        @Override
        public void handleSuccess(Object resultObject) {
            
        }

        @Override
        public void logOutAndShowAuthenticationRejectedMessage(ApiResult apiResult, Context context) {
        }
    }
}
