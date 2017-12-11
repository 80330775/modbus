package com.tjy.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.tjy.entity.Area;
import com.tjy.entity.Device;
import com.tjy.entity.Server;

@Repository
public interface IModbusDao {
	List<Server> selectAllServers();

	List<Device> selectDeviceByServerId(@Param("serverId") int serverId);

	List<Device> selectDeviceByFloor(@Param("floor") int floor);

	List<Integer> selectAllFloor();

	List<Area> selectAllRecord();
}
