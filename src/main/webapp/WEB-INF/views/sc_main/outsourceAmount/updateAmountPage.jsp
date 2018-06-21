<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    $(function() {
        $('#updateAmountForm').form({
            url : '${path}/sc/collection/outsourceBalance/updateAmountByCustId',
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
                    outsourceAmountDataGrid();
                    parent.$.modalDialog.handler.dialog('close');
                } else {
                    var form = $('#updateAmountForm');
                    parent.$.messager.alert('提示', eval(result.msg), 'warning');
                }
            }
        });
    });
</script>
<div style="padding:35px 50px;">
    <form id="updateAmountForm" method="post">
        <table>
            <tr>
                <td><span>移交日期</span></td>
                <td>
                    <span class="input">
                        <input type="hidden" name="custId" value="${amount.custId}"/>
                        <input type="hidden" name="ious" value="${ious}"/>
                        <input type="text" name="transfer" placeholder="移交时间"
                               onclick="WdatePicker({maxDate:'%y-%M-%d', dateFmt:'yyyy-MM-dd 00:00:00'})"
                               style="width:200px;height:20px;border-radius: 5px;" value="${transfer}"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td><span>留案到期日期</span></td>
                <td>
                    <span class="input">
                        <input type="text" name="thePushDay" placeholder="退案时间"
                               onclick="WdatePicker({minDate:'%y-%M-{%d+1}', dateFmt:'yyyy-MM-dd 00:00:00'})"
                               style="width:200px;height:20px;border-radius: 5px;" value="${thePushDay}"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td><span>委外公司</span></td>
                <td>
                    <span class="input">
                        <select name="company" style="width:200px;height:20px;border-radius: 5px;">
                            <c:forEach items="${companies}" var="item">
                                <c:if test="${item.company eq amount.company}">
                                    <option value="${item.company}" selected="selected">${item.company}</option>
                                </c:if>
                                <c:if test="${item.company ne amount.company}">
                                    <option value="${item.company}">${item.company}</option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </span>
                </td>
            </tr>
            <tr>
                <td><span>是否留案</span></td>
                <td>
                    <span class="input">
                        <c:if test="${amount.isLeaveCase == 'Y'}" >
                            <input type="checkbox" name="isLeaveCase" value="Y" checked="checked"/>
                        </c:if>
                        <c:if test="${amount.isLeaveCase != 'Y'}" >
                            <input type="checkbox" name="isLeaveCase" value="Y" />
                        </c:if>
                        <span style="color: red">留案请勾选</span>
                    </span>
                </td>
            </tr>
        </table>
    </form>
</div>
