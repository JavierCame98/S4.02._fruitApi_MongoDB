package cat.itacademy.s04.t02.n03.fruit.model;

import java.time.LocalDate;
import java.util.List;

public record OrderResponseDto(
        String id,
        String clientName,
        LocalDate deliveryDate,
        List<OrderItem>items
) {
}
