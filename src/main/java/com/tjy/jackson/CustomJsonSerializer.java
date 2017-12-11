package com.tjy.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomJsonSerializer {
	private ObjectMapper mapper = new ObjectMapper();
	private MyFilterProvider filterProvider = new MyFilterProvider();

	// filterProvider是根据getXXX方法来决定过滤规则的，而不是字段名
	// 注意class不要导错包
	public void filter(Class<?> clazz, String include, String exclude) {
		if (clazz == null)
			return;
		if (include != null && include.length() > 0)
			filterProvider.include(clazz, include.split(","));
		if (exclude != null && exclude.length() > 0)
			filterProvider.exclude(clazz, exclude.split(","));
		mapper.addMixIn(clazz, MyFilterProvider.class);
	}

	public String toJson(Object obj) throws JsonProcessingException {
		mapper.setFilterProvider(filterProvider);
		return mapper.writeValueAsString(obj);
	}

	public void filter(JSON json) {
		filter(json.type(), json.include(), json.exclude());
	}
}
