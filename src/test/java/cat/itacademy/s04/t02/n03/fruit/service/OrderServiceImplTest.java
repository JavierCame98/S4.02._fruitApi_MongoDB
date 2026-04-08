package cat.itacademy.s04.t02.n03.fruit.service;

import cat.itacademy.s04.t02.n03.fruit.exceptions.ResourceNotFoundException;
import cat.itacademy.s04.t02.n03.fruit.mappers.OrderMapper;
import cat.itacademy.s04.t02.n03.fruit.model.Order;
import cat.itacademy.s04.t02.n03.fruit.model.OrderItem;
import cat.itacademy.s04.t02.n03.fruit.model.OrderItemRequestDto;
import cat.itacademy.s04.t02.n03.fruit.model.OrderRequestDto;
import cat.itacademy.s04.t02.n03.fruit.model.OrderResponseDto;
import cat.itacademy.s04.t02.n03.fruit.respositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private static final String ORDER_ID = "order-123";

    private OrderItem orderItem;
    private Order order;
    private OrderRequestDto requestDto;
    private OrderResponseDto responseDto;

    @BeforeEach
    void setUp() {
        orderItem = new OrderItem("Apple", 5);

        order = Order.builder()
                .id(ORDER_ID)
                .clientName("John Doe")
                .deliveryDate(LocalDate.now().plusDays(1))
                .items(List.of(orderItem))
                .build();

        requestDto = new OrderRequestDto(
                "John Doe",
                LocalDate.now().plusDays(1),
                List.of(new OrderItemRequestDto("Apple", 5))
        );

        responseDto = new OrderResponseDto(ORDER_ID, "John Doe", LocalDate.now().plusDays(1), List.of(orderItem));
    }


    @Test
    @DisplayName("create: should save order and return response DTO")
    void create_ShouldReturnResponseDto() {
        when(orderMapper.toEntity(requestDto)).thenReturn(order);
        when(orderRepository.save(order)).thenReturn(order);
        when(orderMapper.toResponseDto(order)).thenReturn(responseDto);

        OrderResponseDto result = orderService.create(requestDto);

        assertThat(result).isEqualTo(responseDto);
        verify(orderRepository).save(order);
    }


    @Test
    @DisplayName("getAll: should return list of response DTOs")
    void getAll_ShouldReturnListOfDtos() {
        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderMapper.toResponseDto(order)).thenReturn(responseDto);

        List<OrderResponseDto> result = orderService.getAll();

        assertThat(result).hasSize(1).contains(responseDto);
    }

    @Test
    @DisplayName("getAll: should return empty list when no orders exist")
    void getAll_ShouldReturnEmptyList_WhenNoOrders() {
        when(orderRepository.findAll()).thenReturn(List.of());

        List<OrderResponseDto> result = orderService.getAll();

        assertThat(result).isEmpty();
        verifyNoInteractions(orderMapper);
    }


    @Test
    @DisplayName("getById: should return response DTO when order exists")
    void getById_ShouldReturnResponseDto_WhenOrderExists() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderMapper.toResponseDto(order)).thenReturn(responseDto);

        OrderResponseDto result = orderService.getById(ORDER_ID);

        assertThat(result).isEqualTo(responseDto);
    }

    @Test
    @DisplayName("getById: should throw ResourceNotFoundException when order not found")
    void getById_ShouldThrowResourceNotFoundException_WhenNotFound() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getById(ORDER_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(ORDER_ID);
    }


    @Test
    @DisplayName("update: should update order and return updated response DTO")
    void update_ShouldReturnUpdatedResponseDto_WhenOrderExists() {
        Order updatedOrder = Order.builder()
                .clientName("Jane Doe")
                .deliveryDate(LocalDate.now().plusDays(2))
                .items(List.of(new OrderItem("Banana", 3)))
                .build();

        OrderRequestDto updateRequest = new OrderRequestDto(
                "Jane Doe",
                LocalDate.now().plusDays(2),
                List.of(new OrderItemRequestDto("Banana", 3))
        );

        OrderResponseDto updatedResponse = new OrderResponseDto(
                ORDER_ID, "Jane Doe", LocalDate.now().plusDays(2),
                List.of(new OrderItem("Banana", 3))
        );

        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));
        when(orderMapper.toEntity(updateRequest)).thenReturn(updatedOrder);
        when(orderRepository.save(updatedOrder)).thenReturn(updatedOrder);
        when(orderMapper.toResponseDto(updatedOrder)).thenReturn(updatedResponse);

        OrderResponseDto result = orderService.update(ORDER_ID, updateRequest);

        assertThat(result.clientName()).isEqualTo("Jane Doe");
        assertThat(updatedOrder.getId()).isEqualTo(ORDER_ID); // id was preserved
        verify(orderRepository).save(updatedOrder);
    }

    @Test
    @DisplayName("update: should throw ResourceNotFoundException when order not found")
    void update_ShouldThrowResourceNotFoundException_WhenNotFound() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.update(ORDER_ID, requestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(ORDER_ID);

        verify(orderRepository, never()).save(any());
    }


    @Test
    @DisplayName("delete: should delete order when it exists")
    void delete_ShouldDeleteOrder_WhenOrderExists() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.of(order));

        orderService.delete(ORDER_ID);

        verify(orderRepository).delete(order);
    }

    @Test
    @DisplayName("delete: should throw ResourceNotFoundException when order not found")
    void delete_ShouldThrowResourceNotFoundException_WhenNotFound() {
        when(orderRepository.findById(ORDER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.delete(ORDER_ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(ORDER_ID);

        verify(orderRepository, never()).delete(any());
    }
}