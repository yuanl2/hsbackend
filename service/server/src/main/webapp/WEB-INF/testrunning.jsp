<%@ page contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="com.hansun.dto.Consume" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang = "zh-CN">
<head>
	<meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1" />
	<title>爱摩客-Knocknock</title>
    <link rel="stylesheet" href="css/bootstrap.min.css" />
    <link rel="stylesheet" href="css/font-awesome.min.css" />
    <link rel="stylesheet" href="css/animate.min.css" />
    <link rel="stylesheet" href="css/style.css" />
    <link rel="stylesheet" href="css/404-1.css" />
</head>
<body>
<input type='hidden' id='device_id' value='${device_id}'/>
<input type='hidden' id='orderId' value='${orderId}'/>

<section id="about">
	<div class="container">
		<div class="row text-center">
            <div class="col-md-4">
                <div class="contact-text1 ">
                    <img src="images/1111.png" alt="" />
                </div>
            </div>
			<div class="col-md-4">
                <div class="contact-text1 ">
                    <span> 设备${device_id}正在运行</span>
                    <span>订单号 ${orderId}</span>
                    <span>
                        <span id="t_h" >00时</span>
                        <span id="t_m" >00分</span>
                        <span id="t_s" >00秒</span>
                    </span>
                </div>
			</div>
		</div>
	</div>
		<div class="container">
    			<div class="row text-center">
        					<p>爱生活 爱摩客</p>
        		</div>
    		<div class="row text-center">
    					<p>联系方式 Tel: XXXXXXXXXXX </p>
    		</div>
    	</div>
</section>
<footer id="footer">
		<div class="container">
            <div class="row">
                        <p></p>
            </div>
    	</div>
</footer>
<script type="text/javascript" src="js/jquery-2.1.1.min.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
    <script type="text/javascript">
    var endTime = (new Date()).getTime() + ${duration} *  1000 +  2000;
    </script>
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
</body>
</html>