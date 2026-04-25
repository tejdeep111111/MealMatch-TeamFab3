package com.teamfab.mealmatch.service;

import com.teamfab.mealmatch.dto.OrderRequest;
import com.teamfab.mealmatch.dto.OrderResponse;
import com.teamfab.mealmatch.entity.Order;
import com.teamfab.mealmatch.entity.Subscription;
import com.teamfab.mealmatch.entity.User;
import com.teamfab.mealmatch.exception.ResourceNotFoundException;
import com.teamfab.mealmatch.repository.OrderRepository;
import com.teamfab.mealmatch.repository.SubscriptionRepository;
import com.teamfab.mealmatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public OrderResponse createOrder(OrderRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Subscription subscription = subscriptionRepository.findById(request.getSubscriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        Order order = Order.builder()
                .subscription(subscription)
                .user(user)
                .provider(subscription.getProvider())
                .scheduledDate(request.getScheduledDate())
                .status("SCHEDULED")
                .price(subscription.getMenuItem().getPrice())
                .build();

        return toResponse(orderRepository.save(order));
    }

    public List<OrderResponse> getMyOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return orderRepository.findByUser(user).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public List<OrderResponse> getProviderOrders(String providerEmail) {
        return orderRepository.findByProviderEmail(providerEmail).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    public OrderResponse updateOrderStatus(UUID id, String status, String providerEmail) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getProvider().getEmail().equals(providerEmail)) {
            throw new com.teamfab.mealmatch.exception.UnauthorizedException("Not authorized");
        }
        order.setStatus(status);
        return toResponse(orderRepository.save(order));
    }

    private OrderResponse toResponse(Order o) {
        return OrderResponse.builder()
                .id(o.getId())
                .subscriptionId(o.getSubscription().getId())
                .userId(o.getUser().getId())
                .providerId(o.getProvider().getId())
                .providerName(o.getProvider().getName())
                .scheduledDate(o.getScheduledDate())
                .status(o.getStatus())
                .price(o.getPrice())
                .build();
    }
}
