package cat.itacademy.s04.t02.n03.fruit.controller;

import cat.itacademy.s04.t02.n03.fruit.exceptions.ResourceNotFoundException;
import cat.itacademy.s04.t02.n03.fruit.model.OrderItem;
import cat.itacademy.s04.t02.n03.fruit.model.OrderRequestDto;
import cat.itacademy.s04.t02.n03.fruit.model.OrderResponseDto;
import cat.itacademy.s04.t02.n03.fruit.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ORDER_ID = "order-123";
    private static final String BASE_URL = "/orders";

    private OrderResponseDto responseDto;
    private OrderRequestDto validRequest;

    @BeforeEach
    void setUp() {
        OrderItem orderItem = new OrderItem("Apple", 5);

        responseDto = new OrderResponseDto(
                ORDER_ID,
                "John Doe",
                LocalDate.now().plusDays(1),
                List.of(orderItem)
        );

        validRequest = new OrderRequestDto(
                "John Doe",
                LocalDate.now().plusDays(1),
                List.of(new cat.itacademy.s04.t02.n03.fruit.model.OrderItemRequestDto("Apple", 5))
        );
    }

    // ─── POST /orders ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("POST /orders: should return 201 Created with response body")
    void create_ShouldReturn201_WhenValidRequest() throws Exception {
        when(orderService.create(any(OrderRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(ORDER_ID))
                .andExpect(jsonPath("$.clientName").value("John Doe"));
    }

    @Test
    @DisplayName("POST /orders: should return 400 when clientName is blank")
    void create_ShouldReturn400_WhenClientNameIsBlank() throws Exception {
        OrderRequestDto invalidRequest = new OrderRequestDto(
                "",
                LocalDate.now().plusDays(1),
                List.of(new cat.itacademy.s04.t02.n03.fruit.model.OrderItemRequestDto("Apple", 5))
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.clientName").exists());
    }

    @Test
    @DisplayName("POST /orders: should return 400 when items list is empty")
    void create_ShouldReturn400_WhenItemsIsEmpty() throws Exception {
        OrderRequestDto invalidRequest = new OrderRequestDto(
                "John Doe",
                LocalDate.now().plusDays(1),
                List.of()
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.items").exists());
    }

    @Test
    @DisplayName("POST /orders: should return 400 when deliveryDate is today")
    void create_ShouldReturn400_WhenDeliveryDateIsToday() throws Exception {
        OrderRequestDto invalidRequest = new OrderRequestDto(
                "John Doe",
                LocalDate.now(),
                List.of(new cat.itacademy.s04.t02.n03.fruit.model.OrderItemRequestDto("Apple", 5))
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.deliveryDate").exists());
    }

    @Test
    @DisplayName("POST /orders: should return 400 when deliveryDate is in the past")
    void create_ShouldReturn400_WhenDeliveryDateIsInThePast() throws Exception {
        OrderRequestDto invalidRequest = new OrderRequestDto(
                "John Doe",
                LocalDate.now().minusDays(1),
                List.of(new cat.itacademy.s04.t02.n03.fruit.model.OrderItemRequestDto("Apple", 5))
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.deliveryDate").exists());
    }

    @Test
    @DisplayName("POST /orders: should return 400 when quantityInKilos is zero")
    void create_ShouldReturn400_WhenQuantityIsZero() throws Exception {
        OrderRequestDto invalidRequest = new OrderRequestDto(
                "John Doe",
                LocalDate.now().plusDays(1),
                List.of(new cat.itacademy.s04.t02.n03.fruit.model.OrderItemRequestDto("Apple", 0))
        );

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    // ─── GET /orders ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /orders: should return 200 with list of orders")
    void getAll_ShouldReturn200_WithListOfOrders() throws Exception {
        when(orderService.getAll()).thenReturn(List.of(responseDto));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(ORDER_ID));
    }

    @Test
    @DisplayName("GET /orders: should return 200 with empty list when no orders")
    void getAll_ShouldReturn200_WithEmptyList() throws Exception {
        when(orderService.getAll()).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ─── GET /orders/{id} ────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /orders/{id}: should return 200 with order when found")
    void getById_ShouldReturn200_WhenOrderExists() throws Exception {
        when(orderService.getById(ORDER_ID)).thenReturn(responseDto);

        mockMvc.perform(get(BASE_URL + "/{id}", ORDER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ORDER_ID))
                .andExpect(jsonPath("$.clientName").value("John Doe"));
    }

    @Test
    @DisplayName("GET /orders/{id}: should return 404 when order not found")
    void getById_ShouldReturn404_WhenOrderNotFound() throws Exception {
        when(orderService.getById(ORDER_ID))
                .thenThrow(new ResourceNotFoundException("Order not found with id: " + ORDER_ID));

        mockMvc.perform(get(BASE_URL + "/{id}", ORDER_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found with id: " + ORDER_ID));
    }

    // ─── PUT /orders/{id} ────────────────────────────────────────────────────

    @Test
    @DisplayName("PUT /orders/{id}: should return 200 with updated order")
    void update_ShouldReturn200_WhenValidRequest() throws Exception {
        when(orderService.update(eq(ORDER_ID), any(OrderRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(put(BASE_URL + "/{id}", ORDER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ORDER_ID));
    }

    @Test
    @DisplayName("PUT /orders/{id}: should return 404 when order not found")
    void update_ShouldReturn404_WhenOrderNotFound() throws Exception {
        when(orderService.update(eq(ORDER_ID), any(OrderRequestDto.class)))
                .thenThrow(new ResourceNotFoundException("Order not found with id: " + ORDER_ID));

        mockMvc.perform(put(BASE_URL + "/{id}", ORDER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found with id: " + ORDER_ID));
    }

    @Test
    @DisplayName("PUT /orders/{id}: should return 400 when request body is invalid")
    void update_ShouldReturn400_WhenInvalidRequest() throws Exception {
        OrderRequestDto invalidRequest = new OrderRequestDto(
                "",
                LocalDate.now().plusDays(1),
                List.of(new cat.itacademy.s04.t02.n03.fruit.model.OrderItemRequestDto("Apple", 5))
        );

        mockMvc.perform(put(BASE_URL + "/{id}", ORDER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.clientName").exists());
    }

    // ─── DELETE /orders/{id} ─────────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /orders/{id}: should return 204 when order deleted")
    void delete_ShouldReturn204_WhenOrderExists() throws Exception {
        doNothing().when(orderService).delete(ORDER_ID);

        mockMvc.perform(delete(BASE_URL + "/{id}", ORDER_ID))
                .andExpect(status().isNoContent());

        verify(orderService).delete(ORDER_ID);
    }

    @Test
    @DisplayName("DELETE /orders/{id}: should return 404 when order not found")
    void delete_ShouldReturn404_WhenOrderNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Order not found with id: " + ORDER_ID))
                .when(orderService).delete(ORDER_ID);

        mockMvc.perform(delete(BASE_URL + "/{id}", ORDER_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found with id: " + ORDER_ID));
    }
}