<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/commons/global.jsp" %>
<div>
    <div>
        <form id="downloadFile" action="${path}/sc/collection/download" method="post">
            <input id="custId" name="custId" type="hidden"/>
            <input id="ious" name="ious" type="hidden"/>
        </form>
    </div>
    <div>
        <table>
            <tr>
                <td>身份证号:<input class="easyui-textbox" name="custIds" data-options="prompt:'请输入身份证号',validType:''"
                                style="width:300px;height:25px"> &nbsp;&nbsp;&nbsp;&nbsp;
                    借据号:<input class="easyui-textbox" name="iouss" data-options="prompt:'请输入借据号',validType:''"
                               style="width:300px;height:25px"></td>
            </tr>
            <tr>
                <td>
                    <shiro:hasPermission name="/sc/collection/search">
                        <a href="javascript:void(0)" onclick="outsourceRecordDataGrid()" class="easyui-linkbutton"
                           iconCls="icon-search" style="width:100px;height:25px">查询</a> &nbsp;&nbsp;
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/sc/collection/matching">
                        <a href="javascript:void(0)" onclick="matching()" class="easyui-linkbutton" iconCls="icon-sum"
                           style="width:100px;height:25px">匹配</a> &nbsp;&nbsp;
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/sc/collection/download">
                        <a href="javascript:void(0)" onclick="downloadExcel()" class="easyui-linkbutton"
                           iconCls="icon-print"
                           style="width:100px;height:25px">导出匹配</a> &nbsp;&nbsp;
                    </shiro:hasPermission>
                </td>
                <td>
                    <shiro:hasPermission name="/sc/collection/downloadAll">
                        <a href="javascript:void(0)" onclick="downloadAllExcel()" class="easyui-linkbutton"
                           iconCls="icon-print"
                           style="width:100px;height:25px">导出全部</a> &nbsp;&nbsp;
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/sc/collection/uploadPage">
                        <a href="javascript:void(0)" onclick="importExp()" class="easyui-linkbutton"
                           iconCls="icon-print"
                           style="width:120px;height:25px">大总表导入</a>
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/sc/collection/uploadAmountAndRecordPage">
                        <a href="javascript:void(0)" onclick="importAmountAndRecordExp()" class="easyui-linkbutton"
                           iconCls="icon-print"
                           style="width:120px;height:25px">移交数据导入</a>
                    </shiro:hasPermission>
                </td>
            </tr>
        </table>
    </div>
    <div data-options="region:'center',fit:true,border:false">
        <table id="outsourceDataGrid"></table>
    </div>
</div>
<script type="text/javascript">
    var excelUpload;
    $(function () {
        excelUpload = outsourceRecordDataGrid();
    });
    // 查询
    function outsourceRecordDataGrid() {
        var url = "${path}/sc/collection/search";
        var columns = [[
            {width: '6%', title: '姓名', field: 'name', sortable: true},
            {width: '10%', title: '身份证', field: 'custId', sortable: true},
            {width: '8%', title: '电话', field: 'telNumber', sortable: true},
            {width: '10%', title: '借据号', field: 'ious', sortable: true},
            {width: '5%', title: '分期总金额', field: 'totalAmount', sortable: true},
            {width: '5%', title: '逾期金额', field: 'amountOverride', sortable: true},
            {width: '4%', title: '账龄', field: 'ageCd', sortable: true},
            {width: '4%', title: '逾期天数', field: 'overdue', sortable: true},
            {width: '5%', title: '网络贷款平台', field: 'netLendingPlatform', sortable: true},
            {width: '5%', title: '委外公司', field: 'dcaDistribution', sortable: true},
            {width: '5%', title: '案件类型', field: 'theCaseDistribution', sortable: true},
            {width: '6%', title: '移交日期', field: 'turnOverDay', sortable: true},
            {width: '8%', title: '产品名称', field: 'productName', sortable: true},
            {width: '8%', title: '描述', field: 'remarks', sortable: true}
        ]];
        var params = {
            custId: $("input[name='custIds']").val(),
            ious: $("input[name='iouss']").val()
        };
        publicDataGrid(url, columns, params);
    }

    // 匹配
    function matching() {
        var url = "${path}/sc/collection/matching";
        var columns = [[
            {width: '10%', title: '身份证', field: 'custId'},
            {width: '20%', title: '委过的公司', field: 'wg'},
            {width: '7%', title: '公司1', field: 'ww1'},
            {width: '7%', title: '公司2', field: 'ww2'},
            {width: '7%', title: '公司3', field: 'ww3'},
            {width: '7%', title: '公司4', field: 'ww4'},
            {width: '7%', title: '公司5', field: 'ww5'},
            {width: '7%', title: '公司6', field: 'ww6'},
            {width: '7%', title: '公司7', field: 'ww7'},
            {width: '7%', title: '公司8', field: 'ww8'},
            {width: '7%', title: '公司9', field: 'ww9'},
            {width: '7%', title: '公司10', field: 'ww10'}
        ]];
        var params = {
            custId: $("input[name='custIds']").val(),
            ious: $("input[name='iouss']").val()
        };
        publicDataGrid(url, columns, params);
    }

    function publicDataGrid(url, columns, params) {
        $('#outsourceDataGrid').datagrid({
            url: url,
            striped: true,
            rownumbers: true,
            pagination: true,
            singleSelect: true,
            idField: 'id',
            sortName: 'id',
            sortOrder: 'asc',
            pageSize: 25,
            pageList: [25, 50, 100, 200],
            columns: columns,
            queryParams: params
        });
    }

    // 下载导出Excel
    function downloadExcel() {
        $("#custId").val($("input[name='custIds']").val());
        $("#ious").val($("input[name='iouss']").val());
        $("#downloadFile").submit();
    }
    // 下载导出全部数据
    function downloadAllExcel() {
        var url = "${path}/sc/collection/downloadAll";
        url = encodeURI(url);
        location.href = url;
    }
    // Excel 文件导入
    function importExp() {
        parent.$.modalDialog({
            title: '文件上传',
            width: 500,
            height: 200,
            href: '${path }/sc/collection/uploadPage',
            buttons: [{
                text: '数据导入',
                handler: function () {
                    parent.$.modalDialog.openner_treeGrid = excelUpload;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#excelUploadForm');
                    f.submit();
                }
            }]
        });
    }
    // 移交导入
    function importAmountAndRecordExp() {
        parent.$.modalDialog({
            title: '文件上传',
            width: 500,
            height: 200,
            href: '${path }/sc/collection/uploadAmountAndRecordPage',
            buttons: [{
                text: '数据导入',
                handler: function () {
                    parent.$.modalDialog.openner_treeGrid = excelUpload;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#uploadAmountAndRecordForm');
                    f.submit();
                }
            }]
        });
    }
</script>