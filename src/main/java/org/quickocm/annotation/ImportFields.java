package org.quickocm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that clubs multiple <code>ImportField</code> s on a single object in a class being constructed from csv file row.
 * This is to be used when a single object has multiple fields that need to be mapped to different fileds in the file.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImportFields {
  ImportField[] importFields();
}
