$(function (){
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
});

function like(btn, entityType, entityId, entityUserId, postId) {
    $.post(
        "/like",
        {"entityType":entityType,"entityId":entityId,"entityUserId":entityUserId,"postId":postId},
        function (data){
            data = $.parseJSON(data);
            // 在提示框中显示提示信息
            if(data.code == 0){
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?"已赞":"赞");
            }else{
                alert(data.msg)
            }
        }
    )
}

//置顶
function setTop() {
    $.post(
        "/discuss/top",
        {"discussPostId":$("#postId").val()},
        function (data){
            data = $.parseJSON(data);
            // 在提示框中显示提示信息
            if(data.code == 0){
                $("#topBtn").attr("disabled","disabled");
            }else{
                alert(data.msg);
            }
        }
    );
}

//加精
function setWonderful() {
    $.post(
        "/discuss/wonderful",
        {"discussPostId":$("#postId").val()},
        function (data){
            data = $.parseJSON(data);
            // 在提示框中显示提示信息
            if(data.code == 0){
                $("#wonderfulBtn").attr("disabled","disabled");
            }else{
                alert(data.msg);
            }
        }
    );
}

//删除
function setDelete() {
    $.post(
        "/discuss/delete",
        {"discussPostId":$("#postId").val()},
        function (data){
            data = $.parseJSON(data);
            // 在提示框中显示提示信息
            if(data.code == 0){
                window.location.href = "/index";
            }else{
                alert(data.msg);
            }
        }
    );
}