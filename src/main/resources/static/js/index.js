$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

	//发送AJAX请求之前，将CSRF令牌加入请求头
	// var token = $("meta[name='_csrf']").attr("content");
	// var header = $("meta[name='_csrf_header']").attr("content");
	// $(document).ajaxSend(function(e, xhr, options) {
	// 	xhr.setRequestHeader(header, token);
	// });

	//获取标题内容
	var title = $("#recipient-name").val();
	//获取内容
	var content = $("#message-text").val();
	//发送异步请求(POST)
	$.post(
		"/discuss/add",//请求路径
		{"title":title,"content":content},//请求参数
		function (data){//回调函数
			data = $.parseJSON(data);
			// 在提示框中显示提示信息
			$("#hintBody").text(data.msg);
			//显示提示框
			$("#hintModal").modal("show");
			// 2秒后自动隐藏提示框
			setTimeout(function(){
				$("#hintModal").modal("hide");
				//刷新页面
				if(data.code == 0){
					window.location.reload();
				}
			},2000);
		}
	);
	$("#hintModal").modal("show");
	setTimeout(function(){
		$("#hintModal").modal("hide");
	}, 2000);
}