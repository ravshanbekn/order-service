package kz.orderservice.service;

import kz.orderservice.converter.OrderConverter;
import kz.orderservice.dto.order.OrderRequestDto;
import kz.orderservice.dto.order.OrderResponseDto;
import kz.orderservice.entity.Order;
import kz.orderservice.entity.OrderStatus;
import kz.orderservice.entity.Product;
import kz.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderConverter orderConverter;

    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        Order order = orderConverter.requestDtoToEntity(orderRequestDto);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(calculateTotalPrice(order.getProducts()));
        order.setIsDeleted(false);
        Order savedOrder = orderRepository.save(order);
        return orderConverter.entityToResponseDto(savedOrder);
    }


    private double calculateTotalPrice(List<Product> products) {
        return products.stream()
                .mapToDouble(product -> product.getPrice() * product.getQuantity())
                .sum();
    }
}
