<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    $(function() {
        $('#updateRepaymentForm').form({
            url : '${path}/sc/collection/refund/updateRepaymentById',
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
                    var form = $('#updateRepaymentForm');
                    parent.$.messager.alert('提示', eval(result.msg), 'warning');
                }
            }
        });
    });
</script>
<div style="padding:35px 50px;">
    <form id="updateRepaymentForm" method="post">
        <table>
            <tr style="padding-top: 20px;">
                <td><span>还款金额</span></td>
                <td>
                    <span class="input">
                        <input type="hidden" name="id" value="${repayment.id}"/>
                        <input type="number" name="curAmount" style="width:200px;height:20px;border-radius: 5px;"
                               value="${repayment.curAmount}"/>
                    </span>
                </td>
            </tr>
            <tr style="padding-top: 20px;">
                <td><span>还款日期</span></td>
                <td>
                    <span class="input">
                        <input type="text" name="repaymentDate" placeholder="还款日期"
                               onclick="WdatePicker({dateFmt:'yyyy-MM-dd 00:00:00'})"
                               style="width:200px;height:20px;border-radius: 5px;"
                               value="<fmt:formatDate value="${repayment.repaymentDate}" type="both"></fmt:formatDate>"/>
                    </span>
                </td>
            </tr>
            <tr style="padding-top: 20px;">
                <td><span>委外公司</span></td>
                <td>
                    <span class="input">
                        <select name="company" style="width:200px;height:20px;border-radius: 5px;">
                            <c:forEach items="${companies}" var="item">
                                <c:if test="${item.company eq repayment.company}">
                                    <option value="${item.company}" selected="selected">${item.company}</option>
                                </c:if>
                                <c:if test="${item.company ne repayment.company}">
                                    <option value="${item.company}">${item.company}</option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </span>
                </td>
            </tr>
            <tr style="padding-top: 20px;">
                <td><span>是否全额还款</span></td>
                <td>
                    <span class="input">
                        <c:if test="${repayment.isSumRefund == 0}" >
                            <input type="radio" name="isSumRefund" value="0" checked="checked">全额
                            <input type="radio" name="isSumRefund" value="1">部分
                        </c:if>
                        <c:if test="${repayment.isSumRefund == 1}" >
                            <input type="radio" name="isSumRefund" value="0">全额
                            <input type="radio" name="isSumRefund" value="1" checked="checked">部分
                        </c:if>
                    </span>
                </td>
            </tr>
        </table>
    </form>
</div>
