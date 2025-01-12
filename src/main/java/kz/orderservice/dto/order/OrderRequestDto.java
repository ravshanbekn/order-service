package kz.orderservice.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kz.orderservice.dto.product.ProductRequestDto;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {

    @NotBlank(message = "Order Status should not be blank")
    private String orderStatus;

    @NotEmpty(message = "Products list should not be empty")
    @Valid
    private List<@NotNull(message = "Product cannot be null") ProductRequestDto> products;
}