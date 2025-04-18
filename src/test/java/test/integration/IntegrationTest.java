package test.integration;

import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.apache.catalina.startup.Tomcat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.*;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class IntegrationTest {

    private static final Tomcat tomcat = new Tomcat();

    @BeforeClass
    public static void preparations() throws LifecycleException {
        System.out.println("started preparations");
        tomcat.setPort(8080);
        tomcat.getConnector();
        tomcat.setBaseDir("tomcat-base-dir");

        Host host = tomcat.getHost();
        String webAppLocation = new File("src/main/webapp/").getAbsolutePath();

        Context ctx = tomcat.addWebapp(host, "/myApp", webAppLocation);

        File classesDir = new File("target/classes");
        WebResourceRoot resources = new StandardRoot(ctx);
        resources.addPreResources(new DirResourceSet(
                resources,
                "/WEB-INF/classes",
                classesDir.getAbsolutePath(),
                "/"
        ));
        ctx.setResources(resources);

        tomcat.start();
        System.out.println("running");
    }

    @Ignore
    @Test
    public void testNothing(){}


    @Test
    public void testPing() throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(""))
                .version(HttpClient.Version.HTTP_1_1).headers("Content-Type", "application/json", "X-Forwarded-For", "127.0.0.1")
                .uri(new URI("http://localhost:8080/myApp/ping")).build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> receivedResponse = client.send(request, handler);

        String result = receivedResponse.body();

        System.out.println(result);

        assertEquals("{\"status\":418,\"statusText\":\"I'm a teapot\",\"message\":\"Don't bother me\"}", result);

        client.close();
    }

    @Test
    public void testRegisterAdmin() throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString("{\"login\":\"myLogin\",\"password\":\"password123\"}"))
                .version(HttpClient.Version.HTTP_1_1).headers("Content-Type", "application/json", "X-Forwarded-For", "127.0.0.1")
                .uri(new URI("http://localhost:8080/myApp/admin/register")).build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> receivedResponse = client.send(request, handler);

        String result = receivedResponse.body();

        System.out.println(result);

        //assertEquals("{\"status\":418,\"statusText\":\"I'm a teapot\",\"message\":\"Don't bother me\"}", result);

        client.close();
    }

    @AfterClass
    public static void cleaning() throws LifecycleException {
        System.out.println("started cleaning");
        //tomcat.getServer().await();

        tomcat.stop();
        System.out.println("successfully shut down");
    }
}
