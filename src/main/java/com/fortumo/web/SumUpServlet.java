package com.fortumo.web;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(urlPatterns = "/")
public class SumUpServlet extends HttpServlet {

    private static final long serialVersionUID = -2245494110623955028L;

    private static final Logger log = Logger.getLogger(SumUpServlet.class.getName());
    private final AtomicLong sum = new AtomicLong(0);
    private static int currentThreads;
    private boolean releaseRequest;

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        String value = getValueFromRequest(request);
        processRequest(response, value);
    }

    private void processRequest(HttpServletResponse response, String valueFromRequest) {
        try {
            currentThreads++;
            releaseRequest = "end".equals(valueFromRequest);
            long value = 0;
            synchronized (this) {
                while (!releaseRequest) {
                    try {
                        value = Long.parseLong(valueFromRequest);
                    } catch (NumberFormatException numberFormatException) {
                        log.log(Level.SEVERE, "Error parsing as Long: {0}", valueFromRequest);
                    }
                    sum.addAndGet(value);
                    this.wait();
                }
                this.notifyAll();
                response.getWriter().print(sum);
                log.log(Level.INFO, "Returning value:{0}", sum);
                currentThreads--;
                if(currentThreads <= 0) {
                    currentThreads = 0;
                    sum.set(0);
                }
            }
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            log.log(Level.SEVERE, "Error processing request: {0}", exception.getMessage());
        } catch (IOException exception) {
            log.log(Level.SEVERE, "Error writing the response: {0}", exception.getMessage());
        }
    }

    private static String getValueFromRequest(HttpServletRequest request) {
        String value = "0";
        try {
            value = request.getReader().readLine();
            log.log(Level.INFO, "Received value:{0}", value);
        } catch (IOException exception) {
            log.log(Level.SEVERE, "Error parsing received value: {0}", exception.getMessage());
        }
        return value;
    }
}
