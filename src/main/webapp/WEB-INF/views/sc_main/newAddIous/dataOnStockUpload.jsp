<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    $(function() {
        $('#excelDataOnStockUploadForm').form({
            url : '${path}/sc/collection/newAddIous/uploadDataOnStockExp',
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
                    newAddIousDataGrid();
                    parent.$.modalDialog.handler.dialog('close');
                } else {
                    var form = $('#excelDataOnStockUploadForm');
                    parent.$.messager.alert('提示', eval(result.msg), 'warning');
                }
            }
        });
    });
</script>
<div style="padding: 35px;">
    <form id="excelDataOnStockUploadForm" method="post" enctype="multipart/form-data">
        <table>
            <tr>
                <td><span style="width: 100px">上传上次借据清单：</span></td>
                <td>
                    <span class="input">
                        <input type="file" id="file" name="newFile" placeholder="" style="width: 290px;"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <span style="align-content: center; color: red;">请选择 Excel 文件</span>
                </td>
            </tr>
        </table>
    </form>
</div>
