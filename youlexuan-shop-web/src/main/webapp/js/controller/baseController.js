app.controller('baseController',function ($scope) {
    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],
        onChange: function(){
            $scope.reloadList();//重新加载
        }
    };

    /*//重新加载列表 数据
    $scope.reloadList=function(){
        if (JSON.stringify($scope.searchEntity)==="{}"){
            //切换页码
            $scope.findPage( $scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
        }else {
            $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage)
        }
    };*/

    //重新加载列表 数据
    $scope.reloadList=function(){
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage)
    };

    $scope.selectIds=[];
    /*updateSelection($event,entity.id)*/
    $scope.updateSelection = function ($event,id) {
        if ($event.target.checked){
            $scope.selectIds.push(id);
        } else {
            var idx = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(idx,1);
        }
    };
    //提取json字符串数据中某个属性，返回拼接字符串 逗号分隔
    $scope.jsonToString=function(jsonString,key){
        var json=JSON.parse(jsonString);//将json字符串转换为json对象
        var value="";
        for(var i=0;i<json.length;i++){
            if(i>0){
                value+=","
            }
            value+=json[i][key];
        }
        return value;
    }

});