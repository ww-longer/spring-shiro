/**
 * Created by longer on 2015-07-06.
 */
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
        getMassage();
        //加载所有设备类型
        //sys.deviceTypes.load();
        //加载所有的设备
        //sys.devices.loadDevicesForUser();
        //获取用户活跃度
        getUserVitality();

    });

    function getMassage(){
        $.ajax("/module/report/loadTodayMutual",{
            success: function (price) {
                if(price.err == 0){
                    var data = price.ret;
                    console.log(data);
                    getMassageType(data);
                    loadMassageCount(data);
                }
            }
        });
    }
    //获取消息类型
    /**********************************消息类型*****************************************/
    function getMassageType(data){
        var names = ['普通消息','位置消息','报警消息','RTU消息','透传消息'];
        var color = [ '#3AA1EF', '#ED6F53','#F1AD0A', '#20BF8D', '#E156E7',
            '#A47D7C','#FF9655',  '#92A8CD', '#B5CA92', '#DB843D'];
        if(data.length != 0){
            var deviceType = [data[0].type1, data[0].type2, data[0].type3, data[0].type8, data[0].type9];
            var massageData = [];

            for(var i=0; i < names.length; i ++){
                massageData[i] = {name: names[i], color: color[i], y: deviceType[i]};
            }
            highCharts('#container-massage', 'bar',names, massageData, false, '条');
        }
    }

    //获取用户设备类型
    /**********************************用户设备类型*****************************************/
    function getUserType(e, device){        //所有设备
        e.preventDefault();
        var deviceTypes = _global.deviceTypes;
        if(user.userType == 2){
            getAllUserDevice();
            var deviceType = [];
            for(var i = 0, k = 0; i < deviceTypes.length; i ++) {
                for (var n = 0, m = 0; n < device.length; n ++) {
                    if(m == 0){
                        if (device[n].type == deviceTypes[i].id) {
                            deviceType[k] = deviceTypes[i];
                            k ++;   m ++;
                        }
                    }else{
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
        }else if(user.userType = 99){
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
    function showUserDeviceType(pageSize){
        var color = [ '#3AA1EF', '#ED6F53','#F1AD0A', '#20BF8D', '#E156E7',
            '#A47D7C','#FF9655',  '#92A8CD', '#B5CA92', '#DB843D'];
        var name = [];
        var type = [];
        var length = 10 + pageSize;
        for(var i = 0; i < usersDeviceType.length; i ++){
            var deviceType = usersDeviceType[i].data;
            if((deviceType.length - pageSize) < 10){
                length = deviceType.length;
            }
            var data = [];
            var n = pageSize;
            for(var m = 0; n < length; n ++, m ++){
                name[m] = allUserName[n];
                data[m] = deviceType[n];
            }
            type[i] = {name:usersDeviceType[i].name, color:color[i], data: data};
        }
        pagination.html(pageSize/10 + 1);
        loadUserType('#container-userTypes', 'column', 'normal', name, type);
    }
    //上一页
    function loadPageUp(){
        var page1 = page;
        if(page1 > 0){
            var pageSize = page - 10;
            showUserDeviceType(pageSize);
            page = page - 10;
        }else{
            _fn.alert("已经是第一页!")
        }
    }
    //下一页
    function loadPageDown(){
        var page1 = page;
        var deviceType = usersDeviceType[0].data;
        if(deviceType.length != null){
            var length = deviceType.length;
            if((page1 + 10) < length){
                var pageSize = page + 10;
                showUserDeviceType(pageSize);
                page = page + 10;
            }else{
                _fn.alert("已经是最后一页!")
            }
        }
    }
    //获取所有平台用户的名称
    function getAllUserName(platformUsers){
        var names = [];
        for(var i= 0, m = 0; i < platformUsers.length; i ++){
            var name = platformUsers[i].displayName;
            if(name == null){
                name = platformUsers[i].name;
            }
            if(platformUsers[i].userType != 99){
                names[m] = name;
                m ++;
            }
        }
        return names;
    }
    //获取平台下用户设备类型数据
    function getUserDeviceType(platformUsers, deviceType){
        var types = [];
        for(var i=0; i < deviceType.length; i ++){
            var deviceCount = [];
            for(var j= 0, m = 0; j < platformUsers.length; j ++){
                if(platformUsers[j].userType != 2){
                    var count = getPlatformNextUserCount(deviceType[i].id, platformUsers[j].id);
                    deviceCount[m] = count;
                    m ++;
                }
            }
            types[i] = {name: deviceType[i].name, data: deviceCount};
        }
        return types;
    }
    //获取平台用户设备类型数据
    function getPlatformUserDeviceType(users, deviceType, devices){
        var types = [];
        for(var i=0; i < deviceType.length; i ++){
            var deviceCount = [];
            var m = 0;
            for(var j=0; j < users.length; j ++){
                if(users[j].userType != 99){
                    var count = getUserDeviceTypeCount(deviceType[i].id, users[j].id, devices);
                    deviceCount[m] = count;
                    m ++;
                }
            }
            types[i] = {name: deviceType[i].name, data: deviceCount};
        }
        return types;
    }
    //获得所有设备类型的设备
    function getUserDeviceTypeCount(deviceType, userId, devices){
        var count = 0;
        for(var i=0; i < devices.length; i ++){
            if(devices[i].platformId == userId){
                if(devices[i].type == deviceType){
                    count ++;
                }
            }
        }
        return count;
    }
    //获取平台下每个用户的所有设备
    function getPlatformNextUserCount(deviceType, id){
        var count = 0;
        var devices = CurrentPlatformDevice;
        for(var i=0; i < userDevice.length; i ++){
            if(userDevice[i].userId == id){
                for(var j = 0; j < devices.length; j ++){
                    if(devices[j].id == userDevice[i].deviceId){
                        if(deviceType == devices[j].type){
                            count ++;
                            break;
                        }
                    }
                }
            }
        }
        return count;
    }
    //获取所有的用户与设备关联数据
    function getAllUserDevice(){
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
    function deviceTypeFanShaped(e, deviceType){
        e.preventDefault();
        var color = [ '#3AA1EF', '#ED6F53','#F1AD0A', '#20BF8D', '#E156E7',
            '#A47D7C','#FF9655',  '#92A8CD', '#B5CA92', '#DB843D'];
        if(user.userType == 99){
            adminDeviceType( deviceType, color);
        }if(user.userType == 2){
            userDeviceType( deviceType, color);
        }
    }
    //管理员
    function adminDeviceType(deviceType, color){
        $.ajax(_global.config.loadAllDevicesUrl, {
            success: function (date) {
                if (date.err == 0) {
                    var device = date.ret;
                    var devices = [];
                    for(var i=0; i<deviceType.length; i ++){
                        var count = getDeviceCount(deviceType[i].id, device);
                        devices[i] = {name : deviceType[i].name, color: color[i], y: count};
                    }
                    var text = "平台设备类型展示";
                    _fn.fanShaped("#container-deviceType", devices, color, text, true, "95%");
                }
            }
        });
    }
    //平台用户
    function userDeviceType(deviceTypes, color){
        var device = CurrentPlatformDevice;
        var deviceType = [];
        var deviceData = [];
        for(var i = 0,k = 0; i < deviceTypes.length; i ++) {
            for (var n = 0, m = 0; n < device.length; n ++) {
                if(m == 0){
                    if (device[n].type == deviceTypes[i].id) {
                        deviceType[k] = deviceTypes[i];
                        k ++;   m ++;
                    }
                }else{
                    break;
                }
            }
        }
        for(var i = 0; i < deviceType.length; i ++){
            var count = getDeviceCount(deviceType[i].id, device);
            deviceData[i] = {name : deviceType[i].name, color: color[i], y: count};
        }
        var text = "当前平台设备类型展示";
        fanShaped("#container-deviceType", deviceData, color, text, true, "80%");

    }
    function getDeviceCount(deviceTypeId, devices){
        var count = 0;
        for(var i=0; i<devices.length; i ++){
            if(deviceTypeId == devices[i].type){
                count ++;
            }
        }
        return count;
    }

    //用户活跃度
    function getUserVitality(){
        var color = [ '#3AA1EF', '#ED6F53','#F1AD0A', '#20BF8D', '#E156E7',
            '#A47D7C','#FF9655',  '#92A8CD', '#B5CA92', '#DB843D'];
        var data = [{name:'活跃用户',y: 483},{name: '中等', y: 590},{name: '一般', y: 346},{name: '不活跃', y: 118}];
        var text = '用户活跃度统计';
        fanShaped("#container-userVitality", data, color, text, true, "95%");
    }

    //用户设备消息
    /**********************************用户设备消息***************************************/
    function getUserDeviceMassage(allUsers){
        $.ajax("/module/report/loadAllUserMassage", {
            success: function (datas) {
                if(datas.err == 0){
                    totalDeviceMassages = datas.ret;
                    allUser = allUsers;
                    userDeviceMassage(pageSize);
                }
            }
        });
    }
    function userDeviceMassage(page){
        var names = [];
        var color = ['#3AA1EF', '#ED6F53','#F1AD0A', '#20BF8D', '#E156E7',
            '#A47D7C','#FF9655',  '#92A8CD', '#B5CA92', '#DB843D'];
        var users = allUser;
        var massage = totalDeviceMassages;
        var data1 = [];
        var data2 = [];
        var data3 = [];
        var data4 = [];
        var data5 = [];
        var m = page;
        var p = m + 10;
        if(p > users.length) {
            p = users.length;
        }
        for (var n = 0; m < p; m ++) {
            for (var j = 0; j < massage.length; j ++) {
                if(users[m].id == massage[j].userId){
                    data1[n] = 0;
                    if(massage[j].type1 != null){
                        data1[n] = massage[j].type1;
                    }
                    data2[n] = 0;
                    if(massage[j].type2 != null){
                        data2[n] = massage[j].type2;
                    }data3[n] = 0;
                    if(massage[j].type3 != null){
                        data3[n] = massage[j].type3;
                    }
                    data4[n] = 0;
                    if(massage[j].type8 != null){
                        data4[n] = massage[j].type8;
                    }
                    data5[n] = 0;
                    if(massage[j].type9 != null){
                        data5[n] = massage[j].type9;
                    }
                    names[n] = allUserName[m];
                    n ++;
                    break;
                }
            }
        }
        var data = [{name:'普通消息', color:color[0], data:data1},
            {name:'位置跟踪', color:color[1], data:data2},
            {name:'报警消息', color:color[2], data:data3},
            {name:'RTU消息', color:color[3], data:data4},
            {name:'透传消息', color:color[4], data:data5}];
        pagination1.html(page/10 + 1);
        loadUserType('#container-userDevice-massage', 'column', '', names, data);
    }
    //上一页
    function loadPageUp1(){
        var page1 = pageSize;
        if(page1 > 0){
            var pageSize1 = page1 - 10;
            userDeviceMassage(pageSize1);
            pageSize = pageSize - 10;
        }else{
            _fn.alert("已经是第一页!")
        }
    }
    //下一页
    function loadPageDown1(){
        var users = allUser;
        if(users.length != null){
            var length = users.length;
            if((pageSize + 10) < length){
                var pageSize1 = pageSize + 10;
                userDeviceMassage(pageSize1);
                pageSize = pageSize + 10;
            }else{
                _fn.alert("已经是最后一页!")
            }
        }
    }


    //消息收发量统计
    /************************************消息收发量统计*************************************/
    function loadMassageCount(datas){
        $.ajax("/module/report/message_sum",{
            success: function (massages) {
                if(massages.err == 0){
                    var massage = massages.ret;
                    massage.reverse();
                    var names = [];
                    for(var i = 0; i < massage.length; i ++){
                        names[i] = massage[i];
                    }
                    var color = [ '#3AA1EF', '#ED6F53'];
                    var name = ['接收','发送'];
                    var massageData = [];
                    var total = [];
                    var t = 16;
                    if(datas.length <= 16){
                        t = datas.length;
                    }
                    for(var i = 0; i < t; i ++){
                        total[i] = datas[i];
                    }
                    total.reverse();
                    var data1 = [];
                    var data2 = [];
                    var data_name = [];
                    for(var i = 0; i < total.length-1; i ++){
                        data1[i] = total[i].in==null ? 0:total[i].in;
                        data2[i] = total[i].out==null ? 0:total[i].out;
                        data_name[i] = names[names.length - (total.length - 1) + i];
                    }
                    var data = [data1, data2];
                    /*var data = [[243,325,324,281,455,375,427], [234,274,407,342,422,439,459]];*/
                    for(var i=0; i < name.length; i ++){
                        massageData[i] = {name:name[i], color: color[i], data: data[i]};
                    }
                    loadUserType('#container-massageCount', '', 'normal', data_name, massageData);
                }
            }
        });
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
                enabled:false               // 禁用版权信息
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