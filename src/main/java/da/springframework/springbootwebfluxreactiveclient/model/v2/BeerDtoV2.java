package da.springframework.springbootwebfluxreactiveclient.model.v2;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class BeerDtoV2 {


    private UUID id;

    private String beerName;

    private BeerStyleEnum beerStyle;

    private String upc;

    private BigDecimal price;

    private Integer quantityOnHand;

    private OffsetDateTime createdDate;

    private OffsetDateTime lastModifiedDate;
}
