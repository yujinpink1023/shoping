package shop.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import shop.SupportServiceApplication;
import shop.domain.InventoryDecreased;
import shop.domain.InventoryIncreased;

@Entity
@Table(name = "Inventory_table")
@Data
//<<< DDD / Aggregate Root
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String price;

    private Integer qty;

    public static InventoryRepository repository() {
        InventoryRepository inventoryRepository = SupportServiceApplication.applicationContext.getBean(
            InventoryRepository.class
        );
        return inventoryRepository;
    }

    //<<< Clean Arch / Port Method
    public static void decreaseInventory(DeliveryStarted deliveryStarted) {
        // 해당 상품의 재고 찾기 및 처리
        repository().findByProductId(deliveryStarted.getProductId()).ifPresent(inventory -> {
            // 재고 감소
            inventory.setQty(inventory.getQty() - deliveryStarted.getQty());
            repository().save(inventory);

            // InventoryDecreased 이벤트 발행
            InventoryDecreased inventoryDecreased = new InventoryDecreased(inventory);
            inventoryDecreased.publishAfterCommit();
        });
    }

    //>>> Clean Arch / Port Method
    //<<< Clean Arch / Port Method
    public static void increaseInventory(DeliveryCancelled deliveryCancelled) {
        // 해당 상품의 재고 찾기 및 처리
        repository().findByProductId(deliveryCancelled.getProductId()).ifPresent(inventory -> {
            // 재고 증가
            inventory.setQty(inventory.getQty() + deliveryCancelled.getQty());
            repository().save(inventory);

            // InventoryIncreased 이벤트 발행
            InventoryIncreased inventoryIncreased = new InventoryIncreased(inventory);
            inventoryIncreased.publishAfterCommit();
        });
    }
    //>>> Clean Arch / Port Method

}
//>>> DDD / Aggregate Root
