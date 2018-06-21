<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/commons/global.jsp" %>
<script type="text/javascript">
    $(function () {
        $('#addAmountForm').form({
            url: '${path}/sc/collection/outsourceBalance/addAmount',
            onSubmit: function () {
                progressLoad();
                var isValid = $(this).form('validate');
                if (!isValid) {
                    progressClose();
                }
                return isValid;
            },
            success: function (result) {
                progressClose();
                result = $.parseJSON(result);
                if (result.success) {
                    outsourceAmountDataGrid();
                    parent.$.modalDialog.handler.dialog('close');
                } else {
                    $("#msgerr").text(result.msg);
                    var form = $('#addAmountForm');
                    parent.$.messager.alert('提示', eval(result.msg), 'warning');
                }
            }
        });
    });
</script>
<div style="padding: 30px 50px;">
    <form id="addAmountForm" method="post">
        <table>
            <tr>
                <td colspan="2"><span id="msgerr" style="width:200px;height:20px;border-radius: 5px;color: red;"></span>
                </td>
            </tr>
            <tr>
                <td><span>姓名</span></td>
                <td>
                    <span class="input">
                        <input type="text" name="name" style="width:200px;height:20px;border-radius: 5px;"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td><span>身份证号</span></td>
                <td>
                    <span class="input">
                        <input type="text" name="custId" style="width:200px;height:20px;border-radius: 5px;"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td><span>手机号</span></td>
                <td>
                    <span class="input">
                        <input type="text" name="telNumber" style="width:200px;height:20px;border-radius: 5px;"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td><span>借据号</span></td>
                <td>
                    <span class="input">
                        <input type="text" name="ious" style="width:200px;height:20px;border-radius: 5px;"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td><span>最新催收金额</span></td>
                <td>
                    <span class="input">
                        <input type="text" name="nowCollectionAmount"
                               style="width:200px;height:20px;border-radius: 5px;"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td><span>最新账龄</span></td>
                <td>
                    <span class="input">
                        <input type="text" name="nowAgecd" style="width:200px;height:20px;border-radius: 5px;"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td><span>移交账龄</span></td>
                <td>
                    <span class="input">
                        <input type="text" name="transferAgecd" style="width:200px;height:20px;border-radius: 5px;"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td><span>移交日</span></td>
                <td>
                    <span class="input">
                        <input type="text" name="transfer" placeholder="选择移交时间"
                               onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})"
                               style="width:200px;height:20px;border-radius: 5px;"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td><span>出催日期(留案日)</span></td>
                <td>
                    <span class="input">
                        <input type="text" name="thePushDay" placeholder="选择时间"
                               onclick="WdatePicker({minDate:'%y-%M-{%d+1}', dateFmt:'yyyy-MM-dd 00:00:00'})"
                               style="width:200px;height:20px;border-radius: 5px;"/>
                    </span>
                </td>
            </tr>
            <tr>
                <td><span>公司</span></td>
                <td>
                    <span class="input">
                        <select name="company" style="width:150px;height:20px;border-radius: 5px;">
                            <option value=""></option>
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
                        <input type="checkbox" name="isLeaveCase" value="Y"/>
                        <span style="color: red">留案请勾选</span>
                    </span>
                </td>
            </tr>
        </table>
    </form>
</div>
