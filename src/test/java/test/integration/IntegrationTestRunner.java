package test.integration;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import java.util.List;

public class IntegrationTestRunner {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(IntegrationTest.class);

        System.out.println("Ran " + result.getRunCount() + " tests");
        System.out.println("Failed: " + result.getFailureCount());

        List<Failure> fails = result.getFailures();

        for(Failure fail : fails){
            System.out.println(fail.getTestHeader());
            System.out.println(fail.getMessage());
        }
    }
}
