<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    $(function() {
        $('#addCompanyForm').form({
            url : '${path}/sc/collection/company/add',
            onSubmit : function() {
                progressLoad();
                var isValid = $(this).form('validate');
                if (!isValid) {
                    progressClose();
                }
                return isValid;
            },
            success : function(result) {
                progressClose();
                result = $.parseJSON(result);
                if (result.success) {
                    companyDataGrid();
                    parent.$.modalDialog.handler.dialog('close');
                } else {
                    $("#com_add_msgerr").text(result.msg);
                    var form = $('#addCompanyForm');
                    parent.$.messager.alert('提示', eval(result.msg), 'warning');
                }
            }
        });
    });
</script>
<div style="padding: 30px;">
    <form id="addCompanyForm" method="post">
        <table>
            <tr>
                <td colspan="2"><span id="com_add_msgerr" style="width:200px;height:20px;border-radius: 5px;color: red;"></span></td>
            </tr>
            <tr>
                <td><span>公司编号</span></td>
                <td>
                    <span class="input">
                        <input type="text" name="companyNumber" placeholder="请填写数字"
                               style="width:200px;height:20px;border-radius: 5px;"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td><span>公司名称</span></td>
                <td>
                    <span class="input">
                        <input type="text" name="company"
                               style="width:200px;height:20px;border-radius: 5px;"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td><span>公司地址</span></td>
                <td>
                    <span class="input">
                        <input type="text" name="address" style="width:200px;height:20px;border-radius: 5px;"/>
                    </span>
                </td>
            </tr>
        </table>
    </form>
</div>
