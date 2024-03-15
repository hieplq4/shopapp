package com.project.shopapp.services;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Order;
import com.project.shopapp.models.OrderStatus;
import com.project.shopapp.models.User;
import com.project.shopapp.repositories.OrderRepository;
import com.project.shopapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService{
    private final UserRepository userRepository;
    private  final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    @Override
    public Order createOrder(OrderDTO orderDTO) throws Exception {
    // tim xem user_id co ton tai hay khong
        User user = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException("cannot find user" +orderDTO.getUserId()));
        //convert orderDTO => Order
        //dung thu vien model mapper
        //tao mot luong bang anh xa rieng de kiem soat viec anh xa
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        //Cap nhat cac truong cua don hang tu orderId
        Order order = new Order();
        modelMapper.map(orderDTO,order);
        order.setUser(user);
        order.setOrderDate((new Date()));
        order.setStatus(OrderStatus.PENDING);
        //Kiem tra shipping date phai >= ngay hom nay
        LocalDate shippingDate = orderDTO.getShippingDate() == null
                ? LocalDate.now() : orderDTO.getShippingDate();
        if (shippingDate.isBefore(LocalDate.now())) {
            throw new DataNotFoundException("Date must be at least today !");
        }
        order.setShippingDate(shippingDate);
        order.setActive(true);
        orderRepository.save(order);
        return order;
    }

    @Override
    public Order getOrder(Long id) {

        return orderRepository.findById(id).orElse(null);

    }

    @Override
    public List<Order> findByUserId(Long userId) {

        return orderRepository.findByUserId(userId);
    }

    @Override
    public Order updateOrder(Long id, OrderDTO orderDTO)
            throws DataNotFoundException {
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new DataNotFoundException("Cannot find order with id: " + id));
        User existingUser = userRepository.findById(
                orderDTO.getUserId()).orElseThrow(() ->
                new DataNotFoundException("Cannot find user with id: " + id));
        // Tạo một luồng bảng ánh xạ riêng để kiểm soát việc ánh xạ
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        // Cập nhật các trường của đơn hàng từ orderDTO
        modelMapper.map(orderDTO, order);
        order.setUser(existingUser);
        return orderRepository.save(order);
    }


    @Override
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElse(null);
        //khong xoa cung ma xoa mem
        if(order != null){
            order.setActive(false);
            orderRepository.save(order);
        }
    }
}