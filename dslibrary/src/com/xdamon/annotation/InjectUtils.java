package com.xdamon.annotation;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

public class InjectUtils {

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
	 *            Target activity for field injection.
	 */
	public static void inject(Activity target) {
		inject(target, target, Finder.ACTIVITY);
	}

	/**
	 * 
	 * @param target
	 *            Target class for field injection.
	 * @param source
	 *            Activity on which IDs will be looked up.
	 */
	public static void inject(Object target, Activity source) {
		inject(target, source, Finder.ACTIVITY);
	}

	/**
	 * 
	 * @param target
	 *            Target view for field injection.
	 */
	public static void inject(View target) {
		inject(target, target, Finder.VIEW);
	}

	/**
	 * 
	 * @param target
	 *            target class for field injection.
	 * @param source
	 *            view on which IDs will be looked up.
	 */
	public static void inject(Object target, View source) {
		inject(target, source, Finder.VIEW);
	}

	/**
	 * 
	 * @param target
	 *            target dialog for field injection.
	 */
	public static void inject(Dialog target) {
		inject(target, target, Finder.DIALOG);
	}

	/**
	 * 
	 * @param target
	 *            target class for field injection.
	 * @param source
	 *            dialog on which IDs will be looked up.
	 */
	public static void inject(Object target, Dialog source) {
		inject(target, source, Finder.DIALOG);
	}

	private static void inject(Object target, Object source, Finder finder) {
		Class<?> targetClass = target.getClass();

		// inject fields
		Field[] fields = targetClass.getDeclaredFields();
		if (null != fields && fields.length > 0) {
			for (Field field : fields) {

				// inject view
				InjectView injectView = field.getAnnotation(InjectView.class);
				if (injectView != null) {
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
				InjectViews injectViews = field.getAnnotation(InjectViews.class);
				if (injectViews != null) {
					int[] ids = injectViews.value();
					Object views = Array.newInstance(field.getType().getComponentType(), ids.length);
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

	}
}
