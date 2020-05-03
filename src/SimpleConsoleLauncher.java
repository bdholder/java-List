package org.roundrockisd.stonypoint.test;

import static java.util.Arrays.copyOfRange;

import java.util.Optional;
import org.junit.platform.console.ConsoleLauncher;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

class SimpleConsoleLauncher {
    public static void main(String[] args) {
        ConsoleLauncher.execute(System.out, System.err, args)
            .getTestExecutionSummary()
            .ifPresent(SimpleConsoleLauncher::printSummary);
    }

    private static void printSummary(TestExecutionSummary summary) {
        if (summary.getTotalFailureCount() == 0) {
            return;
        }

        System.out.println("Simplified Failure List");
        for (var failure : summary.getFailures()) {
            var throwable = failure.getException();
            System.out.printf("* %s: %s%n", failure.getTestIdentifier().getDisplayName(), throwable.getMessage());
            for (var element : throwable.getStackTrace()) {
                if (element.getClassName().startsWith("org.roundrockisd.stonypoint")) {
                    System.out.printf("    at %s%n", element);
                }
            }
            System.out.println();
        }
    }
}