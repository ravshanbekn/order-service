package kz.orderservice.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kz.orderservice.dto.order.OrderRequestDto;
import kz.orderservice.dto.order.OrderResponseDto;
import kz.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody @Valid @NotNull OrderRequestDto orderRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(orderRequestDto));
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Long orderId,
                                                        @RequestBody @Valid @NotNull OrderRequestDto orderRequestDto) {
        return ResponseEntity
                .ok(orderService.updateOrder(orderId, orderRequestDto));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getOrders(@RequestParam(required = false) String status,
                                                            @RequestParam(required = false) Double minPrice,
                                                            @RequestParam(required = false) Double maxPrice) {
        return ResponseEntity
                .ok(orderService.getOrdersWithFilters(status, minPrice, maxPrice));
    }
}