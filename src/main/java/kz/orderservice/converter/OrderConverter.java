package kz.orderservice.converter;

import kz.orderservice.dto.order.OrderRequestDto;
import kz.orderservice.dto.order.OrderResponseDto;
import kz.orderservice.entity.Order;
import kz.orderservice.entity.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderConverter {
    private final ProductConverter productConverter;

    public Order requestDtoToEntity(OrderRequestDto orderRequestDto) {
        Order order = new Order();
        order.setStatus(OrderStatus.fromString(orderRequestDto.getOrderStatus()));
        order.setCustomerName(orderRequestDto.getCustomerName());
        order.setProducts(
                orderRequestDto.getProducts().stream()
                        .map(productConverter::requestDtoToEntity).toList());
        return order;
    }

    public OrderResponseDto entityToResponseDto(Order order) {
        OrderResponseDto orderResponseDto = new OrderResponseDto();
        orderResponseDto.setOrderId(order.getOrderId());
        orderResponseDto.setCustomerName(order.getCustomerName());
        orderResponseDto.setStatus(order.getStatus());
        orderResponseDto.setTotalPrice(order.getTotalPrice());
        orderResponseDto.setProducts(
                order.getProducts().stream()
                        .map(productConverter::entityToResponseDto).toList());
        orderResponseDto.setIsDeleted(order.getIsDeleted());
        return orderResponseDto;
    }
}
