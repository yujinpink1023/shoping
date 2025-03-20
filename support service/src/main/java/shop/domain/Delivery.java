package shop.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import shop.SupportServiceApplication;
import shop.domain.DeliveryCancelled;
import shop.domain.DeliveryStarted;

@Entity
@Table(name = "Delivery_table")
@Data
//<<< DDD / Aggregate Root
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String orderId;

    private String address;

    private String customerId;

    private String productId;

    private Integer qty;

    private String status;

    public static DeliveryRepository repository() {
        DeliveryRepository deliveryRepository = SupportServiceApplication.applicationContext.getBean(
            DeliveryRepository.class
        );
        return deliveryRepository;
    }

    //<<< Clean Arch / Port Method
    public static void startDelivery(OrderPlaced orderPlaced) {
        // 새로운 배송 정보 생성
        Delivery delivery = new Delivery();
        delivery.setOrderId(String.valueOf(orderPlaced.getId()));
        delivery.setAddress(orderPlaced.getAddress());
        delivery.setCustomerId(orderPlaced.getCustomerId());
        delivery.setProductId(orderPlaced.getProductId());
        delivery.setQty(orderPlaced.getQty());
        delivery.setStatus("DELIVERY_STARTED");
        
        repository().save(delivery);

        // DeliveryStarted 이벤트 발행
        DeliveryStarted deliveryStarted = new DeliveryStarted(delivery);
        deliveryStarted.publishAfterCommit();
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void cancelDelivery(OrderCancelled orderCancelled) {
        // 해당 주문의 배송 정보 찾기 및 처리
        repository().findByOrderId(String.valueOf(orderCancelled.getId())).ifPresent(delivery -> {
            delivery.setStatus("DELIVERY_CANCELLED");
            repository().save(delivery);

            // DeliveryCancelled 이벤트 발행
            DeliveryCancelled deliveryCancelled = new DeliveryCancelled(delivery);
            deliveryCancelled.publishAfterCommit();
        });
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
