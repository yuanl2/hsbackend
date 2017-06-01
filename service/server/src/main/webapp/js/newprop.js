function show_time(n,m){
      var time_sys = new Date().setTime(m);
      var time_end = new Date().setTime(n);
      var a=time_end - time_sys;

  var timer=setInterval(function(){
      $('.xianshi').css('display','block');
      a-=1000;
      var time_distance = a;  // 结束时间减去当前时间
      var int_day, int_hour, int_minute, int_second;
    if(time_distance >= 0){
        int_day = Math.floor(time_distance/86400000)
        time_distance -= int_day * 86400000;
        int_hour = Math.floor(time_distance/3600000)
        time_distance -= int_hour * 3600000;
        int_minute = Math.floor(time_distance/60000)
        time_distance -= int_minute * 60000;
        int_second = Math.floor(time_distance/1000)
        if(int_hour < 10)
        int_hour = "0" + int_hour;
        if(int_minute < 10)
        int_minute = "0" + int_minute;
        if(int_second < 10)
        int_second = "0" + int_second;
        if(int_day>=10){
          var str=int_day+'天';
        }else{
          var str=int_day+'天'+int_hour+'时'+int_minute+'分'+int_second+'秒';
        }

        $('.timestamp').html(str);
  }else{
    product.overtime();
    clearInterval(timer);

  }
},1000);
  $('.xianshi').css('display','none');
  return ''
};

  var product = {
    xianshi: function(n,m){
        var str="<div class='xianshi'><p>限时商品，还剩<span class='timestamp'></span></p></div>";
        $('.k0_dl').append(str);
        show_time(n,m);
    },
    member: function(){
      var str="<div class='xianshi'><p>该商品是会员专属商品</p></div>";
       $('.k0_dl').append(str);
    },
    xianshimember:function(n,m){
      var str="<div class='xianshi'><p>限时会员商品，还剩<span class='timestamp'></span></p></div>";
      $('.k0_dl').append(str);
      show_time(n,m);
    },
    notmember:function(){
      var str='<div id="prop"><div class="prop"></div><div class="vipkuang"><img src="images/jingya.png" class="vipimg"><p class="note">该商品是会员专属商品，客官您还不是会员，无法购买此商品！</p><div style="margin-top:0.1rem"><input type="button" value="开通会员" class="btn btn-danger kai"><input type="button" value="返回购买" class="btn btn-default return"></div></div></div>';
      $('body').append(str);
    },
     notmember_: function () {
      var str = '<div id="prop"><div class="prop"></div><div class="vipkuang"><img src="images/jingya.png" class="vipimg"><p class="note">该商品是会员专属商品，支付宝暂不支持会员购买！</p><div style="margin-top:0.1rem"><input type="button" value="返回购买" class="btn btn-warning return1"></div></div></div>';
      $('body').append(str);
  },
    overtime:function(){
      var str='<div id="prop"><div class="prop"></div><div class="vipkuang"><img src="images/weiqu.png" class="vipimg"><p class="note">客官您手慢了！该商品已经不在限时优惠时´åï¼</p><div style="margin-top:0.1rem"><input type="button" value="è¿åè´­ä¹°" class="btn btn-warning return1"></div></div></div>';
      $('body').append(str);
    }
}
