package com.livingsocial.android.api;

import android.content.Context;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
public class ApiResultProcessorTest {

    Context mockContext;
    ApiResultProcessor resultProcessor;
    ApiException apiException;
    ApiResult result;
    ApiResultProcessor spiedProcessor;

    @Before
    public void setup() {
        mockContext = mock(Context.class);
        resultProcessor = new TestResultProcessor();
        spiedProcessor = spy(resultProcessor);
        apiException = mock(ApiException.class);
        result = mock(ApiResult.class);
    }

    @Test
    public void shouldRequestResultObjectForSuccessResult() {
        when(result.success()).thenReturn(true);
        resultProcessor.processResult(result, mockContext);
        verify(result).getResultObject();
    }

    @Test
    public void shouldRequestErrorMessageForExceptionResult() {
        when(result.success()).thenReturn(false);
        resultProcessor.processResult(result, mockContext);
        verify(result).getErrorMessageForUser();
    }

    @Test
    public void shouldCallHandleSuccessForSuccessResult() {
        when(result.success()).thenReturn(true);
        spiedProcessor.processResult(result, mockContext);
        verify(spiedProcessor).handleSuccess(anyObject());
    }

    @Test
    public void shouldCallHandleErrorForErrorResult() {
        when(result.success()).thenReturn(false);
        spiedProcessor.processResult(result, mockContext);
        verify(spiedProcessor).handleError(anyString());
    }

    public class TestResultProcessor extends ApiResultProcessor {
        @Override
        public void handleSuccess(Object resultObject) {
        }
    }
}
