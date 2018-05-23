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
                                style="width:300px;height:32px"> &nbsp;&nbsp;&nbsp;&nbsp;
                    借据号:<input class="easyui-textbox" name="iouss" data-options="prompt:'请输入借据号',validType:''"
                               style="width:300px;height:32px"></td>
            </tr>
            <tr>
                <td><a href="javascript:void(0)" onclick="outsourceDataGrid()" class="easyui-linkbutton"
                       iconCls="icon-search" style="width:100px;height:32px">查询</a> &nbsp;&nbsp;
                    <a href="javascript:void(0)" onclick="matching()" class="easyui-linkbutton" iconCls="icon-sum"
                       style="width:100px;height:32px">数据更新</a> &nbsp;&nbsp;
                    <%--<a href="javascript:void(0)" onclick="downloadExcel()" class="easyui-linkbutton"
                       iconCls="icon-print"
                       style="width:100px;height:32px">导出匹配</a> &nbsp;&nbsp;--%>
                </td>
                <td>
                    <a href="javascript:void(0)" onclick="downloadAllExcel()" class="easyui-linkbutton"
                       iconCls="icon-print"
                       style="width:100px;height:32px">导出全部</a> &nbsp;&nbsp;
                    <a href="javascript:void(0)" onclick="importExp()" class="easyui-linkbutton"
                       iconCls="icon-print"
                       style="width:100px;height:32px">导入</a>
                </td>
            </tr>
        </table>
    </div>
    <div data-options="region:'center',fit:true,border:false">
        <table id="outsourceAmountDataGrid"></table>
    </div>
</div>
<script type="text/javascript">
    var excelUpload;
    $(function () {
        excelUpload = outsourceDataGrid();
    });
    // 查询
    function outsourceDataGrid() {
        var url = "${path}/sc/collection/outsourceBalance/dataGrid";
        var columns = [[
            {width: '6%', title: '姓名', field: 'name', sortable: true},
            {width: '12%', title: '身份证', field: 'custId', sortable: true},
            {width: '10%', title: '电话', field: 'telNumber', sortable: true},
            {width: '12%', title: '借据号', field: 'ious', sortable: true},
            {width: '8%', title: '最新催收金额', field: 'nowCollectionAmount', sortable: true},
            {width: '5%', title: '最新账龄', field: 'nowAgecd', sortable: true},
            {width: '10%', title: '移交日期', field: 'transfer', sortable: true},
            {width: '10%', title: '上次催收金额', field: 'lastCollectionAmount', sortable: true},
            {width: '5%', title: '上次账龄', field: 'lastAgecd', sortable: true},
            {width: '10%', title: '更新时间', field: 'updateTime', sortable: true},
            {width: '10%', title: '备注', field: 'remarks', sortable: true}
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
            {width: '15%', title: '身份证', field: 'custId'},
            {width: '15%', title: '委过的公司', field: 'wg'},
            {width: '15%', title: '公司1', field: 'ww1'},
            {width: '15%', title: '公司2', field: 'ww2'},
            {width: '15%', title: '公司3', field: 'ww3'},
            {width: '15%', title: '公司4', field: 'ww4'},
            {width: '9%', title: '公司5', field: 'ww5'}
        ]];
        var params = {
            custId: $("input[name='custIds']").val(),
            ious: $("input[name='iouss']").val()
        };
        publicDataGrid(url, columns, params);
    }

    function publicDataGrid(url, columns, params) {
        $('#outsourceAmountDataGrid').datagrid({
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
    function downloadAllExcel(){
        var url = "${path}/sc/collection/downloadAll";
        url = encodeURI(url);
        location.href = url;
    }
    // Excel 文件导入
    function importExp(){
        parent.$.modalDialog({
            title : '文件上传',
            width : 500,
            height : 180,
            href : '${path }/sc/collection/uploadPage',
            buttons : [ {
                text : '数据导入',
                handler : function() {
                    parent.$.modalDialog.openner_treeGrid = excelUpload;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#excelUploadForm');
                    f.submit();
                }
            } ]
        });
    }
</script>