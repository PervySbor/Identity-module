package test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.List;

public class TestRunner {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(JsonManagerTest.class, LogManagerTest.class, ConfigReaderTest.class, RepositoryTest.class);
        List<Failure> fails = result.getFailures();
        for(Failure fail : fails){
            System.out.println(fail.getMessage());
        }
        System.out.println("Ran " + result.getRunCount() + " test(s)");
        System.out.println("Failed: " + result.getFailureCount());
    }

}
