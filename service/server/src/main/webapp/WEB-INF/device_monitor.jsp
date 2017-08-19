<%@ page contentType="text/html;charset=utf-8"%>
<%@ page language="java" import="com.hansun.dto.Device" pageEncoding="UTF-8"%>
<%@ page language="java" import="java.util.List"%>

<html xmlns="http://www.w3.org/1999/xhtml" lang = "zh-CN">
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
    <meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no"/>
    <meta http-equiv="pragma" content="no-cache"/>
    <meta http-equiv="cache-control" content="no-cache"/>
    <meta http-equiv="expires" content="0"/>
    <title>

    </title>

</head>
<body>
   <table border="1">
       <tr>
           <td>id</td>
           <td>name</td>
           <td>AreaName</td>
           <td>Owner</td>
           <td>Status</td>
           <td>SimCard</td>
           <td>Port</td>
           <td>LoginTime</td>
           <td>LogoutTime</td>
           <td>Signal</td>
           <td>Login_Reason</td>
       </tr>
         <%
     List<Device> devices = (List<Device>)request.getAttribute("devices");
     for(Device row:devices)
            {%>
           <tr>
               <td><%=row.getId() %></td>
               <td><%=row.getName() %></td>
               <td><%=row.getAreaName() %></td>
               <td><%=row.getOwner() %></td>
               <% if (row.getStatus() == 0) { %>
               <td>Disconnected</td>
               <% } else if (row.getStatus() == 1) { %>
               <td>Idle</td>
               <% } else if (row.getStatus() == 3) { %>
               <td>Running</td>
               <% } else if (row.getStatus() == 4) { %>
               <td>Bad Network</td>
               <% } %>
               <td><%=row.getSimCard() %></td>
               <td><%=row.getPort() %></td>
               <td><%=row.getLoginTime() %></td>
               <td><%=row.getLogoutTime() %></td>
               <td><%=row.getSignal() %></td>
               <td><%=row.getLoginReason() %></td>
           </tr>
             <%}
        %>
   </table>
</body>
</html>
