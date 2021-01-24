// ajaxSubmit提交form表单
function updateUserInfo() {
    $('#表单ID').ajaxSubmit({
        url: url,
        type: 'POST',
        //data: $('表单ID').serialize(),
        //processData: false,
        //contentType: false,
        xhrFields: {
            withCredentials: true
        },
        crossDomain: true,
        success: function (obj) {
            // ...
        },
        error: function (obj) {
            // alert('服务器请求失败');
        }
    });
}



var a = new FormData();
//使用append方法向空的FormData对象添加字段
a.append('username','Jack');
a.append('age',17); //数字会被转换成字符串
a.append('userfile',files); //上传文件
a.append('webmasterfile',oBlob);    //二进制对象
var oReq = new XMLHttpRequest();
oReq.open('POST','submitform.php');
oReq.send(a);
//注意：值可以为Blob对象，File对象或者字符串，其它类型的值都会被自动转换为字符串处理
//
//

//使用HTML表单来初始化一个FormData对象
var a = document.getElementById("form");
var oReq = new XMLHttpRequest();
oReq.open('POST','submitform.php');
oReq.send(a);



//使用FormData异步上传用户所选择的文件
function sendForm(){
    var txt = document.getElementById('txt');
    var a = new FormData(document.getElementById('file'));
    var oReq = new XMLHttpRequest();
    oReq.open('POST','submitform.php',true);
    oReq.onload = function(oEvent){
        if(oReq.status == 200){
            txt.innerHTML = 'Uploaded!';
        }else{
            txt.innerHTML = 'Error' + oReq.status;
        }
    }
    oReq.send(a);
}


//
//使用jQuery来发送FormData，但必须要正确的设置相关选项
var a = new FormData(document.getElementById('file'));
$.ajax({
    url:'submitform.php',
    type:'POST',
    data:a,
    processData:false,//告诉jQuery不要去出去发送的数据
    contentType:false//告诉jQuery不要去设置content-type请求头
})