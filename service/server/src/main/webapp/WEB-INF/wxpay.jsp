<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
    long t = System.currentTimeMillis();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" />
<meta name="apple-mobile-web-app-capable" content="yes" />
<meta name="apple-mobile-web-app-status-bar-style" content="black" />
<meta name="format-detection" content="telephone=no" />
<title>微信公众号支付</title>
<link href="../css/css.css?t=<%=t%>" rel="stylesheet" type="text/css">
</head>

<body>
	<div class="index_box">
		<div class="apply_name">微信js支付测试</div>


		<div class="branch_con">
			<ul>
				<li><span class="name">测试支付信息</span></li>
			</ul>
			<p class="cz_btn"><a href="javascript:pay();" class="btn_1">立即支付</a></p>
		</div>
	</div>

	<script type="text/javascript" src="js/common.js?t=<%=t%>"></script>
	<script type="text/javascript">

	var appId = urlparameter("appId");
	var timeStamp = urlparameter("timeStamp");
	var nonceStr = urlparameter("nonceStr");
	var pg = urlparameter("pg");
	var signType = urlparameter("signType");
	var paySign = urlparameter("paySign");


	  function onBridgeReady(){

		   WeixinJSBridge.invoke(
		       'getBrandWCPayRequest', {
		           "appId" : appId,     //公众号名称，由商户传入
		           "timeStamp": timeStamp,         //时间戳，自1970年以来的秒数
		           "nonceStr" : nonceStr, //随机串
		           "package" : "prepay_id=" + pg,
		           "signType" : signType,         //微信签名方式:
		           "paySign" : paySign    //微信签名
		       },

		       function(res){
		           if(res.err_msg == "get_brand_wcpay_request:ok" ) {

		        	   alert("支付成功");
		           }     // 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。
		       }
		   );
		}


	    function pay(){

			if (typeof WeixinJSBridge == "undefined"){
			   if( document.addEventListener ){
			       document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
			   }else if (document.attachEvent){
			       document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
			       document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
			   }
			}else{
			   onBridgeReady();
			}

	    }
	</script>
</body>
</html>