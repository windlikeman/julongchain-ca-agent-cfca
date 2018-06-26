/**
 * Created by 10046663 on 2017/3/29.
 */

var pageinfo = {
    currentPage: 1,
    totalPages: 30,
    size: "normal",
    bootstrapMajorVersion: 3,
    alignment: "right",
    numberOfPages: 10,
    itemTexts: function (type, page, current) {
        switch (type) {
            case "first":
                return "首页";
            case "prev":
                return "上一页";
            case "next":
                return "下一页";
            case "last":
                return "末页";
            case "page":
                return page;
        }
    },
    onPageClicked: function(e,originalEvent,type,page){
        $('#alert-content').text("Page item clicked, type: "+type+" page: "+page);
    }
}

function getPageInfo(currentpage, totalpages, callback) {
    pageinfo.currentPage = currentpage;
    pageinfo.totalPages = totalpages;
    return pageinfo;
}

function getVueApps(domid) {
    return new Vue({
        el: domid,
        data: {
            showapp: true,
            items: [],
            events: [],
            currentpage: 1
        },
        methods: {
            getapps: function () {
                var _self = this;
                $.ajax({
                    async: true,
                    type: 'GET',
                    url: '/apps',
                    dataType: "json", //返回数据形式为json对象，注意不是字符串是对象，如果去掉本行，默认返回类型为字符串
                    success: function (data) {
                        console.log("data:" + data);
                        _self.items = data;
                    }, error: function (errorMsg) {
                        console.log("errorMsg:" + errorMsg);
                    }
                });
            },
            getevents: function (appname) {
                var _self = this;
                console.log($("#mydatetime").val());
                $.ajax({
                    async: true,
                    type: 'GET',
                    url: '/app/' + appname + '/events',
                    dataType: "json", //返回数据形式为json对象，注意不是字符串是对象，如果去掉本行，默认返回类型为字符串
                    success: function (data) {
                        console.log("data:" + data);
                        _self.showapp = false;
                        _self.events = data;
                    }, error: function (errorMsg) {
                        console.log("errorMsg:" + errorMsg);
                    }
                });
            }
        }
    });

    function handleGeteventsResult(data) {
        $('#pageLimit').bootstrapPaginator(getPageInfo(1, 30, "${eventid?c}"));
    }
}