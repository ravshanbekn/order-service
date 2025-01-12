package kz.orderservice.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kz.orderservice.dto.product.ProductRequestDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDto {

    @NotBlank(message = "Order Status should not be blank")
    @Schema(description = "Status of the order", example = "PENDING", allowableValues = {"PENDING", "CONFIRMED", "CANCELLED"})
    private String orderStatus;

    @NotEmpty(message = "Products list should not be empty")
    @Valid
    @Schema(description = "List of products in the order")
    private List<@NotNull(message = "Product cannot be null") ProductRequestDto> products;
}