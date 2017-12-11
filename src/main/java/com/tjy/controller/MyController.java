package com.tjy.controller;

import java.util.Collection;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tjy.entity.Area;
import com.tjy.entity.Server;
import com.tjy.entity.modbus.Results;
import com.tjy.jackson.CustomJsonSerializer;
import com.tjy.jackson.JSON;
import com.tjy.jackson.JSONS;
import com.tjy.service.MyService;

@Controller
public class MyController {

	@Resource
	MyService service;

	/*
	 * @RequestMapping("login") public String login() { return
	 * "/WEB-INF/jsp/login.jsp"; }
	 * 
	 * @ResponseBody
	 * 
	 * @RequestMapping(value = "doLogin", method = RequestMethod.POST) public
	 * User doLogin(@RequestParam String username, @RequestParam String
	 * password, HttpServletRequest request) { User user =
	 * service.selectUser(username, password); if (user != null) {
	 * user.setSuccess(true); request.getSession().setAttribute("user", user);
	 * System.out.println("success"); return user; } else { user = new User();
	 * user.setSuccess(false); user.setErrorMsg("用户名或密码错误！！");
	 * System.out.println("faile"); return user; } }
	 */
	@RequestMapping("detail")
	public String detail(HttpServletRequest request) {
		try {
			String areaParams = service.getJsonAsAllArea();
			request.setAttribute("areaParams", areaParams);
			String deviceTypes = service.getJsonAsDeviceParams();
			request.setAttribute("deviceTypes", deviceTypes);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "/jsp/myJSP.jsp";
	}

	@ResponseBody
	@RequestMapping(value = "datas/floor/{floor}", method = RequestMethod.GET)
	public Collection<Results> getData(@PathVariable String floor) {
		System.out.println("floor = " + floor);
		return service.getData(floor);
	}

	@ResponseBody
	@RequestMapping(value = "datas/floor/{floor}/type/{type}", method = RequestMethod.GET)
	public Results getData(@PathVariable String floor, @PathVariable String type) {
		System.out.println("floor = " + floor + ", type = " + type);
		return service.getData(floor, type);
	}

	@JSON(type = Area.class, exclude = "resultSet")
	@JSON(type = Server.class, include = "id,devices")
	@RequestMapping(value = "parameters", method = RequestMethod.GET)
	public Collection<Area> getAllParameters() {
		return service.getAllArea();
	}
}
