<%@ page contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="com.hansun.dto.Consume" pageEncoding="UTF-8"%>
<%@ page language="java" import="java.util.List"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang = "zh-CN">
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
                <div class="contact-text ">
                    <img src="images/1111.png" alt="" />
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
<script>
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
                    location.href="/hsservice/detail?device_id="+$("#device_id").val() + "&product_id="+pid+ "&user_id="+$("#userId").val();
                  }
                  else if (device_status == "3") { alert('设备正在使用'); }
                  else if (device_status == "-1") { location.href = "/hsservice/disable?device_id=${device_id}"; }
                  else if (device_status == "0") { alert('设备离线'); }
                  else if (device_status == "2") { alert('无此设备'); }
                  else if (device_status == "4") { alert('网络不好'); }
                  else if (device_status == "5") { alert('设备故障'); }
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
</script>
</body>
</html>