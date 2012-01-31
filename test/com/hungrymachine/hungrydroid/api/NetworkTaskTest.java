package com.hungrymachine.hungrydroid.api;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class NetworkTaskTest {
    NetworkTask task;

    @Before
    public void setup() {
        task = new NetworkTask<String>() {
            @Override
            public String getValueFromServer() {
                return "Test";
            }

            @Override
            public String createReturnObject(String cachedValue) {
                return null;
            }
        };
    }

    @Test
    public void testSetErrorMessage() {
        task.setErrorMessage("Error message");
        assertThat(task.getErrorMessage(), equalTo("Error message"));
    }

    @Test
    public void testServerReturnedError(){
        task.setErrorMessage("some message");
        assertThat(task.serverReturnedError(), equalTo(true));
    }

}
