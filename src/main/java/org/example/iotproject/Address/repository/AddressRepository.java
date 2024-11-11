package org.example.iotproject.Address.repository;

import org.example.iotproject.Address.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByAddressName (String addressName);
}
