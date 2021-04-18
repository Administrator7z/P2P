var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
	try {
		if (window.opener) {                
			// IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性              
			referrer = window.opener.location.href;
		}  
	} catch (e) {
	}
}

//按键盘Enter键即可登录
$(document).keyup(function(event){
	if(event.keyCode == 13){
		login();
	}
});
$(function(){
	//手机号数据处理
	$("#phone").on("blur",function(){
		var phone = $.trim( $("#phone").val() );
		if( "" == phone){
			$("#showId").text("必须输入手机号");
		} else if( phone.length != 11) {
			$("#showId").text("手机号的位数不正确");
		} else if( !/^1[1-9]\d{9}$/.test(phone)){
			$("#showId").text("手机号格式不正确");
		} else {
			$("#showId").text("");
		}
	})

	//密码
	$("#loginPassword").on("blur",function(){
		var pwd= $.trim( $("#loginPassword").val() );
		if( "" == pwd ){
			$("#showId").text("必须输入密码");
		} else if( pwd.length < 6 ){
			$("#showId").text("密码至少是6位的");
		} else if( pwd.length > 20){
			$("#showId").text("密码最多20位");
		} else if( !/^[0-9a-zA-Z]+$/.test(pwd)){
			$("#showId").text("密码是数字和字母组合");
		} else if( !/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(pwd)){
			$("#showId").text("密码应同时包含英文和数字");
		} else {
			$("#showId").text("");
		}
	})

	//登录按钮
	$("#btnLogin").on("click",function(){
		//检查数据
		$("#phone").blur();
		$("#loginPassword").blur();

		if( $("#showId").text() == "" ){
			//执行ajax的登录
			$.ajax({
				url:contextPath + "/loan/login",
				type:"post",
				data:{
					phone: $.trim($("#phone").val()),
					abc:  $.md5($.trim( $("#loginPassword").val() ))
				},
				success:function(resp){
					//登录成功，跳转到returnUrl
					if( resp.code == 10000){
						window.location.href= $("#returnUrl").val();
					} else {
						alert(resp.msg);
					}
				},
				error:function(){
					alert("请求失败，重新登录");
				}
			})
		}
	})
})
