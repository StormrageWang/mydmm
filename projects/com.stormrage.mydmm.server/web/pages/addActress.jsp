<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>添加演员</title>
<script src="js/jquery/jquery-1.11.1.js"></script>
<script type="text/javascript">
function addActress(){
	var servletUrl = $("#actressServletUrl").attr("href");
	var actressUrl = $("#actressUrlInput").val();
	var data = {"actressUrl": actressUrl};
	$.post(servletUrl, data, function(data, status){
		//alert(data);
	});
}
</script>
</head>
<body>
<label style="padding-left: 5px;" >url：</label>
<a id="actressServletUrl" href="<%=request.getContextPath()%>/servlet/ActressServlet" style="visibility: false;" ></a>
<input id="actressUrlInput" type="text" style="width:800px;" />
<button type="button" onclick="javascript: addActress();">提交</button>
</body>
</html>