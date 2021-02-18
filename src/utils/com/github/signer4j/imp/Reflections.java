package com.github.signer4j.imp;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public final class Reflections {

	private Reflections() {}

	public static <T extends Annotation> boolean isAnnotationPresent(Class<?> clazz, Class<T> annotationType) {
		return getAnnotation(clazz, annotationType) != null;
	}

	public static <T extends Annotation> boolean isAnnotationPresent(Class<?> clazz, Method method, Class<T> annotationType) {
		return getAnnotation(clazz, method, annotationType) != null;
	}

	public static <T extends Annotation> boolean isAnnotationPresent(Object instance, Method method, Class<T> annotationType) {
		return getAnnotation(instance, method, annotationType) != null;
	}

	public static <T extends Annotation> T getAnnotation(Object instance, Method method, Class<T> annotationType) {
		return getAnnotation(instance.getClass(), method, annotationType);
	}

	public static <T extends Annotation> T getAnnotation(Class<?> clazz, Method method, Class<T> annotationType) {
		T a = method.getAnnotation(annotationType);
		if (a != null) {
			return a;
		}
		return getAnnotation(clazz, annotationType, method.getName(), method.getParameterTypes());
	}

	public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationType, String methodName, Class<?>[] parameterTypes) {
		Method m;
		try {
			m = clazz.getMethod(methodName, parameterTypes);
		} catch (Exception e) {
			m = null;
		}
		if (m != null) {
			T a = m.getAnnotation(annotationType);
			if (a != null) {
				return a;
			}
		}
		Class<?> superclass = clazz.getSuperclass();
		if (superclass == null) {
			return null;
		}
		return getAnnotation(superclass, annotationType, methodName, parameterTypes);
	}

	public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationType) {
		T result = clazz.getAnnotation(annotationType);
		if (result == null) {
			Class<?> superclass = clazz.getSuperclass();
			if (superclass != null) {
				return getAnnotation(superclass, annotationType);
			} else {
				return null;
			}
		} else {
			return result;
		}
	}

	public static String signature(Method method, Object[] args) {
		StringBuilder b = new StringBuilder();
		b.append(method.getReturnType().getSimpleName())
		.append(' ')
		.append(method.getName())
		.append('(');
		if (args != null) {
			for(int i = 0; i < args.length; i++) {
				Object o = args[i];
				if (i > 0) {
					b.append(", ");
				}
				Class<?> type = o.getClass();
				if (type.isPrimitive() || Number.class.isInstance(o)) {
					b.append(o.toString());	
				} else {
					b.append("object");
				}			
			}			
		}
		b.append(')');
		return b.toString();		
	}
}
