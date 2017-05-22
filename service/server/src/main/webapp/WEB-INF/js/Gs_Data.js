/// <reference path="jquery-1.7.2.min.js" />

$(function () {
    $(window.document.body).append("<div class='loading'></div>");
    $(".loading").css({
        "z-index": "999999999",
        "display": "none",
        "top": "0px",
        "left": "0px",
        "position": "fixed",
        "width": "100%",
        "height": "100%",
        "text-align": "center",
        "background": "url(/images/loading.gif)  no-repeat center center",
        "background-color": "#000000",
        "filter": "alpha(opacity=50)",
        "-moz-opacity": "0.5",
        "opacity": "0.5"
    });
});
$(function () {
    $.fn.serializeObject = function () {
        var o = "{";
        var a = this.serializeArray();
        for (var i = 0; i < a.length; i++) {
            o += "'" + a[i]["name"] + "':'" + a[i]["value"] + "'"
            if (i != a.length - 1) o += ",";
        }
        o += "}";
        return o;
    }
    $.fn.Reg_Input_Text = function (regstr, errstr, curstr) {
        var idstr = $(this).attr("id");
        idstr = idstr.substring(3);
        var textid0 = $("#txt" + idstr);
        var lbid0 = $("#lb" + idstr);
        if (regstr != "") {
            var reg = regstr;
            if (!reg.test(textid0.val())) {
                lbid0.text(errstr); return false;
            }
            else {
                lbid0.text(curstr); return true;
            }
        }
    }
    $.fn.Empty_Input_Text = function (errstr, curstr) {
        var idstr = $(this).attr("id");
        idstr = idstr.substring(3);
        var textid0 = $("#txt" + idstr);
        var lbid0 = $("#lb" + idstr);
        if (textid0.val() == "") {
            lbid0.text(errstr);
            return false;
        }
        else {
            lbid0.text(curstr);
            return true;
        }
    }
    $.fn.Empty_Input_CheckBox = function (errstr, curstr) {
        var classstr = $(this).attr("class");
        classstr = classstr.substring(2);
        var cb = $(".cb" + classstr);
        var lb = $(".lb" + classstr);
        var cb_checked=$(".cb" + classstr + ":checked");
        if (cb_checked.length == 0) {
            lb.text(errstr);
            return false;
        }
        else {
            lb.text(curstr);
            return true;
        }
    }
    $.fn.FormSubmit = function () {
        Ajax_Post("", function (data) {
            var json = eval(data);
            alert(json["Message"]);
            location.href = json["Turn_url"];
        });
        return false;
    }
    $.fn.FormValidation = function (jsons) {
        var return_str = true;
        for (var i = 0; i < jsons.length; i++) {
            if (jsons[i]["type"] == "re") {
                return_str = $("#" + jsons[i]["id"]).Reg_Input_Text(jsons[i]["regstr"], jsons[i]["newerr"], jsons[i]["cureinfo"]);
            }
            else if (jsons[i]["type"] == "em") {
                return_str = $("#" + jsons[i]["id"]).Empty_Input_Text(jsons[i]["newerr"], jsons[i]["cureinfo"]);
            }
            if (!return_str) break;
        }
        return return_str;
    }

})
var Ajax_Post = function (url, fun) {
    $(".loading").show();
    $.ajax({
        url: url,
        data: $("form").serializeArray(),
        type: 'post',
        dataType: 'json',
        cache: false,
        success: function (data) {
            fun(data);
            $(".loading").hide();
        }, error: function (data) {
            alert(data);
            $(".loading").hide();
        }
    });
};
var Ajax_List_Post = function (url, data, fun) {
    $(".loading").show();
    $.ajax({
        url: url,
        data: data,
        type: 'post',
        dataType: 'json',
        cache: false,
        success: function (data) {
            fun(data);
            $(".loading").hide();
        }, error: function (data) {
            alert(data);
            $(".loading").hide();
        }
    });
};
var myAjax = function (url, data, fun, erfun) {
    var queryT = (data == null ? "GET" : "POST");
    $.ajax({
        url: url,
        type: queryT,
        data: data,
        cache: false,
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
//同步请求
var myAjax_tb = function (url, data, fun, erfun) {
    var queryT = (data == null ? "GET" : "POST");
    $.ajax({
        url: url,
        type: queryT,
        data: data,
        async: false,
        cache:false,
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
var FormValidation = function (jsons) {
    var return_str = true;
    for (var i = 0; i < jsons.length; i++) {
        if (jsons[i]["type"] == "re") {
            return_str = $("#" + jsons[i]["id"]).Reg_Input_Text(jsons[i]["regstr"], jsons[i]["newerr"], jsons[i]["cureinfo"]);
        }
        else if (jsons[i]["type"] == "em") {
            return_str = $("#" + jsons[i]["id"]).Empty_Input_Text(jsons[i]["newerr"], jsons[i]["cureinfo"]);
        } else if (jsons[i]["type"] == "em_cb") {
            return_str = $("." + jsons[i]["class"]).Empty_Input_CheckBox(jsons[i]["newerr"], jsons[i]["cureinfo"]);
        }
        if (!return_str) break;
    }
    return return_str;
}
