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
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderConverter orderConverter;
    private final ProductConverter productConverter;

    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        Order order = orderConverter.requestDtoToEntity(orderRequestDto);

        order.setIsDeleted(false);
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        order.setCustomerName(username);
        order.setTotalPrice(calculateTotalPrice(order.getProducts()));

        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {}", order.getOrderId());
        return orderConverter.entityToResponseDto(savedOrder);
    }

    @CachePut(cacheNames = "order", key = "#orderId")
    public OrderResponseDto updateOrder(Long orderId, OrderRequestDto orderRequestDto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find order by supplied id: " + orderId));
        validateAccessToOrder(order);

        order.setStatus(OrderStatus.fromString(orderRequestDto.getOrderStatus()));
        List<Product> products = order.getProducts();
        products.clear();
        orderRequestDto.getProducts().stream()
                .map(productConverter::requestDtoToEntity)
                .forEach(products::add);
        order.setTotalPrice(calculateTotalPrice(order.getProducts()));

        Order updatedOrder = orderRepository.save(order);
        log.info("Order updated with ID: {}", orderId);
        return orderConverter.entityToResponseDto(updatedOrder);
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
            predicates.add(criteriaBuilder.isFalse(root.get("isDeleted")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });

        return appropriateOrders.stream()
                .map(orderConverter::entityToResponseDto)
                .toList();
    }

    @Cacheable(cacheNames = "order", key = "#orderId")
    public OrderResponseDto getOrderById(Long orderId) {
        Order order = orderRepository.findByOrderIdAndIsDeletedFalse(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find order by id: " + orderId));
        validateAccessToOrder(order);
        return orderConverter.entityToResponseDto(order);
    }

    @CacheEvict(cacheNames = "order", key = "#orderId")
    public void softDeleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find order by id: " + orderId));
        order.setIsDeleted(true);
        orderRepository.save(order);
        log.info("Order soft-deleted with ID: {}", orderId);
    }

    private double calculateTotalPrice(List<Product> products) {
        return products.stream()
                .mapToDouble(product -> product.getPrice() * product.getQuantity())
                .sum();
    }

    private void validateAccessToOrder(Order order){
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        if (!order.getCustomerName().equals(username)) {
            throw new IllegalArgumentException("Could not get access to order with id: " + order.getOrderId());
        }
    }
}