$(function() {
	var areaParamsStr = $("#areaParams").val();
	var deviceTypesStr = $("#deviceTypes").val();

	$("body").append(areaParamsStr + "<br>" + deviceTypesStr);

	var currentArea;
	var currentType;

	var $areaList = $("#areaList"),
		$typeList = $("#typeList"),
		$thead = $("#thead"),
		$tbody = $("#tbody");

	/**
	 *  设置区域参数
	 */
	function createAreas(areaParams) {
		var html = "";
		for (var i = 0; i < areaParams.length; i++) {
			var param = areaParams[i];
			if (i === 0) {
				html += '<li id=' + param.name + ' class="active list-group-item">' + param.name + "</li>";
				currentArea = param.name;
			} else {
				html += '<li id=' + param.name + ' class="list-group-item">' + param.name + "</li>";
			}
		}

		var areaEleArray = $.parseHTML(html, document);
		for (var i = 0; i < areaEleArray.length; i++) {
			var servers = areaParams[i].servers,
				deviceArray = [];
			for (var j = 0; j < servers.length; j++) {
				var server = servers[j],
					devices = server.devices;
				for (var k = 0; k < devices.length; k++) {
					var device = devices[k];
					device.serverId = server.id;
				}
				deviceArray = deviceArray.concat(devices);
			}
			$.data(areaEleArray[i], "id", param.id);
			createDevices(areaEleArray[i], deviceArray);
		}
		$areaList.append(areaEleArray);
	}

	function createDevices(areaEle, deviceArray) {
		var device,
			type,
			html = "",
			deviceEleArray;
		for (var i = 0; i < deviceArray.length; i++) {
			device = deviceArray[i];
			html += "<tr id=" + device.id + "><td>" + device.id + "</td><td>" + device.room + "</td><td>"
				+ device.serverId + "</td><span></span></tr>";
		}
		deviceEleArray = $.parseHTML(html, document);
		for (var i = 0; i < deviceEleArray.length; i++) {
			device = deviceArray[i];
			type = device.type.toUpperCase();
			$.data(deviceEleArray[i], "slaveId", device.slaveId);
			$.data(deviceEleArray[i], "type", type);
			var devices = $.data(areaEle, type);
			if (!devices) {
				devices = [];
				$.data(areaEle, type, devices);
			}
			devices.push(deviceEleArray[i]);
		}
	}

	/**
	 *  设置类型参数
	 */
	function createTypes(typeParams) {
		var html = "",
			param;
		for (var i = 0; i < typeParams.length; i++) {
			param = typeParams[i];
			if (i === 0) {
				html += "<li id=" + param.type + ' class="active list-group-item">' + param.type + "</li>";
				currentType = param.type;
			} else {
				html += "<li id=" + param.type + ' class="list-group-item">' + param.type + "</li>";
			}
		}
		var typeEleArray = $.parseHTML(html, document);
		for (var i = 0; i < typeEleArray.length; i++) {
			param = typeParams[i];
			var head = createHead(param.locators);
			$.data(typeEleArray[i], "head", head);
		}
		$typeList.append(typeEleArray);
	}


	function createHead(locators) {
		var html = "",
			locator;
		for (var i = 0; i < locators.length; i++) {
			locator = locators[i];
			html += "<th id=" + locator.name + ">" + locator.name + "</th>";
		}
		var locatorEleArray = $.parseHTML(html, document);
		for (var i = 0; i < locatorEleArray.length; i++) {
			locator = locators[i];
			$.data(locatorEleArray[i], "range", locator.range);
			$.data(locatorEleArray[i], "offset", locator.offset);
			$.data(locatorEleArray[i], "decimal", locator.decimal);
		}
		return $("<tr><th>设备ID</th><th>房间</th><th>网关ID</th></tr>").append(locatorEleArray)[0];
	}

	$areaList.on("click", function(event) {
		var target = event.target;
		if (target !== this) {
			if (target.innerHTML !== currentArea) {
				$areaList.find("#" + currentArea).removeClass("active");
				currentArea = target.innerHTML;
				$areaList.find("#" + currentArea).addClass("active");
				$tbody.children().detach();
				var devices = $.data(target, currentType);
				if (devices) {
					$tbody.append(devices);
				}
			}
		}
	});


	$typeList.on("click", function(event) {
		var target = event.target;
		if (target !== this) {
			if (target.innerHTML !== currentType) {
				$typeList.find("#" + currentType).removeClass("active");
				currentType = target.innerHTML;
				$typeList.find("#" + currentType).addClass("active");
				$thead.children().detach();
				var head = $.data(target, "head");
				$thead.append(head);

				$tbody.children().detach();
				var devices = $.data(document.getElementById(currentArea), currentType);
				if (devices) {
					$tbody.append(devices);
				}
			}
		}
	})

	var areaObj = JSON.parse(areaParamsStr);
	areaObj.sort(function(a, b) {
		return a.id - b.id;
	});
	createAreas(areaObj);
	createTypes(JSON.parse(deviceTypesStr));
	$thead.append($.data(document.getElementById(currentType), "head"));
	$tbody.append($.data(document.getElementById(currentArea), currentType));
})