<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
	<div class="tab-pane fade in active" id="home">
		<div class="container-fluid">
			<div class="row">
				<div class="left-page">
					<div class="span7">
						<div class="portlet box blue">
							<div class="portlet-title">
								<div class="caption">
									<i class="icon-calendar"></i>委外中案件比例
								</div>
								<div class="actions">
									<a href="javascript:;" class="btn yellow easy-pie-chart-reload"><i
										class="fa fa-bookmark-o"></i> </a>
								</div>
							</div>
							<div class="portlet-body border-right-color1">
								<div id="out-case-scale"></div>
							</div>
						</div>
					</div>
					<div class="span7" id="userDeviceMassage">
						<div class="portlet box blue">
							<div class="portlet-title">
								<div class="caption">
									<i class="icon-calendar"></i>用户设备消息
								</div>
								<div class="actions">
									<a href="javascript:;" class="btn yellow easy-pie-chart-reload"><i
										class="fa fa-bookmark-o"></i> </a>
								</div>
							</div>
							<div class="portlet-body border-right-color2">
								<%--<div id="container-userDevice-massage"></div>--%>
								<!-- <p class="page">
									第&nbsp;<span id="pageSize1"></span>&nbsp;页
								</p>
								<input type="button" id="but_22" th:value="下一页" /> <input
									type="button" id="but_11" th:value="上一页" /> -->
							</div>
						</div>
					</div>
					<div class="span7">
						<div class="portlet box blue">
							<div class="portlet-title">
								<div class="caption">
									<i class="icon-calendar"></i>消息收发量统计
								</div>
								<div class="actions">
									<a href="javascript:;" class="btn yellow easy-pie-chart-reload"><i
										class="fa fa-bookmark-o"></i> </a>
								</div>
							</div>
							<div class="portlet-body border-right-color3">
								<%--<div id="container-massageCount"></div>--%>
							</div>
						</div>
					</div>
				</div>

				<div class="right-page">
					<div class="span6">
						<div class="portlet box blue">
							<div class="portlet-title">
								<div class="caption">
									<i class="icon-calendar"></i>消息类型
								</div>
								<div class="actions">
									<a href="javascript:;" class="btn yellow easy-pie-chart-reload"><i
										class="fa fa-bookmark-o"></i> </a>
								</div>
							</div>
							<div class="portlet-body border-left-color1">
								<%--<div id="container-massage"></div>--%>
							</div>
						</div>
					</div>
					<div class="span6">
						<div class="portlet box blue">
							<div class="portlet-title">
								<div class="caption">
									<i class="icon-calendar"></i>用户设备类型
								</div>
								<div class="actions">
									<a href="javascript:;" class="btn yellow easy-pie-chart-reload"><i
										class="fa fa-bookmark-o"></i> </a>
								</div>
							</div>
							<div class="portlet-body border-left-color2">
								<%--<div id="container-userTypes"></div>--%>

							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

<script type="text/javascript">
    $(function () {
        // 委外公司分配案件比例
        getOutsourceCaseScale();

    });
    // 委外公司分配案件比例
    function getOutsourceCaseScale(){
        $.post('${path}/sc/collection/statistic/loadAllOutCompanyCaseNum', function(result) {
            if (result.success) {
                var color = [ '#3AA1EF', '#ED6F53','#F1AD0A', '#20BF8D', '#E156E7', '#A47D7C','#FF9655',  '#92A8CD', '#B5CA92', '#DB843D'];
                var text = '委外案件比例';
                var caseNums = result.obj.caseNum;
                var data = [];
                for (var i = 0; i < caseNums.length; i ++){
                    var caseNum = caseNums[i];
                    data[i] = {name: caseNum.company, y: caseNum.count};
                }
                sys.fn.fanShaped("#out-case-scale", data, color, text, true, "95%");
            } else {
                parent.$.messager.alert('错误', result.msg, 'error');
            }
        }, 'JSON');



    }

    /**
     * 公共绘图方法
     * @param target        标签 ID
     * @param type          图表类型
     * @param names         X 轴显示的列名
     * @param massageData   图表数据
     * @param enabled       是否显示图例
     * @param U             数据单位
     */
    function highCharts(target ,type, names, massageData, enabled, U){
        $(target).highcharts({
            chart: {
                type: type,
                height: 350
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
                borderRadius:15,        //圆角边框,值越大,圆角弧度越大
                borderWidth:1,          //边框宽度
                hideDelay:100,          //鼠标离开后提示标签隐藏的等待时间,默认500 毫秒
                pointFormat: '<p><strong> {point.y} </strong></p>' + U
            },
            plotOptions: {
                bar: {
                    dataLabels: {
                        enabled: true
                    }
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
            series: [{
                data: massageData
            }]
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
    function loadUserType(target, type, normal, names, massageData){
        $(target).highcharts({
            chart: {
                type: type,
                height: 320,
                marginTop: 20
            },
            title: {
                text: ''
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
                borderRadius:15,
                formatter: function() {
                    return '<b>'+ this.x +'</b><br/>'+
                        this.series.name +': '+ this.y +'<br/>'
                }
            },
            plotOptions: {
                column: {
                    stacking: normal,
                    dataLabels: {
                        enabled: false,
                        color: 'red'
                    }
                }
            },
            series: massageData ,
            credits:{
                enabled:false              // 禁用版权信息
            }
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
        function fanShaped(tag, counts, color, text, bool, size){
            $(tag).highcharts({
                chart: {
                    height: 290,        //图表的高度。默认高度是根据容器 div 的高度值计算而来，如果容器没有设置高度值，则是 400px。
                    type: 'bar',       //指定绘制区所要绘制的图的类型，例如：type=bar为柱图，type=line为线图
                    spacingTop: 0,          //图与边框之间的距离
                    spacingRight:0,
                    spacingBottom:16,
                    spacingLeft:0,
                    marginTop:10
                },
                title: {    //图表标题
                    text: '',       //标题文字
                    floating:true       //是否浮动(true : 浮动时,图表层位于标题层下边,标题浮现于图层上边,标题不占用页面空间)
                },
                tooltip: {      //信息提示标签框
                    borderRadius:15,        //圆角边框,值越大,圆角弧度越大
                    borderWidth:1,          //边框宽度
                    hideDelay:60,          //鼠标离开后提示标签隐藏的等待时间,默认500 毫秒
                    pointFormat: '<b>{series.name}  {point.y}个  占总数的: {point.percentage:.1f}%</b>'
                },
                plotOptions: {
                    pie: {
                        allowPointSelect: true,
                        borderWidth:0,              //边框宽度
                        borderColor: '#FFFFFF',     //边框颜色
                        cursor: 'pointer',          //鼠标形状
                        colors:color,       //每块饼图的颜色,从12点的位置开始,顺时针排列
                        dataLabels: {
                            enabled: bool      //图例
                        },
                        center:["50%","50%"],     //相对于显示框的位置比例(宽, 高)
                        innerSize:0,             //饼图内的空心圆大小
                        size:size,               //饼图的相识比例大小
                        slicedOffset:10,         //点击饼图扇形区域后偏移的距离大小
                        startAngle:0             //第一个扇形起始的边界
                    }
                },
                series: [{
                    type: 'pie',            //显示的图像类型(饼图, 折线图, 条形图...)
                    name: ' ',
                    sliced:true,
                    data: counts             //饼图的显示数据
                }],
                credits:{
                    enabled:true,               // 禁用版权信息
                    text: text,               // 显示的文字
                    href:'',                    // 链接地址
                    position:{                  // 位置设置
                        align: 'center'
                    }
                }
            });
        }
</script>
