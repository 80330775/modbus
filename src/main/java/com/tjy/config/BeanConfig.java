package com.tjy.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serotonin.modbus4j.ModbusFactory;
import com.serotonin.modbus4j.ip.IpParameters;
import com.tjy.entity.modbus.DeviceParam;

@Configuration
public class BeanConfig {
	@Resource
	Map<String, String> properties;

	@Bean
	public Map<String, DeviceParam> deviceParams() throws IOException {
		Map<String, DeviceParam> devices = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		Set<Entry<String, String>> set = properties.entrySet();
		for (Entry<String, String> entry : set) {
			DeviceParam device = mapper.readValue(entry.getValue(), DeviceParam.class);
			devices.put(entry.getKey(), device);
		}
		return devices;
	}

	@Bean
	public ModbusFactory factory() {
		return new ModbusFactory();
	}

	@Bean
	public IpParameters params() {
		return new IpParameters();
	}
}
