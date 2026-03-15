package cat.itacademy.s04.t02.n03.fruit.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record OrderRequestDto(

        @NotBlank(message = "Client name is required")
        String clientName,

        @NotNull(message = "Delivery date is required")
        @Future(message = "Delivery date must be at least tomorrow")
        LocalDate deliveryDate,

        @NotEmpty(message = "Order must have at least one fruit")
        List<@Valid OrderItemRequestDto> items
) {}
