var test = {
    hello: function () {
        alert(1);
    },
    world: function () {
        
    }
};

(
    function(jQuery){
        var $ = jQuery;

        $.download = function(url, data, method){    // 获得url和data
            if( url && data ){
                // data 是 string 或者 array/object
                data = typeof data == 'string' ? data : jQuery.param(data);        // 把参数组装成 form的  input
                var inputs = '';
                jQuery.each(data.split('&'), function(){
                    var pair = this.split('=');
                    inputs+="<input type='hidden' name='"+ pair[0] +"' value='"+ pair[1] +"' />";
                });        // request发送请求
                jQuery("<form action='"+ url +"' method='"+ (method||"post") +"'>"+inputs+"</form>")
                .appendTo("body").submit().remove();
            };
        };

        $.windowdownload = function(url){
            window.open (url, "下载文件", "height=100, width=400, top=100, left=400, toolbar =no, menubar=no, scrollbars=no, resizable=no, location=no, status=no");//写成一行
        };

        $.fn.waiting = function(params){
            var finalparams = $.extend({
                backgroundcolor:"#555",
                imgurl:"/images/wait.gif",
                clazz:"waiting",
                minheight: 30,
                css:{position: 'absolute'}
            }, params);
            var $waiting = $("<div class='waitingdiv'/>").css(finalparams.css);

            var $p = $(this).append($waiting);
            var pt = $p.position();

            if($p.css('position') != 'static') $waiting.css({top:0, left:0});
            else $waiting.css({top:pt.top, left:pt.left});

            if(finalparams.imgurl){
                var bgimg = ' url("'+finalparams.imgurl+'") no-repeat 50% 50% ';
                $waiting.showimg = {
                    hide:function(){
                        $waiting.css('background-image', 'none');
                    }
                };
            }
            $waiting.css('background', finalparams.backgroundcolor + (bgimg||''));
            if($p.height() <= finalparams.minheight){
                $p.css({"min-height":finalparams.minheight+"px", "background-color":"#fff"});
            }
            $waiting.height($p.outerHeight(true)).width($p.outerWidth(true));
            return $waiting;

        };

    }
)(jQuery);

/**
 * 获取昨日日期
 * @returns {Date}
 */
function GetYesterday()   {
    var   today=new   Date();
    var   yesterday_milliseconds=today.getTime()-1000*60*60*24;

    var   yesterday=new   Date();
    yesterday.setTime(yesterday_milliseconds);

    var strYear=yesterday.getFullYear();

    var strDay=yesterday.getDate();
    var strMonth=yesterday.getMonth()+1;

    if(strMonth<10)
    {
        strMonth="0"+strMonth;
    }
    var strYesterday=strYear+"-"+strMonth+"-"+strDay;
    return    yesterday;
}

/**
 * 获取当前月的第一天
 */
function getCurrentMonthFirst(){
    var date=new Date();
    date.setDate(1);
    return date;
}

/**
 * 获取当前月的最后一天
 */
function getCurrentMonthLast(){
    var date=new Date();
    var currentMonth=date.getMonth();
    var nextMonth=++currentMonth;
    var nextMonthFirstDay=new Date(date.getFullYear(),nextMonth,1);
    var oneDay=1000*60*60*24;
    return new Date(nextMonthFirstDay-oneDay);
}


function getNowFormatDate(day) {
    if (!day){
        day = new Date();
    }

    var Year = 0;
    var Month = 0;
    var Day = 0;
    var CurrentDate = "";
    //初始化时间 
    //Year= day.getYear();//有火狐下2008年显示108的bug 
    Year= day.getFullYear();//ie火狐下都可以 
    Month= day.getMonth()+1;
    Day = day.getDate();
    //Hour = day.getHours(); 
    // Minute = day.getMinutes(); 
    // Second = day.getSeconds(); 
    CurrentDate += Year + "-";
    if (Month >= 10 )
    {
        CurrentDate += Month + "-";
    }
    else
    {
        CurrentDate += "0" + Month + "-";
    }
    if (Day >= 10 )
    {
        CurrentDate += Day ;
    }
    else
    {
        CurrentDate += "0" + Day ;
    }
    return CurrentDate;
}

function showDialog(id,config,selected,savefunction){
    var url = null;
    if (typeof config == "string"){
        url = config;
    }else{
        var beforeparams = config.beforeparams;

        url = config.beforeurl;
        if (beforeparams){
            console.error(selected[0]);
            $.each(beforeparams,function(i,r){
                url += "&" + r["from"] + "=" + selected[0][r["from"]];
            });

        }
    }
    var $dialog = $("#" + id);
    var $pagewrapper = $(".modal-body",$dialog);
    $pagewrapper.html("");
    var $div = $("<div class='row'/>").appendTo($pagewrapper);
    //将对话框数据压入堆栈
    dialogstack.push({data : selected,dialog : $dialog,config:config});
    $.ajax({
        type: 'GET',
        url: CONTEXTPATH + url,
        success: function(ret){

            $div.html(ret);
            $("button[name='buttonsave']",$dialog).unbind("click").click(function(){
                savefunction($dialog);
            });
        }
    });
    $dialog.modal({});
    return $dialog;
}




function getformdata($form){
    var ret = {};
    $("select",$form).each(function(i,r){
        ret[r.name || r.id] = $(r).val();
    });
    $("input",$form).each(function(i,r){
        ret[r.name || r.id] = $(r).val();
    });
    return ret;
}

function getLastMonthDay(){
    var days = [31,28,31,30,31,30,31,31,30,31,30,31];
    var categories = [];
    var month = new Date().getMonth() + 1;
    var date = new Date().getDate();
    var year = new Date().getFullYear();
    var last = new Date();
    //月末
    if (days[month - 1] == date){
        last.setDate(1);
    }else{
        if (month == 1){
            last.setMonth(11);
        }else{
            if (date + 1 > days[month - 2]){
                last.setDate(1);
            }else{
                last.setDate(date + 1);
                last.setMonth(month - 2);
            }
        }
    }
    return last;

}

$.initquery = function(inputs,dicts,$searchdiv,compid,datafun){
    for (var i = 0;i < inputs.length;i ++){
        var input = inputs[i];
        if (input.hidden){
            continue;
        }
        $searchdiv.append("&nbsp;");
        if (input.dict){

            var dict = dicts[input.dict];

            if (dict){

                var defautselect = dict.select;
                var allselect = "";
                if(defautselect == -1){
                    allselect = "selected='selected'";
                }

                var sel = "<select id='" + input.name + "'>";
                if(!(dict.hideall && (dict.hideall === true||dict.hideall ==="true"))){
                    sel += "<option value='' "+allselect+">全部</option>"
                }
                var values = dict;

                for (var j = 0;j < values.length;j ++){
                    var selected = "";
                    if (j == defautselect){
                        selected = "selected='selected'";
                    }else{
                        selected = "";
                    }
                    sel += "<option value='" + values[j].k + "'" + selected + ">" + values[j].v + "</option>";
                }
                sel += "</select>";
                var $sel = $(sel);
                $searchdiv.append($sel);
            }
        }else if (input.cond == 'b' || input.cond == 'bt'){
            if (input.type == 'D' || input.type == 'T'){
                var $datediv = $("<div style='display: inline-block; width: auto;' id='datepicker'  class='input-daterange input-group'></div>");
                $datediv.append("<input type='text' id='" + input.name + "b' placeholder='" + input.desc + "－起'>");
                $datediv.append("&nbsp;-&nbsp;");
                $datediv.append("<input type='text' id='" + input.name + "e' placeholder='" + input.desc + "－止'>");
                $searchdiv.append($datediv);
                var mindate = getNowFormatDate(getLastMonthDay());

                if(input.type== 'D'){
                    $("#" + input.name + "b").val(mindate);
                    $("#" + input.name + "e").val(getNowFormatDate());

                    $("#datepicker").datepicker({
                        format: "yyyy-mm-dd",
                        todayBtn: true,
                        language: "zh-CN",
                        orientation: "auto",
                        todayHighlight: true,
                        toggleActive: true
                    });
                } else if(input.type == 'T'){
                    $("#datepicker input").datetimepicker({
                        lang:'ch',
                        format:'Y-m-d H:m:s'
                    });
                }

            }else{
                $searchdiv.append("<input type='text' id='" + input.name + "b' placeholder='" + input.desc + "－起'>");
                $searchdiv.append("&nbsp;-&nbsp;");
                $searchdiv.append("<input type='text' id='" + input.name + "e' placeholder='" + input.desc + "－止'>");
            }
        }else{
            $searchdiv.append("<input type='text' id='" + input.name + "' placeholder='" + input.desc +"'>");
        }

    }

    var $button = $("<input type='button' value='查询'>");
    $searchdiv.append($button);

    $button.click(function(){
        if (!window.compparams){
            window.comparams = {};
        }
        if (!datafun || typeof datafun !== "function"){
            datafun = getformdata;
        }
        window.comparams[compid] = datafun($searchdiv);
        refreshcomp(compid);
    });


    $button = $("<input type='button' value='导出'>");
    $button.click(function(){
        window.location = CONTEXTPATH + "/layout/export/" + compid + ".rp";
    });
    $searchdiv.append($button);
}

var refreshcomp = function(compid){
    var $searchdiv = $("#shieldui-grid1_" + compid);
    var $waiting = $searchdiv.waiting({
        imgurl : "/images/wait.gif"
    });
    $.ajax({
        type : 'GET',
        url : CONTEXTPATH + "/layout/component.rp?c_simple=1&compid=" + compid,
        data : window.comparams[compid],
        success : function(ret) {
            $searchdiv.html(ret);
            $waiting.remove();
        }
    });
}

$.getSelect = function(params){
    var defaultparams = {};
    params = $.extend(defaultparams,params);
    if (!params.data){
        $.ajax({
            type : "GET",
            url : CONTEXTPATH + "/report/getlist.rp?c_ignorepage=1&servicename=" + params.servicename,
            success : function(ret){
                var $appendTo = $("#" + params.appendto);
                var $sel = $("<select></select>").appendTo($appendTo).before(params.title + "：");
                $sel.attr("name",params.name);
                var data = ret.data;
                var $last = null;
                for (var i = 0;i < data.length;i ++){
                    var row = data[i];
                    $last = $("<option value='" + row[params.key] + "'>" + row[params.value] + "</option>").appendTo($sel);
                }
            },
            dataType : "json"

        });
    }

}

function getPannelBody(compid){
    return $("#panel-body_" + compid);
}

function deleterow(compid,row,selecteditems){
    var $waiting = $("#comp_" + compid).parent().waiting({
        imgurl : "/images/wait.gif"
    });
    var servicename = row.servicename;
    var id = selecteditems[0].autoid;
    $.ajax({
        type: 'POST',
        url: CONTEXTPATH + "/report/execute.rp?servicename=" + servicename + "&autoid=" + id,
        success: function(ret){
            $waiting.remove();
            refreshcomp(compid)
        }
    });
}


function getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
    var r = window.location.search.substr(1).match(reg);
    if (r != null)
        return unescape(r[2]);
    return null;
}


function doOperation(compid,name,operations,selecteditems){
    for (var i = 0;i < operations.length;i ++){
        var row = operations[i];
        if (row.servicename == name){
            var fun = row["function"];

            if (typeof window[fun] == 'function'){
                window[fun](compid,row,selecteditems);
            }
            break;
        }
    }

}

function editrow(servicename,id,compid){

    showDialog("myModal","/system/addrole.html",null,function($dialog){
        var $waiting = $('.modal-content',$dialog).waiting({
            imgurl : "/images/wait.gif"
        });
        var data = getformdata($dialog);
        data.autoid = id;
        $.ajax({
            type : 'POST',
            url : CONTEXTPATH
            + "/report/execute.rp?servicename=editrole",
            data : data,
            success : function(ret) {
                if (ret.code != 0 && ret.code != '0'){
                    alert(ret.msg);
                }else{
                    $("button[name='close']",$dialog).click();
                }
                $waiting.remove();
                refreshcomp(compid);
            },
            dataType : "json"
        });

    });
    $.ajax({
        type : "GET",
        data : {
            autoid : id
        },
        url : CONTEXTPATH+ "/report/getlist.rp?servicename=queryrole",
        success : function(ret){
            if (ret && ret.data && ret.data.length > 0){
                var row = ret.data[0];
                $("input",$("#myModal")).each(function(i,r){
                    $(r).val(row[r.name]);
                });
            }
        },
        dataType : "json"
    });
}



function addrow(compid,config,selected){
    batchoperation(compid,config,[{}]);
}


function batchoperation(compid,config,selected){
    var servicename = config.servicename;
    var selectedarr = [];
    var selectedkeys = [];
    if (selected){
        for (var k in selected){
            var row = selected[k];
            var row_ops = row.row_ops;
            if (row_ops == null){
                selectedarr.push(selected[k]);
                continue;
            }
            //过滤掉不能操作的数据
            if (row_ops && $.inArray(servicename,row_ops) >= 0){
                selectedarr.push(selected[k]);
                selectedkeys.push(k);
            }

        }
    }

    if (selectedarr.length == 0){
        alert("请至少选中一条有效记录");
        return;
    }

    function savefunction($dialog){

        var url = config.url || "/report/execute.rp";

        var data = {
            servicename : config.servicename,
            c_servicename : config.servicename

        };

        if ($dialog){
            var fun = $dialog.getData;
            var formdata = fun();
            if (typeof formdata == "string"){
                data.data = formdata;
            }else{
                data  = $.extend(data,fun());
            }
        }else{
            data.items = JSON.stringify(selectedarr);
        }

        if (!$dialog){
            $dialog = getPannelBody(compid);
        }
        var $waiting = $dialog.waiting({
            imgurl : CONTEXTPATH + "/images/wait.gif"
        });

        $.ajax({
            type : 'POST',
            url : CONTEXTPATH + url,
            data : data,
            success : function(ret) {
                if (ret.code != 0 && ret.code != '0'){
                    alert(ret.msg);
                }else{
                    for (var i = 0;i < selectedkeys.length;i ++){
                        delete selected[selectedkeys[i]];
                    }
                    refreshcomp(compid);
                    $("button[name='close']",$dialog).click();
                }
                $waiting.remove();
            },
            dataType : "json"
        });
    }

    if (config.beforeurl){
        var modalid = config.dialogid || "myModal";
        showDialog(modalid,config,selectedarr,savefunction);
    }else{
        savefunction();
    }

}


function loadComponent(params){
    if (!params){
        return;
    }
    var $pagewrapper = $(".modal-body",$("#myModal"));
    $pagewrapper.html("");
    var $div = $("<div class='row'/>").appendTo($pagewrapper);
    $.ajax({
        type: 'GET',
        url: CONTEXTPATH + "/system/rolemenu.html?servicename=",
        success: function(ret){

            $div.html(ret);
        }
    });

    $('#myModal').modal({});

}

var dialogstack = [];

var RpDialog = {
    register : function(init,getData){
        var dialogparams = dialogstack.pop()
        var selected = null;
        var $dialog = null;
        if (dialogparams){
            $dialog = dialogparams.dialog;
            selected = dialogparams.data;
            $dialog.getData = function(){
                return function(){
                    return getData(selected,$dialog,dialogparams);
                }
            }();

        }
        init(selected,$dialog,dialogparams);
    }
};
$.getJsonValue = function(obj,path){
    if (!path){
        return "";
    }
    if (!obj){
        return "";
    }
    var patharr = path.split(".");
    for (var i = 0;i < patharr.length;i ++){
        var temp = patharr[i];
        obj = obj[temp];
    }
    return obj;
}



