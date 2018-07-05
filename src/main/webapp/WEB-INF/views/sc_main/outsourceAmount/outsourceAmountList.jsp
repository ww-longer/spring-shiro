<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/commons/global.jsp" %>
<div>
    <div>
        <table>
            <tr>
                <td> 姓名:<input class="easyui-textbox" name="amountNames" data-options="prompt:'请输入姓名',validType:''"
                               style="width:120px;height:25px;"> &nbsp;&nbsp;&nbsp;&nbsp;
                    身份证号:<input class="easyui-textbox" name="amountCustIds" data-options="prompt:'请输入身份证号',validType:''"
                                style="width:180px;height:25px;"> &nbsp;&nbsp;&nbsp;&nbsp;
                    借据号:<input class="easyui-textbox" name="amountIouss" data-options="prompt:'请输入借据号',validType:''"
                               style="width:180px;height:25px; "> &nbsp;&nbsp;&nbsp;&nbsp;
                    公司:<select id="companys" style="width:120px;height:25px;border-radius: 5px;">
                        <option value=""></option>
                        <c:forEach items="${companies}" var="item">
                            <option value="${item.company}">${item.company}</option>
                        </c:forEach>
                    </select>
                </td>
            </tr>
            <tr>
                <td>
                    移交开始时间:<input id="startTransferTimes" name="startTransferTimes" placeholder="选择开始时间"
                                  onclick="WdatePicker({maxDate:'#F{$dp.$D(\'endTransferTimes\')}',dateFmt:'yyyy-MM-dd HH:mm:ss'})"
                                  style="width:130px;height:20px;border-radius: 5px;"/>&nbsp;&nbsp;&nbsp;&nbsp;
                    移交结束时间:<input id="endTransferTimes" name="endTransferTimes" placeholder="选择结束时间"
                                  onclick="WdatePicker({minDate:'#F{$dp.$D(\'startTransferTimes\')}',dateFmt:'yyyy-MM-dd HH:mm:ss'})"
                                  style="width:130px;height:20px;border-radius: 5px;"/>&nbsp;&nbsp;&nbsp;&nbsp;
                    退催开始时间:<input id="startThePushDayTimes" name="startThePushDayTimes" placeholder="选择开始时间"
                                  onclick="WdatePicker({maxDate:'#F{$dp.$D(\'endThePushDayTimes\')}',dateFmt:'yyyy-MM-dd HH:mm:ss'})"
                                  style="width:130px;height:20px;border-radius: 5px;"/>&nbsp;&nbsp;&nbsp;&nbsp;
                    退催结束时间:<input id="endThePushDayTimes" name="endThePushDayTimes" placeholder="选择结束时间"
                                  onclick="WdatePicker({minDate:'#F{$dp.$D(\'startThePushDayTimes\')}',dateFmt:'yyyy-MM-dd HH:mm:ss'})"
                                  style="width:130px;height:20px;border-radius: 5px;"/>&nbsp;&nbsp;&nbsp;&nbsp;
                </td>
            </tr>
            <tr>
                <td>
                    <shiro:hasPermission name="/sc/collection/outsourceBalance/dataGrid">
                        <a href="javascript:void(0)" onclick="outsourceAmountDataGrid()" class="easyui-linkbutton"
                           iconCls="icon-search" style="width:80px;height:25px">查询</a> &nbsp;&nbsp;&nbsp;
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/sc/collection/outsourceBalance/uploadPage">
                        <a href="javascript:void(0)" onclick="importAmountExp()" class="easyui-linkbutton"
                           iconCls="icon-print" style="width:120px;height:25px">余额表导入</a> &nbsp;&nbsp;&nbsp;
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/sc/collection/outsourceBalance/his/dataGrid">
                        <a href="javascript:void(0)" onclick="outsourceAmountHisDataGrid()" class="easyui-linkbutton"
                           iconCls="icon-search" style="width:80px;height:25px">历史查询</a> &nbsp;&nbsp;&nbsp;
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/sc/collection/outsourceBalance/his/downloadThePushExp">
                        <a href="javascript:void(0)" onclick="exportThePushExp()" class="easyui-linkbutton"
                           iconCls="icon-print" style="width:110px;height:25px">导出出催清单</a> &nbsp;&nbsp;&nbsp;
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/sc/collection/outsourceBalance/downloadAllAmountExp">
                        <a href="javascript:void(0)" onclick="exportAllAmountExp()" class="easyui-linkbutton"
                           iconCls="icon-print" style="width:110px;height:25px">导出全量余额</a> &nbsp;&nbsp;&nbsp;
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/sc/collection/outsourceBalance/downloadAllAmountExp">
                        <a href="javascript:void(0)" onclick="exportAllLeaveExp()" class="easyui-linkbutton"
                           iconCls="icon-print" style="width:110px;height:25px">导出留案清单</a> &nbsp;&nbsp;&nbsp;
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/sc/collection/outsourceBalance/addAmountPage">
                        <a href="javascript:void(0)" onclick="addAmountData()" class="easyui-linkbutton"
                           iconCls="icon-add" style="width:80px;height:25px">添加</a> &nbsp;&nbsp;&nbsp;
                    </shiro:hasPermission>

                </td>
            </tr>
        </table>
    </div>
    <div data-options="region:'center', fit:true, border:false">
        <table id="outsourceAmountDataGrid"></table>
    </div>
</div>
<script type="text/javascript">
    var excelAmountUpload;
    $(function () {
        excelAmountUpload = outsourceAmountDataGrid();
    });
    // 查询余额
    function outsourceAmountDataGrid() {
        var url = "${path}/sc/collection/outsourceBalance/dataGrid";
        var columns = [[
            {width: '4%', title: '姓名', field: 'name', sortable: true},
            {width: '11%', title: '身份证', field: 'custId', sortable: true},
            //{width: '6%', title: '电话', field: 'telNumber', sortable: true},
            {width: '13%', title: '借据号', field: 'ious', sortable: true},
            {width: '6%', title: '最新金额', field: 'nowCollectionAmount', sortable: true},
            {width: '4%', title: '最新账龄', field: 'nowAgecd', sortable: true},
            {width: '4%', title: '移交账龄', field: 'transferAgecd', sortable: true},
            {width: '6%', title: '移交日期', field: 'transfer', sortable: true, formatter: getTime},
            {width: '6%', title: '退案日期', field: 'thePushDay', sortable: true, formatter: getTime},
            {width: '3%', title: '留案', field: 'isLeaveCase', sortable: true},
            {width: '4%', title: '公司', field: 'company', sortable: true},
            {width: '6%', title: '上次金额', field: 'lastCollectionAmount', sortable: true},
            //{width: '4%', title: '上次账龄', field: 'lastAgecd', sortable: true},
            {width: '6%', title: '更新时间', field: 'updateTime', sortable: true, formatter: getTime},
            {width: '6%', title: '创建时间', field: 'creatDate', sortable: true, formatter: getTime},
            {width: '8%', title: '备注', field: 'remarks', sortable: true},
            {
                field: 'action',
                title: '操作',
                width: '4%',
                formatter: function (value, row, index) {
                    var str = '';
                    str += $.formatString('<a href="javascript:void(0)" class="resource-easyui-linkbutton-edit" data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="editAmountFun(\'{0}\',\'{1}\',\'{2}\');" >修改</a>', row.id, row.custId, row.ious);
                    return str;
                }
            }
        ]];
        var params = {
            name: $("input[name='amountNames']").val(),
            custId: $("input[name='amountCustIds']").val(),
            ious: $("input[name='amountIouss']").val(),
            company: $("#companys").val(),
            startTransferTime: $("input[name='startTransferTimes']").val(),
            endTransferTime: $("input[name='endTransferTimes']").val(),
            startThePushDayTime: $("input[name='startThePushDayTimes']").val(),
            endThePushDayTime: $("input[name='endThePushDayTimes']").val()
        };
        publicAmountDataGrid(url, columns, params);
    }
    // 查询余额历史备份
    function outsourceAmountHisDataGrid() {
        var url = "${path}/sc/collection/outsourceBalance/his/dataGrid";
        var columns = [[
            {width: '4%', title: '姓名', field: 'name', sortable: true},
            {width: '10%', title: '身份证', field: 'custId', sortable: true},
            //{width: '6%', title: '电话', field: 'telNumber', sortable: true},
            {width: '12%', title: '借据号', field: 'ious', sortable: true},
            {width: '6%', title: '最新金额', field: 'nowCollectionAmount', sortable: true},
            {width: '4%', title: '最新账龄', field: 'nowAgecd', sortable: true},
            {width: '4%', title: '移交账龄', field: 'transferAgecd', sortable: true},
            {width: '8%', title: '移交日期', field: 'transfer', sortable: true},
            {width: '8%', title: '退案日期', field: 'thePushDay', sortable: true},
            {width: '4%', title: '是否留案', field: 'isLeaveCase', sortable: true},
            {width: '4%', title: '公司', field: 'company', sortable: true},
            {width: '6%', title: '上次金额', field: 'lastCollectionAmount', sortable: true},
            {width: '4%', title: '上次账龄', field: 'lastAgecd', sortable: true},
            {width: '8%', title: '更新时间', field: 'updateTime', sortable: true},
            {width: '8%', title: '备注', field: 'remarks', sortable: true},
            {
                field: 'action',
                title: '留案',
                width: '4%',
                formatter: function (value, row, index) {
                    var str = '';
                    str += $.formatString('<a href="javascript:void(0)" class="his-resource-easyui-linkbutton-edit" data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="editAmountHisFun(\'{0}\');" >留案</a>', row.id);
                    return str;
                }
            }
        ]];
        var params = {
            name: $("input[name='amountNames']").val(),
            custId: $("input[name='amountCustIds']").val(),
            ious: $("input[name='amountIouss']").val(),
            company: $("#companys").val(),
            startTransferTime: $("input[name='startTransferTimes']").val(),
            endTransferTime: $("input[name='endTransferTimes']").val(),
            startThePushDayTime: $("input[name='startThePushDayTimes']").val(),
            endThePushDayTime: $("input[name='endThePushDayTimes']").val()
        };
        publicAmountDataGrid(url, columns, params);
    }

    function publicAmountDataGrid(url, columns, params) {
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
            queryParams: params,
            onLoadSuccess:function(data){
                $('.resource-easyui-linkbutton-edit').linkbutton({text:'编辑'});
                $('.his-resource-easyui-linkbutton-edit').linkbutton({text:'留案'});
            }
        });
    }
    // 导出出催清单数据
    function exportThePushExp() {
        var url = "${path}/sc/collection/outsourceBalance/his/downloadThePushExp?";
        exporCommontExp(url);
    }
    // 导出所有的余额数据
    function exportAllAmountExp() {
        var url = "${path}/sc/collection/outsourceBalance/downloadAllAmountExp?";
        exporCommontExp(url);
    }

    function exportAllLeaveExp() {
        var url = "${path}/sc/collection/outsourceBalance/downloadAllLeaveExp?";
        exporCommontExp(url);
    }
    function exporCommontExp(url) {
        var companys = $("#companys").val();
        var startTransferTimes = $("input[name='startTransferTimes']").val();
        var endTransferTimes = $("input[name='endTransferTimes']").val();
        var startThePushDayTimes = $("input[name='startThePushDayTimes']").val();
        var endThePushDayTimes = $("input[name='endThePushDayTimes']").val();
        url += "company=" + companys;
        url += "&startTransferTime=" + startTransferTimes;
        url += "&endTransferTime=" + endTransferTimes;
        url += "&startThePushDayTime=" + startThePushDayTimes;
        url += "&endThePushDayTime=" + endThePushDayTimes;
        location.href = encodeURI(url);
    }
    // Excel 文件导入
    function importAmountExp() {
        parent.$.modalDialog({
            title: '余额数据更新',
            width: 500,
            height: 200,
            href: '${path }/sc/collection/outsourceBalance/uploadPage',
            buttons: [{
                text: '数据导入',
                handler: function () {
                    parent.$.modalDialog.openner_treeGrid = excelAmountUpload;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#excelAmountUploadForm');
                    f.submit();
                }
            }]
        });
    }

    /**
     * 修改退案时间
     * @param custId
     */
    function editAmountFun(id, custId, ious) {
        parent.$.modalDialog({
            title: '修改退案时间',
            width: 500,
            height: 350,
            href: '${path }/sc/collection/outsourceBalance/updateAmountPage?id='+ id +'&custId=' + custId + '&ious=' + ious,
            buttons: [{
                text: '确定',
                handler: function () {
                    parent.$.modalDialog.openner_treeGrid = excelAmountUpload;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#updateAmountForm');
                    f.submit();
                }
            }]
        });
    }
    /**
     * 从历史案件中直接留案
     *
     */
    function editAmountHisFun(id) {
        parent.$.modalDialog({
            title: '留案',
            width: 500,
            height: 350,
            href: '${path }/sc/collection/outsourceBalance/his/updateAmountHisPage?id='+ id ,
            buttons: [{
                text: '确定',
                handler: function () {
                    parent.$.modalDialog.openner_treeGrid = excelAmountUpload;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#updateAmountHisForm');
                    f.submit();
                }
            }]
        });
    }

    /**
     * 添加余额信息
     */
    function addAmountData() {
        parent.$.modalDialog({
            title: '添加',
            width: 500,
            height: 500,
            href: '${path }/sc/collection/outsourceBalance/addAmountPage',
            buttons: [{
                text: '确定',
                handler: function () {
                    parent.$.modalDialog.openner_treeGrid = excelAmountUpload;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#addAmountForm');
                    f.submit();
                }
            }]
        });
    }

    function getTime(value) {
        var t, y, m, d, h, i, s, str = '';
        if (value == null || value == '') {
            return str;
        }
        var ts;
        if (value instanceof Date) {
            ts = value;
        } else {
            ts = new Date(value);
        }
        t = ts;
        y = t.getFullYear();
        m = t.getMonth() + 1;
        d = t.getDate();
        h = t.getHours();
        i = t.getMinutes();
        s = t.getSeconds();
        // 可根据需要在这里定义时间格式
        str += y + '-' + (m < 10 ? '0' + m : m) + '-' + (d < 10 ? '0' + d : d);
        //str += ' ' + (h < 10 ? '0' + h : h) + ':' + (i < 10 ? '0' + i : i) + ':' + (s < 10 ? '0' + s : s);
        return str;
    }

</script>