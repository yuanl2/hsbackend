<%@ page contentType="text/html;charset=utf-8"%>

<html>
<head>
        <meta http-equiv="Content-Type" content="text/html;charset=utf-8"/>
        <meta name="viewport" content="width=device-width,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no" />
        <meta http-equiv="cache-control" content="max-age=0" />
        <meta http-equiv="cache-control" content="no-cache" />
        <meta http-equiv="expires" content="0" />
        <meta http-equiv="expires" content="Tue, 01 Jan 1980 1:00:00 GMT" />
        <meta http-equiv="Pragma" content="no-cache" />
<title>Insert title here</title>
    <link rel="stylesheet" type="text/css" href="css/bootstrap.css" />
    <link rel="stylesheet" type="text/css" href="css/bootstrap-theme.css" />
    <link rel="stylesheet" type="text/css" href="css/orb.css" />
	<script type="text/javascript" src="js/react-0.12.2.js"></script>
	<script type="text/javascript" src="js/orb.js"></script>
    <script type="text/javascript" src="js/jquery-3.2.1.min.js"></script>
    <style type="text/css">
    body {
        font-size: 1.2em;
    }
    </style>
<script>
window.demo = {};
window.demo.data = [];
$(document).ready(function(){

	jQuery.support.cors = true;
	var datas = $('#data');
	$('#btn').click(function(){

		var username = $('#txtUsername').val();
		var password = $('#txtPassword').val();
		var startTime = $('#txtStartTime').val();
		var endTime = $('#txtEndTime').val();
		var dataArray = [];
		$.ajax({
			type: "GET",
			headers: {
				'Authorization': 'Basic ' + btoa(username+':'+password)
		    },
		    url: 'api/order/user/'+username+'?startTime='+startTime+'&&endTime='+endTime,
		    success:function(data, textStatus, xhr){

		    	datas.empty();
		    	$.each(data,function(index,val){
		    	var d = [];
                d.push(val.user);
                d.push(val.areaName);
                d.push(val.month);
                d.push(val.day);
                d.push(val.cTime);
                d.push(val.deviceName);
                d.push(val.price);
                d.push(val.duration);
                d.push(1);

                dataArray.push(d);

                })
                for(var t=0;t<1;t++) {
                    for(var j = 0;j < dataArray.length; j++) {
                        window.demo.data.push(dataArray[j]);
                    }
                }

		    },
		    error:function(data){
		    	console.log(data);
		    },
		    complete:function(xhr){
		    	if(xhr.status == '401'){
		    		datas.empty();
		    		datas.append('<li>' + xhr.statusText + "</li>");
		    	}
		    }
		});
	});
});
</script>
</head>
<body>
<div>
	Username: <input type="text" id="txtUsername"/>
	Password: <input type="password" id="txtPassword"/>
	StartTime: <input type="startTime" id="txtStartTime"/>
	EndTime: <input type="endTime" id="txtEndTime"/>
	<br/>
	<input id="btn" type="button" value="Authenticate and get data"/>
	<div id="data"></div>
</div>
<div>
    <button onclick="refreshData()">Refresh</button>
    <button onclick="changeTheme()">Theme</button>
    <a download="orbpivotgrid.xls" href="#" onclick="return exportToExcel(this);">Export to Excel</a>

    <div id="rr" style="padding: 7px;"></div>
    <div id="export" style="padding: 7px;"></div>

    <script type="text/javascript">

    function refreshData() {
        pgridwidget.refreshData(window.demo.data);
    }

    function changeTheme() {
        pgridwidget.changeTheme('bootstrap');
    }

    function exportToExcel(anchor) {
        anchor.href = orb.export(pgridwidget);
        return true;
    }

    var config = {
        dataSource: window.demo.data,
        canMoveFields: true,
        dataHeadersLocation: 'columns',
        width: 1200,
        height: 800,
        theme: 'green',
        toolbar: {
            visible: true
        },
        grandTotal: {
            rowsvisible: false,
            columnsvisible: false
        },
        subTotal: {
            visible: true,
            collapsed: true,
            collapsible: true
        },
        rowSettings: {
            subTotal: {
                visible: true,
                collapsed: true,
                collapsible: true
            }
        },
        columnSettings: {
            subTotal: {
                visible: true,
                collapsed: true,
                collapsible: true
            }
        },
        fields: [
            {
                name: '0',
                caption: 'user'
            },
            {
                name: '1',
                caption: 'areaName',
            },
            {
                name: '2',
                caption: 'month',
                sort: {
                    order: 'asc'
                },
                rowSettings: {
                    subTotal: {
                        visible: true,
                        collapsed: true,
                        collapsible: true
                    }
                },
            },
            {
                name: '3',
                caption: 'day',
                sort: {
                    order: 'asc'
                },
                rowSettings: {
                    subTotal: {
                        visible: true,
                        collapsed: true,
                        collapsible: true
                    }
                },
            },
            {
                name: '4',
                caption: 'cTime',
                sort: {
                    order: 'desc'
                }
            },

            {
                name: '5',
                caption: 'deviceName',
            },
            {
                name: '6',
                caption: 'price',
                dataSettings: {
                    aggregateFunc: 'sum',
                    formatFunc: function(value) {
                        return value ? Number(value).toFixed(0) + ' ￥ ' : '';
                    }
                }
            },
            {
                name: '7',
                caption: 'duration',
                dataSettings: {
                    aggregateFunc: 'sum',
                    aggregateFuncName: 'run time',
                    formatFunc: function(value) {
                        return value ? Number(value).toFixed(0) + ' Minutes ' : '';
                    }
                }
            },
            {
                name: '8',
                caption: 'Count',
                aggregateFunc: 'sum'
            },
        ],
        rows    : [ 'month', 'day'],
        columns : [ 'deviceName'],
        data    : [ 'Count','price', 'duration' ],
        /*preFilters : {
            'Class': { 'Matches': 'Regular' },
            'Manufacturer': { 'Matches': /^a|^c/ },
            'Category'    : { 'Does Not Match': 'D' },
           // 'Amount'      : { '>':  40 },
         //   'Quantity'    : [4, 8, 12]
        }*/
    };

    var elem = document.getElementById('rr')

    var pgridwidget = new orb.pgridwidget(config);
    pgridwidget.render(elem);
    </script>
</div>
</body>
</html>