var MobilePhoneNumber = /^[1][3-8]\d{9}/; //手机号正则
var reg_minute = /^\d+$/;
$(function(){
	$(".item_list").click(function(){
		if($("#userId").val()==""){
        		alert("授权失败，请重新操作！");
        		$(".item_list").bind("click",function(){
        			choice($(this));
        		});
        		return;
        	}
        	if($("#device_id").val()==""){
        		alert("扫码失败，请重新操作！");
        		$(".item_list").bind("click",function(){
        			choice($(this));
        		});
        		return;
        	}
        	  myAjax_tb("/hsservice/api/deviceStatus","device_id="+$("#device_id").val(), function (device_status) {
                                 var pid = $(this).find("input[name='product_id']").val();
                                 var price = $(this).find("input[name='product_price']").val();
                      if (device_status == "1") {


                        $.ajax({
                                url : '/hsservice/wxweb/savepackage',
                                type : 'post',
                                data : {
                                    userId : $("#userId").val(),
                                    device_id : $("#device_id").val(),
                                    product_id : $(".product_id").val(),
                                    fullChairNo : $("#fullChairNo").val(),
                                    storeId : $("#storeId").val(),
                                    storeName : $("#storeName").val(),
                                    channelId : $("#channelId").val(),
                                    channelName : $("#channelName").val()
                                },
                                dataType : 'json',
                                success : function(data) {
                                    if(data.status=='0'){
                        //        		alert("下单成功");
                        //        		location.href="blue/wxweb/order?userId="+$("#userId").val();
                                        wxpay(data.msg);
                                    }else if(data.status=='1'&&data.status!=undefined){
                                        alert(data.msg);
                                        $(".item_list").bind("click",function(){
                                            choice($(this));
                                        });
                                    }else if(data.status=='XNB'){
                        //        		alert("下单成功");
                                        location.href="blue/wxweb/paysuccess?orderId="+data.msg;
                                    }else{
                                        alert("网络不稳定");
                                        $(".row_list_item").bind("click",function(){
                                            choice($(this));
                                        });
                                    }
                                },
                                error : function(data) {
                                        alert("网络不稳定");
                                        $(".item_list").bind("click",function(){
                                            choice($(this));
                                        });
                                }
                            });
                                  }
                                  else if (device_status == "3") { alert('设备正在使用'); }
                                  else if (device_status == "-1") { location.href = "/hsservice/disable?device_id=${device_id}"; }
                                  else if (device_status == "0") { alert('设备离线'); }
                                  else if (device_status == "2") { alert('无此设备'); }
                                  else if (device_status == "4") { alert('网络不好'); }
                                  	$(".item_list").bind("click",function(){
                                  		choice($(this));
                                  	});
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
