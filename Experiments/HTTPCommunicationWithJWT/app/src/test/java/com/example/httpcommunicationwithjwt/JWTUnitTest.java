package com.example.httpcommunicationwithjwt;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static junit.framework.TestCase.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;




public class JWTUnitTest {

    private HTTPImplementation Imp;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Before
    public void setup() {

        Imp = mock(HTTPImplementation.class);
    }

    @Test
    public void getJWTTest() {

        when(Imp.getJWT()).thenReturn("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmYWRtaW4iLCJpc19hZG1pbiI6ZmFsc2UsImlzX21vZCI6ZmFsc2UsImlhdCI6MTU1NDQ4MTIyOH0.QiYUbENoSF-9GNsV0tLfStRSClO4wtGJaF-0EhnXfME");

        assertEquals("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJmYWRtaW4iLCJpc19hZG1pbiI6ZmFsc2UsImlzX21vZCI6ZmFsc2UsImlhdCI6MTU1NDQ4MTIyOH0.QiYUbENoSF-9GNsV0tLfStRSClO4wtGJaF-0EhnXfME", Imp.getJWT());
    }

    @Test
    public void JWTSuccess() throws JSONException {

        when(Imp.getUserInfo("fadmin")).thenReturn(true);
        when(Imp.setBio("fadmin", "I exist")).thenReturn(true);
        when(Imp.deleteUser("fadmin")).thenReturn(true);

        assertTrue(Imp.getUserInfo("fadmin"));
        assertTrue(Imp.setBio("fadmin", "I exist"));
        assertTrue(Imp.deleteUser("fadmin"));
    }
}

// Mock objects should be used to test lower level functions.
// The output of a mock function should be used in another function.