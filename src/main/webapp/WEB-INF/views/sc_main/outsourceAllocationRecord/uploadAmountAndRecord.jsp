<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    $(function() {
        $('#uploadAmountAndRecordForm').form({
            url : '${path}/sc/collection/uploadAmountAndRecordExcel',
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
                    outsourceRecordDataGrid();
                    parent.$.modalDialog.handler.dialog('close');
                } else {
                    $("#ar_err").text(result.msg);
                    var form = $('#uploadAmountAndRecordForm');
                    parent.$.messager.alert('提示', eval(result.msg), 'warning');
                }
            }
        });
    });
</script>
<div style="padding: 40px;">
    <form id="uploadAmountAndRecordForm" method="post" enctype="multipart/form-data">
        <table>
            <tr>
                <td colspan="2"><span id="ar_err" style="color: red"></span></td>
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
                    <span style="align-content: center; color: red;">请选择 移交 文件</span>
                </td>
            </tr>
        </table>
    </form>
</div>
