<%@ page contentType="text/html;charset=utf-8"%>
<!DOCTYPE html>

<html xmlns="http://www.w3.org/1999/xhtml" lang = "zh-CN">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width,user-scalable=no,initial-scale=1.0" />
<link rel="stylesheet" href="css/404-1.css" /><title>

</title>
	<style style="text/css">
		#footer {
			color: white;
			background-color: #e94917;
			display: block;
			text-align: center;
			padding: 0.5em 1em;
			position: absolute;
			bottom: 0;
			width: 100%;
			z-index: 10;
		}
		.phone {
			font-family:Verdana, Geneva, Tahoma, sans-serif;
		}
	</style>
</head>
<body>
   <div class="img">
    	<img src="pic/qiqiu.png"/>
    </div>
    <p class="error">${device_id} 爱生活，爱摩客！欢迎使用爱摩客。</p>
    <span class="word" style="padding:1em;">本设备已离线，暂不可用，请稍后再试。</span>
	<span id="footer">服务电话: <span class="phone">XXXXXXXXXXXXXXXXXXXX</span></span>
</body>
</html>
