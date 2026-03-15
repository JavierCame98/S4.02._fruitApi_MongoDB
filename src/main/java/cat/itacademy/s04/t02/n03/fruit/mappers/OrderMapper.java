package cat.itacademy.s04.t02.n03.fruit.mappers;

import cat.itacademy.s04.t02.n03.fruit.model.Order;
import cat.itacademy.s04.t02.n03.fruit.model.OrderItem;
import cat.itacademy.s04.t02.n03.fruit.model.OrderRequestDto;
import cat.itacademy.s04.t02.n03.fruit.model.OrderResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public Order toEntity (OrderRequestDto orderRequestDto){

        List<OrderItem> items = orderRequestDto.items().stream()
                .map(itemsDto -> new OrderItem (
                        itemsDto.fruitName(),
                        itemsDto.quantityInKilos()))
                .toList();

        return Order.builder()
                .clientName(orderRequestDto.clientName())
                .deliveryDate(orderRequestDto.deliveryDate())
                .items(items)
                .build();
    }

    public OrderResponseDto toResponseDto (Order order){
        return new OrderResponseDto(
                order.getId(),
                order.getClientName(),
                order.getDeliveryDate(),
                order.getItems()
        );
    }
}
