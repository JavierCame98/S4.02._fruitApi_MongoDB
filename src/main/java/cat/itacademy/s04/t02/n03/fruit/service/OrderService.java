package cat.itacademy.s04.t02.n03.fruit.service;

import cat.itacademy.s04.t02.n03.fruit.model.OrderRequestDto;
import cat.itacademy.s04.t02.n03.fruit.model.OrderResponseDto;

import java.util.List;

public interface OrderService {
    OrderResponseDto create(OrderRequestDto orderRequestDto);
    OrderResponseDto update(String id, OrderRequestDto orderRequestDto);
    void delete(String id);
    List<OrderResponseDto> getAll();
    OrderResponseDto getById(String id);
}
