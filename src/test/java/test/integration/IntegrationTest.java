package test.integration;

import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;
import org.junit.*;
import org.apache.catalina.startup.Tomcat;
import test.utils.JsonChecker;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

        WebappLoader webappLoader = new WebappLoader(/*Thread.currentThread().getContextClassLoader()*/);
        webappLoader.setDelegate(true);  // аналог <Loader delegate="true"/>
        ctx.setLoader(webappLoader);


        tomcat.start();
        System.out.println("running");
    }

    @Ignore
    @Test
    public void testNothing(){}


    @Test
    public void testPing() throws URISyntaxException, IOException, InterruptedException {
        System.out.println("testPing:");
        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(""))
                .version(HttpClient.Version.HTTP_1_1).headers("Content-Type", "application/json", "X-Forwarded-For", "127.0.0.1")
                .uri(new URI("http://localhost:8080/myApp/ping")).build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> receivedResponse = client.send(request, handler);

        String result = receivedResponse.body();

        System.out.println(result);

        assertEquals("{\"error\":\"{\\\"status\\\":418,\\\"statusText\\\":\\\"I'm a teapot\\\",\\\"message\\\":\\\"Don't bother me\\\"}\"}", result);

        client.close();
    }

    @Test
    public void testRegisterAdmin_ifValidLogin() throws URISyntaxException, IOException, InterruptedException {
        System.out.println("testRegisterAdmin_ifValidLogin:");
        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString("{\"login\":\"myLogin\",\"password\":\"password123\"}"))
                .version(HttpClient.Version.HTTP_1_1).headers("Content-Type", "application/json", "X-Forwarded-For", "127.0.0.1")
                .uri(new URI("http://localhost:8080/myApp/admin/register")).build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> receivedResponse = client.send(request, handler);

        String result = receivedResponse.body();

        System.out.println(result);

        assertEquals("{\"response\":\"{\\\"status\\\":200,\\\"statusText\\\":\\\"Ok\\\",\\\"message\\\":\\\"Successfully created admin account\\\"}\"}", result);

        client.close();
    }

    @Test
    public void testRegisterAdmin_ifRepeatedLogin() throws URISyntaxException, IOException, InterruptedException {
        System.out.println("testRegisterAdmin_ifRepeatedLogin:");
        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString("{\"login\":\"myLogin\",\"password\":\"password123\"}"))
                .version(HttpClient.Version.HTTP_1_1).headers("Content-Type", "application/json", "X-Forwarded-For", "127.0.0.1")
                .uri(new URI("http://localhost:8080/myApp/admin/register")).build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> receivedResponse = client.send(request, handler);

        String result = receivedResponse.body();

        System.out.println("result of registering new user: " + result);

        receivedResponse = client.send(request, handler);

        result = receivedResponse.body();

        System.out.println("result of trying to register with taken login: " + result);

        assertEquals("{\"error\":\"{\\\"status\\\":409,\\\"statusText\\\":\\\"Conflict\\\",\\\"message\\\":\\\"Login is already taken\\\"}\"}", result);

        //assertEquals("{\"status\":418,\"statusText\":\"I'm a teapot\",\"message\":\"Don't bother me\"}", result);

        client.close();
    }

    @Test
    public void testRegisterUser_ifValidLogin() throws URISyntaxException, IOException, InterruptedException {
        System.out.println("testRegisterUser_ifValidLogin:");
        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString("{\"login\":\"myLogin\",\"password\":\"password123\"}"))
                .version(HttpClient.Version.HTTP_1_1).headers("Content-Type", "application/json", "X-Forwarded-For", "127.0.0.1")
                .uri(new URI("http://localhost:8080/myApp/user/register")).build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> receivedResponse = client.send(request, handler);

        String result = receivedResponse.body();

        System.out.println(result);

        assertTrue(JsonChecker.fieldExists("jwt", result));
        assertTrue(JsonChecker.fieldExists("jwt", result));

        client.close();
    }


    @Test
    public void testLogin_ifRegistered() throws URISyntaxException, IOException, InterruptedException {
        System.out.println("testLogin_ifRegistered: (called testRegisterAdmin_ifValidLogin inside)");
        testRegisterAdmin_ifValidLogin();
        System.out.println("testing testRegisterAdmin_ifRepeatedLogin (actually):");
        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString("{\"login\":\"myLogin\",\"password\":\"password123\"}"))
                .version(HttpClient.Version.HTTP_1_1).headers("Content-Type", "application/json", "X-Forwarded-For", "127.0.0.1")
                .uri(new URI("http://localhost:8080/myApp/admin/login"))
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();

        HttpResponse<String> receivedResponse = client.send(request, handler);

        String result = receivedResponse.body();

        System.out.println(result);

        assertTrue(JsonChecker.fieldExists("jwt", result));

        client.close();
    }

    @Test
    public void testRefresh() throws URISyntaxException, IOException, InterruptedException {
        System.out.println("testRefresh:");
        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest loginRequest = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString("{\"login\":\"myLogin\",\"password\":\"password123\"}"))
                .version(HttpClient.Version.HTTP_1_1).headers("Content-Type", "application/json", "X-Forwarded-For", "127.0.0.1")
                .uri(new URI("http://localhost:8080/myApp/user/register")).build();


        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> receivedResponse = client.send(loginRequest, handler);

        String json = receivedResponse.body();
        String refreshToken = JsonChecker.get("refresh", json);



        HttpRequest refreshRequest = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString("{\"refresh\":\""+ refreshToken + "\"}"))
                .version(HttpClient.Version.HTTP_1_1).headers("Content-Type", "application/json", "X-Forwarded-For", "127.0.0.1")
                .uri(new URI("http://localhost:8080/myApp/refresh"))
                .build();

        receivedResponse = client.send(refreshRequest, handler);
        String result = receivedResponse.body();

        assertTrue(JsonChecker.fieldExists("jwt", result));

        System.out.println(result);

        client.close();
    }

    @After
    public void repeatingCleaning() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method execUDQuery = Class.forName("identity.module.interfaces.DAO")
                .getMethod("executeUDQuery", String.class, Map.class);
        Map<String, String> props = new HashMap<>();
        execUDQuery.invoke(null, "DELETE FROM Session", props);
        execUDQuery.invoke(null, "DELETE FROM Subscription", props);
        execUDQuery.invoke(null, "DELETE FROM User", props);
    }

    @AfterClass
    public static void cleaning() throws LifecycleException{
        System.out.println("started cleaning");
        //tomcat.getServer().await();

        tomcat.stop();
        System.out.println("successfully shut down");
    }
}
