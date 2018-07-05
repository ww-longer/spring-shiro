<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="/commons/global.jsp" %>
<div class="tab-pane fade in active" id="home">
    <div class="container-fluid">
        <div class="row">
            <%-- 先公司,在账龄 --%>
            <div class="left-page" style="width: 49.9%; float: left; margin-top: 5px;">
                <div class="portlet-title">
                    <div class="caption" style="background-color: #5196FF85; height: 30px; margin: 5px;">
                        <span style="font-size: 22px; font-weight: 600; color: #585858; background-color: #FFDD6069; padding: 5px;">委外案件公司账龄比例</span>
                    </div>
                </div>
                <div class="portlet-body border-right-color1" style="padding: 10px;">
                    <div id="out-company-agecd-count" style="float: left"></div>
                    <div id="out-company-agecd-amount" style="float: right"></div>
                </div>
            </div>
            <%-- 先账龄,在公司 --%>
            <div class="right-page" style="float: left; width: 49.9%; margin-top: 5px;">
                <div class="portlet-title">
                    <div class="portlet-title">
                        <div class="caption" style="background-color: #5196FF85; height: 30px; margin: 5px;">
                            <span style="font-size: 22px; font-weight: 600; color: #585858; background-color: #FFDD6069; padding: 5px;">委外案件账龄公司比例</span>
                        </div>
                    </div>
                </div>
                <div class="portlet-body border-right-color2" style="padding: 10px;">
                    <div id="out-agecd-company-count" style="float: left"></div>
                    <div id="out-agecd-company-amount" style="float: right"></div>
                </div>
            </div>
            <%-- 催收效率折线图 --%>
            <div class="right-page" style="float: left; width: 100%; margin-top: 10px;">
                <div class="portlet-title">
                    <div class="caption" style="background-color: #5196FF85; height: 30px; margin: 5px;">
                        <span style="font-size: 22px; font-weight: 600; color: #585858; background-color: #FFDD6069; padding: 5px;">催收还款效率</span>
                    </div>
                </div>
                <div class="portlet-body border-left-color1" style="padding: 10px;">
                    <div>
                        公司:<select id="reportCompanys" style="width:120px;height:25px;border-radius: 5px;">
                        <option value=""></option>
                        <c:forEach items="${companies}" var="item">
                            <option value="${item.company}">${item.company}</option>
                        </c:forEach>
                    </select>&nbsp;&nbsp;&nbsp;&nbsp;
                        月份:<select id="reportDateLists" style="width:120px;height:25px;border-radius: 5px;">
                        <option value=""></option>
                        <c:forEach items="${dateList}" var="item">
                            <option value="${item}">${item}</option>
                        </c:forEach>
                    </select>&nbsp;&nbsp;&nbsp;&nbsp;
                        账龄:<select id="reportAgecdLists" style="width:120px;height:25px;border-radius: 5px;">
                        <option value=""></option>
                        <c:forEach items="${agecdMap}" var="i">
                            <option value="<c:out value="${i.value}"/>"><c:out value="${i.key}"/></option>
                        </c:forEach>
                    </select>
                        <a href="javascript:void(0)" onclick="getRepaymentRateCaseScale()" class="easyui-linkbutton"
                           iconCls="icon-search" style="width: 90px; height:25px">查询</a> &nbsp;&nbsp;
                    </div>
                    <div id="out-company-repayment-rate"></div>
                </div>
            </div>

            <%-- 移交还款柱状图 --%>
            <div class="right-page" style="float: left; width: 100%; margin-top: 10px;">
                <div class="portlet-title">
                    <div class="caption" style="background-color: #5196FF85; height: 30px; margin: 5px;">
                        <span style="font-size: 22px; font-weight: 600; color: #585858; background-color: #FFDD6069; padding: 5px;">移交还款额比例柱状图</span>
                    </div>
                </div>
                <div class="portlet-body border-left-color1" style="padding: 10px;">
                    <div>
                        账龄:<select id="reportTurnAgecd" style="width:120px;height:25px;border-radius: 5px;">
                        <option value=""></option>
                        <c:forEach items="${agecdMap}" var="i">
                            <option value="<c:out value="${i.value}"/>"><c:out value="${i.key}"/></option>
                        </c:forEach>
                    </select>
                        <a href="javascript:void(0)" onclick="getCompanyDivideAmountScale()" class="easyui-linkbutton"
                           iconCls="icon-search" style="width: 90px; height:25px">查询</a> &nbsp;&nbsp;
                    </div>
                    <div id="out-company-impropriate-refund"></div>
                </div>
            </div>

            <%-- 还款率折线图 --%>
            <div class="right-page" style="float: left; width: 100%; margin-top: 10px;">
                <div class="portlet-title">
                    <div class="caption" style="background-color: #5196FF85; height: 30px; margin: 5px;">
                        <span style="font-size: 22px; font-weight: 600; color: #585858; background-color: #FFDD6069; padding: 5px;">还款率折线图</span>
                    </div>
                </div>
                <div class="portlet-body border-left-color1" style="padding: 10px;">
                    <div>
                        月份:<select id="reportTurnOverDateLists" style="width:120px;height:25px;border-radius: 5px;">
                        <option value=""></option>
                        <c:forEach items="${dateList}" var="item">
                            <option value="${item}">${item}</option>
                        </c:forEach>
                    </select>&nbsp;&nbsp;&nbsp;&nbsp;
                        公司:<select id="reportTurnOverCompanys" style="width:120px;height:25px;border-radius: 5px;">
                        <option value=""></option>
                        <c:forEach items="${companies}" var="item">
                            <option value="${item.company}">${item.company}</option>
                        </c:forEach>
                    </select>&nbsp;&nbsp;&nbsp;&nbsp;
                        账龄:<select id="reportTurnOverAgecd" style="width:120px;height:25px;border-radius: 5px;">
                        <option value=""></option>
                        <c:forEach items="${agecdMap}" var="i">
                            <option value="<c:out value="${i.value}"/>"><c:out value="${i.key}"/></option>
                        </c:forEach>
                    </select>&nbsp;&nbsp;&nbsp;&nbsp;
                        <a href="javascript:void(0)" onclick="getTurnOverRepaymentRateScale()" class="easyui-linkbutton"
                           iconCls="icon-search" style="width: 90px; height:25px">查询</a> &nbsp;&nbsp;&nbsp;&nbsp;
                        <a href="javascript:void(0)" onclick="downloadTurnOverRepaymentRate()" class="easyui-linkbutton"
                           iconCls="icon-print" style="width: 130px; height:25px">还款率数据下载</a> &nbsp;&nbsp;&nbsp;&nbsp;
                    </div>
                    <div id="out-company-TurnOverRepayment-refund"></div>
                </div>
            </div>

            <%-- 还款率对应移交金额柱状图 --%>
            <div class="right-page" style="float: left; width: 100%; margin-top: 10px;">
                <div class="portlet-title">
                    <div class="caption" style="background-color: #5196FF85; height: 30px; margin: 5px;">
                        <span style="font-size: 22px; font-weight: 600; color: #585858; background-color: #FFDD6069; padding: 5px;">移交还款金额柱状图</span>
                    </div>
                </div>
                <div class="portlet-body border-left-color1" style="padding: 10px;">
                    <div id="out-TurnOverRepayment-amount"></div>
                </div>
            </div>

            <%-- 还款率对应移交金额柱状图 --%>
            <div class="right-page" style="float: left; width: 100%; margin-top: 10px;">
                <div class="portlet-title">
                    <div class="caption" style="background-color: #5196FF85; height: 30px; margin: 5px;">
                        <span style="font-size: 22px; font-weight: 600; color: #585858; background-color: #FFDD6069; padding: 5px;">月份还款率汇总折线图</span>
                    </div>
                </div>
                <div class="portlet-body border-left-color1" style="padding: 10px;">
                    <div id="out-total-rurnOver-Repayment"></div>
                </div>
            </div>

        </div>
    </div>
</div>

<script type="text/javascript">
    var color;
    $(function () {
        color = ['#3AA1EF', '#ED6F53', '#F1AD0A', '#20BF8D', '#E156E7', '#DB843D', '#00FFFF', '#B5CA92',
            '#993399', '#3300FF', '#FFCC33', '#92A8CD', '#CC3366', '#FF9655', '#FF0099', '#00FFCC'];
        // 委外公司下账龄阶段比例
        getOutsourceCaseAgecdScale();
        // 还款率折线图
        setTimeout("getRepaymentRateCaseScale()", 600);
        // 按公司划分移交还款总额
        setTimeout("getCompanyDivideAmountScale()", 1500);//延时1.5秒
        // 获取移交还款率
        setTimeout("getTurnOverRepaymentRateScale()", 1500);

    });

    // 委外公司下账龄阶段比例
    function getOutsourceCaseAgecdScale() {
        $.post('${path}/sc/collection/statistic/loadOutCaseCompanyAndAgecdNum', function (result) {
            if (result.success) {
                /*---------公司账龄---------*/
                var caseNums = result.obj.caseNum, agecdNums = result.obj.agecdNum;
                var countDatas = [], countData1 = [], countData2 = [], sumDatas = [], sumData1 = [], sumData2 = [];
                var allCount = 0, allSum = 0;
                // 组装公司
                for (var i = 0; i < caseNums.length; i++) {
                    var caseNum = caseNums[i];
                    allCount += caseNum.count;
                    allSum += caseNum.sum;
                }
                for (var i = 0; i < caseNums.length; i++) {
                    var caseNum = caseNums[i];
                    countData1[i] = {name: caseNum.company, color: color[i], y: caseNum.count, counts: allCount};
                    sumData1[i] = {name: caseNum.company, color: color[i], y: caseNum.sum, counts: allSum};
                }
                countDatas[0] = countData1;
                sumDatas[0] = sumData1;
                // 组装账龄
                for (var j = 0; j < agecdNums.length; j++) {
                    var agecdNum = agecdNums[j];
                    for (var i = 0; i < caseNums.length; i++) {
                        var caseNum = caseNums[i];
                        if (agecdNum['company'] == caseNum['company']) {
                            countData2[j] = {
                                name: 'M' + agecdNum['agecd'],
                                y: agecdNum.count,
                                color: color[i],
                                count: caseNum.count,
                                counts: allCount
                            };
                            sumData2[j] = {
                                name: 'M' + agecdNum['agecd'],
                                y: agecdNum.sum,
                                color: color[i],
                                count: caseNum.sum,
                                counts: allSum
                            };
                            break;
                        }
                    }
                }
                countDatas[1] = countData2;
                sumDatas[1] = sumData2;
                var cadName = ['公司', '账龄'];
                var CAData1 = [{name: cadName[0], data: countDatas[0], size: '60%'},
                            {name: cadName[1], data: countDatas[1], size: '100%', innerSize: '60%'}],
                        CAData2 = [{name: cadName[0], data: sumDatas[0], size: '60%'},
                            {name: cadName[1], data: sumDatas[1], size: '100%', innerSize: '60%'}];

                /*---------账龄公司--------*/
                var gszlCounts = result.obj.gszlCounts, gszlAmounts = result.obj.gszlAmounts;
                var gszlCount = [], gszlCount1 = [], gszlCount2 = [], gszlAmount = [], gszlAmount1 = [], gszlAmount2 = [];
                var allGszlCount = 0, allGszlAmount = 0;
                // 组装公司
                for (var i = 0; i < gszlCounts.length; i++) {
                    var gzCount = gszlCounts[i];
                    allGszlCount += gzCount.count;
                    allGszlAmount += gzCount.sum;
                }
                for (var i = 0; i < gszlCounts.length; i++) {
                    var gzCount = gszlCounts[i];
                    gszlCount1[i] = {
                        name: 'M' + gzCount['agecd'],
                        color: color[i],
                        y: gzCount.count,
                        counts: allGszlCount
                    };
                    gszlAmount1[i] = {
                        name: 'M' + gzCount['agecd'],
                        color: color[i],
                        y: gzCount.sum,
                        counts: allGszlAmount
                    };
                }
                gszlCount[0] = gszlCount1;
                gszlAmount[0] = gszlAmount1;
                // 组装账龄
                for (var j = 0; j < gszlAmounts.length; j++) {
                    var gzCount = gszlAmounts[j];
                    for (var i = 0; i < gszlCounts.length; i++) {
                        var gzAmount = gszlCounts[i];
                        if (gzAmount['agecd'] == gzCount['agecd']) {
                            gszlCount2[j] = {
                                name: gzCount['company'], y: gzCount.count, color: color[i],
                                count: gzAmount.count, counts: allGszlCount
                            };
                            gszlAmount2[j] = {
                                name: gzCount['company'], y: gzCount.sum, color: color[i],
                                count: gzAmount.sum, counts: allGszlAmount
                            };
                            break;
                        }
                    }
                }
                gszlCount[1] = gszlCount2;
                gszlAmount[1] = gszlAmount2;
                var ACData1 = [{name: cadName[1], data: gszlCount[0], size: '60%'},
                            {name: cadName[0], data: gszlCount[1], size: '100%', innerSize: '60%'}],
                        ACData2 = [{name: cadName[1], data: gszlAmount[0], size: '60%'},
                            {name: cadName[0], data: gszlAmount[1], size: '100%', innerSize: '60%'}];
                doubleFanShaped('#out-company-agecd-count', 'pie', '公司账龄-借据数比例', color, 'ww', CAData1, ' 个');
                doubleFanShaped('#out-company-agecd-amount', 'pie', '公司账龄-移交金额比例', color, 'ww', CAData2, ' 元');

                doubleFanShaped('#out-agecd-company-count', 'pie', '账龄公司-借据数比例', color, 'ww', ACData1, ' 个');
                doubleFanShaped('#out-agecd-company-amount', 'pie', '账龄公司-移交金额比例', color, 'ww', ACData2, ' 元');
            } else {
                parent.$.messager.alert('错误', result.msg, 'error');
            }
        }, 'JSON');
    }

    // 委外公司还款效率
    function getRepaymentRateCaseScale() {
        var param = {
            company: $("#reportCompanys").val(),
            month: $("#reportDateLists").val(),
            handOverAgecd: $("#reportAgecdLists").val()
        };
        $.post('${path}/sc/collection/statistic/loadRepaymentRateCaseNum', param, function (result) {
            if (result.success) {
                var repayments = result.obj.repayments;
                var datas = [], i = 0, names = [];
                for (var key in repayments) {
                    var caseNum = repayments[key], data = [];
                    for (var j = 0; j < caseNum.length; j++) {
                        var n = caseNum[j];
                        data[j] = n.y.toFixed(2) * 1;
                        names[j] = n.x;
                    }
                    datas[i] = {name: key + '月', data: data};
                    i++;
                }
                var mrak = '<p><strong>{series.name} </strong></p><br/><p>第: {point.x + 1} 天, <br/>还款率: {point.y} %</p>';
                highCharts("#out-company-repayment-rate", '', names, datas, true, mrak);
            } else {
                parent.$.messager.alert('错误', result.msg, 'error');
            }
        }, 'JSON');
    }

    // 按公司划分移交还款总额
    function getCompanyDivideAmountScale() {
        var param = {handOverAgecd: $("#reportTurnAgecd").val()};
        $.post('${path}/sc/collection/statistic/loadCompanyDivideAmountNum', param, function (result) {
            if (result.success) {
                var datas = result.obj.datas, dateList = result.obj.dateList;
                var data = [], j = 0, names = [];
                for (var i = 0; i < datas.length; i++) {
                    var d = datas[i];
                    data[i] = {name: d['name'], data: d['data'], stack: d['stack'], color: color[i]};
                }
                for (var key in dateList) {
                    names[j] = dateList[key];
                    j++;
                }
                var mrak = '<p><strong>{series.name}</strong></p><br/><p>金额: {point.y:.2f} <br/>' +
                        '比例: {point.percentage:.2f} % <br/>总金额: {point.stackTotal}</p>';
                highCharts("#out-company-impropriate-refund", 'column', names, data, true, mrak);
            } else {
                parent.$.messager.alert('错误', result.msg, 'error');
            }
        }, 'JSON');
    }

    /**
     * 还款率折线图 + 对应移交金额柱状图
     */
    function getTurnOverRepaymentRateScale() {
        var param = {
            month: $("#reportTurnOverDateLists").val(),
            company: $("#reportTurnOverCompanys").val(),
            handOverAgecd: $("#reportTurnOverAgecd").val()
        };
        $.post('${path}/sc/collection/statistic/loadTurnOverRepaymentRateNum', param, function (result) {
            if (result.success) {
                var datas = result.obj.turnOver, totalLists = result.obj.totalList;
                var data = [], totalData = [], names1 = [], names2 = [], names3 = [], count = 0,
                        totalCount = 0, columnData = [], yjData = [], hkData = [];
                for (var i = 0; i < datas.length; i++) {
                    var d = datas[i];
                    data[i] = {name: d.transfer, data: d.xArray, color: color[i % color.length]};
                    if (d.xArray != null) count = d.xArray.length;
                    yjData[i] = d.amount - d.handOverAmount;
                    hkData[i] = d.handOverAmount;
                    names2[i] = d.transfer;
                }
                for (var i = 0; i < count; i++) {
                    names1[i] = i + 1;
                }
                var mrak1 = '<p><strong>{series.name}</strong></p><br/>还款率: {point.y:.2f} % ';
                highCharts("#out-company-TurnOverRepayment-refund", '', names1, data, true, mrak1);

                columnData[0] = {name: '未还金额', data: yjData, color: color[0]};
                columnData[1] = {name: '已还金额', data: hkData, color: color[1]};
                var mrak2 = '<p><strong>{series.name}</strong></p><br/><p>金额: {point.y:.2f} <br/>' +
                        '比例: {point.percentage:.2f} % <br/>总金额: {point.stackTotal}</p>';
                highCharts("#out-TurnOverRepayment-amount", 'column', names2, columnData, true, mrak2);

                for (var i = 0; i < totalLists.length; i++) {
                    var d = totalLists[i];
                    totalData[i] = {name: d.transfer, data: d.xArray, color: color[i % color.length]};
                    if (d.xArray != null) totalCount = d.xArray.length;
                }
                for (var i = 0; i < totalCount; i++) {
                    names3[i] = i + 1;
                }
                highCharts("#out-total-rurnOver-Repayment", '', names3, totalData, true, mrak1);
            } else {
                parent.$.messager.alert('错误', result.msg, 'error');
            }
        }, 'JSON');
    }

    /**
     * 下载导出
     */
    function downloadTurnOverRepaymentRate() {
        var url = "${path}/sc/collection/statistic/downloadTurnOverRepaymentRate?";
        var month = $("#reportTurnOverDateLists").val();
        var company = $("#reportTurnOverCompanys").val();
        var handOverAgecd = $("#reportTurnOverAgecd").val();
        url += "month=" + month;
        url += "&company=" + company;
        url += "&handOverAgecd=" + handOverAgecd;
        location.href = encodeURI(url);
    }

    /**
     * 公共绘图方法
     * @param target        标签 ID
     * @param type          图表类型
     * @param names         X 轴显示的列名
     * @param massageData   图表数据
     * @param enabled       是否显示图例
     * @param mrak             数据单位
     */
    function highCharts(target, type, names, massageData, enabled, mrak) {
        $(target).highcharts({
            chart: {
                type: type,
                height: 400,
                spacingTop: 0,          //图与边框之间的距离
                spacingRight: 0,
                spacingBottom: 10,
                spacingLeft: 0,
                margin: 50
            },
            title: {
                text: null
            },
            xAxis: {
                categories: names
            },
            yAxis: {
                min: 0,
                title: {
                    text: null
                },
                labels: {
                    overflow: 'justify'
                }
            },
            tooltip: {      //信息提示标签框
                borderRadius: 15,        //圆角边框,值越大,圆角弧度越大
                borderWidth: 1,          //边框宽度
                hideDelay: 100,          //鼠标离开后提示标签隐藏的等待时间,默认500 毫秒
                pointFormat: mrak
            },
            plotOptions: {
                bar: {
                    dataLabels: {
                        enabled: true
                    }
                },
                pie: {
                    allowPointSelect: true,
                    borderWidth: 0.5,              //边框宽度
                    borderColor: '#FFFFFF',     //边框颜色
                    cursor: 'pointer',          //鼠标形状
                    //colors: color,       //每块饼图的颜色,从12点的位置开始,顺时针排列
                    dataLabels: {
                        enabled: true //图例
                    },
                    center: ['50%', '50%'],     //相对于显示框的位置比例(宽, 高)
                    innerSize: 0,             //饼图内的空心圆大小
                    size: '100%',               //饼图的相识比例大小
                    slicedOffset: 0,         //点击饼图扇形区域后偏移的距离大小
                    startAngle: 0             //第一个扇形起始的边界
                },
                series: {
                    label: {
                        connectorAllowed: false
                    },
                    cursor: 'pointer'          //鼠标形状
                },
                column: {
                    cursor: 'pointer',          //鼠标形状
                    stacking: 'normal'
                }
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'top',
                x: -40,
                y: 100,
                floating: true,
                borderWidth: 1,
                shadow: true,
                enabled: enabled
            },
            credits: {
                enabled: false
            },
            series: massageData
        });
    }
    /**
     * 绘制扇形图
     * tag ----  图形需要显示的 id 标签
     * counts ---- 需要绘制扇形的数据 {"扇形区块的名称",[区块数据的大小]}
     * color ---- 扇形中需要填充的颜色数组
     * text ----  显示在图标下方的文字
     * bool ---- 是否显示图例
     * size ---- 显示图形的比例达大小
     * */
    function doubleFanShaped(target, type, title, color, names, massageData, mark) {
        $(target).highcharts({
            chart: {
                type: type,
                width: 360,
                height: 270,
                spacingTop: 5,          //图与边框之间的距离
                spacingRight: 5,
                spacingBottom: 10,
                spacingLeft: 10,
                marginTop: 30
            },
            title: {
                text: title,
                align: 'center',
                style: {color: '#ED6F53', fontSize: "16px"}
            },
            xAxis: {
                categories: names
            },
            yAxis: {
                title: {
                    text: null
                }
            },
            legend: {
                align: 'right',
                x: -20,
                verticalAlign: 'top',
                y: -9,
                floating: true,
                borderColor: '#CCC',
                borderWidth: 1,
                shadow: false
            },
            tooltip: {
                borderRadius: 15,
                formatter: function () {
                    var bl = (this.y / this.point.counts ) * 100;
                    var gsbl = (this.y / this.point.count ) * 100;
                    return '<b>' + this.series.name + '<br/>' +
                            this.point.name + ': ' + this.y + mark +
                            ((gsbl > 0) ? ',<br/>  公司占比:' + gsbl.toFixed(2) + '%' : '')
                            + ',<br/>  总占比: ' + bl.toFixed(2) + '%<br/></b>';
                }
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    borderWidth: 0.5,              //边框宽度
                    borderColor: '#FFFFFF',     //边框颜色
                    cursor: 'pointer',          //鼠标形状
                    colors: color,       //每块饼图的颜色,从12点的位置开始,顺时针排列
                    dataLabels: {
                        enabled: true //图例
                    },
                    center: ['50%', '50%'],     //相对于显示框的位置比例(宽, 高)
                    innerSize: 0,             //饼图内的空心圆大小
                    size: '100%',               //饼图的相识比例大小
                    slicedOffset: 0,         //点击饼图扇形区域后偏移的距离大小
                    startAngle: 0             //第一个扇形起始的边界
                }
            },
            series: massageData,
            credits: {
                enabled: false              // 禁用版权信息
            }
        });
    }


</script>
