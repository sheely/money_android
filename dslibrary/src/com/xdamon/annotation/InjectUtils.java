package com.xdamon.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.Dialog;
import android.os.SystemClock;
import android.view.View;

import com.xdamon.util.DSLog;

//ProGuard：-keep class * extends java.lang.annotation.Annotation { *; }
public class InjectUtils {

    public static final String INJECT_RANGE_METHOD_NAME = "isInjectSelf";
    public static final String ANDROID_PREFIX = "android.";
    public static final String JAVA_PREFIX = "java.";
    public static final ArrayList<String> wrongPackageList = new ArrayList<String>();

    static {
        wrongPackageList.add("com.xdamon.");
    }

    private final static String TAG = InjectUtils.class.getSimpleName();

    private InjectUtils() {

    }

    public enum Finder {
        VIEW {

            @Override
            public View findViewById(Object source, int id) {
                return ((View) source).findViewById(id);
            }

        },
        ACTIVITY {

            @Override
            public View findViewById(Object source, int id) {
                return ((Activity) source).findViewById(id);
            }

        },
        DIALOG {

            @Override
            public View findViewById(Object source, int id) {
                return ((Dialog) source).findViewById(id);
            }

        };
        public abstract View findViewById(Object source, int id);
    }

    /**
     * 
     * @param target
     *            Target activity for injection.
     */
    public static void inject(Activity target) {
        inject(target, target, Finder.ACTIVITY);
    }

    /**
     * 
     * @param target
     *            Target class for injection.
     * @param source
     *            Activity on which IDs will be looked up.
     */
    public static void inject(Object target, Activity source) {
        inject(target, source, Finder.ACTIVITY);
    }

    /**
     * 
     * @param target
     *            Target view for injection.
     */
    public static void inject(View target) {
        inject(target, target, Finder.VIEW);
    }

    /**
     * 
     * @param target
     *            target class for injection.
     * @param source
     *            view on which IDs will be looked up.
     */
    public static void inject(Object target, View source) {
        inject(target, source, Finder.VIEW);
    }

    /**
     * 
     * @param target
     *            target dialog for injection.
     */
    public static void inject(Dialog target) {
        inject(target, target, Finder.DIALOG);
    }

    /**
     * 
     * @param target
     *            target class for injection.
     * @param source
     *            dialog on which IDs will be looked up.
     */
    public static void inject(Object target, Dialog source) {
        inject(target, source, Finder.DIALOG);
    }

    private static void inject(Object target, Object source, Finder finder) {
        long starttime = SystemClock.uptimeMillis();

        ArrayList<Class<?>> targetClassList = new ArrayList<Class<?>>();
        Class<?> targetClass = target.getClass();
        if (isInjectSelf(targetClass)) {
            targetClassList.add(targetClass);
        } else {
            while (!isInWrongPackage(targetClass.getName())) {
                targetClassList.add(targetClass);
                targetClass = targetClass.getSuperclass();
            }
        }
        Collections.reverse(targetClassList);

        for (Class<?> injectClass : targetClassList) {

            // inject fields
            Field[] fields = injectClass.getDeclaredFields();
            if (null != fields && fields.length > 0) {
                for (Field field : fields) {

                    // inject view
                    if (field.isAnnotationPresent(InjectView.class)) {
                        if (!isAssignableFromView(field)) {
                            continue;
                        }
                        InjectView injectView = field.getAnnotation(InjectView.class);
                        View view = finder.findViewById(source, injectView.value());
                        if (view != null) {
                            try {
                                field.setAccessible(true);
                                field.set(target, view);
                            } catch (Exception e) {
                                throw new RuntimeException("unable to inject view for " + target, e);
                            }
                        }

                    }

                    // inject views
                    if (field.isAnnotationPresent(InjectViews.class)) {
                        if (!isAssignableFromView(field)) {
                            continue;
                        }
                        InjectViews injectViews = field.getAnnotation(InjectViews.class);
                        int[] ids = injectViews.value();
                        Object views = Array.newInstance(field.getType().getComponentType(),
                            ids.length);
                        int index = 0;
                        for (int id : ids) {
                            Array.set(views, index++, finder.findViewById(source, id));
                        }
                        try {
                            field.setAccessible(true);
                            field.set(target, views);
                        } catch (Exception e) {
                            throw new RuntimeException("unable to inject views for " + target, e);
                        }
                    }
                }
            }

            // inject methods
            Method[] methods = injectClass.getDeclaredMethods();
            if (methods != null && methods.length > 0) {
                for (Method method : methods) {
                    Annotation[] annotations = method.getAnnotations();
                    if (annotations != null && annotations.length > 0) {
                        for (Annotation annotation : annotations) {
                            Class<?> annoType = annotation.annotationType();
                            if (!annoType.isAnnotationPresent(ListenerClass.class)) {
                                continue;
                            }
                            method.setAccessible(true);

                            ListenerClass listenerClass = annoType.getAnnotation(ListenerClass.class);
                            try {
                                Method valueMethod = annoType.getDeclaredMethod("value");
                                Object values = valueMethod.invoke(annotation);
                                int valueLen = Array.getLength(values);
                                for (int i = 0; i < valueLen; i++) {
                                    View view = finder.findViewById(source, Array.getInt(values, i));
                                    if (view == null) {
                                        continue;
                                    }
                                    if (!listenerClass.targetType().isAssignableFrom(
                                        view.getClass())) {
                                        DSLog.w(
                                            TAG,
                                            view.getClass().getSimpleName()
                                                    + " does not contain method ["
                                                    + listenerClass.setter() + "]");
                                        continue;
                                    }
                                    if (!listenerClass.type().isInstance(target)) {
                                        DSLog.w(TAG, "the target [" + target
                                                + "] can not cast to [" + listenerClass.type()
                                                + "]");
                                        continue;
                                    }
                                    Method setMethod = listenerClass.targetType().getDeclaredMethod(
                                        listenerClass.setter(), listenerClass.type());
                                    setMethod.invoke(view, target);
                                }
                            } catch (Exception e) {
                                throw new RuntimeException("unable to inject "
                                        + annoType.getSimpleName() + " for " + target, e);
                            }
                        }
                    }
                }
            }
        }
        DSLog.i(TAG, target.toString() + " inject elapse "
                + (SystemClock.uptimeMillis() - starttime));
    }

    private static boolean isAssignableFromView(Field field) {
        Class<?> fieldType = field.getType().isArray() ? field.getType().getComponentType()
                : field.getType();
        if (!View.class.isAssignableFrom(fieldType)) {
            DSLog.w(TAG, "the type of field [" + field.getName() + " ] is not view or views");
            return false;
        }
        return true;
    }

    private static boolean isInWrongPackage(String className) {
        if (className.startsWith(ANDROID_PREFIX)) {
            DSLog.i(TAG, className + " incorrectly in Android framework package");
            return true;
        }
        if (className.startsWith(JAVA_PREFIX)) {
            DSLog.i(TAG, className + " incorrectly in Java framework package");
            return true;
        }
        for (String wrongName : wrongPackageList) {
            if (className.startsWith(wrongName)) {
                DSLog.i(TAG, className + " incorrectly in " + wrongName + " package");
                return true;
            }
        }
        return false;
    }

    private static boolean isInjectSelf(Object target) {
        try {
            Method method = target.getClass().getDeclaredMethod(INJECT_RANGE_METHOD_NAME);
            return (Boolean) method.invoke(target);
        } catch (Exception e) {

        }
        return true;
    }

}
