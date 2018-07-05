<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<div class="tab-pane fade in active" id="home">
    <div class="container-fluid">
        <div class="row">
            <div class="left-page">
                <div class="span7">
                    <div class="portlet box blue">
                        <div class="portlet-title">
                            <div class="caption">
                                <i class="icon-calendar"></i>饼图
                            </div>
                            <div class="actions">
                                <a href="javascript:;" class="btn yellow easy-pie-chart-reload"><i
                                        class="fa fa-bookmark-o"></i> </a>
                            </div>
                        </div>
                        <div class="portlet-body border-right-color1">
                            <div id="container-deviceType"></div>
                            <div id="container-userVitality"></div>
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
                            <div id="container-userDevice-massage"></div>
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
                            <div id="container-massageCount"></div>
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
                            <div id="container-massage"></div>
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
                            <div id="container-userTypes"></div>
                            <%-- <p class="page">
                                第&nbsp;<span id="pageSize">${staticPath }</span>&nbsp;页
                            </p>
                            <input type="button" id="but_2" th:value="下一页" /> <input
                                type="button" id="but_1" th:value="上一页" /> --%>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    var user = {};       //当前用户
    var pageUp;
    var pageDown;
    var pageUp1;
    var pageDown1;
    var CurrentPlatformDevice;
    var userDevice;
    var pagination;
    var pagination1;
    var pageSize = 0;
    var totalDeviceMassages = [];    //所有用户消息类型
    var allUser;
    //总用户类型展示
    var allUserName = [];
    var usersDeviceType = [];
    var page = 0;

    $(function () {
        pageUp = $("#but_1").click(loadPageUp);
        pageDown = $("#but_2").click(loadPageDown);
        pageUp1 = $("#but_11").click(loadPageUp1);
        pageDown1 = $("#but_22").click(loadPageDown1);
        pagination = $("#pageSize");
        pagination1 = $("#pageSize1");

        //获取消息类型
        //getMassage();
        //加载所有设备类型
        //sys.deviceTypes.load();
        //加载所有的设备
        //sys.devices.loadDevicesForUser();
        //获取用户活跃度
        getUserVitality();
        loadMassageCount();
        userDeviceMassage();
        getMassageType();
        showUserDeviceType();

    });

    function getMassage() {
        $.ajax("/module/report/loadTodayMutual", {
            success: function (price) {
                if (price.err == 0) {
                    var data = price.ret;
                    getMassageType(data);
                    loadMassageCount(data);
                }
            }
        });
    }
    //获取消息类型
    /**********************************消息类型*****************************************/
    function getMassageType() {
        var names = ['普通消息', '位置消息', '报警消息', 'RTU消息', '透传消息'];
        var color = ['#3AA1EF', '#ED6F53', '#F1AD0A', '#20BF8D', '#E156E7',
            '#A47D7C', '#FF9655', '#92A8CD', '#B5CA92', '#DB843D'];
        var deviceType = [435, 456, 343, 345, 34];
        var massageData = [];

        for (var i = 0; i < names.length; i++) {
            massageData[i] = {name: names[i], color: color[i], y: deviceType[i]};
        }
        highCharts('#container-massage', 'bar', names, massageData, false, '条');
    }

    //获取用户设备类型
    /**********************************用户设备类型*****************************************/
    function getUserType(e, device) {        //所有设备
        e.preventDefault();
        var deviceTypes = _global.deviceTypes;
        if (user.userType == 2) {
            getAllUserDevice();
            var deviceType = [];
            for (var i = 0, k = 0; i < deviceTypes.length; i++) {
                for (var n = 0, m = 0; n < device.length; n++) {
                    if (m == 0) {
                        if (device[n].type == deviceTypes[i].id) {
                            deviceType[k] = deviceTypes[i];
                            k++;
                            m++;
                        }
                    } else {
                        break;
                    }
                }
            }
            $.ajax(_global.config.loadCurrentPlatformUserUrl, {
                cache: false,
                success: function (data) {
                    if (data.err == 0) {
                        allUserName = getAllUserName(data.ret);
                        usersDeviceType = getUserDeviceType(data.ret, deviceType);
                        showUserDeviceType(page);
                    }
                }
            });
        } else if (user.userType = 99) {
            $.ajax(_global.config.loadPlatformTypeUsersAdminUrl, {     //所有平台用户
                cache: false,
                async: false,
                success: function (platformUsers) {
                    if (platformUsers.err == 0) {
                        //加载用户设备消息
                        getUserDeviceMassage(platformUsers.ret);
                        allUserName = getAllUserName(platformUsers.ret);
                        usersDeviceType = getPlatformUserDeviceType(platformUsers.ret, deviceTypes, device);
                        showUserDeviceType(page);
                    }
                }
            });
        }
    }
    //显示设备用户类型图
    function showUserDeviceType() {
        var color = ['#3AA1EF', '#ED6F53', '#F1AD0A', '#20BF8D', '#E156E7',
            '#A47D7C', '#FF9655', '#92A8CD', '#B5CA92', '#DB843D'];
        var name = ['a', 's', 'd', 'y', 'j', 'k'];
        var type = [];
        var data = [[135, 23, 45, 34, 52, 534, 34, 54], [32, 542, 454, 345, 23, 534, 34, 345], [323, 75, 554, 654, 46, 435, 374, 373]];
        var n = 0;
        // Y 轴元素个数(有几种数据类型,每种颜色表示一种类型)
        for (var i = 0; i < 3; i++) {
            type[i] = {name: name[i], color: color[i], data: data[i]};
        }
        loadUserType('#container-userTypes', 'column', 'normal', name, type);
    }
    //上一页
    function loadPageUp() {
        var page1 = page;
        if (page1 > 0) {
            var pageSize = page - 10;
            showUserDeviceType(pageSize);
            page = page - 10;
        } else {
            _fn.alert("已经是第一页!");
        }
    }
    //下一页
    function loadPageDown() {
        var page1 = page;
        var deviceType = usersDeviceType[0].data;
        if (deviceType.length != null) {
            var length = deviceType.length;
            if ((page1 + 10) < length) {
                var pageSize = page + 10;
                showUserDeviceType(pageSize);
                page = page + 10;
            } else {
                _fn.alert("已经是最后一页!");
            }
        }
    }
    //获取所有平台用户的名称
    function getAllUserName(platformUsers) {
        var names = [];
        for (var i = 0, m = 0; i < platformUsers.length; i++) {
            var name = platformUsers[i].displayName;
            if (name == null) {
                name = platformUsers[i].name;
            }
            if (platformUsers[i].userType != 99) {
                names[m] = name;
                m++;
            }
        }
        return names;
    }
    //获取平台下用户设备类型数据
    function getUserDeviceType(platformUsers, deviceType) {
        var types = [];
        for (var i = 0; i < deviceType.length; i++) {
            var deviceCount = [];
            for (var j = 0, m = 0; j < platformUsers.length; j++) {
                if (platformUsers[j].userType != 2) {
                    var count = getPlatformNextUserCount(deviceType[i].id, platformUsers[j].id);
                    deviceCount[m] = count;
                    m++;
                }
            }
            types[i] = {name: deviceType[i].name, data: deviceCount};
        }
        return types;
    }
    //获取平台用户设备类型数据
    function getPlatformUserDeviceType(users, deviceType, devices) {
        var types = [];
        for (var i = 0; i < deviceType.length; i++) {
            var deviceCount = [];
            var m = 0;
            for (var j = 0; j < users.length; j++) {
                if (users[j].userType != 99) {
                    var count = getUserDeviceTypeCount(deviceType[i].id, users[j].id, devices);
                    deviceCount[m] = count;
                    m++;
                }
            }
            types[i] = {name: deviceType[i].name, data: deviceCount};
        }
        return types;
    }
    //获得所有设备类型的设备
    function getUserDeviceTypeCount(deviceType, userId, devices) {
        var count = 0;
        for (var i = 0; i < devices.length; i++) {
            if (devices[i].platformId == userId) {
                if (devices[i].type == deviceType) {
                    count++;
                }
            }
        }
        return count;
    }
    //获取平台下每个用户的所有设备
    function getPlatformNextUserCount(deviceType, id) {
        var count = 0;
        var devices = CurrentPlatformDevice;
        for (var i = 0; i < userDevice.length; i++) {
            if (userDevice[i].userId == id) {
                for (var j = 0; j < devices.length; j++) {
                    if (devices[j].id == userDevice[i].deviceId) {
                        if (deviceType == devices[j].type) {
                            count++;
                            break;
                        }
                    }
                }
            }
        }
        return count;
    }
    //获取所有的用户与设备关联数据
    function getAllUserDevice() {
        $.ajax(_global.config.loadAllUserDevice, {     //所有平台用户
            cache: false,
            type: "post",
            success: function (userDevices) {
                if (userDevices.err == 0) {
                    userDevice = userDevices.ret;
                }
            }
        });
    }

    //加载设备类型
    /********************************设备类型--用户活跃度************************************/
    function deviceTypeFanShaped(e, deviceType) {
        e.preventDefault();
        var color = ['#3AA1EF', '#ED6F53', '#F1AD0A', '#20BF8D', '#E156E7',
            '#A47D7C', '#FF9655', '#92A8CD', '#B5CA92', '#DB843D'];
        if (user.userType == 99) {
            adminDeviceType(deviceType, color);
        }
        if (user.userType == 2) {
            userDeviceType(deviceType, color);
        }
    }
    //管理员
    function adminDeviceType(deviceType, color) {
        $.ajax(_global.config.loadAllDevicesUrl, {
            success: function (date) {
                if (date.err == 0) {
                    var device = date.ret;
                    var devices = [];
                    for (var i = 0; i < deviceType.length; i++) {
                        var count = getDeviceCount(deviceType[i].id, device);
                        devices[i] = {name: deviceType[i].name, color: color[i], y: count};
                    }
                    var text = "平台设备类型展示";
                    _fn.fanShaped("#container-deviceType", devices, color, text, true, "95%");
                }
            }
        });
    }
    //平台用户
    function userDeviceType(deviceTypes, color) {
        var device = CurrentPlatformDevice;
        var deviceType = [];
        var deviceData = [];
        for (var i = 0, k = 0; i < deviceTypes.length; i++) {
            for (var n = 0, m = 0; n < device.length; n++) {
                if (m == 0) {
                    if (device[n].type == deviceTypes[i].id) {
                        deviceType[k] = deviceTypes[i];
                        k++;
                        m++;
                    }
                } else {
                    break;
                }
            }
        }
        for (var i = 0; i < deviceType.length; i++) {
            var count = getDeviceCount(deviceType[i].id, device);
            deviceData[i] = {name: deviceType[i].name, color: color[i], y: count};
        }
        var text = "当前平台设备类型展示";
        fanShaped("#container-deviceType", deviceData, color, text, true, "80%");

    }
    function getDeviceCount(deviceTypeId, devices) {
        var count = 0;
        for (var i = 0; i < devices.length; i++) {
            if (deviceTypeId == devices[i].type) {
                count++;
            }
        }
        return count;
    }

    //用户活跃度
    function getUserVitality() {
        var color = ['#3AA1EF', '#ED6F53', '#F1AD0A', '#20BF8D', '#E156E7',
            '#A47D7C', '#FF9655', '#92A8CD', '#B5CA92', '#DB843D'];
        var data = [{name: '活跃用户', y: 483}, {name: '中等', y: 590}, {name: '一般', y: 346}, {name: '不活跃', y: 118}];
        var text = '用户活跃度统计';
        sys.fn.fanShaped("#container-userVitality", data, color, text, true, "95%");
    }

    //用户设备消息
    /**********************************用户设备消息***************************************/
    function getUserDeviceMassage(allUsers) {
        $.ajax("/module/report/loadAllUserMassage", {
            success: function (datas) {
                if (datas.err == 0) {
                    totalDeviceMassages = datas.ret;
                    allUser = allUsers;
                    userDeviceMassage(pageSize);
                }
            }
        });
    }
    function userDeviceMassage() {
        var names = ['张华', '李强', '桑奇', '秦钟', '孟桓', '邹阳', '琳琳', '芷若', '穗香'];
        var color = ['#3AA1EF', '#ED6F53', '#F1AD0A', '#20BF8D', '#E156E7',
            '#A47D7C', '#FF9655', '#92A8CD', '#B5CA92', '#DB843D'];
        var data1 = [23, 234, 43, 534, 34, 54, 64, 34, 35];
        var data2 = [56, 72, 45, 24, 57, 42, 23, 45, 35];
        var data3 = [65, 75, 64, 73, 64, 52, 53, 46, 56];
        var data4 = [45, 54, 67, 43, 48, 74, 56, 54, 63];
        var data5 = [67, 67, 53, 46, 75, 63, 45, 35, 67];

        var data = [{name: '普通消息', color: color[0], data: data1},
            {name: '位置跟踪', color: color[1], data: data2},
            {name: '报警消息', color: color[2], data: data3},
            {name: 'RTU消息', color: color[3], data: data4},
            {name: '透传消息', color: color[4], data: data5}];
        loadUserType('#container-userDevice-massage', 'column', '', names, data);
    }
    //上一页
    function loadPageUp1() {
        var page1 = pageSize;
        if (page1 > 0) {
            var pageSize1 = page1 - 10;
            userDeviceMassage(pageSize1);
            pageSize = pageSize - 10;
        } else {
            _fn.alert("已经是第一页!")
        }
    }
    //下一页
    function loadPageDown1() {
        var users = allUser;
        if (users.length != null) {
            var length = users.length;
            if ((pageSize + 10) < length) {
                var pageSize1 = pageSize + 10;
                userDeviceMassage(pageSize1);
                pageSize = pageSize + 10;
            } else {
                _fn.alert("已经是最后一页!")
            }
        }
    }


    //消息收发量统计
    /************************************消息收发量统计*************************************/
    function loadMassageCount() {
        var names = [];
        var massage = [['qwe'], ['ewr']];
        for (var i = 0; i < massage.length; i++) {
            names[i] = massage[i];
        }
        var color = ['#3AA1EF', '#ED6F53', '#F1AD0A', '#20BF8D', '#E156E7',
            '#FF9655', '#92A8CD', '#B5CA92', '#DB843D', '#A47D7C'];
        var name = ['接收', '发送'];
        var massageData = [];
        var data_name = [['一月'], ['二月'], ['三月'], ['四月'], ['五月'], ['六月'], ['七月']];

        var data = [[
            {name: data_name[0], y: 243, color: color[0], count: 3068},
            {name: data_name[1],y: 325, color: color[1], count: 3068},
            {name: data_name[2],y: 345, color: color[2], count: 3068},
            {name: data_name[3],y: 643, color: color[3], count: 3068},
            {name: data_name[4],y: 843, color: color[4], count: 3068},
            {name: data_name[5],y: 234, color: color[5], count: 3068},
            {name: data_name[6],y: 435, color: color[6], count: 3068}
        ], [
            {name: data_name[0],y: 454, color: color[0], count: 3068},
            {name: data_name[1],y: 654, color: color[1], count: 3068},
            {name: data_name[2],y: 345, color: color[2] , count: 3068},
            {name: data_name[3],y: 734, color: color[3], count: 3068},
            {name: data_name[4],y: 134, color: color[4], count: 3068},
            {name: data_name[5],y: 633, color: color[5], count: 3068},
            {name: data_name[5],y: 633, color: color[3], count: 3068},
            {name: data_name[5],y: 633, color: color[2], count: 3068},
            {name: data_name[5],y: 633, color: color[3], count: 3068},
            {name: data_name[5],y: 633, color: color[8], count: 3068},
            {name: data_name[6],y: 146, color: color[6], count: 3068}
        ]];
        for (var i = 0; i < data.length; i++) {
            if (i == 0) {
                massageData[i] = data[i];
            } else {
                massageData[i] = data[i]; // 指定内环大小
            }
        }
        doubleloadUserType('#container-massageCount', 'pie', color, data_name, massageData);
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
    function highCharts(target, type, names, massageData, enabled, U) {
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
                borderRadius: 15,        //圆角边框,值越大,圆角弧度越大
                borderWidth: 1,          //边框宽度
                hideDelay: 100,          //鼠标离开后提示标签隐藏的等待时间,默认500 毫秒
                pointFormat: '<p><strong> {point.y} </strong></p>' + U
            },
            plotOptions: {
                bar: {
                    dataLabels: {
                        enabled: true
                    }
                },
                pie: {
                    allowPointSelect: true,
                    borderWidth: 0,              //边框宽度
                    borderColor: '#FFFFFF',     //边框颜色
                    cursor: 'pointer',          //鼠标形状
                    colors: color,       //每块饼图的颜色,从12点的位置开始,顺时针排列
                    dataLabels: {
                        enabled: true //图例
                    },
                    center: ["50%", "50%"],     //相对于显示框的位置比例(宽, 高)
                    innerSize: 0,             //饼图内的空心圆大小
                    size: size,               //饼图的相识比例大小
                    slicedOffset: 10,         //点击饼图扇形区域后偏移的距离大小
                    startAngle: 0             //第一个扇形起始的边界
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
    function doubleloadUserType(target, type, color, names, massageData) {
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
                borderRadius: 15,
                formatter: function () {
                    var bl = (this.y/this.point.count ) * 100;
                    return '<b>' + this.series.name + '</b><br/>' +
                            this.point.name + ': ' + this.y + ',  占比:' + bl.toFixed(2) + '%<br/>';
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
                    size: '90%',               //饼图的相识比例大小
                    slicedOffset: 0,         //点击饼图扇形区域后偏移的距离大小
                    startAngle: 0             //第一个扇形起始的边界
                }
            },
            series: [{
                name: '公司',
                data: massageData[0],
                size: '60%' // 指定饼图大小
            }, {
                name: '账龄',
                data: massageData[1],
                size: '100%',  // 指定大小
                innerSize: '60%' // 指定内环大小
            }],
            credits: {
                enabled: false              // 禁用版权信息
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
    function fanShaped(tag, counts, color, text, bool, size) {
        $(tag).highcharts({
            chart: {
                height: 290,        //图表的高度。默认高度是根据容器 div 的高度值计算而来，如果容器没有设置高度值，则是 400px。
                type: 'bar',       //指定绘制区所要绘制的图的类型，例如：type=bar为柱图，type=line为线图
                spacingTop: 0,          //图与边框之间的距离
                spacingRight: 0,
                spacingBottom: 16,
                spacingLeft: 0,
                marginTop: 10
            },
            title: {    //图表标题
                text: '',       //标题文字
                floating: true       //是否浮动(true : 浮动时,图表层位于标题层下边,标题浮现于图层上边,标题不占用页面空间)
            },
            tooltip: {      //信息提示标签框
                borderRadius: 15,        //圆角边框,值越大,圆角弧度越大
                borderWidth: 1,          //边框宽度
                hideDelay: 60,          //鼠标离开后提示标签隐藏的等待时间,默认500 毫秒
                pointFormat: '<b>{series.name}  {point.y}个  占总数的: {point.percentage:.1f}%</b>'
            },
            plotOptions: {
                pie: {
                    allowPointSelect: true,
                    borderWidth: 0,              //边框宽度
                    borderColor: '#FFFFFF',     //边框颜色
                    cursor: 'pointer',          //鼠标形状
                    colors: color,       //每块饼图的颜色,从12点的位置开始,顺时针排列
                    dataLabels: {
                        enabled: bool      //图例
                    },
                    center: ["50%", "50%"],     //相对于显示框的位置比例(宽, 高)
                    innerSize: 0,             //饼图内的空心圆大小
                    size: size,               //饼图的相识比例大小
                    slicedOffset: 10,         //点击饼图扇形区域后偏移的距离大小
                    startAngle: 0             //第一个扇形起始的边界
                }
            },
            series: [{
                type: 'pie',            //显示的图像类型(饼图, 折线图, 条形图...)
                name: ' ',
                sliced: true,
                data: counts             //饼图的显示数据
            }],
            credits: {
                enabled: true,               // 禁用版权信息
                text: text,               // 显示的文字
                href: '',                    // 链接地址
                position: {                  // 位置设置
                    align: 'center'
                }
            }
        });
    }

</script>
