package cat.itacademy.s04.t02.n03.fruit.mappers;


import cat.itacademy.s04.t02.n03.fruit.model.OrderItem;
import cat.itacademy.s04.t02.n03.fruit.model.OrderItemRequestDto;
import cat.itacademy.s04.t02.n03.fruit.model.OrderItemResponseDto;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {

    public OrderItem toEntity (OrderItemRequestDto orderItemRequestDto){
        return OrderItem.builder()
                .fruitName(orderItemRequestDto.fruitName())
                .quantityInKilos(orderItemRequestDto.quantityInKilos())
                .build();
    }

    public OrderItemResponseDto toResponeDto (OrderItem orderItem){
        return new OrderItemResponseDto(
                orderItem.getFruitName(),
                orderItem.getQuantityInKilos()
        );
    }


}
