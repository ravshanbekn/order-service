package kz.orderservice.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import kz.orderservice.dto.ErrorResponseDto;
import kz.orderservice.dto.order.OrderRequestDto;
import kz.orderservice.dto.order.OrderResponseDto;
import kz.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @Operation(
            summary = "Create a new order",
            description = "Creates a new order based on the provided data",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Order created",
                            content = @Content(
                                    schema = @Schema(implementation = OrderResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid data",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponseDto.class)))
            }
    )
    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@RequestBody @Valid OrderRequestDto orderRequestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(orderRequestDto));
    }

    @Operation(
            summary = "Update an existing order by ID",
            description = "Updates the details of an existing order identified by the provided order ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order updated successfully",
                            content = @Content(
                                    schema = @Schema(implementation = OrderResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid data",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponseDto.class)))
            }
    )
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Long orderId,
                                                        @RequestBody @Valid OrderRequestDto orderRequestDto) {
        return ResponseEntity
                .ok(orderService.updateOrder(orderId, orderRequestDto));
    }

    @Operation(
            summary = "Get orders with optional filters",
            description = "Fetches a list of orders, optionally filtered by order status and price range",
            parameters = {
                    @Parameter(name = "status", description = "Filter orders by status", example = "PENDING"),
                    @Parameter(name = "minPrice", description = "Filter orders by minimum price", example = "100.0"),
                    @Parameter(name = "maxPrice", description = "Filter orders by maximum price", example = "5000.0")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of orders fetched successfully",
                            content = @Content(
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = OrderResponseDto.class)))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponseDto.class)))
            }
    )
    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getOrders(@RequestParam(required = false) String status,
                                                            @RequestParam(required = false) Double minPrice,
                                                            @RequestParam(required = false) Double maxPrice) {
        return ResponseEntity
                .ok(orderService.getOrdersWithFilters(status, minPrice, maxPrice));
    }

    @Operation(
            summary = "Get order by ID",
            description = "Fetches an order by ID",
            parameters = {
                    @Parameter(name = "orderId", description = "The unique identifier of the order", example = "1", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order fetched successfully",
                            content = @Content(
                                    schema = @Schema(implementation = OrderResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Order not found",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponseDto.class)))
            }
    )
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponseDto> getOrders(@PathVariable Long orderId) {
        return ResponseEntity
                .ok(orderService.getOrderById(orderId));
    }

    @Operation(
            summary = "Soft delete an order by ID",
            description = "Marks an order as deleted (soft delete) by its unique ID without actually removing it from the database",
            parameters = {
                    @Parameter(name = "orderId", description = "The unique identifier of the order to be deleted", example = "1", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Order successfully marked as deleted"),
                    @ApiResponse(responseCode = "404", description = "Order not found",
                            content = @Content(
                                    schema = @Schema(implementation = ErrorResponseDto.class)))
            }
    )
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> softDeleteOrder(@PathVariable Long orderId) {
        orderService.softDeleteOrder(orderId);
        return ResponseEntity
                .noContent()
                .build();
    }
}