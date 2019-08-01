package micronaut.features;

import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ControllersSpec {
    private static EmbeddedServer server;
    private static HttpClient client;

    @BeforeClass
    public static void setupServer() {

        server = ApplicationContext.run(EmbeddedServer.class);

        client = server
                .getApplicationContext()
                .createBean(HttpClient.class, server.getURL());
    }

    @AfterClass
    public static void stopServer() {
        if (client != null) {
            client.stop();
        }
        if (server != null) {
            server.stop();
        }
    }

    @Test
    public void testEngineerResponse() {
        String response = client.toBlocking()
                .retrieve(HttpRequest.GET("/micronaut/ioc"));
        assertTrue("Hello Paul".equals(response));
    }

    @Test
    public void testPathParamResponse() {
        String response = client.toBlocking()
                .retrieve(HttpRequest.GET("/micronaut/user/politrons"));
        assertTrue(response.contains("Paul"));
    }

    @Test
    public void testRxJavaResponse() {
        String response = client.toBlocking()
                .retrieve(HttpRequest.GET("/micronaut/rxJava/politrons"));
        assertTrue(response.contains("HELLO POLITRONS TO REACTIVE WORLD IN MICRONAUT!!!!"));
    }

    @Test
    public void testCompletableFutureResponse() {
        String response = client.toBlocking()
                .retrieve(HttpRequest.GET("/micronaut/completableFuture"));
        assertEquals("HELLO ASYNC JAVA WORLD!!!", response);
    }

    @Test
    public void testAOPResponse() {
        String response = client.toBlocking()
                .retrieve(HttpRequest.GET("/micronaut/aop/POLITRONS"));
        assertEquals("POLITRONS WORKS", response);
    }

    @Test
    public void testAOPResponseError() {
        String response = client.toBlocking()
                .retrieve(HttpRequest.GET("/micronaut/aop/politrons"));
        assertEquals("Argument passed to method must be String in upper case", response);
    }

    /**
     * EmbeddedServer of the micronaut test framework is so cool, that´s is able to find all
     * endpoints of your application and being able to invoke it with the client.
     * So no need to specify any main API, all endpoints of all resources are in Here.
     */
    @Test
    public void testSecondResponse() {
        String response = client.toBlocking()
                .retrieve(HttpRequest.GET("/second"));
        assertEquals("War of the Worlds", response);
    }
}