package com.FurryArtyPawPrints.ViewModule.repositories;


import com.FurryArtyPawPrints.ViewModule.models.UserModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepository extends ReactiveCrudRepository<UserModel, Integer> {
    Mono<UserModel> findByUserEmail(String userEmail);
}