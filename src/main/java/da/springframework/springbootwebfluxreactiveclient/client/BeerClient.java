package da.springframework.springbootwebfluxreactiveclient.client;

import da.springframework.springbootwebfluxreactiveclient.model.BeerDto;
import da.springframework.springbootwebfluxreactiveclient.model.BeerPagedList;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BeerClient {

    Mono<BeerDto> getBeerById(UUID id, Boolean showInventoryOnHand);

    Mono<BeerPagedList> listBeers(Integer pageNumber, Integer pageSize, String beerName, String beerStyle, Boolean showInventoryOnHand);

    Mono<ResponseEntity<Void>> createNewBeer(BeerDto beerDto);

    Mono<ResponseEntity> updateBeer(BeerDto beerDto);

    Mono<ResponseEntity> deleteBeer(UUID id);

    Mono<BeerDto> getBeerByUPC(String upc);
}
