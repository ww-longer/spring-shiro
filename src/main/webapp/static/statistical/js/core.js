(function ($, _win, _doc) {
    /**
     * 对数组类型扩展一个搜索方法，针对对象数组内的id字段进行搜索
     * @param id
     * @returns {*}
     */
    Array.prototype.$get = function (id) {
        for (var i = 0, j = this.length; i < j; i++) {
            if (this[i].id == id) return this[i];
        }
        return null;
    };

    Array.prototype.$search = function (field, value) {
        var ret = [];
        for (var i = 0, j = this.length; i < j; i++) {
            if (this[i][field] == value) ret.push(this[i]);
        }
        return ret;
    };

    var sys = {};

    var fn = {
        alert: function(info, yesCb){
            fn.confirm(info, yesCb);
            confirmModal.find("button.no").hide();
        },
        confirm: function (info, yesCb, noCb) {
            info = info || "";
            yesCb = yesCb || $.noop;
            noCb = noCb || $.noop;
            confirmModal.find(".modal-body").html(info);
            confirmModal.find("button.yes").off().click(yesCb);
            confirmModal.find("button.no").off().show().click(noCb);
            confirmModal.modal("show");
        },
        /**
         * 用于标记表单提交后，发生参数错误的字段，同时返回连接好的错误信息
         * @param form
         * @param errors
         */
        markFormError: function(form, errors){
            var f = $(form);
            f.find(".has-error").removeClass('has-error');
            var err = "";
            if(errors) $.each(errors, function(field, code){
                f.find('[name=' + field + ']').parents(".form-group").addClass("has-error");
                err += code + "<br />";
            });
            return err;
        },
        /**
         * 远程加载modal，采用一个loading modal作为过渡，等待反馈
         * @param name
         * @param afterLoad
         */
        loadModal: function (name, modalInit, afterLoad, args) {
            if (modalsLoaded[name]) {
                modalsLoaded[name].modal("show");
                if ($.isFunction(afterLoad)) {
                    afterLoad.call(modalsLoaded[name], args);
                }
            } else {
                $("#loading-modal").modal("show");
                $.ajax(config.loadModalUrl + name, {
                    dataType: "html",
                    async: false,
                    success: function (html) {
                        var m = $(html);
                        $(document.body).append(m);
                        if ($.isFunction(modalInit)) {
                            modalInit.call(m);
                        }
                        m.modal("show");
                        modalsLoaded[name] = m;
                        if ($.isFunction(afterLoad)) {
                            afterLoad.call(m, args);
                        }
                    },
                    complete: function () {
                        $("#loading-modal").modal("hide");
                    }
                });
            }
        },
        getval: function (val, defaultVal) {
            if (val === undefined || val === null) {
                return defaultVal == undefined ? "" : defaultVal;
            } else {
                return val;
            }
        },
        leadingZero: function (num) {
            var str = num.toString();
            if (str.length < 2) {
                str = '0' + str;
            }
            return str;
        },
        /**
         * Convert timestamp to datetime format
         * @param ts
         * @param withoutSecond
         * @returns {string}
         */
        ts2dt: function (ts, withoutSecond) {
            var dt = new Date(ts);
            return dt.getFullYear() + '-' + this.leadingZero(dt.getMonth() + 1) +
                '-' + this.leadingZero(dt.getDate()) + ' '
                + this.leadingZero(dt.getHours()) + ':' + this.leadingZero(dt.getMinutes()) +
                (withoutSecond ? '' : ':' + this.leadingZero(dt.getSeconds()));
        },
        /**
         * Convert timestamp to date format
         * @param ts
         * @returns {string}
         */
        ts2d: function (ts) {
            var dt = new Date(ts);
            return dt.getFullYear() + '-' + this.leadingZero(dt.getMonth() + 1) +
                '-' + this.leadingZero(dt.getDate());
        },
        /**
         * 绘制扇形图
         * tag ----  图形需要显示的 id 标签
         * counts ---- 需要绘制扇形的数据 {"扇形区块的名称",[区块数据的大小]}
         * color ---- 扇形中需要填充的颜色数组
         * text ----  显示在图标下方的文字
         * bool ---- 是否显示图例
         * size ---- 显示图形的比例达大小
         * */
        "fanShaped" : function (tag, counts, color, text, bool, size){
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
    };

    sys.fn = fn;
    _win.sys = sys;

})(jQuery, window, document);
