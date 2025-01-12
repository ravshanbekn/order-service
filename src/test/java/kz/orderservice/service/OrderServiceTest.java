package kz.orderservice.service;

import jakarta.persistence.EntityNotFoundException;
import kz.orderservice.converter.OrderConverter;
import kz.orderservice.converter.ProductConverter;
import kz.orderservice.dto.order.OrderRequestDto;
import kz.orderservice.dto.order.OrderResponseDto;
import kz.orderservice.dto.product.ProductRequestDto;
import kz.orderservice.dto.product.ProductResponseDto;
import kz.orderservice.entity.Order;
import kz.orderservice.entity.OrderStatus;
import kz.orderservice.entity.Product;
import kz.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @InjectMocks
    private OrderService orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderConverter orderConverter;
    @Mock
    private ProductConverter productConverter;

    private Order order;
    private Product updatedProduct;
    private OrderRequestDto orderRequestDto;
    private OrderResponseDto orderResponseDto;
    private OrderRequestDto updatedOrderRequestDto;
    private ProductRequestDto productRequestDto;
    private SecurityContext securityContextMock;
    private Authentication authenticationMock;
    private UserDetails userDetailsMock;
    private final Long orderId = 1L;
    private final String username = "username";
    private Double orderTotalPrice;
    private Double updatedTotalPrice;
    private final String orderUpdatedStatus = "CONFIRMED";

    @BeforeEach
    void setUp() {
        long productId = 1L;
        String productName = "Product Name";
        double productPrice = 1500.0;
        int productQuantity = 2;
        orderTotalPrice = productPrice * productQuantity;

        productRequestDto = ProductRequestDto.builder()
                .name(productName)
                .price(productPrice)
                .quantity(productQuantity)
                .build();

        String orderStatus = "PENDING";
        orderRequestDto = OrderRequestDto.builder()
                .orderStatus(orderStatus)
                .products(List.of(productRequestDto))
                .build();

        ProductResponseDto productResponseDto = ProductResponseDto.builder()
                .productId(productId)
                .name(productName)
                .price(productPrice)
                .quantity(productQuantity)
                .build();

        orderResponseDto = OrderResponseDto.builder()
                .orderId(orderId)
                .status(OrderStatus.fromString(orderStatus))
                .totalPrice(orderTotalPrice)
                .products(List.of(productResponseDto))
                .build();

        Product product = Product.builder()
                .productId(productId)
                .name(productName)
                .price(productPrice)
                .quantity(productQuantity)
                .build();

        List<Product> products = new ArrayList<>();
        products.add(product);
        order = Order.builder()
                .orderId(orderId)
                .customerName(username)
                .status(OrderStatus.fromString(orderStatus))
                .products(products)
                .build();

        updatedProduct = Product.builder()
                .productId(productId)
                .name(productName)
                .price(2000.0)
                .quantity(1)
                .build();

        updatedTotalPrice = updatedProduct.getPrice() * updatedProduct.getQuantity();

        List<ProductRequestDto> newProductsRequestDtos = List.of(productRequestDto);
        updatedOrderRequestDto = OrderRequestDto.builder()
                .orderStatus(orderUpdatedStatus)
                .products(newProductsRequestDtos)
                .build();

        securityContextMock = Mockito.mock(SecurityContext.class);
        authenticationMock = Mockito.mock(Authentication.class);
        userDetailsMock = Mockito.mock(UserDetails.class);
    }

    @Test
    @DisplayName("Testing createOrder method for successful execution")
    void testCreateOrder() {
        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
            when(orderConverter.requestDtoToEntity(orderRequestDto)).thenReturn(order);
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);
            when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
            when(authenticationMock.getPrincipal()).thenReturn(userDetailsMock);
            when(userDetailsMock.getUsername()).thenReturn(username);
            when(orderRepository.save(order)).thenReturn(order);
            when(orderConverter.entityToResponseDto(order)).thenReturn(orderResponseDto);

            orderService.createOrder(orderRequestDto);

            securityContextHolderMock.verify(SecurityContextHolder::getContext);
            verify(orderConverter, times(1)).requestDtoToEntity(orderRequestDto);
            verify(securityContextMock, times(1)).getAuthentication();
            verify(authenticationMock, times(1)).getPrincipal();
            verify(userDetailsMock, times(1)).getUsername();
            verify(orderRepository, times(1)).save(order);
            verify(orderConverter, times(1)).entityToResponseDto(order);

            assertEquals(username, order.getCustomerName());
            assertEquals(orderTotalPrice, order.getTotalPrice());
            assertFalse(order.getIsDeleted());
        }
    }

    @Test
    @DisplayName("Testing updateOrder method for successful execution")
    void testUpdateOrder() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);
        when(orderConverter.entityToResponseDto(order)).thenReturn(orderResponseDto);
        when(productConverter.requestDtoToEntity(productRequestDto)).thenReturn(updatedProduct);

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);
            when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
            when(authenticationMock.getPrincipal()).thenReturn(userDetailsMock);
            when(userDetailsMock.getUsername()).thenReturn(username);

            orderService.updateOrder(orderId, updatedOrderRequestDto);

            verify(orderRepository, times(1)).findById(orderId);
            verify(orderRepository, times(1)).save(order);
            verify(orderConverter, times(1)).entityToResponseDto(order);

            assertEquals(orderUpdatedStatus, order.getStatus().toString());
            assertEquals(updatedTotalPrice, order.getTotalPrice());
        }
    }

    @Test
    @DisplayName("Testing updateOrder method when order is not found")
    void testUpdateOrderWhenOrderIsNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.updateOrder(orderId, updatedOrderRequestDto));

        verify(orderRepository, times(1)).findById(orderId);
    }

    @Test
    @DisplayName("Testing updateOrder method when order does not belong to requester")
    void testUpdateOrderWhenOrderDoesNotBelongToRequester() {
        String realOrderOwner = "realOwner";
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);
            when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
            when(authenticationMock.getPrincipal()).thenReturn(userDetailsMock);
            when(userDetailsMock.getUsername()).thenReturn(realOrderOwner);

            assertThrows(IllegalArgumentException.class, () -> orderService.updateOrder(orderId, updatedOrderRequestDto));

            verify(orderRepository, times(1)).findById(orderId);
        }
    }

    @Test
    @DisplayName("Testing getOrdersWithFilters with status, minPrice and maxPrice filters")
    void testGetOrdersWithFilters() {
        String status = "PENDING";
        Double minPrice = 1000.0;
        Double maxPrice = 5000.0;

        when(orderRepository.findAll(any(Specification.class))).thenReturn(List.of(order));
        when(orderConverter.entityToResponseDto(order)).thenReturn(orderResponseDto);

        List<OrderResponseDto> result = orderService.getOrdersWithFilters(status, minPrice, maxPrice);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(order -> order.getStatus().toString().equals(status)));
        assertTrue(result.stream().allMatch(order -> order.getTotalPrice() >= minPrice));
        assertTrue(result.stream().allMatch(order -> order.getTotalPrice() <= maxPrice));

        verify(orderRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    @DisplayName("Testing getOrderById method for successful execution")
    void testGetOrderById() {
        when(orderRepository.findByOrderIdAndIsDeletedFalse(orderId)).thenReturn(Optional.of(order));
        when(orderConverter.entityToResponseDto(order)).thenReturn(orderResponseDto);

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);
            when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
            when(authenticationMock.getPrincipal()).thenReturn(userDetailsMock);
            when(userDetailsMock.getUsername()).thenReturn(username);

            OrderResponseDto result = orderService.getOrderById(orderId);

            assertNotNull(result);
            assertEquals(orderResponseDto, result);
            verify(orderRepository, times(1)).findByOrderIdAndIsDeletedFalse(orderId);
            verify(orderConverter, times(1)).entityToResponseDto(order);
        }
    }

    @Test
    @DisplayName("Testing getOrderById method when order is not found")
    void testGetOrderByIdWhenOrderIsNotFound() {
        when(orderRepository.findByOrderIdAndIsDeletedFalse(orderId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.getOrderById(orderId));

        verify(orderRepository, times(1)).findByOrderIdAndIsDeletedFalse(orderId);
    }

    @Test
    @DisplayName("Testing getOrderById method when order does not belong to requester")
    void testGetOrderByIdWhenOrderDoesNotBelongToRequester() {
        String realOrderOwner = "realOwner";
        when(orderRepository.findByOrderIdAndIsDeletedFalse(orderId)).thenReturn(Optional.of(order));

        try (MockedStatic<SecurityContextHolder> securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class)) {
            securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContextMock);
            when(securityContextMock.getAuthentication()).thenReturn(authenticationMock);
            when(authenticationMock.getPrincipal()).thenReturn(userDetailsMock);
            when(userDetailsMock.getUsername()).thenReturn(realOrderOwner);

            assertThrows(IllegalArgumentException.class, () -> orderService.getOrderById(orderId));

            verify(orderRepository, times(1)).findByOrderIdAndIsDeletedFalse(orderId);
        }
    }

    @Test
    @DisplayName("Testing softDeleteOrder method for successful execution")
    void testSoftDeleteOrder() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.softDeleteOrder(orderId);

        verify(orderRepository, times(1)).findById(orderId);
        verify(orderRepository, times(1)).save(order);
        assertTrue(order.getIsDeleted());
    }

    @Test
    @DisplayName("Testing softDeleteOrder method when order is not found")
    void testSoftDeleteOrderWhenOrderIsNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.softDeleteOrder(orderId));

        verify(orderRepository, times(1)).findById(orderId);
    }
}