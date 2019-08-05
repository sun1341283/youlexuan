 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,uploadService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(){
		var id = $location.search()['id'];
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
				//商品描述
                editor.html($scope.entity.goodsDesc.introduction);
				//商品图片
                $scope.entity.goodsDesc.itemImages = JSON.parse($scope.entity.goodsDesc.itemImages);
                //扩展属性
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse( $scope.entity.goodsDesc.customAttributeItems);
                //规格参数
				$scope.entity.goodsDesc.specificationItems = JSON.parse($scope.entity.goodsDesc.specificationItems);

				//items列表
				for(var i=0;i<$scope.entity.itemList.length;i++){
					$scope.entity.itemList[i].spec = JSON.parse($scope.entity.itemList[i].spec );

				}
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}

	$scope.entity={"goods":{},"goodsDesc":{'itemImages':[],'specificationItems':[]},"itemList":[]}
	$scope.add = function () {
		$scope.entity.goodsDesc.introduction = editor.html();
		goodsService.add($scope.entity).success(
			function (response) {
				if(response.success){
                    $scope.entity={};
					alert("添加商品成功");
				}else{
					alert(response.message);
				}
            }
		)
    }

    $scope.upload = function () {
        uploadService.uploadFile().success(
            function (response) {
                if(response.success){
                    $scope.entity_imag.url = response.message;
                }else{
                    alert(response.message);
                }
            }
        )
    }
    //将单个上传的图片保存到entity.goodsDesc.itemImages 中
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.entity_imag);
    }
    
    $scope.remRow = function ($index) {
        $scope.entity.goodsDesc.itemImages.splice($index,1)
    }

    //查找三级分类中的一级分类
	$scope.findItemCat1List  = function() {
		itemCatService.findByParentId(0).success(
			function (response) {
				$scope.itemCat1List = response;
            }
		)
    }

    //选择了一级分类，关联出二级分类
	$scope.$watch('entity.goods.category1Id',function (newValue,oldValue) {
		itemCatService.findByParentId(newValue).success(
			function (response) {
				$scope.itemCat2List = response;
            }
		)
    })

    //选择了二级分类，关联出三级分类
    $scope.$watch('entity.goods.category2Id',function (newValue,oldValue) {
        itemCatService.findByParentId(newValue).success(
            function (response) {
                $scope.itemCat3List = response;
            }
        )
    })
	//选择了三级分类、关联出模板信息
	$scope.$watch('entity.goods.category3Id',function (newValue,oldValue) {
		itemCatService.findOne(newValue).success(
			function (response) {
				$scope.entity.goods.typeTemplateId = response.typeId;
            }
		)
    })

	//模板ID做了改变，那么查询模板 对象，将模板对象中的 brandid、speids\custom_attribute_items 绑定到entity响应的位置上
	
	$scope.$watch('entity.goods.typeTemplateId',function (newValue,oldValue) {

		typeTemplateService.findOne(newValue).success(
            function (response) {
                $scope.typeTemplate = response;
                $scope.typeTemplate.brandIds = JSON.parse($scope.typeTemplate.brandIds);

                //回显时有id
                var id = $location.search()['id'];
                if(id == null){
                    $scope.entity.goodsDesc.customAttributeItems =  JSON.parse($scope.typeTemplate.customAttributeItems);
				}

            }
        )
        //根据模板ID得到带着规格项的规格列表
		typeTemplateService.findSpecList(newValue).success(
			function (response) {
				$scope.specList = response;
            }
		)
    })

    /**
	 * 根据选择的规格，加工specificationItems属性 $scope.goodsDesc.specificationItems
	 * [{"attributeName":"网络制式","attributeValue":["移动3G","移动4G"]},{"attributeName":"屏幕尺寸","attributeValue":["6寸","5寸"]}]
     */
	$scope.updateSpecAttribute = function ($event,name,value) {
		var obj = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,"attributeName",name);
		if(obj == null){
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":name,"attributeValue":[value]});
		}else{
			if($event.target.checked){
                obj.attributeValue.push(value);
			}else{
				var index = obj.attributeValue.indexOf(value);
                obj.attributeValue.splice(index,1);
                if(obj.attributeValue.length==0){
                	var attrIndex = $scope.entity.goodsDesc.specificationItems.indexOf(obj);
                    $scope.entity.goodsDesc.specificationItems.splice(attrIndex,1);
				}
			}
		}
    }

    /**
	 *1、初始化一个对象
	 *  items = [{"attributeName":"网络","attributeValue":["移动3G"]},{"attributeName":"机身内存","attributeValue":["16G","32G"]}]
	 *  entity.itemList = [{spce:{},price:0,num:9999,status:'0',isDefault:'0'}];
     */
    $scope.creatItemList = function () {
        $scope.entity.itemList=  [{spec:{},price:0,num:99999,status:'0',isDefault:'0'}];//初始
        var items = $scope.entity.goodsDesc.specificationItems;
        for(var i=0;i<items.length;i++){
            $scope.entity.itemList = addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
		}

    }
	addColumn = function (list,columnName,columnValues) {
    	var newList =[];
    	for(var i=0;i<list.length;i++){ //外层遍历itemList
    		var oldRow = list[i];
			for(var j=0;j<columnValues.length;j++){ //内存遍历attributeValue
				// var newRow = oldRow; //浅克隆
				var newRow = JSON.parse(JSON.stringify(oldRow)); //深克隆
                newRow.spec[columnName]=columnValues[j];
                newList.push(newRow);
			}
		}
		return newList;
		
    }


    //列表显示状态名称
    $scope.itemCat1List = [];
	$scope.auditStatusList = ['待审核','审核通过','审核驳回','关闭']
	$scope.findItemCatList = function () {
		itemCatService.findAll().success(
			function (response) {
			for(var i=0;i<response.length;i++){
				var itemCat = response[i];
				$scope.itemCat1List[itemCat.id] = itemCat.name;
			}
        })
    }

    //判断规格的复选框是否打钩
	$scope.checkAttribute = function (specName,optionName) {
		var specItem = $scope.entity.goodsDesc.specificationItems;
		var obj = $scope.searchObjectByKey(specItem,"attributeName",specName);
		if(obj == null){
			return false;
		}else{
			if (obj.attributeValue.indexOf(optionName)>=0){
				return true;
			}else {
				return false;
			}
		}

    }
    
    $scope.updateStatus = function (status) {
		goodsService.updateStatus($scope.selectIds,status).success(
			function (response) {
				alert(response.message);
				if(response.success){
                    $scope.reloadList();//刷新列表
                    $scope.selectIds=[];//清空ID集合
                }
            }
		)
    }

    
});	