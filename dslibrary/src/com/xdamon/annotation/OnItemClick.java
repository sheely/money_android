package com.xdamon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ListenerClass(targetType = AdapterView.class, 
                type = OnItemClickListener.class, 
                setter = "setOnItemClickListener")
public @interface OnItemClick {
    int[] value();
}
