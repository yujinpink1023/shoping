package shop.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import lombok.Data;

//<<< EDA / CQRS
@Entity
@Table(name = "MyView_table")
@Data
public class MyView {

    @Id
    //@GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    private String customerId;
    private String productId;
    private Integer price;
    private Integer qty;
    private String address;
    private String orderStatus;
    private String deliveryStatus;
}
