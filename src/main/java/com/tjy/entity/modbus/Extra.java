package com.tjy.entity.modbus;

public class Extra {
	private int deviceId;
	private String name;

	public Extra(int deviceId, String name) {
		this.deviceId = deviceId;
		this.name = name;
	}

	public int getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(int deviceId) {
		this.deviceId = deviceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + deviceId;
		result = prime * result + name.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Extra extra = (Extra) obj;
		if (deviceId != extra.deviceId || !name.equals(extra.name))
			return false;
		return true;
	}
}
