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

		#downtime{
        	align: center;
        }

	</style>
	 <script type="text/javascript">
     var endTime = (new Date()).getTime() + ${duration} * 60 *  1000 +  2000;
     </script>
</head>
<body>
   <div class="img">
    	<img src="pic/qiqiu.png"/>
    </div>
    <p class="error">爱生活，爱摩客！欢迎使用爱摩客。</p>
    <span class="word" style="padding:1em;"> ${device_id} 设备正在运行
        <span id="t_h" >00时</span>
        <span id="t_m" >00分</span>
        <span id="t_s" >00秒</span>
    </span>
    <script type="text/javascript">
    function getRTime(){
    var EndTime= new Date(endTime); //截止时间
    var NowTime = new Date();
    var t = EndTime.getTime() - NowTime.getTime();

   if(t <= 1000) {
   location.href = "finish?device_id=${device_id}&extra=";
   }

    var h=Math.floor(t/1000/60/60%24);
    var m=Math.floor(t/1000/60%60);
    var s=Math.floor(t/1000%60);

    document.getElementById("t_h").innerHTML = h + "时";
    document.getElementById("t_m").innerHTML = m + "分";
    document.getElementById("t_s").innerHTML = s + "秒";
    }
    setInterval(getRTime,1000);
    </script>
   <span id="footer">服务电话: <span class="phone">XXXXXXXXXXXXXXXXX</span></span>
</body>
</html>
