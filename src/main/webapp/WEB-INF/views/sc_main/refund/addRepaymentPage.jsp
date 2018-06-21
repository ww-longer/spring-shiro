<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    $(function() {
        $('#uploadRepaymentForm').form({
            url : '${path}/sc/collection/refund/uploadRepaymentExcel',
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
                    outsourceRefundDataGrid();
                    parent.$.modalDialog.handler.dialog('close');
                } else {
                    $("#repayment_err").text(result.msg);
                    var form = $('#uploadRepaymentForm');
                    parent.$.messager.alert('提示', eval(result.msg), 'warning');
                }
            }
        });
    });
</script>
<div style="padding: 40px;">
    <form id="uploadRepaymentForm" method="post" enctype="multipart/form-data">
        <table>
            <tr>
                <td colspan="2"><span id="repayment_err" style="color: red"></span></td>
            </tr>
            <tr>
                <td><span>上&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;传：</span></td>
                <td>
                    <span class="input">
                        <input type="file" id="file" name="file" placeholder="" style="width: 350px;"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <span style="align-content: center; color: red;">请选择 历史还款 文件</span>
                </td>
            </tr>
        </table>
    </form>
</div>
