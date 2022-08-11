package da.springframework.springbootwebfluxreactiveclient.client;

import da.springframework.springbootwebfluxreactiveclient.config.WebClientConfig;
import da.springframework.springbootwebfluxreactiveclient.model.BeerDto;
import da.springframework.springbootwebfluxreactiveclient.model.BeerPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BeerClientImplTest {

    BeerClientImpl beerClient;

    @BeforeEach
    void setUp() {
        beerClient = new BeerClientImpl(new WebClientConfig().webClient());
    }

    private BeerDto getBeerDto() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null, null, null);
        BeerPagedList beerPagedList = beerPagedListMono.block();
        BeerDto beer = beerPagedList.getContent().get(0);

        return beer;
    }

    @Test
    void listBeers() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null, null, null);

        BeerPagedList beerPagedList = beerPagedListMono.block();

        assertThat(beerPagedList).isNotNull();
        assertThat(beerPagedList.getContent().size()).isGreaterThan(0);

        System.out.println(beerPagedList.toList());
    }

    @Test
    void listBeersPageSize10() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(1, 10, null, null, null);

        BeerPagedList beerPagedList = beerPagedListMono.block();

        assertThat(beerPagedList).isNotNull();
        assertThat(beerPagedList.getContent().size()).isEqualTo(10);
    }

    @Test
    void listBeersNoRecords() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(10, 20, null, null, null);

        BeerPagedList beerPagedList = beerPagedListMono.block();

        assertThat(beerPagedList).isNotNull();
        assertThat(beerPagedList.getContent().size()).isEqualTo(0);
    }

    @Disabled("API returning inventory when should not be")
    @Test
    void getBeerById() {
        BeerDto beer = getBeerDto();

        Mono<BeerDto> beerDtoMono = beerClient.getBeerById(beer.getId(), false);
        BeerDto beerDto = beerDtoMono.block();

        assertThat(beerDto.getId()).isEqualTo(beer.getId());
        assertThat(beerDto.getQuantityOnHand()).isNull();
    }

    @Test
    void functionalTestGetBeerById() throws InterruptedException {
        AtomicReference<String> beerName = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        beerClient.listBeers(null, null, null, null, null)
                .map(beerPagedList -> beerPagedList.getContent().get(0).getId())
                .map(id -> beerClient.getBeerById(id, false))
                .flatMap(beerDtoMono -> beerDtoMono)
                .subscribe(beerDto -> {
                    System.out.println(beerDto.getBeerName());
                    beerName.set(beerDto.getBeerName());
                    assertThat(beerName.get()).isEqualTo("Mango Bobs");
                    countDownLatch.countDown();
                });

        countDownLatch.await();

        assertThat(beerName.get()).isEqualTo("Mango Bobs");
    }

    @Test
    void getBeerByIdShowInventoryOnHand() {
        BeerDto beer = getBeerDto();

        Mono<BeerDto> beerDtoMono = beerClient.getBeerById(beer.getId(), true);
        BeerDto beerDto = beerDtoMono.block();

        assertThat(beerDto.getId()).isEqualTo(beer.getId());
        assertThat(beerDto.getQuantityOnHand()).isNotNull();
    }

    @Test
    void getBeerByUPC() {
        BeerDto beer = getBeerDto();

        Mono<BeerDto> beerDtoMono = beerClient.getBeerByUPC(beer.getUpc());
        BeerDto beerDto = beerDtoMono.block();

        assertThat(beerDto.getUpc()).isEqualTo(beer.getUpc());
    }

    @Test
    void createNewBeer() {
        BeerDto beerDto = BeerDto.builder()
                .beerName("Dogfishhead 90 Min IPA")
                .beerStyle("IPA")
                .upc("23156468954656")
                .price(new BigDecimal("10.99"))
                .build();

        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.createNewBeer(beerDto);
        ResponseEntity response = responseEntityMono.block();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void updateBeer() {
        BeerDto beerDto = getBeerDto();

        BeerDto newBeerDto = BeerDto.builder()
                .beerName(beerDto.getBeerName())
                .beerStyle(beerDto.getBeerStyle())
                .upc(beerDto.getUpc())
                .price(beerDto.getPrice())
                .build();

        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.updateBeer(beerDto.getId(), newBeerDto);
        ResponseEntity<Void> response = responseEntityMono.block();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void testDeleteBeerHandleException() {
        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeer(UUID.randomUUID());

        ResponseEntity<Void> response = responseEntityMono.onErrorResume(
                throwable -> {
                    if (throwable instanceof WebClientResponseException) {
                        WebClientResponseException exception = (WebClientResponseException) throwable;
                        return Mono.just(ResponseEntity.status(exception.getStatusCode()).build());
                    } else {
                        throw new RuntimeException(throwable);
                    }
                }
        ).block();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteBeerNotFound() {
        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeer(UUID.randomUUID());
        assertThrows(WebClientResponseException.class, () -> {
            ResponseEntity<Void> response = responseEntityMono.block();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        });
    }

    @Test
    void deleteBeer() {
        BeerDto beerDto = getBeerDto();

        Mono<ResponseEntity<Void>> responseEntityMono = beerClient.deleteBeer(beerDto.getId());
        ResponseEntity<Void> response = responseEntityMono.block();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}