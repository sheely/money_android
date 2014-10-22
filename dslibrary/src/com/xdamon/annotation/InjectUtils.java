package com.xdamon.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.SystemClock;
import android.view.View;

import com.xdamon.util.DSLog;

//ProGuard：-keep class * extends java.lang.annotation.Annotation { *; }
public class InjectUtils {

    public static final String INJECT_RANGE_METHOD_NAME = "isInjectSelf";
    public static final String ANDROID_PREFIX = "android.";
    public static final String JAVA_PREFIX = "java.";
    public static final String JAVAX_PREFIX = "javax.";
    public static final ArrayList<String> wrongPackageList = new ArrayList<String>();

    static {
        wrongPackageList.add("com.xdamon.");
    }
    
    public static void addExcludePackage(String packagename){
        if(!wrongPackageList.contains(packagename)){
            wrongPackageList.add(packagename);
        }
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

            @Override
            public Context getContext(Object source) {
                return ((View) source).getContext();
            }

        },
        ACTIVITY {

            @Override
            public View findViewById(Object source, int id) {
                return ((Activity) source).findViewById(id);
            }

            @Override
            public Context getContext(Object source) {
                return ((Activity) source);
            }

        },
        DIALOG {

            @Override
            public View findViewById(Object source, int id) {
                return ((Dialog) source).findViewById(id);
            }

            @Override
            public Context getContext(Object source) {
                return ((Dialog) source).getContext();
            }

        };
        public abstract View findViewById(Object source, int id);

        public abstract Context getContext(Object source);
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
        if (isInjectSelf(target)) {
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
                        while (true) {
                            if (field.getType().isArray()) {
                                DSLog.e(TAG, "annotation InjectView can not apply to array ["
                                        + field.getName() + "]");
                                break;
                            }
                            if (!isMatch(field, View.class)) {
                                break;
                            }
                            InjectView injectView = field.getAnnotation(InjectView.class);
                            View view = finder.findViewById(source, injectView.value());
                            if (view != null) {
                                try {
                                    field.setAccessible(true);
                                    field.set(target, view);
                                } catch (Exception e) {
                                    throw new RuntimeException("unable to inject view for "
                                            + field.getName(), e);
                                }
                            }
                            break;
                        }
                    }

                    // inject views
                    if (field.isAnnotationPresent(InjectViews.class)) {
                        while (true) {
                            if (!field.getType().isArray()) {
                                DSLog.e(TAG, "annotation InjectViews must be apply to array , ["
                                        + field.getName() + " is not]");
                                break;
                            }
                            if (!isMatch(field, View.class)) {
                                break;
                            }
                            InjectViews injectViews = field.getAnnotation(InjectViews.class);
                            int[] ids = injectViews.value();
                            Object views = Array.newInstance(field.getType().getComponentType(),
                                ids.length);
                            int index = 0;
                            try {
                                for (int id : ids) {
                                    Array.set(views, index++, finder.findViewById(source, id));
                                }
                                field.setAccessible(true);
                                field.set(target, views);
                            } catch (Exception e) {
                                throw new RuntimeException("unable to inject views for "
                                        + field.getName(), e);
                            }
                            break;
                        }
                    }

                    // inject resource
                    if (field.isAnnotationPresent(InjectResource.class)) {
                        while (true) {
                            if (field.getType().isArray()) {
                                DSLog.e(TAG, "annotation InjectResource can not apply to array ["
                                        + field.getName() + "]");
                                break;
                            }
                            InjectResource injectRes = field.getAnnotation(InjectResource.class);
                            Object res = ResLoader.loadRes(finder.getContext(source),
                                injectRes.type(), injectRes.id());
                            if (res.getClass().isArray()) {
                                DSLog.e(TAG,
                                    "resource value is array can not apply to annotation InjectResource");
                                break;
                            }
                            if (!isMatch(field, res.getClass())) {
                                break;
                            }
                            try {
                                field.setAccessible(true);
                                field.set(target, res);
                            } catch (Exception e) {
                                throw new RuntimeException("unable to inject resource for "
                                        + field.getName(), e);
                            }
                            break;
                        }
                    }

                    // inject resources
                    if (field.isAnnotationPresent(InjectResources.class)) {
                        while (true) {
                            if (!field.getType().isArray()) {
                                DSLog.e(
                                    TAG,
                                    "annotation InjectResources must be apply to array , ["
                                            + field.getName() + " is not]");
                                break;
                            }
                            if (!isMatch(field, View.class)) {
                                break;
                            }
                            InjectResources injectResources = field.getAnnotation(InjectResources.class);
                            int[] ids = injectResources.ids();
                            Object ress = Array.newInstance(field.getType().getComponentType(),
                                ids.length);
                            int index = 0;
                            try {
                                for (int id : ids) {
                                    Array.set(ress, index++, ResLoader.loadRes(
                                        finder.getContext(source), injectResources.type(), id));
                                }
                                field.setAccessible(true);
                                field.set(target, ress);
                            } catch (Exception e) {
                                throw new RuntimeException("unable to inject resources for "
                                        + field.getName(), e);
                            }
                            break;
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
                                        DSLog.e(
                                            TAG,
                                            view.getClass().getSimpleName()
                                                    + " does not contain method ["
                                                    + listenerClass.setter() + "]");
                                        continue;
                                    }
                                    if (!listenerClass.type().isInstance(target)) {
                                        DSLog.e(TAG, "the target [" + target
                                                + "] can not cast to [" + listenerClass.type()
                                                + "]");
                                        continue;
                                    }
                                    Method setMethod = listenerClass.targetType().getDeclaredMethod(
                                        listenerClass.setter(), listenerClass.type());
                                    setMethod.setAccessible(true);
                                    setMethod.invoke(view, target);
                                }
                            } catch (Exception e) {
                                throw new RuntimeException("unable to inject "
                                        + annoType.getSimpleName() + " for " + method.getName(), e);
                            }
                        }
                    }
                }
            }
        }
        DSLog.i(TAG, target.toString() + " inject elapse "
                + (SystemClock.uptimeMillis() - starttime));
    }

    private static boolean isMatch(Field field, Class<?> representedClass) {
        Class<?> fieldType = field.getType().isArray() ? field.getType().getComponentType()
                : field.getType();
        Class<?> representedType = representedClass.isArray() ? representedClass.getComponentType()
                : representedClass;
        if (!representedType.isAssignableFrom(fieldType)) {
            DSLog.e(TAG,
                "the field [" + field.getName() + "] type is [" + fieldType.getSimpleName()
                        + "] can not be converted to " + representedType.getSimpleName());
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
        if (className.startsWith(JAVAX_PREFIX)) {
            DSLog.i(TAG, className + " incorrectly in Javax framework package");
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
            Method method = target.getClass().getMethod(INJECT_RANGE_METHOD_NAME);
            method.setAccessible(true);
            return (Boolean) method.invoke(target);
        } catch (Exception e) {

        }
        return true;
    }

}
