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
									<div class="item_list">
										<section>
										   <input type='hidden' name='product_id' value='<%=product_id%>'/>
   									       <input type='hidden' name='extra' value=''/>
										   <i class="<%=path%>"></i>
											<header>
												<h2><%=desc%></h2>
											</header>
											<p>￥ <%=price%></p>
										</section>
									</div>
								</div>
								<footer>
									<ul class="actions">
										<li><a href="#" class="button big">Get Started</a></li>
										<li><a href="#" class="button alt big">Learn More</a></li>
									</ul>
								</footer>
							</section>
				</div>


		<!-- Scripts -->
			<script src="assets/js/jquery.min.js"></script>
			<script src="js/common.js"></script>
			<script src="assets/js/jquery.dropotron.min.js"></script>
			<script src="assets/js/skel.min.js"></script>
			<script src="assets/js/skel-viewport.min.js"></script>
			<script src="assets/js/util.js"></script>
			<!--[if lte IE 8]><script src="assets/js/ie/respond.min.js"></script><![endif]-->
	</body>
</html>