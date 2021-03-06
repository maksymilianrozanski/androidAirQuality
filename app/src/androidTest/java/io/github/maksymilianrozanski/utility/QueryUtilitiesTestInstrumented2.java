package io.github.maksymilianrozanski.utility;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import io.github.maksymilianrozanski.utility.QueryUtilities;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class QueryUtilitiesTestInstrumented2 {

    @Test
    public void getStringFromJsonObjectTest() throws Exception{

        JSONObject jsonObject = new JSONObject("{\"key1\":\"PM10\"}");

        String obtainedValue = QueryUtilities.getStringFromJSONObject(jsonObject, "key1");
        String expectedValue = jsonObject.getString("key1");
        assertTrue(obtainedValue.equals(expectedValue));
    }
}
