package com.tjy.entity.modbus;

import java.util.List;

public class DeviceParam {
	private String type;
	private List<Locator> locators;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Locator> getLocators() {
		return locators;
	}

	public void setLocators(List<Locator> locators) {
		this.locators = locators;
	}

	public static class Locator {
		private int range;
		private int offset;
		private int dataType;
		private String name;
		private int decimal;

		public int getRange() {
			return range;
		}

		public void setRange(int range) {
			this.range = range;
		}

		public int getOffset() {
			return offset;
		}

		public void setOffset(int offset) {
			this.offset = offset;
		}

		public int getDataType() {
			return dataType;
		}

		public void setDataType(int dataType) {
			this.dataType = dataType;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getDecimal() {
			return decimal;
		}

		public void setDecimal(int decimal) {
			this.decimal = decimal;
		}
	}
}
