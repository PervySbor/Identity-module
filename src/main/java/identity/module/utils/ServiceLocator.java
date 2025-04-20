package identity.module.utils;


import jakarta.servlet.ServletContext;

@Deprecated
public class ServiceLocator {
    private static ServletContext ctx;

    public static void init(ServletContext context){
        ctx = context;
    }

    public static Object getService(String key){
        if(ctx == null)
            throw new NullPointerException("Context haven't been set yet");
        return ctx.getAttribute(key);
    }

}
