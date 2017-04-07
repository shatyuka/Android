package me.ghui.v2ex.network.converter;


import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by ghui on 07/04/2017.
 */

public class RetrofitUniversalConverterFactory extends Converter.Factory {

	private HashMap<Class<? extends Annotation>, Converter.Factory> mFactoryClassHashMap = new HashMap<>();

	public void addConverterFactory(Converter.Factory factory, Class<? extends Annotation> clazz) {
		if (factory != null && clazz == null) {
			throw new NullPointerException("Converter.Factory or Class cannot be null");
		}
		mFactoryClassHashMap.put(clazz, factory);
	}

	@Override
	public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
		for (Annotation annotation : annotations) {
			Converter.Factory factory = mFactoryClassHashMap.get(annotation.annotationType());
			if (factory != null) {
				return factory.responseBodyConverter(type, annotations, retrofit);
			}
		}
		return null;
	}

	@Override
	public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
		for (Annotation annotation : methodAnnotations) {
			Converter.Factory factory = mFactoryClassHashMap.get(annotation.annotationType());
			if (factory != null) {
				return factory.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
			}
		}
		return null;
	}
}
