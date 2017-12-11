package com.tjy.jackson;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

public final class JsonSerializeUtil {
	private static final String INCLUDE = "include";
	private static final String EXCLUDE = "exclude";
	private final ObjectMapper mapper = new ObjectMapper();
	private final SimpleFilterProvider filterProvider = new SimpleFilterProvider();

	@JsonFilter(INCLUDE)
	private interface Include {
	}

	@JsonFilter(EXCLUDE)
	private interface Exclude {
	}

	public void filter(Class<?> clazz, String include, String exclude) {
		if (clazz == null)
			throw new NullPointerException("class is null");
		if (include != null && include.length() > 0) {
			filterProvider.addFilter(INCLUDE, SimpleBeanPropertyFilter.filterOutAllExcept(include.split(",")));
			mapper.addMixIn(clazz, Include.class);
		}
		if (exclude != null && exclude.length() > 0) {
			filterProvider.addFilter(EXCLUDE, SimpleBeanPropertyFilter.serializeAllExcept(exclude.split(",")));
			mapper.addMixIn(clazz, Exclude.class);
		}
	}

	public String toJson(Object obj) throws JsonProcessingException {
		mapper.setFilterProvider(filterProvider);
		return mapper.writeValueAsString(obj);
	}
}
