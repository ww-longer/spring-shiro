<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/commons/global.jsp" %>
<div>
    <div>
        <table>
            <tr>
                <td>
                    身份证号:<input class="easyui-textbox" name="naiCustIds" data-options="prompt:'请输入身份证号',validType:''"
                                style="width:150px;height:25px"> &nbsp;&nbsp;&nbsp;&nbsp;
                    借据号:<input class="easyui-textbox" name="naiIouss" data-options="prompt:'请输入借据号',validType:''"
                               style="width:150px;height:25px">&nbsp;&nbsp;&nbsp;&nbsp;
                    公司:<select id="naiCompanys" style="width:120px;height:25px;border-radius: 5px;">
                        <option value=""></option>
                        <c:forEach items="${companies}" var="item">
                            <option value="${item.company}">${item.company}</option>
                        </c:forEach>
                    </select>&nbsp;&nbsp;&nbsp;&nbsp;
                </td>
            </tr>
            <tr>
                <td>
                    <shiro:hasPermission name="/sc/collection/newAddIous/dataGrid">
                        <a href="javascript:void(0)" onclick="newAddIousDataGrid()" class="easyui-linkbutton"
                           iconCls="icon-search" style="width:100px;height:25px">查询</a> &nbsp;&nbsp;
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/sc/collection/newAddIous/importNewAddIousPage">
                        <a href="javascript:void(0)" onclick="importDataOnStockPage()" class="easyui-linkbutton"
                           iconCls="icon-print" style="width:120px;height:25px">导入上次清单</a> &nbsp;&nbsp;
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/sc/collection/newAddIous/batchDispose">
                        <a href="javascript:void(0)" onclick="batchDispose()" class="easyui-linkbutton"
                           iconCls="icon-print" style="width:150px;height:25px">批量委外</a> &nbsp;&nbsp;
                    </shiro:hasPermission>
                </td>
            </tr>
        </table>
    </div>
    <div data-options="region:'center',fit:true,border:false">
        <table id="naIousId"></table>
    </div>
    <form id="batchDisposeId" method="post">
        <input id="pdlist" name="iouss" type="hidden">
    </form>
</div>
<script type="text/javascript">
    var naIousId;
    var caseAddToAmount;
    var clickId;
    $(function () {
        naIousId = newAddIousDataGrid();
        caseAddToAmount = $('#batchDisposeId').form({
            url: '${path}/sc/collection/newAddIous/batchDispose',
            onSubmit: function () {
                xz();
                progressLoad();
                var isValid = $(this).form('validate');
                if (!isValid) {
                    progressClose();
                }
                return isValid;
            },
            success: function (result) {
                progressClose();
                result = $.parseJSON(result);
                if (result.success) {
                    newAddIousDataGrid();
                } else {
                    parent.$.messager.alert('提示', eval(result.msg), 'warning');
                }
            }
        });
    });
    // 查询
    function newAddIousDataGrid() {
        var url = "${path}/sc/collection/newAddIous/dataGrid";
        var columns = [[
            {
                field: 'ck',
                title: '<input id=\"detailcheckbox\" class="detailcheckbox" type=\"checkbox\"  >',
                formatter: function (value, rec, rowIndex) {
                    return "<input type=\"checkbox\"  name=\"plList\"   value=\"" + rec.ious + "\" >";
                }
            },
            {width: '15%', title: '身份证', field: 'custId', sortable: true},
            {width: '15%', title: '借据号', field: 'ious', sortable: true},
            {width: '10%', title: '最新逾期金额', field: 'nowCollectionAmount', sortable: true},
            {width: '10%', title: '最新账龄', field: 'nowAgecd', sortable: true},
            {width: '10%', title: '最新逾期天数', field: 'overdue', sortable: true},
            {width: '10%', title: '预分配公司', field: 'company', sortable: true},
            {width: '12%', title: '更新时间', field: 'updateTime', sortable: true},
            {width: '12%', title: '创建时间', field: 'creatDate', sortable: true}
        ]];
        var params = {
            custId: $("input[name='naiCustIds']").val(),
            ious: $("input[name='naiIouss']").val(),
            company: $("#naiCompanys").val()
        };
        publicNaIousDataGrid(url, columns, params);
        $(".detailcheckbox").click(checkbox);
    }
    function publicNaIousDataGrid(url, columns, params) {
        $('#naIousId').datagrid({
            url: url,
            striped: true,
            rownumbers: true,
            pagination: true,
            singleSelect: true,
            checkOnSelect: false,
            selectOnCheck: false,
            idField: 'id',
            sortName: 'id',
            sortOrder: 'asc',
            pageSize: 200,
            pageList: [50, 100, 200, 500],
            columns: columns,
            queryParams: params
        });
    }
    //全选
    function checkbox() {
        if ($(".detailcheckbox").is(':checked')) {
            $("input[name='plList']").prop("checked", true);
        } else {
            $("input[name='plList']").prop("checked", false);
        }
        xz();
    }
    function xz() {
        var items = $("input[name='plList']");
        var result = "";
        $.each(items, function (index, item) {
            if ($(item).is(':checked')) {
                result += item.value + ",";
            }
        });
        $("#pdlist").val(result);
    }

    // Excel 文件导入
    function importDataOnStockPage() {
        parent.$.modalDialog({
            title: '文件上传',
            width: 520,
            height: 200,
            href: '${path }/sc/collection/newAddIous/importDataOnStockPage',
            buttons: [{
                text: '数据导入',
                handler: function () {
                    parent.$.modalDialog.openner_treeGrid = naIousId;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#excelDataOnStockUploadForm');
                    f.submit();
                }
            }]
        });
    }

    function batchDispose() {
        $("#batchDisposeId").submit();
    }
</script>