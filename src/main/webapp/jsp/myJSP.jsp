<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8" />
<link rel="stylesheet" href="dist/css/bootstrap.min.css" />
<script src="jquery/jquery-3.2.1.min.js"></script>
<script src="dist/js/bootstrap.min.js"></script>
<title>登陆成功</title>
<style type="text/css">
</style>
</head>
<body>
	<div class="left-div col-md-1">
		<ul id="areaList" class="list-group"></ul>
		<ul id="typeList" class="list-group"></ul>
	</div>

	<div class="col-md-11">
		<table class="table table-striped table-bordered table-hover">
			<thead id="thead"></thead>
			<tbody id="tbody"></tbody>
		</table>
	</div>
	<input id="areaParams" type="hidden" value='${areaParams}' />
	<input id="deviceTypes" type="hidden" value='${deviceTypes}' />
</body>
<script type="text/javascript" src="js/sss.js"></script>
</html>
