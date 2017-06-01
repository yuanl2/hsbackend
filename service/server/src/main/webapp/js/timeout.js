window.onload = function () {
    window.setTimeout(function () {
        $.dialog({ content: '60秒支付时间已过<br/>请重新扫码支付', title: "alert" });
        window.clearTimeout();
    }, 60000);
}