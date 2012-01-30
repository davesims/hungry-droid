package com.livingsocial.android.api;

import android.app.Activity;
import com.livingsocial.android.LivingSocial;
import com.livingsocial.android.LsPreferences;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
public class ApiBaseTest {
    private TestApiBase testApiBase;

    @Before
    public void setup() {
        LivingSocial.setAppContext(new Activity().getApplicationContext());
        testApiBase = new TestApiBase();
    }

    @Test
    public void testFacebookUid() {
        LsPreferences.setFacebookCredentials("user_uid", "token", 1);
        HashMap<String, String> facebookCredentials = testApiBase.getFaceBookCredentials();
        assertThat(facebookCredentials.get("ls_fb_sig_user"), equalTo("user_uid"));
    }

    @Test
    public void testFacebookAccessToken() {
        LsPreferences.setFacebookCredentials("user_uid", "token", 1);

        HashMap<String, String> facebookCredentials = testApiBase.getFaceBookCredentials();
        assertThat(facebookCredentials.get("ls_fb_token"), equalTo("token"));
    }

    @Test
    public void testGetHeadersWithBasicAuth() {
//        LsPreferences.setLivingSocialCredentialsString("fu@fu.com", "password");
//        HashMap<String, String> headers = testApiBase.getHeaders(true);
//        assertThat(headers.get("Authorization"), equalTo(""));
    }

    private class TestApiBase extends ApiBase {
    }
}
