 //用户表控制层 
app.controller('cartController' ,function($scope,$controller,cartService,addressService){
	
	$controller('baseController',{$scope:$scope});//继承
	
	$scope.findCartList = function () {
		cartService.findCartList().success(
			function (response) {
				$scope.cartList = response;
				$scope.totalValue = cartService.sum($scope.cartList);
            }
		)
    }
    
    $scope.addGoodsToCartList = function (itemId,num) {
		cartService.addGoodsToCartList(itemId,num).success(
			function (response) {
				if(response.success){
					$scope.findCartList();
				}else{
                    alert(response.messages);
				}
            }
		)
    }

    $scope.findListByUserId = function () {
        addressService.findListByUserId().success(
            function (response) {
                $scope.addressList = response;
                //选择默认地址
                for(var i=0;i<$scope.addressList.length;i++){
                    if($scope.addressList[i].isDefault=='1'){
                        $scope.address = $scope.addressList[i];
                        // alert($scope.address.address);
                        break;
                    }
                }
            }
        )
    }

    $scope.selectAddress = function (addr) {

        $scope.address =addr;
    }
    $scope.isSelectedAddress = function (addr) {
        if(addr == $scope.address){
            return true;
        }else {
            return false;
        }
    }

    /**
     *
     */
    $scope.order = {paymentType:'1'}
    $scope.selectPayType = function (type) {
        $scope.order.paymentType = type;
    }
    
    $scope.submitOrder = function () {

        //补齐order的数据
        $scope.order.receiverAreaName=$scope.address.address;//地址
        $scope.order.receiverMobile=$scope.address.mobile;//手机
        $scope.order.receiver=$scope.address.contact;//联系人

        cartService.submitOrder($scope.order).success(
            function (response) {
                if(response.success){
                    alert("下单成功");
                    location.href = "/pay.html";
                }
            }
        )
    }
    
});	