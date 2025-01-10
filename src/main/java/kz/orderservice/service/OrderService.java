package kz.orderservice.service;

import jakarta.persistence.EntityNotFoundException;
import kz.orderservice.converter.OrderConverter;
import kz.orderservice.converter.ProductConverter;
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
    private final ProductConverter productConverter;

    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        Order order = orderConverter.requestDtoToEntity(orderRequestDto);
        order.setIsDeleted(false);
        order.setTotalPrice(calculateTotalPrice(order.getProducts()));
        return orderConverter.entityToResponseDto(orderRepository.save(order));
    }

    public OrderResponseDto updateOrder(Long orderId, OrderRequestDto orderRequestDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find order by supplied id: " + orderId));

        order.setStatus(OrderStatus.fromString(orderRequestDto.getOrderStatus()));
        List<Product> products = order.getProducts();
        products.clear();
        orderRequestDto.getProducts().stream()
                .map(productConverter::requestDtoToEntity)
                .forEach(products::add);
        order.setTotalPrice(calculateTotalPrice(order.getProducts()));
        return orderConverter.entityToResponseDto(orderRepository.save(order));
    }


    private double calculateTotalPrice(List<Product> products) {
        return products.stream()
                .mapToDouble(product -> product.getPrice() * product.getQuantity())
                .sum();
    }
}
