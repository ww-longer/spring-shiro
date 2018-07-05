<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/commons/global.jsp" %>
<div>
    <div>
        <table>
            <tr>
                <td>
                    身份证号:<input class="easyui-textbox" name="reCustIds" data-options="prompt:'请输入身份证号',validType:''"
                                style="width:150px;height:25px"> &nbsp;&nbsp;&nbsp;&nbsp;
                    借据号:<input class="easyui-textbox" name="reIouss" data-options="prompt:'请输入借据号',validType:''"
                               style="width:150px;height:25px">&nbsp;&nbsp;&nbsp;&nbsp;
                    公司:<select id="repaymentCompanys" style="width:120px;height:25px;border-radius: 5px;">
                        <option value=""></option>
                        <c:forEach items="${companies}" var="item">
                            <option value="${item.company}">${item.company}</option>
                        </c:forEach>
                        </select>
                    &nbsp;&nbsp;&nbsp;&nbsp;
                    还款开始时间:<input id="startRepaymentDateTimes" name="startRepaymentDateTimes" placeholder="选择开始时间"
                                  onclick="WdatePicker({maxDate:'#F{$dp.$D(\'endRepaymentDateTimes\')}',dateFmt:'yyyy-MM-dd HH:mm:ss'})"
                                  style="width:130px;height:20px;border-radius: 5px;"/>&nbsp;&nbsp;&nbsp;&nbsp;
                    还款结束时间:<input id="endRepaymentDateTimes" name="endRepaymentDateTimes" placeholder="选择结束时间"
                                  onclick="WdatePicker({minDate:'#F{$dp.$D(\'startRepaymentDateTimes\')}',dateFmt:'yyyy-MM-dd HH:mm:ss'})"
                                  style="width:130px;height:20px;border-radius: 5px;"/>&nbsp;&nbsp;&nbsp;&nbsp;
                </td>
            </tr>
            <tr>
                <td>
                    <shiro:hasPermission name="/sc/collection/refund/dataGrid">
                        <a href="javascript:void(0)" onclick="outsourceRefundDataGrid()" class="easyui-linkbutton"
                           iconCls="icon-search" style="width:100px;height:25px">查询</a> &nbsp;&nbsp;
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/sc/collection/refund/downLoadRepaymentExp">
                        <a href="javascript:void(0)" onclick="importRepaymentExp()" class="easyui-linkbutton"
                           iconCls="icon-print" style="width:100px;height:25px">导出</a> &nbsp;&nbsp;
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/sc/collection/refund/downLoadAmountAndRepaymentExp">
                        <a href="javascript:void(0)" onclick="importAmountAndRepaymentExp()" class="easyui-linkbutton"
                           iconCls="icon-print" style="width:150px;height:25px">导出余额+还款</a> &nbsp;&nbsp;
                    </shiro:hasPermission>
                    <a href="javascript:void(0)" onclick="importRepaymentHisExp()" class="easyui-linkbutton"
                       iconCls="icon-print" style="width:150px;height:25px">导入历史还款</a> &nbsp;&nbsp;
                </td>
            </tr>
        </table>
    </div>
    <div data-options="region:'center',fit:true,border:false">
        <table id="outsourceRefundId"></table>
    </div>
</div>
<script type="text/javascript">
    var excelAmountUpload;
    $(function () {
        excelAmountUpload = outsourceRefundDataGrid();
    });
    // 查询
    function outsourceRefundDataGrid() {
        var url = "${path}/sc/collection/refund/dataGrid";
        var columns = [[
            {width: '5%', title: '姓名', field: 'name', sortable: true},
            {width: '10%', title: '身份证', field: 'custId', sortable: true},
            {width: '6%', title: '电话', field: 'telNumber', sortable: true},
            {width: '12%', title: '借据号', field: 'ious', sortable: true},
            {width: '5%', title: '还款金额', field: 'curAmount', sortable: true},
            {width: '8%', title: '还款日期', field: 'repaymentDate', sortable: true},
            {width: '8%', title: '移交日期', field: 'transfer', sortable: true},
            {width: '4%', title: '移交账龄', field: 'handOverAgecd', sortable: true},
            {width: '4%', title: '移交金额', field: 'handOverAmount', sortable: true},
            {width: '3%', title: '公司', field: 'company', sortable: true},
            {
                width: '4%', title: '还款情况', field: 'isSumRefund', sortable: true,
                formatter: function (value, row, index) {
                    switch (value) {
                        case 0:
                            return '全额';
                        case 1:
                            return '部分';
                    }
                }
            },
            {width: '8%', title: '创建时间', field: 'creatDate', sortable: true},
            {width: '8%', title: '备注', field: 'remarks', sortable: true},
            {
                field: 'action',
                title: '操作',
                width: '5%',
                formatter: function (value, row, index) {
                    var str = '';
                    str += $.formatString('<a href="javascript:void(0)" class="resource-easyui-linkbutton-edit" ' +
                            'data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="editRepaymentFun(\'{0}\');" >修改</a>', row.id);
                    return str;
                }
            }
        ]];
        var params = {
            custId: $("input[name='reCustIds']").val(),
            ious: $("input[name='reIouss']").val(),
            company: $("#repaymentCompanys").val(),
            startRepaymentDateTime: $("input[name='startRepaymentDateTimes']").val(),
            endRepaymentDateTime: $("input[name='endRepaymentDateTimes']").val()

        };
        publicRefundDataGrid(url, columns, params);
    }

    function publicRefundDataGrid(url, columns, params) {
        $('#outsourceRefundId').datagrid({
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
            queryParams: params,
            onLoadSuccess: function (data) {
                $('.resource-easyui-linkbutton-edit').linkbutton({text: '编辑'});
            }
        });
    }
    /**
     * 导出还款
     */
    function importRepaymentExp() {
        var url = "${path}/sc/collection/refund/downLoadRepaymentExp?";
        var repaymentCompanys = $("#repaymentCompanys").val();
        var startRepaymentDateTimes = $("input[name='startRepaymentDateTimes']").val();
        var endRepaymentDateTimes = $("input[name='endRepaymentDateTimes']").val();
        url += "company=" + repaymentCompanys;
        url += "&startRepaymentDateTime=" + startRepaymentDateTimes;
        url += "&endRepaymentDateTime=" + endRepaymentDateTimes;
        location.href = encodeURI(url);
    }
    /**
     * 导出余额 + 还款
     */
    function importAmountAndRepaymentExp() {
        var url = "${path}/sc/collection/refund/downLoadAmountAndRepaymentExp?";
        var repaymentCompanys = $("#repaymentCompanys").val();
        var startRepaymentDateTimes = $("input[name='startRepaymentDateTimes']").val();
        var endRepaymentDateTimes = $("input[name='endRepaymentDateTimes']").val();
        url += "company=" + repaymentCompanys;
        url += "&startRepaymentDateTime=" + startRepaymentDateTimes;
        url += "&endRepaymentDateTime=" + endRepaymentDateTimes;
        location.href = encodeURI(url);
    }

    /**
     * 修改还款数据
     * @param id
     */
    function editRepaymentFun(id) {
        parent.$.modalDialog({
            title: '修改还款',
            width: 500,
            height: 300,
            href: '${path }/sc/collection/refund/updateRepaymentPage?id=' + id,
            buttons: [{
                text: '确定',
                handler: function () {
                    parent.$.modalDialog.openner_treeGrid = excelAmountUpload;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#updateRepaymentForm');
                    f.submit();
                }
            }]
        });
    }

    /**
     * 导入历史还款记录
     */
    function importRepaymentHisExp() {
        parent.$.modalDialog({
            title: '导入历史还款',
            width: 500,
            height: 200,
            href: '${path}/sc/collection/refund/uploadRepaymentHisPage',
            buttons: [{
                text: '确定',
                handler: function () {
                    //因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    parent.$.modalDialog.openner_treeGrid = excelAmountUpload;
                    var f = parent.$.modalDialog.handler.find('#uploadRepaymentForm');
                    f.submit();
                }
            }]
        });
    }

</script>