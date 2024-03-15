package com.project.shopapp.repositories;

import com.project.shopapp.dtos.OrderDTO;
import com.project.shopapp.models.OrderDetail;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail,Long> {
    List<OrderDetail>findByOrderId(Long orderId);
}
