package org.jenkinsci.plugins.lambdatestrunner.jenkins.response;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ResponseMapperTest {

    @Test
    public void testAsObject() throws IOException {
        String responseBody = "{\"exitCode\": 0, \"output\": \"all good\", \"requestId\": \"b5fff7ea-085e-11e9-a665-db2069b28de5\", \"s3Prefix\": \"2018-12-23-16-00-41\"}";

        Response response = new Response();
        response.setExitCode(0);
        response.setOutput("all good");
        response.setRequestId("b5fff7ea-085e-11e9-a665-db2069b28de5");
        response.setS3Prefix("2018-12-23-16-00-41");

        Response mappedResponse = ResponseMapper.asObject(responseBody);
        assertEquals(mappedResponse, response);
    }

    @Test(expected = IOException.class)
    public void testAsObjectException() throws IOException {
        String responseBody = "{\"exitCode\": 0";
        ResponseMapper.asObject(responseBody);
    }
}