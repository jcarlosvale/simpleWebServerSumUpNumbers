package com.fortumo.web;

import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SumUpServletTest {

    @Test
    public void testServlet() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        BufferedReader bufferedReader = new BufferedReader(new StringReader("end"));
        when(request.getReader()).thenReturn(bufferedReader);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new SumUpServlet().doPost(request, response);

        verify(request, atLeast(1)).getReader(); // only if you want to verify username was called...
        writer.flush();
        assertTrue(stringWriter.toString().contains("0"));
    }

    @Test
    public void testServletLock() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        BufferedReader bufferedReader = new BufferedReader(new StringReader("1"));
        when(request.getReader()).thenReturn(bufferedReader);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        new SumUpServlet().doPost(request, response);

        verify(request, atLeast(1)).getReader(); // only if you want to verify username was called...
        writer.flush();
        assertTrue(stringWriter.toString().contains("0"));
    }
}
