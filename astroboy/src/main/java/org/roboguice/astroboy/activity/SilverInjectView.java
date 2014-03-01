package org.roboguice.astroboy.activity;

import org.silver.AnnotatedBy;
import org.silver.Silver;
import roboguice.inject.InjectView;

import java.util.Set;

@Silver
public interface SilverInjectView {
    @AnnotatedBy(InjectView.class)
    Set<Class<?>> getAnnotated();
}
