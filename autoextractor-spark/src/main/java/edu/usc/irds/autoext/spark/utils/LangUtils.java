package edu.usc.irds.autoext.spark.utils;

import scala.reflect.ClassManifestFactory$;
import scala.reflect.ClassTag;

/**
 * Utilities for enhancing Scala and Java interoperability
 */
public class LangUtils {

    /**
     * Gets Scala reflection class Tag from Java Class
     * @param type java Class
     * @param <T> type
     * @return class tag for scala reflections
     */
    public static<T> ClassTag<T> getClassTag(Class<T> type){
        return  ClassManifestFactory$.MODULE$.fromClass(type);

    }
}
