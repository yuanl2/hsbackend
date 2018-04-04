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
    <link rel="stylesheet" href="css/test-running.css" />
</head>
<body>
<input type='hidden' id='device_id' value='${device_id}'/>
<input type='hidden' id='orderId' value='${orderId}'/>
<input type='hidden' id='startTime' value='${startTime}'/>
<input type='hidden' id='duration' value='${duration}'/>
<section id="about">
	<div class="container">
		<div class="row text-center">
            <div class="col-md-4">
                <div class="contact-text1 ">
                    <img src="images/1111.png" alt="" />
                </div>
            </div>
			<div class="col-md-4">
                <div class="test-contact-text1 ">
                    <p class="span1"> 设备${device_id}下发失败</p>
                    <p class="span2">订单号 ${orderId}</p>

                </div>
			</div>
		</div>
	</div>
		<div class="container">
    			<div class="row text-center">
        					<p>请联系退款事宜</p>
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
</body>
</html>