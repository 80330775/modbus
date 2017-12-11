package com.tjy.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.modbus4j.BatchRead;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.ip.IpParameters;
import com.serotonin.modbus4j.locator.NumericLocator;
import com.tjy.dao.IModbusDao;
import com.tjy.entity.Area;
import com.tjy.entity.Device;
import com.tjy.entity.Server;
import com.tjy.entity.modbus.DeviceParam;
import com.tjy.entity.modbus.DeviceParam.Locator;
import com.tjy.entity.modbus.Extra;
import com.tjy.entity.modbus.Results;
import com.tjy.jackson.CustomJsonSerializer;

@Service
public class MyService {
	@Resource
	IModbusDao dao;

	@Resource
	IpParameters params;

	@Resource
	ModbusFactory factory;

	@Resource
	Map<String, DeviceParam> deviceParams;

	private Map<String, Area> areas;

	private ExecutorService executorService = Executors.newCachedThreadPool();

	@PostConstruct
	public void init() {
		System.out.println("init()!!  init()!!  init()!!  init()!!  init()!!  init()!!  init()!!  ");
		List<Area> areaList = dao.selectAllRecord();
		System.out.println("areas.size = " + areaList.size());
		if (areaList != null && !areaList.isEmpty()) {
			Area area;
			final int areaCount = areaList.size();
			areas = new HashMap<>(areaCount);
			for (int i = 0; i < areaCount; i++) {
				area = areaList.get(i);
				areas.put(area.getName(), area);
				System.out.println("区域名=" + area.getName());
				area.init(this);
				// executorService.execute(area);
			}
		}
	}

	public Collection<Results> getData(String floor) {
		Area area = areas.get(floor);
		if (area != null) {
			return area.findAllResult();
		}
		return null;
	}

	public Results getData(String floor, String type) {
		Area area = areas.get(floor);
		if (area != null) {
			return area.findResultByType(type);
		}
		return null;
	}

	public Collection<Area> getAllArea() {
		return areas.values();
	}

	public String getJsonAsAllArea() throws JsonProcessingException {
		Collection<Area> areaList = areas.values();
		CustomJsonSerializer serializer = new CustomJsonSerializer();
		serializer.filter(Area.class, null, "resultSet");
		serializer.filter(Server.class, "id,devices", null);
		return serializer.toJson(areaList);
	}

	public String getJsonAsDeviceParams() throws JsonProcessingException {
		Collection<DeviceParam> params = deviceParams.values();
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(params);
	}

	public BatchRead<Extra> createBatchRead(List<Device> devices) {
		Device device;
		DeviceParam deviceParam;
		List<Locator> locators;
		Locator locator;
		Extra extra;

		BatchRead<Extra> batch = new BatchRead<>();
		for (int i = 0, deviceCount = devices.size(); i < deviceCount; i++) {
			device = devices.get(i);
			deviceParam = deviceParams.get(device.getType());
			if (deviceParam != null) {
				locators = deviceParam.getLocators();
				for (int j = 0, locatorCount = locators.size(); j < locatorCount; j++) {
					locator = locators.get(j);
					extra = new Extra(device.getId(), locator.getName());
					batch.addLocator(extra, new NumericLocator(device.getSlaveId(), locator.getRange(),
							locator.getOffset(), locator.getDataType()));
				}
			}
		}
		return batch;
	}

	public ModbusMaster createTcpMaster(String ip, int port, boolean encapsulated) {
		params.setHost(ip);
		params.setPort(port);
		params.setEncapsulated(encapsulated);
		return factory.createTcpMaster(params, true);
	}
}
