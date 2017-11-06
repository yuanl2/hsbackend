<%@ page contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="com.hansun.dto.Consume" pageEncoding="UTF-8"%>
<%@ page language="java" import="java.util.List"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang = "zh-CN">
<html>
	<head>
		<title>Dopetrope by HTML5 UP</title>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<!--[if lte IE 8]><script src="assets/js/ie/html5shiv.js"></script><![endif]-->
		<link rel="stylesheet" href="assets/css/main.css" type="text/css"/>
		<!--[if lte IE 8]><link rel="stylesheet" href="assets/css/ie8.css" /><![endif]-->
	</head>
	<body class="homepage">
        <input type='hidden' id='userId' value='${openid}'/>
        <input type='hidden' id='device_id' value='${device_id}'/>
        <input type="hidden" value="" id="userId"/>
        <input type="hidden" value="000000005e12251e015e4b464446638c" id="chairId"/>
        <input type="hidden" value="170000001791" id="chairNo"/>
        <input type="hidden" value="170000001791" id="fullChairNo"/>
        <input type="hidden" value="000000005d019dc2015da6373d3d4d26" id="storeId"/>
        <input type="hidden" value="自营仓库3" id="storeName"/>
        <input type="hidden" value="000000005c39b522015c3da68f3d0005" id="channelId"/>
        <input type="hidden" value="公司仓库" id="channelName"/>
        <!-- Header -->
            <div id="header-wrapper">
                <!-- Banner -->
                    <section id="banner">
                            <h2>爱生活，爱摩客！欢迎使用爱摩客</h2>
                    </section>

                <!-- Intro -->
                    <section id="intro" class="container">
                        <div class="row">
                             <%
                             List<Consume>  city=(List<Consume>)request.getAttribute("consumes");
                             for(Consume row:city){
                             %>
                            <div class="item_list">
                            <section>
                                <input type='hidden' name='product_id' value='<%=row.getId() %>'/>
                                <input type='hidden' name='product_price' value='<%=row.getPrice() %>'/>
                                <input type='hidden' name='device_id' value='${device_id}'/>
                                <input type='hidden' name='extra' value=''/>
                                <input type='hidden' name='openid' value='${openid}'/>
                                <i class="<%=row.getPicpath() %>"></i>
                                <header>
                                    <h2><%=row.getDescription() %></h2>
                                </header>
                                <p>￥ <%=row.getPrice() %></p>
                            </section>
                            </div>
                             <% } %>
                        </div>
                        <footer>
                            <ul class="actions">
                                <li><a href="#" class="button big">Get Started</a></li>
                                <li><a href="#" class="button alt big">Learn More</a></li>
                            </ul>
                        </footer>
                    </section>
            </div>
<script src="assets/js/jquery.min.js"></script>
<script>
$(function(){
	$(".item_list").click(function(){
	    var pid = $(this).find("input[name='product_id']").val();
        var price = $(this).find("input[name='product_price']").val();
		if($("#userId").val()==""){
        		alert("授权失败，请重新操作！");

        		return;
        }
        if($("#device_id").val()==""){
            alert("扫码失败，请重新操作！");

            return;
        }
          myAjax_tb("/hsservice/api/deviceStatus","device_id="+$("#device_id").val(), function (device_status) {
                  if (device_status == '1') {
                    $.ajax({
                            url : '/hsservice/weixin/savepackage?callback=test',
                            type : 'post',
                            data : {
                                userId : $("#userId").val(),
                                device_id : $("#device_id").val(),
                                product_id : pid,
                                price : price
                            },
                            dataType : 'json',
                            success : function(data) {
                                if(data.status=='0'){
                    //        		alert("下单成功");
                    //        		location.href="blue/wxweb/order?userId="+$("#userId").val();
                                    wxpay(data.msg);
                                }else if(data.status=='1'&&data.status!=undefined){
                                    alert(data.msg);
                                }else if(data.status=='XNB'){
                    //        		alert("下单成功");
                                    location.href="blue/wxweb/paysuccess?orderId="+data.msg;
                                }else{
                                    alert("网络不稳定");

                                }
                            },
                            error : function(data) {
                                    alert("网络不稳定!!!!");

                            }
                        });
                              }
                              else if (device_status == "3") { alert('设备正在使用'); }
                              else if (device_status == "-1") { location.href = "/hsservice/disable?device_id=${device_id}"; }
                              else if (device_status == "0") { alert('设备离线'); }
                              else if (device_status == "2") { alert('无此设备'); }
                              else if (device_status == "4") { alert('网络不好'); }

                            }, function () { alert("请求设备状态出错"); })


	});
});
//function choice(obj) {
//	$(".item_list").unbind("click");
//
//
//
//}

//同步请求
function myAjax_tb(url, data, fun, erfun) {
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
function wxpay(obj){
//	if (confirm("您确定要支付该订单吗？")) {
		$.ajax({
			url : 'blue/wxweb/wxpay',
			type : 'post',
			data : {
				orderId : obj
			},
			dataType : 'json',
			success : function(data) {
				if(data.status=="0"){
					var result = data.data;
					var orderId = data.msg;
					onBridgeReady(result, orderId);
				}

			},
			error : function(data) {
					alert("网络不稳定");
			}
		});
//	}
}
function onBridgeReady(data, orderId){
	   WeixinJSBridge.invoke(
	       'getBrandWCPayRequest', {
	           "appId" : data.appId,     //公众号名称，由商户传入
	           "timeStamp" : data.timeStamp,         //时间戳，自1970年以来的秒数
	           "nonceStr" : data.nonceStr, //随机串
	           "package" : data.package,
	           "signType" : data.signType,         //微信签名方式：
	           "paySign" : data.paySign //微信签名
	       },
	       function(res){
	           if(res.err_msg == "get_brand_wcpay_request:ok" ) {
	        	   location.href="blue/wxweb/paysuccess?orderId="+orderId;
	           }// 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。
	       }
	   );
	}
</script>
		<!-- Scripts -->
			<script src="assets/js/jquery.dropotron.min.js"></script>
			<script src="assets/js/skel.min.js"></script>
			<script src="assets/js/skel-viewport.min.js"></script>
			<script src="assets/js/util.js"></script>
			<!--[if lte IE 8]><script src="assets/js/ie/respond.min.js"></script><![endif]-->
	</body>
</html>