<%@ page contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="com.hansun.dto.Consume" pageEncoding="UTF-8"%>
<%@ page language="java" import="java.util.List"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang = "zh-CN">
<head>
	<meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1" />
	<title>爱摩客-Knocknock</title>
    <link rel="stylesheet" href="css/bootstrap.min.css" />
    <link rel="stylesheet" href="css/animate.min.css" />
    <link href='css/fonts.css' rel='stylesheet' >
    <link rel="stylesheet" href="css/style.css" />
</head>
<body>
<input type='hidden' id='userId' value='${openid}'/>
<input type='hidden' id='device_id' value='${device_id}'/>
<section id="about">
	<div class="container">
		<div class="row text-center">
            <div class="col-md-4">
                <div class="contact-text ">
                    <img src="images/1111.jpg" alt="" />
                </div>
            </div>
		    <% List<Consume>  city=(List<Consume>)request.getAttribute("consumes");
              for(Consume row:city){
            %>
			<div class="col-md-4">
                <input type='hidden' name='product_id' value='<%=row.getId() %>'/>
                <input type='hidden' name='product_price' value='<%=row.getPrice() %>'/>
                <input type='hidden' name='device_id' value='${device_id}'/>
                <input type='hidden' name='extra' value=''/>
                <input type='hidden' name='openid' value='${openid}'/>
				<div class="features">
					<img src="<%=row.getPicpath()%>" alt="<%=row.getDescription()%>" />
				</div>
			</div>
			<% } %>
		</div>
	</div>
        <div class="container">
                <div class="features-bottom">
                            <p></p>
                </div>
        </div>
		<div class="container">
                <div class="features-bottom">
                            <p>专卖店: ${store}</p>
                </div>
                <div class="features-bottom">
        					<p id="service_protocol">爱摩客服务协议</p>
        		</div>
                <div class="features-bottom">
                            <p>联系方式 Tel:400-821-0741 </p>
                </div>
    	</div>
    	<div id="boxes">
          <div id="dialog" class="window">
            <div id="lorem" class="example">
            <p>使用前请仔细阅读“用户服务声明”并正确使用按摩沙发，避免发生意外。</p>
              <p style="font-size:14px;font-weight: bold;"><img src="images/read-2.jpg"/>&ensp;不宜使用人群：</p>
              <p>    1.	残障人士、感观或神经有缺陷的人士、未成年人，须在监护人或专业人士的监督与指导下使用。</p>
              <p>    2.	有心脏问题、佩戴心脏起搏器等医用电子仪器者。</p>
              <p>    3.	正在接受医生治疗者、经医生嘱咐需要休养或感觉身体不适者，使用前请咨询医生。</p>
              <p>    4.	患有恶性肿瘤、急性疾病、心脏病、严重高血压等疾病的患者。</p>
              <p>    5.	孕妇或正处于经期者；处于发热期（特别是体温38℃以上的）人士。</p>
              <p>    6.	骨质疏松、颈椎骨折等患者、身体有创伤或体表患病者。</p>
              <p>    7.	年满60岁以上人士，请慎重使用。</p>
              <p style="font-size:14px;font-weight: bold;"><img src="images/read-1.jpg"/>&ensp;禁止事项：</p>
              <p>    1.发现按摩沙发异常，请立即停止使用。</p>
              <p>    2.如发现按摩沙发损坏、破裂、漏电或相关部件暴露出来时，禁止使用。</p>
              <p>    3.禁止将其他重物放置或挤压按摩沙发，禁止在按摩沙发上玩耍、站立、倒立。</p>
            </div>
            <div id="popupfoot"> <a href="#" class="button-close agree">我已阅读并同意</a> </div>
            </div>
          <div style="width: 1478px; font-size: 20pt; color:white; height: 602px; display: none; opacity: 0.3;" id="mask"></div>
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
        var id = '#dialog';
   		//Get the screen height and width
   		var maskHeight = $(document).height();
   		var maskWidth = $(window).width();

   		//Set heigth and width to mask to fill up the whole screen
   		$('#mask').css({'width':maskWidth,'height':maskHeight});

   		//transition effect
   		$('#mask').fadeIn(500);
   		$('#mask').fadeTo("slow",0.9);

   		//Get the window height and width
   		var winH = $(window).height();
   		var winW = $(window).width();

   		//transition effect
   		$(id).fadeIn(2000);

   	//if close button is clicked
   	$('#service_protocol').click(function (e) {
   		//Cancel the link behavior
   		e.preventDefault();
   		$('#dialog').fadeIn(2000);
   	});
   	   	//if close button is clicked
       	$('.window .button-close').click(function (e) {
       		//Cancel the link behavior
       		e.preventDefault();

       		$('#mask').hide();
       		$('.window').hide();
       	});

   	//if mask is clicked
   	$('#mask').click(function () {
   		$(this).hide();
   		$('.window').hide();
   	});

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
                   $.ajax({
                       url : '/hsservice/weixin/paycancel',
                       type : 'post',
                       data : {
                           orderId : orderId
                       },
                       dataType : 'json',
                       success : function(data) {
                       },
                       error : function(data) {
                               alert("取消订单错误");
                       }
                   });
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
