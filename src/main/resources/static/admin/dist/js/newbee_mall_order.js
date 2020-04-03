$(function () {
    $("#jqGrid").jqGrid({
        url: '/admin/orders/list',// 请求后台json数据的url
        datatype: "json",// 后台返回的数据格式
        colModel: [
            {label: 'id', name: 'orderId', index: 'orderId', width: 50, key: true, hidden: true},
            {label: '订单号', name: 'orderNo', index: 'orderNo', width: 120},
            {label: '订单总价', name: 'totalPrice', index: 'totalPrice', width: 60},
            {label: '订单状态', name: 'orderStatus', index: 'orderStatus', width: 80, formatter: orderStatusFormatter},
            {label: '支付方式', name: 'payType', index: 'payType', width: 80,formatter:payTypeFormatter},
            {label: '收件人地址', name: 'userAddress', index: 'userAddress', width: 10, hidden: true},
            {label: '收件人姓名', name: 'userName', index: 'userName', width: 10, hidden: true},
            {label: '收件人手机号', name: 'userPhone', index: 'userPhone', width: 10, hidden: true},
            {label: '创建时间', name: 'createTime', index: 'createTime', width: 120},
            {label: '操作', name: 'createTime', index: 'createTime', width: 120, formatter: operateFormatter}
        ],// 列表信息：表头 宽度 是否显示 渲染参数 等属性
        height: 760,// 表格高度  可自行调节
        rowNum: 20,// 默认一页显示多少条数据 可自行调节
        rowList: [20, 50, 80],// 翻页控制条中 每页显示记录数可选集合
        styleUI: 'Bootstrap', // 主题 这里选用的是Bootstrap主题
        loadtext: '信息读取中...',// 数据加载时显示的提示信息
        rownumbers: false,// 是否显示行号，默认值是false，不显示
        rownumWidth: 20,// 行号列的宽度
        autowidth: true,// 宽度自适应
        multiselect: true,// 是否可以多选
        // reloadAfterSubmit: true, //成功保存记录后重新加载grid数据
        pager: "#jqGridPager",// 分页信息DOM
        jsonReader: {
            root: "data.list",//数据列表模型
            page: "data.currPage", //数据页码
            total: "data.totalPage",//数据总页码
            records: "data.totalCount"//数据总记录数
        },
        // 向后台请求的参数
        prmNames: {
            page: "page",
            rows: "limit",
            order: "order",
        },
        // 数据加载完成并且DOM创建完毕之后的回调函数
        gridComplete: function () {
            //隐藏grid底部滚动条
            $("#jqGrid").closest(".ui-jqgrid-bdiv").css({"overflow-x": "hidden"});
        }
    });

    $(window).resize(function () {
        $("#jqGrid").setGridWidth($(".card-body").width());
    });

    function operateFormatter(cellvalue, rowObject) {
        return "<a href=\'##\' onclick=openOrderItems(" + rowObject.rowId + ")>查看订单信息</a>" +
            "<br>" +
            "<a href=\'##\' onclick=openExpressInfo(" + rowObject.rowId + ")>查看收件人信息</a>";
    }

    function orderStatusFormatter(cellvalue) {
        //订单状态:0.待支付 1.已支付 2.配货完成 3:出库成功 4.交易成功 -1.手动关闭 -2.超时关闭 -3.商家关闭
        if (cellvalue == 0) {
            return "待支付";
        }
        if (cellvalue == 1) {
            return "已支付";
        }
        if (cellvalue == 2) {
            return "配货完成";
        }
        if (cellvalue == 3) {
            return "出库成功";
        }
        if (cellvalue == 4) {
            return "交易成功";
        }
        if (cellvalue == -1) {
            return "手动关闭";
        }
        if (cellvalue == -2) {
            return "超时关闭";
        }
        if (cellvalue == -3) {
            return "商家关闭";
        }
    }

    function payTypeFormatter(cellvalue) {
        //支付类型:0.无 1.支付宝支付 2.微信支付
        if (cellvalue == 0) {
            return "无";
        }
        if (cellvalue == 1) {
            return "支付宝支付";
        }
        if (cellvalue == 2) {
            return "微信支付";
        }
    }

    $(window).resize(function () {
        $("#jqGrid").setGridWidth($(".card-body").width());
    });

});

/**
 * jqGrid重新加载
 */
function reload() {
    initFlatPickr();
    var page = $("#jqGrid").jqGrid('getGridParam', 'page');
    $("#jqGrid").jqGrid('setGridParam', {
        page: page
    }).trigger("reloadGrid");
}

/**
 * 查看订单项信息
 * @param orderId
 */
function openOrderItems(orderId) {
    $('.modal-title').html('订单详情');
    $.ajax({
        type: 'GET',//方法类型
        url: '/admin/order-items/' + orderId,
        contentType: 'application/json',
        success: function (result) {
            if (result.resultCode == 200) {
                $('#orderItemModal').modal('show');
                var itemString = '';
                for (i = 0; i < result.data.length; i++) {
                    itemString += result.data[i].goodsName + ' x ' + result.data[i].goodsCount + ' 商品编号 ' + result.data[i].goodsId + ";<br>";
                }
                $("#orderItemString").html(itemString);
            } else {
                swal(result.message, {
                    icon: "error",
                });
            }
            ;
        },
        error: function () {
            swal("操作失败", {
                icon: "error",
            });
        }
    });
}

/**
 * 查看收件人信息
 * @param orderId
 */
function openExpressInfo(orderId) {
    var rowData = $("#jqGrid").jqGrid("getRowData", orderId);
    $('.modal-title').html('收件信息');
    $('#expressInfoModal').modal('show');
    $("#userNameInfo").html(rowData.userName);
    $("#userPhoneInfo").html(rowData.userPhone);
    $("#userAddressInfo").html(rowData.userAddress);
}

/**
 * 订单编辑
 */
function orderEdit() {
    reset();
    var id = getSelectedRow();
    if (id == null) {
        return;
    }
    var rowData = $("#jqGrid").jqGrid("getRowData", id);
    $('.modal-title').html('订单编辑');
    $('#orderInfoModal').modal('show');
    $("#orderId").val(id);
    $("#userName").val(rowData.userName);
    $("#userPhone").val(rowData.userPhone);
    $("#userAddress").val(rowData.userAddress);
    $("#totalPrice").val(rowData.totalPrice);
}


//绑定modal上的保存按钮
$('#saveButton').click(function () {
    var totalPrice = $("#totalPrice").val();
    var userName = $("#userName").val();
    var userPhone = $("#userPhone").val();
    var userAddress = $("#userAddress").val();
    var id = getSelectedRowWithoutAlert();
    var url = '/admin/orders/update';
    var data = {
        "orderId": id,
        "totalPrice": totalPrice,
        "userName": userName,
        "userPhone": userPhone,
        "userAddress": userAddress
    };
    $.ajax({
        type: 'POST',//方法类型
        url: url,
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (result) {
            if (result.resultCode == 200) {
                $('#orderInfoModal').modal('hide');
                swal("保存成功", {
                    icon: "success",
                });
                $("#jqGrid").trigger("reloadGrid");
            } else {
                $('#orderInfoModal').modal('hide');
                swal(result.message, {
                    icon: "error",
                });
            }
            ;
        },
        error: function () {
            swal("操作失败", {
                icon: "error",
            });
        }
    });
});

/**
 * 订单拣货完成
 */
function orderCheckDone() {
    var ids = getSelectedRows();
    if (ids == null) {
        return;
    }
    var orderNos = '';
    for (i = 0; i < ids.length; i++) {
        var rowData = $("#jqGrid").jqGrid("getRowData", ids[i]);
        if (rowData.orderStatus != '已支付') {
            orderNos += rowData.orderNo + " ";
        }
    }
    if (orderNos.length > 0 & orderNos.length < 100) {
        swal(orderNos + "订单的状态不是支付成功无法执行配货完成操作", {
            icon: "error",
        });
        return;
    }
    if (orderNos.length >= 100) {
        swal("你选择了太多状态不是支付成功的订单，无法执行配货完成操作", {
            icon: "error",
        });
        return;
    }
    swal({
        title: "确认弹框",
        text: "确认要执行配货完成操作吗?",
        icon: "warning",
        buttons: true,
        dangerMode: true,
    }).then((flag) => {
            if (flag) {
                $.ajax({
                    type: "POST",
                    url: "/admin/orders/checkDone",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function (r) {
                        if (r.resultCode == 200) {
                            swal("配货完成", {
                                icon: "success",
                            });
                            $("#jqGrid").trigger("reloadGrid");
                        } else {
                            swal(r.message, {
                                icon: "error",
                            });
                        }
                    }
                });
            }
        }
    )
    ;
}

/**
 * 订单出库
 */
function orderCheckOut() {
    var ids = getSelectedRows();
    if (ids == null) {
        return;
    }
    var orderNos = '';
    for (i = 0; i < ids.length; i++) {
        var rowData = $("#jqGrid").jqGrid("getRowData", ids[i]);
        if (rowData.orderStatus != '已支付' && rowData.orderStatus != '配货完成') {
            orderNos += rowData.orderNo + " ";
        }
    }
    if (orderNos.length > 0 & orderNos.length < 100) {
        swal(orderNos + "订单的状态不是支付成功或配货完成无法执行出库操作", {
            icon: "error",
        });
        return;
    }
    if (orderNos.length >= 100) {
        swal("你选择了太多状态不是支付成功或配货完成的订单，无法执行出库操作", {
            icon: "error",
        });
        return;
    }
    swal({
        title: "确认弹框",
        text: "确认要执行出库操作吗?",
        icon: "warning",
        buttons: true,
        dangerMode: true,
    }).then((flag) => {
            if (flag) {
                $.ajax({
                    type: "POST",
                    url: "/admin/orders/checkOut",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function (r) {
                        if (r.resultCode == 200) {
                            swal("出库成功", {
                                icon: "success",
                            });
                            $("#jqGrid").trigger("reloadGrid");
                        } else {
                            swal(r.message, {
                                icon: "error",
                            });
                        }
                    }
                });
            }
        }
    )
    ;
}

function closeOrder() {
    var ids = getSelectedRows();
    if (ids == null) {
        return;
    }
    swal({
        title: "确认弹框",
        text: "确认要关闭订单吗?",
        icon: "warning",
        buttons: true,
        dangerMode: true,
    }).then((flag) => {
            if (flag) {
                $.ajax({
                    type: "POST",
                    url: "/admin/orders/close",
                    contentType: "application/json",
                    data: JSON.stringify(ids),
                    success: function (r) {
                        if (r.resultCode == 200) {
                            swal("关闭成功", {
                                icon: "success",
                            });
                            $("#jqGrid").trigger("reloadGrid");
                        } else {
                            swal(r.message, {
                                icon: "error",
                            });
                        }
                    }
                });
            }
        }
    )
    ;
}


function reset() {
    $("#totalPrice").val(0);
    $("#userAddress").val('');
    $('#edit-error-msg').css("display", "none");
}
/*
模糊查询订单信息
 */
function orderSearch() {
    var orderNo = $('#orderNo').val() || '';
    var orderStatus = $('#orderStatus').val() || '';
    var startTime = $('#startTime').val() || '';
    var endTime = $('#endTime').val() || ''
    $("#jqGrid").jqGrid('setGridParam', {
        page: 1,
        postData: {
            orderNo: orderNo,
            orderStatus: orderStatus,
            startTime: startTime,
            endTime: endTime
        }
    }).trigger("reloadGrid");
}