package kz.orderservice.dto.order;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import kz.orderservice.dto.product.ProductRequestDto;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {
    @NotNull
    private String orderStatus;

    @NotEmpty
    private List<ProductRequestDto> products;
}