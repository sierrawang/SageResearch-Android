package org.sagebionetworks.research.domain.inject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

import dagger.MapKey;

/**
 * Defines whether or not an individual injected dependency represents the default value, or
 * a user's value that should override the default.
 */
public enum DependencyInjectionType {
    DEFAULT, OVERRIDE;

    @MapKey
    public @interface DependencyInjectionTypeKey {
        DependencyInjectionType value();
    }

    @Retention(RetentionPolicy.CLASS)
    @Qualifier
    public @interface Default {

    }

    @Retention(RetentionPolicy.CLASS)
    @Qualifier
    public @interface Override {

    }
}
