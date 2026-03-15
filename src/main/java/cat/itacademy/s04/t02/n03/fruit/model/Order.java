package cat.itacademy.s04.t02.n03.fruit.model;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    private String clientName;

    private LocalDate deliveryDate;

    private List<OrderItem> items;

}
