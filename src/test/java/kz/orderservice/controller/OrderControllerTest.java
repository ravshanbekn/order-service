package kz.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.orderservice.dto.order.OrderRequestDto;
import kz.orderservice.dto.order.OrderResponseDto;
import kz.orderservice.dto.product.ProductRequestDto;
import kz.orderservice.dto.product.ProductResponseDto;
import kz.orderservice.entity.OrderStatus;
import kz.orderservice.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {
    @InjectMocks
    private OrderController orderController;
    @Mock
    private OrderService orderService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private OrderRequestDto orderRequestDto;
    private OrderResponseDto orderResponseDto;
    private ProductResponseDto productResponseDto;
    private final Long orderId = 10L;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper();

        long productId = 1L;
        String productName = "Product Name";
        double productPrice = 1500.0;
        int productQuantity = 2;
        String orderStatus = "PENDING";
        double orderTotalPrice = productPrice * productQuantity;

        ProductRequestDto productRequestDto = ProductRequestDto.builder()
                .name(productName)
                .price(productPrice)
                .quantity(productQuantity)
                .build();
        orderRequestDto = OrderRequestDto.builder()
                .orderStatus(orderStatus)
                .products(List.of(productRequestDto))
                .build();
        productResponseDto = ProductResponseDto.builder()
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
    }

    @Test
    @DisplayName("Testing createOrder controller for successful execution")
    void shouldCreateOrderSuccessfully() throws Exception {
        when(orderService.createOrder(orderRequestDto)).thenReturn(orderResponseDto);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderId").value(orderResponseDto.getOrderId()))
                .andExpect(jsonPath("$.totalPrice").value(orderResponseDto.getTotalPrice()))
                .andExpect(jsonPath("$.status").value(orderResponseDto.getStatus().toString()))
                .andExpect(jsonPath("$.products[0].productId").value(productResponseDto.getProductId()))
                .andExpect(jsonPath("$.products[0].name").value(productResponseDto.getName()))
                .andExpect(jsonPath("$.products[0].price").value(productResponseDto.getPrice()))
                .andExpect(jsonPath("$.products[0].quantity").value(productResponseDto.getQuantity()));

        verify(orderService, times(1)).createOrder(orderRequestDto);
    }

    @Test
    @DisplayName("Testing createOrder controller for bad request when arguments are missing")
    void shouldReturnBadRequestForInvalidData() throws Exception {
        OrderRequestDto invalidOrderRequestDto = new OrderRequestDto();

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidOrderRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Testing updateOrder controller for successful execution")
    void shouldUpdateOrderSuccessfully() throws Exception {
        when(orderService.updateOrder(orderId, orderRequestDto)).thenReturn(orderResponseDto);

        mockMvc.perform(put("/orders/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderId").value(orderResponseDto.getOrderId()))
                .andExpect(jsonPath("$.totalPrice").value(orderResponseDto.getTotalPrice()))
                .andExpect(jsonPath("$.status").value(orderResponseDto.getStatus().toString()))
                .andExpect(jsonPath("$.products[0].productId").value(productResponseDto.getProductId()))
                .andExpect(jsonPath("$.products[0].name").value(productResponseDto.getName()))
                .andExpect(jsonPath("$.products[0].price").value(productResponseDto.getPrice()))
                .andExpect(jsonPath("$.products[0].quantity").value(productResponseDto.getQuantity()));

        verify(orderService, times(1)).updateOrder(orderId, orderRequestDto);
    }

    @Test
    @DisplayName("Testing updateOrder controller for bad request when arguments are missing")
    void shouldReturnBadRequestForInvalidDataOnUpdate() throws Exception {
        OrderRequestDto invalidOrderRequestDto = new OrderRequestDto();

        mockMvc.perform(put("/orders/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidOrderRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Testing getOrders controller for successful execution with filters")
    void shouldGetOrdersWithFiltersSuccessfully() throws Exception {
        String orderStatus = "PENDING";
        double minPrice = 100.0;
        double maxPrice = 5000.0;
        when(orderService.getOrdersWithFilters(orderStatus, minPrice, maxPrice))
                .thenReturn(List.of(orderResponseDto));

        mockMvc.perform(get("/orders")
                        .param("status", orderStatus)
                        .param("minPrice", String.valueOf(minPrice))
                        .param("maxPrice", String.valueOf(maxPrice)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].orderId").value(orderResponseDto.getOrderId()))
                .andExpect(jsonPath("$[0].totalPrice").value(orderResponseDto.getTotalPrice()))
                .andExpect(jsonPath("$[0].status").value(orderResponseDto.getStatus().toString()))
                .andExpect(jsonPath("$[0].products[0].productId").value(productResponseDto.getProductId()))
                .andExpect(jsonPath("$[0].products[0].name").value(productResponseDto.getName()))
                .andExpect(jsonPath("$[0].products[0].price").value(productResponseDto.getPrice()))
                .andExpect(jsonPath("$[0].products[0].quantity").value(productResponseDto.getQuantity()));

        verify(orderService, times(1)).getOrdersWithFilters(orderStatus, minPrice, maxPrice);
    }

    @Test
    @DisplayName("Testing getOrder by ID controller for successful execution")
    void shouldGetOrderByIdSuccessfully() throws Exception {
        when(orderService.getOrderById(orderId)).thenReturn(orderResponseDto);

        mockMvc.perform(get("/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.orderId").value(orderResponseDto.getOrderId()))
                .andExpect(jsonPath("$.totalPrice").value(orderResponseDto.getTotalPrice()))
                .andExpect(jsonPath("$.status").value(orderResponseDto.getStatus().toString()))
                .andExpect(jsonPath("$.products[0].productId").value(productResponseDto.getProductId()))
                .andExpect(jsonPath("$.products[0].name").value(productResponseDto.getName()))
                .andExpect(jsonPath("$.products[0].price").value(productResponseDto.getPrice()))
                .andExpect(jsonPath("$.products[0].quantity").value(productResponseDto.getQuantity()));

        verify(orderService, times(1)).getOrderById(orderId);
    }

    @Test
    @DisplayName("Testing softDeleteOrder controller for successful execution")
    void shouldSoftDeleteOrderSuccessfully() throws Exception {
        doNothing().when(orderService).softDeleteOrder(orderId);

        mockMvc.perform(delete("/orders/{orderId}", orderId))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).softDeleteOrder(orderId);
    }
}