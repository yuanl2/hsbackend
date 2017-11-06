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
        <div class="apply_name">商品</div>


        <div class="branch_con">
            <ul>
                <li><span class="name">beacon 1分钱 1只</span></li>
                <li><span class="name">测试支付信息</span></li>
            </ul>
            <p class="cz_btn"><a href="javascript:reppay();" class="btn_1">确定购买</a></p>
        </div>
    </div>

    <script type="text/javascript" src="js/common.js?t=<%=t%>"></script>
    <script type="text/javascript" >

         var code = urlparameter("code");

         function reppay(){

             ajaxUtil({}, mainpath+"/wxprepay?code=" + code, repay);

         }

          function repay(response){
              var info = response;
              var url = "wxpay?appId=" + info.appId + "&timeStamp=" +info.timeStamp + "&nonceStr=" + info.nonceStr +
                        "&pg=" +info.pg + "&signType=" +info.signType + "&paySign=" +info.paySign;

              window.location.href= url + "&showwxpaytitle=1";
          }



    </script>
</body>
</html>