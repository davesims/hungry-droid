package com.hungrymachine.hungrydroid.api;

import android.app.Activity;
import android.app.Application;
import com.hungrymachine.hungrydroid.HungryDroid;
import com.hungrymachine.hungrydroid.net.NetworkService;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;



@RunWith(RobolectricTestRunner.class)
public class ApiExceptionTest {
    
    @Before
    public void setup() {
        Application app = new Application();
        HungryDroid.initialize(app, new Activity());
    }

    @Test
    public void testShouldHandleNullArgumentInCreateConstructor() throws Exception {
        Exception ex = ApiException.create(null);
        assertThat(ex, instanceOf(ApiException.ServerError.class));
    }

    @Test
    public void shouldCreateConnectionUnavailableExceptionForNoNetworkException() {
        RuntimeException ex = new RuntimeException(NetworkService.NO_NETWORK_AVAILABLE);
        Exception apiException = ApiException.create(ex);
        assertThat(apiException, instanceOf(ApiException.ConnectionUnavailable.class));
    }

    @Test
    public void shouldCreateNoAuthenticatedExceptionForNoAuthFoundException() {
        RuntimeException ex = new RuntimeException(ApiException.NO_AUTH);
        Exception apiException = ApiException.create(ex);
        assertThat(apiException, instanceOf(ApiException.NotAuthenticated.class));
    }

    @Test
    public void shouldCreateTimeoutExceptionForSocketTimeout() {
        RuntimeException ex = new RuntimeException(ApiException.SOCKET_TIMEOUT_EXCEPTION);
        Exception apiException = ApiException.create(ex);
        assertThat(apiException, instanceOf(ApiException.TimeOut.class));
    }

    @Test
    public void shouldCreateServerErrorExceptionForEverythingElse() {
        RuntimeException ex = new RuntimeException("");
        Exception apiException = ApiException.create(ex);
        assertThat(apiException, instanceOf(ApiException.ServerError.class));

        ex = new IllegalArgumentException();
        apiException = ApiException.create(ex);
        assertThat(apiException, instanceOf(ApiException.ServerError.class));
    }

}
