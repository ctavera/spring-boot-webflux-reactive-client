package da.springframework.springbootwebfluxreactiveclient.client;

import da.springframework.springbootwebfluxreactiveclient.config.WebClientConfig;
import da.springframework.springbootwebfluxreactiveclient.model.BeerDto;
import da.springframework.springbootwebfluxreactiveclient.model.BeerPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;

class BeerClientImplTest {

    BeerClientImpl beerClient;

    @BeforeEach
    void setUp() {
        beerClient = new BeerClientImpl(new WebClientConfig().webClient());
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

    private BeerDto getBeerDto() {
        Mono<BeerPagedList> beerPagedListMono = beerClient.listBeers(null, null, null, null, null);
        BeerPagedList beerPagedList = beerPagedListMono.block();
        BeerDto beer = beerPagedList.getContent().get(0);

        return beer;
    }

    @Test
    void createNewBeer() {
    }

    @Test
    void updateBeer() {
    }

    @Test
    void deleteBeer() {
    }
}