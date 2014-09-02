package com.xdamon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.widget.AdapterView;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ListenerClass(targetType = AdapterView.class,
                type = AdapterView.OnItemLongClickListener.class,
                setter = "setOnItemLongClickListener")
public @interface OnItemLongClick {
    int[] value();
}
