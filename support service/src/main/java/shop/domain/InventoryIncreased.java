package shop.domain;

import java.time.LocalDate;
import java.util.*;
import lombok.*;
import shop.domain.*;
import shop.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class InventoryIncreased extends AbstractEvent {

    private Long id;
    private String productId;
    private Integer qty;

    public InventoryIncreased(Inventory aggregate) {
        super(aggregate);
        this.id = aggregate.getId();
        this.productId = aggregate.getProductId();
        this.qty = aggregate.getQty();
    }

    public InventoryIncreased() {
        super();
    }
}
//>>> DDD / Domain Event
