package com.tjy.jackson;

import java.lang.annotation.Annotation;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

public class JsonReturnHandler implements HandlerMethodReturnValueHandler {
	
	@Override
	public void handleReturnValue(Object obj, MethodParameter methodParameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest) throws Exception {
		mavContainer.setRequestHandled(true);
		HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
		CustomJsonSerializer jsonSerializer = new CustomJsonSerializer();
		Annotation[] annotations = methodParameter.getMethodAnnotations();
		for (Annotation a : annotations) {
			if (a instanceof JSON) {
				JSON json = (JSON) a;
				jsonSerializer.filter(json);
			} else if (a instanceof JSONS) {
				JSONS jsons = (JSONS) a;
				for (JSON json : jsons.value()) {
					jsonSerializer.filter(json);
				}
			}
		}
		response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		String json = jsonSerializer.toJson(obj);
		response.getWriter().write(json);
	}

	@Override
	public boolean supportsReturnType(MethodParameter parameter) {
		return parameter.hasMethodAnnotation(JSONS.class) || parameter.hasMethodAnnotation(JSON.class);
	}
}
