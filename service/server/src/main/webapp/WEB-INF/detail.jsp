<%@ page contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="com.hansun.dto.Consume" pageEncoding="UTF-8"%>
<%
     Consume consume = (Consume)request.getAttribute("consume");
     int product_id = consume.getId();
     float price = consume.getPrice();
     String desc = consume.getDescription();
     String path = consume.getPicpath();
 %>
<html xmlns="http://www.w3.org/1999/xhtml" lang = "zh-CN">
<head>
<meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
<meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
<meta http-equiv="cache-control" content="max-age=0" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT" />
<meta http-equiv="Pragma" content="no-cache" />
<link href="css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<link href="css/pay2prop.css" rel="stylesheet" type="text/css" />
<title>
	${device_id}
</title>
    <script src="js/jquery-1.7.2.min.js"></script>
    <script src="js/jquery.timers-1.2.js"></script>
    <link href="css/css_min480.css" rel="stylesheet" type="text/css" /><link href="css/zepto.alert.css" rel="stylesheet" />
	<style type="text/css">
		#info_success {
			color: white;
		}
		#info_success .title {
			font-weight: bold;
			font-size: 1.2em;
			display: block;
			margin-bottom: 1em;
		}
	</style>
    <script src="js/zepto.alert.js"></script>
    <script src="js/Gs_Data.js"></script>

    <script>
        $(function () {

        })
        function stime(k) {

        	$.mydialog.alert_ok("<span id='info_success'><span class='title'>支付成功</span>如果长时间未出币,<br />请联系现场服务人员<br />或点击申诉按钮<br />电话：13120759398</span>",
				function () { location.replace('index?device_id=${device_id}&extra='); }
				);

        }
        function onsucc()
        {
            location.href = "index?device_id=${device_id}&extra=";
        }
        function sub_ok(m)
        {
            $.mydialog.open();
            if (m == "xy") { $('#xy_form').submit(); return false; }
            myAjax_tb("/control/deviceStatus.ashx", "did=215302&pay_method="+m, function (device_status) {
                $.mydialog.close();
                if (device_status == "1") {
                    if (m == "xy") $('#xy_form').submit();
                    else if (m == "pf") $('#pf_form').submit();
                    else if (m == "zfb") $('#zfb_form').submit();
                    else if (m == "tp") $('#tp_form').submit();
                    else if (m == "wx" || m == "kb") $.mydialog.alert('微信支付，请使用微信扫一扫');
                    else if (m == "pp") $('#ExpressCheckoutForm').submit();
                }
                else if (device_status == "-3") { $.mydialog.alert('有Sql注入危险'); }
                else if (device_status == "-2") { $.mydialog.alert('设备不可用'); }
                else if (device_status == "-1") { location.href = "deviceStatus?device_id=${device_id}"; }
                else if (device_status == "0") { $.mydialog.alert('设备故障'); }
                else if (device_status == "2") { $.mydialog.alert('请刷卡'); }
                else if (device_status == "3") { $.mydialog.alert('等待刷卡充值'); }
                else if (device_status == "4") { $.mydialog.alert('充值成功'); }
                else if (device_status == "5") { $.mydialog.alert('充值失败'); }
                else if (device_status == "6") { $.mydialog.alert('暂停支付'); }
                else if (device_status == "15") { $.mydialog.alert('支付通道不可用'); }
                else if (device_status == "16") { $.mydialog.alert('通道未开启,操作失败!请选择通道后重试'); }
                else if (device_status == "17") { $.mydialog.alert('设备不可用,无法支付'); }
            }, function () { alert("请求设备状态出错"); })
        }
        //同步请求
        var myAjax_tb = function (url, data, fun, erfun) {
            var queryT = (data == null ? "GET" : "POST");
            $.ajax({
                url: url,
                type: queryT,
                data: data,
                async:false,
                success: function (d) {
                    fun(d);
                },
                error: function (xr, ts, et) {
                    if (erfun) {
                        erfun(xr, ts, et);
                    } else {
                        alert("数据请求失败");
                    }
                }
            });
        }
    </script>
</head>
<body>
	<div class="main">
		<div class="main_top">
		<div class="main_logo">
		<span>爱生活，爱摩客！欢迎使用爱摩客。</span>
		</div>
		</div>
		<div class="main_bottom">
		<img src="images/top_2.jpg" />
		</div>
		<div class="main_list">
		<div class="mrow mdetail">
		<div class="k0">
		<dl class="k0_dl">
		<dt class="k0_dt">
		<img src="<%=path%>" width="200" />
		</dt>
		<dd class="k0_dd0"><%=desc%></dd>
		<dd class="k0_dd0 mred">￥<%=price%></dd></dl></div><div class="clear"></div></div><div class="mdetailbtns">
<input type="hidden" name="oid" id="oid" value="" />
<input type="hidden" name="did" id="did" value="${device_id}" />
<input type="hidden" name="pid" id="pid" value="<%=product_id%>" />
</div>
<div class="mline">
</div>
<div class="mdede">介绍：<br /><p sttyle='height:'><%=desc%></p><br>
<b>注意：</b>如果支付成功后不出币，请联系</div>
</div>
	</div>
</body>
</html>
<script src="js/timeout.js?r=324549018"></script>
