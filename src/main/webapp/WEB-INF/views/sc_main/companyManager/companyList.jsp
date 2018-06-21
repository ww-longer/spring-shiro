<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/commons/global.jsp" %>
<div>
    <div>
        <table>
            <tr>
                <td>
                    公司:<input class="easyui-textbox" name="com_companys" data-options="prompt:'请输入公司名称',validType:''"
                              style="width:200px;height:25px; "> &nbsp;&nbsp;&nbsp;&nbsp;
                </td>
            </tr>
            <tr>
                <td>
                    <shiro:hasPermission name="/sc/collection/company/dataGrid">
                        <a href="javascript:void(0)" onclick="companyDataGrid()" class="easyui-linkbutton"
                           iconCls="icon-search" style="width:80px;height:25px">查询</a> &nbsp;&nbsp;&nbsp;
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/sc/collection/company/addPage">
                        <a href="javascript:void(0)" onclick="addCompanyFun()" class="easyui-linkbutton"
                           iconCls="icon-green" style="width:120px;height:25px">添加委外公司</a> &nbsp;&nbsp;&nbsp;
                    </shiro:hasPermission>
                </td>
            </tr>
        </table>
    </div>
    <div data-options="region:'center', fit:true, border:false">
        <table id="comapnyDataId"></table>
    </div>
</div>
<script type="text/javascript">
    var comapnyDataId;
    $(function () {
        comapnyDataId = companyDataGrid();
    });
    // 查询委外公司
    function companyDataGrid() {
        var url = "${path}/sc/collection/company/dataGrid";
        var columns = [[
            {width: '10%', title: '公司编号', field: 'companyNumber', sortable: true},
            {width: '15%', title: '公司名称', field: 'company', sortable: true},
            {width: '20%', title: '公司地址', field: 'address', sortable: true},
            {width: '15%', title: '创建时间', field: 'createDate', sortable: true},
            {width: '15%', title: '创建人', field: 'createUser', sortable: true},
            {
                field: 'action',
                title: '操作',
                width: '20%',
                formatter: function (value, row, index) {
                    var str = '';
                    <shiro:hasPermission name="/sc/collection/company/editPage">
                    str += $.formatString('<a href="javascript:void(0)" class="resource-easyui-linkbutton-edit" data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="editCompanyFun(\'{0}\');" >修改</a>&nbsp;&nbsp;|&nbsp;&nbsp;', row.id);
                    </shiro:hasPermission>
                    <shiro:hasPermission name="/sc/collection/company/delete">
                    str += $.formatString('<a href="javascript:void(0)" class="resource-easyui-linkbutton-del" data-options="plain:true,iconCls:\'fi-pencil icon-blue\'" onclick="deleteCompanyFun(\'{0}\');" >删除</a>', row.id);
                    </shiro:hasPermission>
                    return str;
                }
            }
        ]];
        var params = {
            company: $("input[name='com_companys']").val()
        };
        com_dataGrid(url, columns, params);
    }

    function com_dataGrid(url, columns, params) {
        $('#comapnyDataId').datagrid({
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
                $('.resource-easyui-linkbutton-del').linkbutton({text:'删除'});
            }
        });
    }


    /**
     * 删除公司
     */
    function deleteCompanyFun(id){
        parent.$.messager.confirm('询问', '您是否要删除当前公司!', function(b) {
            if (b) {
                progressLoad();
                $.post('${path }/sc/collection/company/delete', {
                    id : id
                }, function(result) {
                    if (result.success) {
                        parent.$.messager.alert('提示', result.msg, 'info');
                        companyDataGrid();
                        parent.indexMenuZTree.reAsyncChildNodes(null, "refresh");
                    }
                    progressClose();
                }, 'JSON');
            }
        });
    }

    /**
     * 修改公司
     * @param custId
     */
    function editCompanyFun(id) {
        parent.$.modalDialog({
            title: '修改退案时间',
            width: 500,
            height: 250,
            href: '${path }/sc/collection/company/editPage?id=' + id,
            buttons: [{
                text: '确定',
                handler: function () {
                    parent.$.modalDialog.openner_treeGrid = comapnyDataId;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#updateCompanyForm');
                    f.submit();
                }
            }]
        });
    }

    function addCompanyFun() {
        parent.$.modalDialog({
            title: '添加',
            width: 500,
            height: 250,
            href: '${path }/sc/collection/company/addPage',
            buttons: [{
                text: '确定',
                handler: function () {
                    parent.$.modalDialog.openner_treeGrid = comapnyDataId;//因为添加成功之后，需要刷新这个treeGrid，所以先预定义好
                    var f = parent.$.modalDialog.handler.find('#addCompanyForm');
                    f.submit();
                }
            }]
        });
    }

</script>