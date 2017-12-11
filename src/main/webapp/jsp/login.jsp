<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<script src="http://apps.bdimg.com/libs/jquery/2.1.4/jquery.js"></script>
<title>登陆页面</title>
</head>
<body>
	<label for="username">用户名：</label>
	<input type="text" id="username" />
	<br>
	<label for="password">密&nbsp;&nbsp;&nbsp;码：</label>
	<input type="password" id="password" />
	<br>
	<input id="login" type="button" value="登陆" />

	<script>
		$("#login").on("click", function() {
			$.ajax({
				url : "doLogin.html",
				type : "post",
				data : {
					username : $("#username").val(),
					password : $("#password").val()
				},
				success:function(result){
					var data = JSON.parse(result);
					$("body").append("<p>"+result+"</p>");
					if(data.success) {
						window.location.href = "detail.html";
						//window.location.assign("detail.html?mac="+data.mac);
					} else {
						$("body").append("<p><span color='red'>"+data.errorMsg+"123"+"</span></p>");
					}
				}
			});
		});
	</script>
</body>
</html>