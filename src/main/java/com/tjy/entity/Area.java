package com.tjy.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tjy.entity.modbus.Results;
import com.tjy.service.MyService;

public class Area implements Runnable {
	private int id;
	private String name;
	private List<Server> servers;
	private Map<String, Results> resultSet = new HashMap<>();

	public void init(MyService service) {
		System.out.println("areaId = " + id);
		System.out.println("servers.size = " + servers.size());
		if (servers != null && !servers.isEmpty()) {
			Server server;
			for (int i = 0, length = servers.size(); i < length; i++) {
				server = servers.get(i);
				server.init(service, resultSet);
			}
		}
	}

	@Override
	public void run() {
		if (servers != null && !servers.isEmpty()) {
			for (int i = 0, length = servers.size(); i < length; i++) {
				servers.get(i).run();
			}
		}
	}

	public Collection<Results> findAllResult() {
		return resultSet.values();
	}

	public Results findResultByType(String type) {
		return resultSet.get(type);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Server> getServers() {
		return servers;
	}

	public void setServers(List<Server> servers) {
		this.servers = servers;
	}

	public Map<String, Results> getResultSet() {
		return resultSet;
	}
}
