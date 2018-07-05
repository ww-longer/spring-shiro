<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/commons/global.jsp" %>
<div>
    <div>
        <table>
            <tr>
                <td>
                    公司:<select id="repaymentReportCompanys" style="width:120px;height:25px;border-radius: 5px;">
                            <option value=""></option>
                            <c:forEach items="${companies}" var="item">
                                <option value="${item.company}">${item.company}</option>
                            </c:forEach>
                        </select>
                </td>
            </tr>
            <tr>
                <td>
                    <shiro:hasPermission name="/sc/collection/statistic/repaymentRate">

                    </shiro:hasPermission>
                    <a href="javascript:void(0)" onclick="repaymentRateStatementDataGrid()" class="easyui-linkbutton"
                       iconCls="icon-search" style="width:80px;height:25px">还款率</a> &nbsp;&nbsp;
                    <a href="javascript:void(0)" onclick="exportRepaymentReportExp()" class="easyui-linkbutton"
                       iconCls="icon-print" style="width:110px;height:25px">还款率导出</a> &nbsp;&nbsp;
                    <a href="javascript:void(0)" onclick="companyRankingExport()" class="easyui-linkbutton"
                       iconCls="icon-search" style="width:120px;height:25px">公司排名导出</a> &nbsp;&nbsp;

                </td>
            </tr>
        </table>
    </div>
    <div data-options="region:'center',fit:true,border:false">
        <table id="rateStatementId"></table>
    </div>
</div>
<script type="text/javascript">
    var repaymentRateId;
    $(function () {
        repaymentRateId = repaymentRateStatementDataGrid();
    });
    // 查询还款率
    function repaymentRateStatementDataGrid() {
        var url = "${path}/sc/collection/statistic/repaymentRateStatement";
        var columns = [[
            {width: '5%', title: '移交日', field: 'transfer'},
            {width: '6%', title: 'M1移交', field: 'm1yj'},
            {width: '6%', title: 'M2移交', field: 'm2yj'},
            {width: '6%', title: 'M3移交', field: 'm3yj'},
            {width: '6%', title: 'M4移交', field: 'm4yj'},
            {width: '6%', title: 'M5移交', field: 'm5yj'},
            {width: '6%', title: 'M6移交', field: 'm6yj'},
            {width: '6%', title: 'M7+移交', field: 'm7yj'},
            {width: '6%', title: 'M1还款', field: 'm1hk'},
            {width: '6%', title: 'M2还款', field: 'm2hk'},
            {width: '6%', title: 'M3还款', field: 'm3hk'},
            {width: '6%', title: 'M4还款', field: 'm4hk'},
            {width: '6%', title: 'M5还款', field: 'm5hk'},
            {width: '6%', title: 'M6还款', field: 'm6hk'},
            {width: '6%', title: 'M7+还款', field: 'm7hk'},
            {width: '6%', title: 'M1还款率(%)', field: 'm1hkl'},
            {width: '6%', title: 'M2还款率(%)', field: 'm2hkl'},
            {width: '6%', title: 'M3还款率(%)', field: 'm3hkl'},
            {width: '6%', title: 'M4还款率(%)', field: 'm4hkl'},
            {width: '6%', title: 'M5还款率(%)', field: 'm5hkl'},
            {width: '6%', title: 'M6还款率(%)', field: 'm6hkl'},
            {width: '6%', title: 'M7+还款率(%)', field: 'm7hkl'},
            {width: '6%', title: 'M1实际还款', field: 'm1sjhk'},
            {width: '6%', title: 'M2实际还款', field: 'm2sjhk'},
            {width: '6%', title: 'M3实际还款', field: 'm3sjhk'},
            {width: '6%', title: 'M4实际还款', field: 'm4sjhk'},
            {width: '6%', title: 'M5实际还款', field: 'm5sjhk'},
            {width: '6%', title: 'M6实际还款', field: 'm6sjhk'},
            {width: '6%', title: 'M7+实际还款', field: 'm7sjhk'}
        ]];
        var params = {
            company: $("#repaymentReportCompanys").val()

        };
        publicRepaymentRateDataGrid(url, columns, params);
    }
    
    // 排名计算并导出
    function companyRankingExport() {
        var url = "${path}/sc/collection/statistic/companyRankingExp";
        location.href = encodeURI(encodeURI(url));
    }

    function publicRepaymentRateDataGrid(url, columns, params) {
        $('#rateStatementId').datagrid({
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
            queryParams: params
        });
    }
    /**
     * 导出还款率
     */
    function exportRepaymentReportExp() {
        var url = "${path}/sc/collection/statistic/downLoadRepaymentReportExp?";
        var repaymentReportCompanys = $("#repaymentReportCompanys").val();
        url += "company=" + repaymentReportCompanys;
        location.href = encodeURI(encodeURI(url));
    }

</script>