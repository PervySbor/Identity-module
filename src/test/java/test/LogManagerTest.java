package test;

import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

import static org.junit.jupiter.api.Assertions.assertEquals;

//can be improved (parsed&checked values) later
public class LogManagerTest {
    @Test
    public void testCorrectInheritingExceptionLogging()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Level level = Level.CONFIG;
        Class<?> logManagerClass = Class.forName("identity.module.LogManager");
        Method logException = logManagerClass.getDeclaredMethod("logException", Exception.class, Level.class);
        logException.setAccessible(true);
        try {
            try {
                throw new IOException("my IO exception");
            } catch (IOException e) {
                RuntimeException runtimeEx = new RuntimeException("my Runtime exception");
                runtimeEx.initCause(e);
                throw runtimeEx;
            }
        } catch (RuntimeException e) {
            String result = (String) logException.invoke(null, e, level);
            System.out.println(result);
        }
    }

    @Test
    public void testCorrectSingleExceptionLogging()
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        System.out.println("Started single test");
        Level level = Level.CONFIG;
        Class<?> logManagerClass = Class.forName("identity.module.LogManager");
        Method logException = logManagerClass.getDeclaredMethod("logException", Exception.class, Level.class);
        logException.setAccessible(true);
        try {
             throw new IOException("my IO exception");
        } catch (IOException e) {
            String result = (String) logException.invoke(null, e, level);
            System.out.println(result);
        }
    }
}
