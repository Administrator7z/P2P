
//同意实名认证协议
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


	//姓名处理
	$("#realName").on("blur",function(){
		var name = $.trim( $("#realName").val() );
		if("" == name){
			showError("realName","姓名必须有值")
		} else if(name.length < 2){
			showError("realName","姓名不正确")
		} else if( !/^[\u4e00-\u9fa5]{0,}$/.test(name)){
			showError("realName","姓名必须是中文");
		} else {
			showSuccess("realName");
		}
	})
	//姓名完成

	//身份证
	$("#idCard").on("blur",function(){
		var idcard= $.trim( $("#idCard").val() );
		if("" == idcard){
			showError("idCard","必须输入身份证号");
		} else if(!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idcard)){
			showError("idCard","身份证号码不正确");
		} else {
			showSuccess("idCard");
		}
	});

	//认证按钮
	$("#btnRegist").on("click",function(){
		//调用dom的blur
		$("#realName").blur();
		$("#idCard").blur();

		//获取Err dom对象的 text值，判断数据检查的结果
		var textError = $("div[id $= 'Err']").text();
		if( ""  == textError){
			//发起ajax做实名认证
			$.ajax({
				url:contextPath + "/loan/realName",
				type:"post",
				data:{
					name: $.trim( $("#realName").val() ),
					idcard: $.trim( $("#idCard").val() )
				},
				dataType:"json",
				success:function(resp){
					if( resp.code == 10000){
						//跳转到实名认证
						alert(resp.msg);
						window.location.href = contextPath + "/loan/page/myCenter";
					} else {
						alert(resp.msg);
					}

				},
				error:function(){
					alert("请求失败");
				}
			})
		}
	})


});
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