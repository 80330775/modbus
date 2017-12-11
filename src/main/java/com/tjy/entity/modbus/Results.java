package com.tjy.entity.modbus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Results {
	private String type;
	private final List<Result> results = new ArrayList<>();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Result> getResults() {
		return results;
	}

	public void addResult(Result result) {
		results.add(result);
	}

	public static class Result {
		private int deviceId;
		private String status;
		private Map<String, Integer> values = new HashMap<>();

		public int getDeviceId() {
			return deviceId;
		}

		public void setDeviceId(int deviceId) {
			this.deviceId = deviceId;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public Map<String, Integer> getValues() {
			return values;
		}

		public void setValues(Map<String, Integer> values) {
			this.values = values;
		}

		public void put(String name, Integer value) {
			values.put(name, value);
		}

		@Override
		public String toString() {
			return values.toString();
		}
	}
}
