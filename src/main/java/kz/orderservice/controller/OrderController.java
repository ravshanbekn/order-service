package kz.orderservice.controller;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import kz.orderservice.dto.order.OrderRequestDto;
import kz.orderservice.dto.order.OrderResponseDto;
import kz.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody @Valid @NotNull OrderRequestDto orderRequestDto){
        OrderResponseDto order = orderService.createOrder(orderRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}
