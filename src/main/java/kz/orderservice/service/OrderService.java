package kz.orderservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
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

import java.util.ArrayList;
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

    public List<OrderResponseDto> getOrdersWithFilters(String status, Double minPrice, Double maxPrice) {
        List<Order> appropriateOrders = orderRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), OrderStatus.fromString(status)));
            }

            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("totalPrice"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("totalPrice"), maxPrice));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });

        return appropriateOrders.stream()
                .map(orderConverter::entityToResponseDto)
                .toList();
    }

    private double calculateTotalPrice(List<Product> products) {
        return products.stream()
                .mapToDouble(product -> product.getPrice() * product.getQuantity())
                .sum();
    }
}
