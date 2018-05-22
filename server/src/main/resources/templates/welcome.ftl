<!DOCTYPE html>
<html>
<head>
    <title>Submit CMBC Message</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="keywords" content="Device Info">
    <meta name="description" content="测试工具包(民生银行)接口">

    <!-- Bootstrap -->
    <link href="/css/bootstrap.min.css" rel="stylesheet">
    <!-- styles -->
    <link href="/css/styles.css" rel="stylesheet">
    <link href="/css/index.css" rel="stylesheet">

    <!-- extras link-->
    <link rel="apple-touch-icon" href="//mindmup.s3.amazonaws.com/lib/img/apple-touch-icon.png">
    <link href="http://twitter.github.com/bootstrap/assets/js/google-code-prettify/prettify.css" rel="stylesheet">
    <link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/css/bootstrap-combined.no-icons.min.css"
          rel="stylesheet">
    <link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/css/bootstrap-responsive.min.css"
          rel="stylesheet">
    <link href="http://netdna.bootstrapcdn.com/font-awesome/3.0.2/css/font-awesome.css" rel="stylesheet">

    <script src="/js/vue.js"></script>
    <script src="http://cdn.bootcss.com/jquery/1.9.1/jquery.min.js"></script>
    <script src="http://cdn.bootcss.com/twitter-bootstrap/2.3.1/js/bootstrap.min.js"></script>
</head>

<body class="login-bg">
<div class="header">
    <div class="container">
        <div class="row">
            <div class="col-md-12">
                <!-- Logo -->
                <div class="logo">
                    <h1 style="color: white">测试工具包(民生银行)接口</h1>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="container" id="apps">
    <div class="hero-unit">
        <hr>
        <span>Encrypt info is :</span>
        <div class="row">
            <div id="message" class="col-md-12" contenteditable="true">
                <textarea style="white-space: pre-line; width:100%; height:600px" v-model="message"
                          placeholder="add message"></textarea>
            </div>
        </div>

        <span>Decrypt info is:</span>
        <div class="row">
            <div id="decryptMessage" class="col-md-12" contenteditable="true">
                <textarea style="white-space: pre-line; width:100%; height:600px" v-model="decryptMessage"
                          placeholder="decrypt by cmbc"></textarea>
            </div>
        </div>

        <div id="tips" class="form-group" hidden="hidden">
            <p class="form-control-static" style="color:red">{{errormsg}}</p>
        </div>
        <div>
            <#--<button type="submit" class="btn btn-primary" v-on:click="signAndEncryptMessage ">SignAndEncryptMessage</button>-->
            <button type="submit" class="btn btn-primary" v-on:click="decryptAndVerifyMessage">DecryptAndVerifyMessage</button>
            <button type="submit" class="btn btn-primary" v-on:click="changeCert ">换证</button>
            <#--<button type="submit" class="btn btn-primary" v-on:click="decryptAndVerifyFile">DecryptAndVerifyFile</button>-->
            <#--<button type="submit" class="btn btn-primary" v-on:click="p7DetachMessageSign ">P7DetachMessageSign</button>-->
            <#--<button type="submit" class="btn btn-primary" v-on:click="p7DetachMessageVerify">P7DetachMessageVerify</button>-->
            <#--<button type="submit" class="btn btn-primary" v-on:click="envelopeEncryptMessage">EnvelopeEncryptMessage</button>-->
            <#--<button type="submit" class="btn btn-primary" v-on:click="envelopeDecryptMessage">EnvelopeDecryptMessage</button>-->
        </div>
    </div>
</div>


<!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
<script src="/js/jquery-3.2.0.js"></script>
<!-- Include all compiled plugins (below), or include individual files as needed -->
<script src="/js/bootstrap.min.js"></script>
<script src="/js/custom.js"></script>

<script type="text/javascript">
    var apps = new Vue({
        el: '#apps',
        data: {
            message:"{\n" +
            "  \"message\": \"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\",\n" +
            "  \"type\": 1\n" +
            "}",
            decryptMessage:"haha"
        },
        errormsg:"",
        methods: {
            changeCert: function () {
                $.ajax({
                    type: "post",
                    async: true,  //异步请求
                    url: "/changeCert",
                    contentType: "application/json",
                    data: this.message,
                    dataType: "json",  //返回数据形式为json对象
                    success: function (result) {
                        //请求成功时执行该函数内容，result即为服务器返回的json对象
                        $("#tips").hide();
                        var result_str_pretty = JSON.stringify(result, null, 4); //使用四个空格缩进
                        alert(result_str_pretty);
                    },
                    error: function (errorMsg) {
                        $("#tips").show();
                        apps.errormsg = errorMsg;
                        console.log("errorMsg:" + errorMsg);
                        alert(errorMsg);
                    }
                });
            },
            decryptAndVerifyMessage: function () {
                alert(this.message);
                $.ajax({
                    type: "post",
                    async: true,  //异步请求
                    url: "/decrypt/verify/message",
                    contentType: "application/json",
                    data: this.message,
                    dataType: "json",  //返回数据形式为json对象
                    success: function (result) {
                        //请求成功时执行该函数内容，result即为服务器返回的json对象
                        $("#tips").hide();
                        var result_str_pretty = JSON.stringify(result, null, 4); //使用四个空格缩进
                        console.log("result_str_pretty:" + result_str_pretty);
                        this.decryptMessage = result.message;
                        alert(this.decryptMessage);
                    },
                    error: function (errorMsg) {
                        $("#tips").show();
                        apps.errormsg = errorMsg;
                        console.log("errorMsg:" + errorMsg);
                        alert(errorMsg);
                    }
                });
            }
        }
    })

</script>

</body>
</html>