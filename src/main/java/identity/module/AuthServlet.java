package identity.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import identity.module.utils.JsonManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;



import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AuthServlet extends HttpServlet {

    private  AuthorisationService authService;

    @Override
    public void init() throws ServletException {
        super.init();
        authService = (AuthorisationService) getServletContext().getAttribute("AuthorisationService");
        if (authService == null){
            throw new IllegalStateException("failed to fetch AuthorisationService");
        }
    }

    @Override
    public void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse){
        String path = httpRequest.getServletPath();
        switch(path){
            case "/user/login": case "/admin/login": pathHandler(httpRequest, httpResponse, authService::login, List.of("refresh", "jwt", "message")); break;
            case "/user/register": pathHandler(httpRequest, httpResponse, authService::registerUser, List.of("jwt", "refresh", "message")); break;
            case "/admin/register": pathHandler(httpRequest, httpResponse, authService::registerAdmin, List.of("response", "message")); break;
            case "/refresh": pathHandler(httpRequest, httpResponse, authService::refresh, List.of("jwt", "message")); break;
            case "/ping": returnError(httpResponse, 418, "Don't bother me"); break;
            default: returnError(httpResponse, 404, "Incorrect path"); break;

        }
    }

    //wrapper for /refresh
    private void pathHandler(HttpServletRequest httpRequest, HttpServletResponse httpResponse,Function<String,Properties> serviceMethod, List<String> bodyProperties){
        Properties result;
        Map<String,String> body = new HashMap<>();
        if(!httpRequest.getHeader("Content-Type").equals("application/json")){
            returnError(httpResponse,422, "Incorrect content type");
        }else {
            try{
                PrintWriter writer = httpResponse.getWriter();
                String jsonBody = httpRequest.getReader().readLine(); //as the whole json must be on a single line, according to the HTTP/1.1
                result = serviceMethod.apply(jsonBody);
                //special properties
                httpResponse.setHeader("Content-Type","application/json");
                httpResponse.setStatus(Integer.parseInt(result.getProperty("statusCode", "500")));
                //response body formation
                for (String property : bodyProperties) {
                    if (result.getProperty(property) != null) {
                        body.put(property, result.getProperty(property));
                    }
                }
                String bodyString = JsonManager.serialize(body);
                writer.write(bodyString);
            } catch(IOException e){
                httpResponse.setStatus(500);
            }
        }
    }

    //wrapper for /login, /admin/register and /user/register
    private void pathHandler(HttpServletRequest httpRequest, HttpServletResponse httpResponse, BiFunction<String, String, Properties> serviceMethod, List<String> bodyProperties){
        Properties result;
        Map<String,String> body = new HashMap<>();
        String userIp = httpRequest.getHeader("X-Forwarded-For");
        if(!httpRequest.getHeader("Content-Type").equals("application/json")){
            returnError(httpResponse,422, "Incorrect content type");
        } else if(userIp == null){
            returnError(httpResponse,422, "Failed to fetch user ip");
        } else {
            try{
                PrintWriter writer = httpResponse.getWriter();
                String jsonBody = httpRequest.getReader().readLine(); //as the whole json must be on a single line, according to the HTTP/1.1
                result = serviceMethod.apply(jsonBody, userIp);
                //special properties
                httpResponse.setHeader("Content-Type","application/json");
                httpResponse.setStatus(Integer.parseInt(result.getProperty("statusCode", "500")));
                //response body formation
                for (String property : bodyProperties) {
                    if (result.getProperty(property) != null) {
                        body.put(property, result.getProperty(property));
                    }
                }
                String bodyString = JsonManager.serialize(body);
                writer.write(bodyString);
            } catch(IOException e){
                httpResponse.setStatus(500);
            }
        }
    }


    private void returnError(HttpServletResponse httpResponse, int statusCode, String message){
        httpResponse.setHeader("Content-Type","application/json");
        //Properties result = authService.returnError(statusCode, shortErrorMsg, message);
        try {
            PrintWriter writer = httpResponse.getWriter();
            Map<String, String> toBeSerialized = new HashMap<String,String>();
            toBeSerialized.put("message", message);
            String serializedError = JsonManager.serialize(toBeSerialized);
            writer.write(serializedError);
            httpResponse.setStatus(statusCode);
        } catch(IOException e){
            httpResponse.setStatus(500);
        }
    }
}
