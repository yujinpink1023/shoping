package shop.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import shop.OrderServiceApplication;
import shop.domain.OrderCancelled;
import shop.domain.OrderPlaced;

@Entity
@Table(name = "Order_table")
@Data
//<<< DDD / Aggregate Root
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String customerId;

    private String productId;

    private Integer price;

    private Integer qty;

    private String address;

    private String status;

    @PostPersist
    public void onPostPersist() {
        OrderPlaced orderPlaced = new OrderPlaced(this);
        orderPlaced.publishAfterCommit();

        OrderCancelled orderCancelled = new OrderCancelled(this);
        orderCancelled.publishAfterCommit();
    }

    public static OrderRepository repository() {
        OrderRepository orderRepository = OrderServiceApplication.applicationContext.getBean(
            OrderRepository.class
        );
        return orderRepository;
    }

    //<<< Clean Arch / Port Method
    public static void sendMail(InventoryIncreased inventoryIncreased) {
        // 재고 증가 알림 메일 발송을 위한 주문 조회
        repository().findByProductId(inventoryIncreased.getProductId()).ifPresent(order -> {
            // 메일 발송 로직은 실제 구현 필요
            System.out.println("Sending mail for inventory increase: " + order.toString());
        });
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void updateStatus(DeliveryStarted deliveryStarted) {
        // 배송 시작에 따른 주문 상태 업데이트
        repository().findById(Long.valueOf(deliveryStarted.getOrderId())).ifPresent(order -> {
            order.setStatus("DELIVERY_STARTED");
            repository().save(order);
        });
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void updateStatus(DeliveryCancelled deliveryCancelled) {
        // 배송 취소에 따른 주문 상태 업데이트
        repository().findById(Long.valueOf(deliveryCancelled.getOrderId())).ifPresent(order -> {
            order.setStatus("DELIVERY_CANCELLED");
            repository().save(order);
        });
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
