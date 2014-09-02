package com.xdamon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.widget.RadioGroup;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ListenerClass(targetType = RadioGroup.class,
                type = RadioGroup.OnCheckedChangeListener.class,
                setter = "setOnCheckedChangeListener")
public @interface OnRadioGroupCheckedChange {
    int[] value();
}
