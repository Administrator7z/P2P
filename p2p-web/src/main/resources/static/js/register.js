//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}


//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}

//注册协议确认
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});
	//手机号数据处理
    $("#phone").on("blur",function(){
    	//blur 失去焦点 触发操作
		var phone = $.trim( $("#phone").val() );
		if( "" == phone){
			showError("phone","必须输入手机号");
		} else if( phone.length != 11) {
			showError("phone","手机号的位数不正确");
		} else if( !/^1[1-9]\d{9}$/.test(phone)){
			showError("phone","手机号格式不正确");
		} else {
			showSuccess("phone");
			//判断手机号是否注册过 ，ajax请求
			$.ajax({
				url: contextPath + "/loan/register/phone",
				data: {
					phone:phone
				},
				dataType:"json",
				success:function(resp){
					if( resp.code == 30000 ){
						alert(resp.msg)
					}
				},
				error:function(){
					alert("请稍候重试");
				}
			})
		}

	})
	//手机号处理完成


	//密码处理
	$("#loginPassword").on("blur",function(){
		var pwd= $.trim( $("#loginPassword").val() );
		if( "" == pwd ){
			showError("loginPassword","必须输入密码");
		} else if( pwd.length < 6 ){
			showError("loginPassword","密码至少是6位的");
		} else if( pwd.length > 20){
			showError("loginPassword","密码最多20位");
		} else if( !/^[0-9a-zA-Z]+$/.test(pwd)){
			showError("loginPassword","密码是数字和字母组合");
		} else if( !/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(pwd)){
			showError("loginPassword","密码应同时包含英文和数字");
		} else {
			showSuccess("loginPassword");
		}
	})
	//密码处理完成


	//获取短信验证码按钮的click
	$("#messageCodeBtn").on("click",function(){
		var btnObj = $("#messageCodeBtn");

		if( !btnObj.hasClass("on")) {
			//执行手机号的blur事件
			$("#phone").blur();

			//怎么判断phone输入数据是正确的
			var errorText = $("#phoneErr").text();
			if ("" == errorText) {
				//用户输入了手机号，可以发送验证码
				var phoneValue = $.trim($("#phone").val());
				// 当手机号已经有。 发送验证码
				$.ajax({
					url: contextPath + "/loan/sendCode",
					type: "post",
					data: {
						phone: phoneValue
					},
					dataType: "json",
					success: function (resp) {
						alert(resp.msg);
					},
					error: function () {
						btnObj.removeClass("on");
						alert("请稍候重试");
					}

				});
				btnObj.addClass("on");
				$.leftTime(60, function (d) {
					var second = parseInt(d.s);
					console.log("d.status=" + d.status + "#d.s=" + d.s + ",#second=" + second);

					if (second == 0) {
						btnObj.text("获取验证码");
						btnObj.removeClass("on");
					} else {
						btnObj.text(second + "秒后获取");
					}
				})
			}
		}

	})


	//验证码文本框
	$("#messageCode").on("blur",function(){
		var code = $.trim( $("#messageCode").val() );
		if( "" == code ){
			showError("messageCode","必须输入验证码");
		} else if( code.length != 6){
			showError("messageCode","请输入有效的验证码");
		} else {
			showSuccess("messageCode");
		}
	});

    //注册按钮单击
	$("#btnRegist").on("click",function(){
		//处理数据，blur
		$("#phone").blur();
		$("#loginPassword").blur();
		$("#messageCode").blur();

		var textError = $("div[id $='Err']").text();
		if( "" == textError){
			//数据有效， 发送ajax请求， 注册
			var phoneValue = $.trim( $("#phone").val() );
			var pwdValue = $.trim( $("#loginPassword").val() );
			var codeValue = $.trim( $("#messageCode").val() );


			//alert( $.md5(pwdValue))
			$.ajax({
				url: contextPath + "/loan/register",
				type:"post",
				data:{
					phone:phoneValue,
					abc:$.md5(pwdValue),
					code:codeValue
				},
				dataType:"json",
				success:function(resp){

					if( resp.code == 10000){
						//跳转到实名认证
						window.location.href = contextPath + "/loan/page/realName";
					} else {
						alert(resp.msg)
					}

				},
				error:function(){
					alert("请稍候重试")
				}
			})
		}

	})
});
