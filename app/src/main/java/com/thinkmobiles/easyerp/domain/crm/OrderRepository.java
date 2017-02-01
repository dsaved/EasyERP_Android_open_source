package com.thinkmobiles.easyerp.domain.crm;

import com.thinkmobiles.easyerp.data.api.Rest;
import com.thinkmobiles.easyerp.data.model.crm.orders.detail.ResponseGerOrderDetails;
import com.thinkmobiles.easyerp.data.model.crm.orders.ResponseGetOrders;
import com.thinkmobiles.easyerp.data.model.user.organization.ResponseGetOrganizationSettings;
import com.thinkmobiles.easyerp.data.services.OrderService;
import com.thinkmobiles.easyerp.data.services.UserService;
import com.thinkmobiles.easyerp.presentation.base.NetworkRepository;
import com.thinkmobiles.easyerp.presentation.screens.crm.orders.OrdersContract;
import com.thinkmobiles.easyerp.presentation.screens.crm.orders.details.OrderDetailsContract;

import org.androidannotations.annotations.EBean;

import rx.Observable;

/**
 * Created by Lynx on 1/16/2017.
 */

@EBean(scope = EBean.Scope.Singleton)
public class OrderRepository extends NetworkRepository implements OrdersContract.OrdersModel, OrderDetailsContract.OrderDetailsModel {

    private OrderService orderService;
    private UserService userService;

    public OrderRepository() {
        orderService = Rest.getInstance().getOrderService();
        userService = Rest.getInstance().getUserService();
    }

    @Override
    public Observable<ResponseGetOrders> getOrders() {
        return getNetworkObservable(orderService.getOrder());
    }

    @Override
    public Observable<ResponseGerOrderDetails> getOrderDetails(String orderId) {
        return getNetworkObservable(orderService.getOrderDetails(orderId));
    }

    @Override
    public Observable<ResponseGetOrganizationSettings> getOrganizationSettings() {
        return getNetworkObservable(userService.getOrganizationSettings());
    }
}