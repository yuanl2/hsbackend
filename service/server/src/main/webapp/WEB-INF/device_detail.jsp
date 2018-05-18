<%@ page contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="com.hansun.dto.Consume" pageEncoding="UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang = "zh-CN">
<%
     Consume consume = (Consume)request.getAttribute("consume");
     int product_id = consume.getId();
     float price = consume.getPrice();
     String desc = consume.getDescription();
     String path = consume.getPicpath();
 %>
<head>
	<meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1" />
	<title>爱摩客-Knocknock</title>
    <link rel="stylesheet" href="css/bootstrap.min.css" />
    <link rel="stylesheet" href="css/font-awesome.min.css" />
    <link rel="stylesheet" href="css/animate.min.css" />
    <link href='https://fonts.googleapis.com/css?family=Roboto+Slab:400,700,300,100' rel='stylesheet' type='text/css'>
    <link href='https://fonts.googleapis.com/css?family=Roboto:400,500,700,300,100' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="css/style.css" />
</head>
<body>
<input type='hidden' id='userId' value='${openid}'/>
<input type='hidden' id='device_id' value='${device_id}'/>
<section id="about">
	<div class="container">
		<div class="row text-center">
            <div class="col-md-4">
                <div class="contact-text1 ">
                    <img src="images/1111.jpg" alt="" />
                </div>
            </div>
			<div class="col-md-4">
                <input type='hidden' name='product_id' value='<%=product_id%>'/>
                <input type='hidden' name='product_price' value='<%=price%>'/>
                <input type='hidden' name='device_id' value='${device_id}'/>
                <input type='hidden' name='extra' value=''/>
                <input type='hidden' name='openid' value='${openid}'/>
				<div class="features">
					<img src="<%=path%>" alt="<%=desc%>" />
				</div>
			</div>
		</div>
	</div>
		<div class="container">
    			<div class="row text-center">
        					<p>爱生活 爱摩客</p>
        		</div>
    		<div class="row text-center">
    					<p>联系方式 Tel: 400-821-0741 </p>
    		</div>
    	</div>
</section>
<script type="text/javascript" src="js/jquery-2.1.1.min.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script>
window.alert = function(name){
     var iframe = document.createElement("IFRAME");
    iframe.style.display="none";
    iframe.setAttribute("src", 'data:text/plain,');
    document.documentElement.appendChild(iframe);
    window.frames[0].window.alert(name);
    iframe.parentNode.removeChild(iframe);
}

$(function(){
	$(".col-md-4").click(function(){
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
                    if( price > 0) {
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
                                    onBridgeReady(data,data.orderId);
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
                        else{
                           location.href="/hsservice/paysuccess?product_id="+pid+"&device_id="+$("#device_id").val()+"&userId="+$("#userId").val()

                        }
                          }
                          else if (device_status == "3") { alert('已有用户支付使用'); }
                          else if (device_status == "4") { alert('设备正在使用'); }
                          else if (device_status == "-1") { location.href = "/hsservice/disable?device_id=${device_id}"; }
                          else if (device_status == "0") { alert('设备离线'); }
                          else if (device_status == "2") { alert('无此设备'); }
                          else if (device_status == "5") { alert('网络不好'); }
                          else if (device_status == "6") { alert('设备故障'); }
                         }, function () { alert("请求设备状态出错"); })


	});
});

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
	           	   location.href="/hsservice/paysuccess?orderId="+orderId+"&product_id="+data.product_id+"&device_id="+data.device_id+"&userId="+data.userId;
	           }// 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回    ok，但并不保证它绝对可靠。
	           else if (res.err_msg == "get_brand_wcpay_request:cancel") {
               	   alert("你已取消支付")
               }
               else if (res.err_msg == "get_brand_wcpay_request:fail") {
                   alert("支付失败！")
                   location.href="/hsservice/index?device_id="+data.device_id;
               }
	       }
	   );
	}
</script>
</body>
</html>