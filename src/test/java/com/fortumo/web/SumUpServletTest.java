package com.fortumo.web;

import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class SumUpServletTest {

    @Test
    void testServletEndThread() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        BufferedReader bufferedReader = new BufferedReader(new StringReader("end"));
        when(request.getReader()).thenReturn(bufferedReader);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new SumUpServlet().doPost(request, response);

        verify(request, atLeast(1)).getReader();
        writer.flush();
        assertTrue(stringWriter.toString().contains("0"));
    }

    @Test
    void testServletLockOneThread() throws IOException, InterruptedException {
        SumUpServlet servlet = new SumUpServlet();
        String expectedResponse = "1";

        //first
        StringWriter actualResponseThread1 = createThreadAndAssertValue(servlet, "1");

        //endSignal
        StringWriter actualResponseThread2 = endSignalAndAssertValue(servlet);

        wait(actualResponseThread1);
        assertEquals(expectedResponse, actualResponseThread1.toString());
        assertEquals(expectedResponse, actualResponseThread2.toString());
    }

    @Test
    void testServletLockThirtyThreads() throws IOException, InterruptedException {
        SumUpServlet servlet = new SumUpServlet();
        List<StringWriter> listActualResponse = new ArrayList<>();
        String expectedResponse = "30";

        for(int i = 0; i < 30; i++) {
            listActualResponse.add(createThreadAndAssertValue(servlet, "1"));
        }

        //endSignal
        listActualResponse.add(endSignalAndAssertValue(servlet));

        //assertions
        for(StringWriter actualResponseWriter : listActualResponse) {
            wait(actualResponseWriter);
            assertEquals(expectedResponse, actualResponseWriter.toString());
        }
    }

    @Test
    void testServletInvalidValue3Threads() throws IOException, InterruptedException {
        SumUpServlet servlet = new SumUpServlet();
        List<StringWriter> listActualResponse = new ArrayList<>();
        String expectedResponse = "80";

        listActualResponse.add(createThreadAndAssertValue(servlet, "30"));
        listActualResponse.add(createThreadAndAssertValue(servlet, "50"));
        listActualResponse.add(createThreadAndAssertValue(servlet, "a"));

        //endSignal
        listActualResponse.add(endSignalAndAssertValue(servlet));

        //assertions
        for(StringWriter actualResponseWriter : listActualResponse) {
            wait(actualResponseWriter);
            assertEquals(expectedResponse, actualResponseWriter.toString());
        }
    }

    @Test
    void testServletMultipleExecution() throws IOException, InterruptedException {
        SumUpServlet servlet = new SumUpServlet();
        List<StringWriter> listActualResponse = new ArrayList<>();
        String expectedResponse = "100";

        //first execution
        listActualResponse.add(createThreadAndAssertValue(servlet, "25"));
        listActualResponse.add(createThreadAndAssertValue(servlet, "35"));
        listActualResponse.add(createThreadAndAssertValue(servlet, "40"));
        //endSignal
        listActualResponse.add(endSignalAndAssertValue(servlet));
        //assertions
        for(StringWriter actualResponseWriter : listActualResponse) {
            wait(actualResponseWriter);
            assertEquals(expectedResponse, actualResponseWriter.toString());
        }

        listActualResponse.clear();
        expectedResponse = "60";
        //second execution
        listActualResponse.add(createThreadAndAssertValue(servlet, "20"));
        listActualResponse.add(createThreadAndAssertValue(servlet, "30"));
        listActualResponse.add(createThreadAndAssertValue(servlet, "10"));
        //endSignal
        listActualResponse.add(endSignalAndAssertValue(servlet));

        //assertions
        for(StringWriter actualResponseWriter : listActualResponse) {
            wait(actualResponseWriter);
            assertEquals(expectedResponse, actualResponseWriter.toString());
        }
    }

    @Test
    void testServletMaxValueAllowed() throws IOException, InterruptedException {
        SumUpServlet servlet = new SumUpServlet();
        List<StringWriter> listActualResponse = new ArrayList<>();
        String expectedResponse = "10000000000";

        listActualResponse.add(createThreadAndAssertValue(servlet, "9999999997"));
        listActualResponse.add(createThreadAndAssertValue(servlet, "1"));
        listActualResponse.add(createThreadAndAssertValue(servlet, "2"));
        //endSignal
        listActualResponse.add(endSignalAndAssertValue(servlet));

        //assertions
        for(StringWriter actualResponseWriter : listActualResponse) {
            wait(actualResponseWriter);
            assertEquals(expectedResponse, actualResponseWriter.toString());
        }
    }

    private StringWriter endSignalAndAssertValue(SumUpServlet servlet) throws IOException, InterruptedException {
        Thread.sleep(500);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader("end")));
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
        servlet.doPost(request, response);
        return stringWriter;
    }

    private StringWriter createThreadAndAssertValue(SumUpServlet servlet, String value) throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(value)));
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
        Thread thread = new Thread(() -> servlet.doPost(request, response));
        thread.start();
        return stringWriter;
    }

    private void wait(StringWriter response) throws InterruptedException {
        while(response.toString().equals("")) {
            Thread.sleep(500);
        }
    }
}
