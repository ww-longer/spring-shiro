<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    $(function() {
        $('#updateCompanyForm').form({
            url : '${path}/sc/collection/company/edit',
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
                console.log(result);
                if (result.success) {
                    companyDataGrid();
                    parent.$.modalDialog.handler.dialog('close');
                } else {
                    $("#com_edit_msgerr").text(result.msg);
                    var form = $('#updateCompanyForm');
                    parent.$.messager.alert('提示', eval(result.msg), 'warning');
                }
            }
        });
    });
</script>
<div style="padding: 30px;">
    <form id="updateCompanyForm" method="post">
        <table>
            <tr>
                <td colspan="2"><span id="com_edit_msgerr" style="width:200px;height:20px;border-radius: 5px;color: red;"></span></td>
            </tr>
            <tr>
                <td><span>公司编号</span></td>
                <td>
                    <span class="input">
                        <input type="hidden" name="id" value="${company.id}"/>
                        <input type="text" name="companyNumber" placeholder="请填写数字"
                               style="width:200px;height:20px;border-radius: 5px;" value="${company.companyNumber}"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td><span>公司名称</span></td>
                <td>
                    <span class="input">
                        <input type="text" name="company"
                               style="width:200px;height:20px;border-radius: 5px;" value="${company.company}"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td><span>公司地址</span></td>
                <td>
                    <span class="input">
                        <input type="text" name="address"
                               style="width:200px;height:20px;border-radius: 5px;" value="${company.address}"/>
                    </span>
                </td>
            </tr>
        </table>
    </form>
</div>
