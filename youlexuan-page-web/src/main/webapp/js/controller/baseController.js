app.controller("baseController",function ($scope) {
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

    $scope.reloadList = function(){
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
    }

    $scope.searchEntity = {};

    //删除
    $scope.selectIds = [];
    $scope.updateSelection  = function ($event,id) {
        if($event.target.checked){
            $scope.selectIds.push(id);
        }else{
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index,1);
        }
    }

    // 传一个json串和一个key 得到value  	[{"id":4,"text":"小米"},{"id":6,"text":"360"}] text

    $scope.jsonToString = function (jsonString,key) {
        var jsonObj = JSON.parse(jsonString);
        var value = "";
        for(var i=0;i<jsonObj.length;i++){
            if(i>0){
                value +=",";
            }
            value += jsonObj[i][key];
        }
        return value;
    }

    $scope.searchObjectByKey = function (list,key,keyValue) {
        for(var i=0;i<list.length;i++){
            if(list[i][key]==keyValue){
                return list[i];
            }
        }
        return null;
    }


})