package com.xdamon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.view.View;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ListenerClass(targetType = View.class,
                type = View.OnFocusChangeListener.class,
                setter = "setOnFocusChangeListener")
public @interface OnFocusChange {
    int[] value();
}
