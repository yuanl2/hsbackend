<%@ page contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="com.hansun.dto.Consume" pageEncoding="UTF-8"%>
<%@ page language="java" import="java.util.List"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<html xmlns="http://www.w3.org/1999/xhtml" lang = "zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no"/>
    <meta http-equiv="pragma" content="no-cache"/>
    <meta http-equiv="cache-control" content="no-cache"/>
    <meta http-equiv="expires" content="0"/>
    <title>
       ${device_name}
       ${path}
       ${basePath}
    </title>
    <link href="${pageContext.request.contextPath}css/css_min480.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}css/new.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}css/zepto.alert.css" rel="stylesheet"/>
    <script src="js/jquery-1.7.2.min.js"></script>
    <script src="js/jQuery.resizeEnd.min.js"></script>
    <script src="js/zepto.alert.js"></script>
    <script src="js/Gs_Data.js"></script>
    <script>
        var width = 0;
        var height = 0;
        var rowcount = 0;
        $(function () {
            var SetRow = function (row, cell) {
                $(".k0").css({ "width": "" + (100 / row) + "%" });
                if (rowcount == 0) {
                    for (var i = 0; i < $(".k0").length; i++) {
                        if (i % row == 0) {
                            $(".k0").slice(i, i + row).wrapAll("<div class='mrow' style='height:" + (100 / cell) + "%'></div>");
                        }
                    }
                    rowcount++;
                }

                $(".main_list").css({ "height": height + "px" });

                var w = width / row;
                var h = (height / cell) - 50;

                var min_len = w;
                if (h < w)
                    min_len = h;

                $(".img_size").css({ "height": min_len + "px", "width": min_len + "px" });
            };
            var SetFn = function () {
                width = document.documentElement.clientWidth;
                height = document.documentElement.clientHeight - $("#main_head").height() - $("#main_footer").height();

                $(".main_list").css({ "height": height + "px" });
                var count = $(".k0").length;
                if (count == 1) {
                    SetRow(1, 1);
                } else if (count == 2) {
                    SetRow(1, 2);
                }
                else if (count == 3 || count == 4) {
                    SetRow(2, 2);
                }
                else if (count == 5 || count == 6) {
                    SetRow(2, 3);
                }
                else {
                    SetRow(3, 3);

                }

            };

            $(window).resizeEnd({
                delay: 100
            }, function () {
                SetFn();
            });
            SetFn();

            $(".k0").click(function () {
                var did = $(this).find("input[name='device_id']").val();
                var extra = $(this).find("input[name='extra']").val();
                if ($(this).find("input[name='product_id']").length == 0) {
                    location.href = "DetailZDY_.aspx?device_id=" + did + "&extra=" + extra;
                    return false;
                }
                var pid = $(this).find("input[name='product_id']").val();
                $.mydialog.open();
                myAjax("http://localhost:8080/hsservice/deviceStatus", "device_id=" + did, function (device_status) {
                    $.mydialog.close();
                    if (device_status == "1") {
                        location.href = "detail?product_id=" + pid + "&device_id=" + did + "&extra=" + extra + "";
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
            });

        });



    </script>
</head>
<body>
<img src="https://c.cnzz.com/wapstat.php?siteid=1260506039&r=&rnd=516089417" width="0" height="0" style="display:none"/>
<div class="main">
    <div id="main_head" class="main_top">
        <div class="main_logo"><span onclick="javascript:location.reload(true)">爱生活，爱摩客！欢迎使用爱摩客。</span></div>
    </div>
    <div class="main_list">
     <%
     List<Consume>  city=(List<Consume>)request.getAttribute("consumes");
     for(Consume row:city){
     %>
     <div class="k0 k1"><input type='hidden' name='product_id' value='<%=row.getId() %>'/>
             <input type='hidden' name='device_id' value='${device_id}'/>
             <input type='hidden' name='extra' value=''/>
             <dl class="k0_dl">
                 <dt class="k0_dt"><img class="img_size" src="<%=row.getPicpath() %>"/></dt>
                 <div style="position:relative;top:0;z-index:11;background-color:white;opacity: 0.80;">
                     <div style="color:black;" class="k0_dd0"><%=row.getDescription() %></div>
                     <div class="k0_dd0 mred">￥<%=row.getPrice() %></div>
                 </div>
             </dl>
     </div>
     <% } %>
    </div>
    <div id="main_footer" class="fotter"><span>服务电话 xxx-xxxx-xxxx</span>
        <span class="skgk" onclick="location.href= 'Complaint_1.aspx?device_name=J28441'"> 申诉 </span>
    </div>
</div>
</body>
</html>
<script src="js/timeout.js?r=516089417"></script>
<script type="text/javascript">
    var _maq = _maq || [];
    _maq.push(['_setAccount', 'ip']);
    (function () {
        var ma = document.createElement('script'); ma.type = 'text/javascript'; ma.async = true;
        ma.src = 'iplog.ashx';
        var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ma, s);
    })();
</script>