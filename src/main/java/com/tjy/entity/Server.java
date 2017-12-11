package com.tjy.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.PortableInterceptor.HOLDING;

import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.BatchResults;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.base.KeyedModbusLocator;
import com.serotonin.modbus4j.base.ReadFunctionGroup;
import com.serotonin.modbus4j.exception.ErrorResponseException;
import com.serotonin.modbus4j.exception.ModbusInitException;
import com.serotonin.modbus4j.exception.ModbusTransportException;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersRequest;
import com.serotonin.modbus4j.msg.WriteRegisterRequest;
import com.tjy.entity.modbus.Extra;
import com.tjy.entity.modbus.Results;
import com.tjy.entity.modbus.Results.Result;
import com.tjy.service.MyService;

public class Server implements Runnable {
	private int id;
	private String ip;
	private int port;
	private boolean encapsulated;
	private List<Device> devices;
	private ModbusMaster master;
	private BatchRead<Extra> batchRead;
	private Map<Integer, Result> mValues;

	@Override
	public void run() {
		try {
			while (true) {
				System.out.println("read  read  read  read  read  read  read  read  read  read");
				if (!master.isInitialized()) {
					master.init();
				}
				if (batchRead != null) {
					BatchResults<Extra> batchResults = master.send(batchRead);

					List<KeyedModbusLocator<Extra>> locators;
					Extra extra;

					List<ReadFunctionGroup<Extra>> allFunctionGroup = batchRead.getReadFunctionGroups(master);
					for (int i = 0, groupCount = allFunctionGroup.size(); i < groupCount; i++) {
						locators = allFunctionGroup.get(i).getLocators();
						for (int j = 0, locatorCount = locators.size(); j < locatorCount; j++) {
							extra = locators.get(j).getKey();
							mValues.get(extra.getDeviceId()).put(extra.getName(), batchResults.getIntValue(extra));
						}
					}
					Thread.sleep(5000);
				} else {
					break;
				}
				System.out.println("mValues.size = " + mValues.size());
				for (Result result : mValues.values()) {
					System.out.println("result.getDeviceId = " + result.getDeviceId());
					System.out.println(result.toString());
				}
			}
		} catch (ModbusInitException e) {
			e.printStackTrace();
		} catch (ModbusTransportException e) {
			e.printStackTrace();
		} catch (ErrorResponseException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (master != null) {
				master.destroy();
				System.out.println("master.destroy()");
			}
		}
	}

	public void init(MyService service, Map<String, Results> results) {
		System.out.println("serverId = " + id);
		master = service.createTcpMaster(ip, port, encapsulated);
		System.out.println("ip = " + ip + ", port = " + port);
		System.out.println("devices.size = " + devices.size());
		if (devices != null && !devices.isEmpty()) {
			batchRead = service.createBatchRead(devices);
			Device device;
			Results resultSet;
			Result result;
			final int deviceCount = devices.size();
			mValues = new HashMap<>(deviceCount);
			for (int i = 0; i < deviceCount; i++) {
				device = devices.get(i);
				resultSet = results.get(device.getType());
				if (resultSet == null) {
					resultSet = new Results();
					resultSet.setType(device.getType());
					results.put(device.getType(), resultSet);
					System.out.println("设备类型=" + device.getType());
				}
				result = new Result();
				result.setDeviceId(device.getId());
				resultSet.addResult(result);
				mValues.put(device.getId(), result);
			}
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isEncapsulated() {
		return encapsulated;
	}

	public void setEncapsulated(boolean encapsulated) {
		this.encapsulated = encapsulated;
	}

	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}
}
