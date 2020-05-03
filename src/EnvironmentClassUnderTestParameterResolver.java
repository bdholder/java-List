package org.roundrockisd.stonypoint.test;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Constructor;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver;
import org.roundrockisd.stonypoint.util.List;

/**
 * Reads the environment variable {@code CLASS_UNDER_TEST} and attempts to acquire a no-argument {@code Constructor} for the class
 */
public class EnvironmentClassUnderTestParameterResolver extends TypeBasedParameterResolver<Constructor<? extends List>> {   
    @Override
    public Constructor<? extends List> resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return getConstructor(getClassUnderTestName());
    }

    private static String getClassUnderTestName() {
        return requireNonNull(System.getenv("CLASS_UNDER_TEST"), "Environment variable CLASS_UNDER_TEST undefined");
    }

    private static Constructor<? extends List> getConstructor(String className) {
        try {
            return Class.forName(className)
                .asSubclass(List.class)
                .getConstructor();
        }
        catch (ClassCastException e) {
            throw new RuntimeException(String.format("class %s does not implement %s", className, List.class.getName()), e);
        }
        catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }
}