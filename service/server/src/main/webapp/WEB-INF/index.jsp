<%@ page contentType="text/html;charset=utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html><html xmlns=http://www.w3.org/1999/xhtml lang=zh-CN><head><meta charset=utf-8><meta http-equiv=X-UA-Compatible content="IE=edge"><link rel=icon href=${contextPath}/iview-admin/favicon.ico><title>爱摩客后台管理</title><link href=${contextPath}/iview-admin/css/chunk-023b.8efc7d50.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-053f.c47a3102.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-0c71.16101f38.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-11ed.f50ec4db.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-192a.b519633d.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-2d92.89b4ce41.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-3137.22382691.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-3573.da6885cc.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-36ee.4d4bc66e.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-38cf.3aa4217e.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-3b58.c47a3102.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-3dca.ed115cd5.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-3fd3.a91db0e7.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-4672.8f49bab4.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-4859.0982e707.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-4de7.2be74445.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-50a0.08e58799.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-54f4.dc8dc23e.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-555b.7f8503ff.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-5a8b.a104b073.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-6be1.cf26e268.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-6c14.50ff212a.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-6c53.77186cff.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-8654.47427851.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-d1d0.7958a5a8.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-d84c.b537d768.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-e9c5.bffb4709.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-ed0e.c47a3102.css rel=prefetch><link href=${contextPath}/iview-admin/css/chunk-f2e4.92887d78.css rel=prefetch><link href=${contextPath}/iview-admin/js/1092.506ef128.js rel=prefetch><link href=${contextPath}/iview-admin/js/56b3.26b2ec9f.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-023b.2e1d1ff5.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-053f.34a10326.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-0c71.0466b6cc.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-11ed.d60ab34b.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-192a.9b6fe401.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-2d92.1a18967b.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-3137.c1fd23e2.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-3573.4ee7c7a8.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-36ee.3932be8d.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-38cf.3b613871.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-3b58.d9cf1abd.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-3dca.0300f72c.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-3fd3.3b5e1ee9.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-4672.d679019a.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-4859.f9e4b53a.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-4de7.fe6a62b5.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-50a0.0bef94d3.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-54f4.61aab988.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-555b.caf725c2.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-594d.25c3fbc3.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-5a8b.a1daa9b5.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-6be1.5a734456.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-6c14.83ab618a.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-6c53.07958048.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-8654.a7440699.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-d1d0.32ddff59.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-d84c.c9c67224.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-e9c5.7730cac5.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-ed0e.a7cc7107.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-f2d5.880d7faf.js rel=prefetch><link href=${contextPath}/iview-admin/js/chunk-f2e4.6226de6f.js rel=prefetch><link href=${contextPath}/iview-admin/js/d08b.37c41303.js rel=prefetch><link href=${contextPath}/iview-admin/js/e84c.25d2f8f6.js rel=prefetch><link href=${contextPath}/iview-admin/css/app.f4ce15d7.css rel=preload as=style><link href=${contextPath}/iview-admin/css/chunk-vendors.6927e4ad.css rel=preload as=style><link href=${contextPath}/iview-admin/js/app.1a714aaa.js rel=preload as=script><link href=${contextPath}/iview-admin/js/chunk-vendors.d80ade9d.js rel=preload as=script><link href=${contextPath}/iview-admin/css/chunk-vendors.6927e4ad.css rel=stylesheet><link href=${contextPath}/iview-admin/css/app.f4ce15d7.css rel=stylesheet></head><body><noscript><strong>We're sorry but iview-admin doesn't work properly without JavaScript enabled. Please enable it to continue.</strong></noscript><div id=app></div><script src=${contextPath}/iview-admin/js/chunk-vendors.d80ade9d.js></script><script src=${contextPath}/iview-admin/js/app.1a714aaa.js></script></body></html>