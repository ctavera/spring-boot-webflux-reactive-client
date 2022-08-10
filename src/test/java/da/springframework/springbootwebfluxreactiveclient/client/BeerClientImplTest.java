package da.springframework.springbootwebfluxreactiveclient.client;

import da.springframework.springbootwebfluxreactiveclient.config.WebClientConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BeerClientImplTest {

    BeerClientImpl beerClient;

    @BeforeEach
    void setUp() {
        beerClient = new BeerClientImpl(new WebClientConfig().webClient());
    }

    @Test
    void getBeerById() {
    }

    @Test
    void listBeers() {
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

    @Test
    void getBeerByUPC() {
    }
}