# A Simple Web Server That Can Sum Up Numbers in a Multi-Threaded Environment
by João Carlos (https://www.linkedin.com/in/joaocarlosvale/)

## Description:

It is a simple JAVA HTTP server application that:
* accepts at least 20 simultaneous requests, using a Tomcat web servers as the engine. The arriving requests are
 HTTP POST requests.
* If a request with a number arrives, it keeps the number around and does not respond yet.
* If a request with the keyword "end" (without the quotes) arrives then respond with the sum of all
received numbers to all open requests (e.g. if you request with numbers 4 and 7 and end, all three
requests should get response 11).
*  Requests can arrive in parallel at the same time, the system does not lose any numbers or requests.
*  Expected numbers are without decimal places and the sum of them will not exceed 10 billion.
*  After doing the "end" calculation forget all the numbers and be ready for the repeat cycle of operation
(getting new requests with new numbers, giving out a response on the next end).

## Technologies used:
* Java 8
* Servlets
* Docker
* Maven 

## Commands:

To generate WAR and DOCKER IMAGE:

    mvn clean package

To run (using docker):

    docker run -p 8080:8080 jcarlosvale/sum-up-application:1.0
    
To run tests:

    mvn test

How to test or validate if it is working (using CURL):
Open multiple terminal and execute for example
    
    curl -d 12 http://localhost:8080/   (terminal 1 - will be locked)
    
    curl -d 16 http://localhost:8080/   (terminal 2 - will be locked)

    curl -d 22 http://localhost:8080/   (terminal 3 - will be locked)
    
    curl -d end http://localhost:8080/  (terminal 4 - END SIGNAL)

Validation: 
    
    The application will return the value 50 for each terminal. 
