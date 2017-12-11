package com.tjy.entity.modbus;

import java.util.List;

import com.tjy.entity.Device;

public class Table {
	private List<TableHead> head;
	private List<TableBody> body;

	public List<TableHead> getHead() {
		return head;
	}

	public void setHead(List<TableHead> head) {
		this.head = head;
	}

	public List<TableBody> getBody() {
		return body;
	}

	public void setBody(List<TableBody> body) {
		this.body = body;
	}

	public static class TableHead {
		private String type;
		private List<String> locatorNames;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public List<String> getLocatorNames() {
			return locatorNames;
		}

		public void setLocatorNames(List<String> locatorNames) {
			this.locatorNames = locatorNames;
		}
	}

	public static class TableBody {
		private int floor;
		private List<Device> devices;

		public int getFloor() {
			return floor;
		}

		public void setFloor(int floor) {
			this.floor = floor;
		}

		public List<Device> getDevices() {
			return devices;
		}

		public void setDevices(List<Device> devices) {
			this.devices = devices;
		}
	}
}
