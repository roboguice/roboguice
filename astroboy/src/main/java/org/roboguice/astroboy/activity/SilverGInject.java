package org.roboguice.astroboy.activity;

import com.google.inject.Inject;
import org.silver.AnnotatedBy;
import org.silver.Silver;

import java.util.Set;

@Silver
public interface SilverGInject {
  @AnnotatedBy(Inject.class)
  Set<Class<?>> getAnnotated();
}
