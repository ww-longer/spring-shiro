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
        <form id="outsourceAllocationRecordAddForm" method="post">
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
                           style="width:100px;height:32px">匹配</a> &nbsp;&nbsp;
                        <a href="javascript:void(0)" onclick="downloadExcel()" class="easyui-linkbutton" iconCls="icon-print"
                           style="width:100px;height:32px">导出</a> &nbsp;&nbsp;
                    </td>
                </tr>
            </table>
        </form>
    </div>
    <div data-options="region:'center',fit:true,border:false">
        <table id="outsourceDataGrid"></table>
    </div>
</div>
<script type="text/javascript">
    $(function () {
        outsourceDataGrid();
    });
    // 查询
    function outsourceDataGrid() {
        var url = "${path}/sc/collection/search";
        var columns = [[
            {width: '9%', title: '姓名', field: 'name', sortable: true},
            {width: '15%', title: '身份证', field: 'custId', sortable: true},
            {width: '10%', title: '电话', field: 'telNumber', sortable: true},
            {width: '15%', title: '借据号', field: 'ious', sortable: true},
            {width: '10%', title: '网络贷款平台', field: 'netLendingPlatform', sortable: true},
            {width: '10%', title: '委外公司', field: 'dcaDistribution', sortable: true},
            {width: '10%', title: '案件类型', field: 'theCaseDistribution', sortable: true},
            {width: '10%', title: '移交日期', field: 'turnOverDay', sortable: true},
            {width: '10%', title: '产品名称', field: 'productName', sortable: true}
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
    // 下载导出Excel
    function downloadExcel() {
        $("#custId").val($("input[name='custIds']").val());
        $("#ious").val($("input[name='iouss']").val());
        $("#downloadFile").submit();
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
</script>