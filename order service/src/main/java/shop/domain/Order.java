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

    public static void sendMail(InventoryIncreased inventoryIncreased) {
        repository().findById(inventoryIncreased.getId()).ifPresent(order -> {
            System.out.println("Sending mail for inventory increase: " + order.toString());
        });
    }


    public static void updateStatus(DeliveryStarted deliveryStarted) {
        repository().findById(Long.valueOf(deliveryStarted.getOrderId())).ifPresent(order -> {
            order.setStatus("DELIVERY_STARTED");
            repository().save(order);
        });
    }


    public static void updateStatus(DeliveryCancelled deliveryCancelled) {
        repository().findById(Long.valueOf(deliveryCancelled.getOrderId())).ifPresent(order -> {
            order.setStatus("DELIVERY_CANCELLED");
            repository().save(order);
        });
    }

}
//>>> DDD / Aggregate Root
