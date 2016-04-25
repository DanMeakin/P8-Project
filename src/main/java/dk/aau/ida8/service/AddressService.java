package dk.aau.ida8.service;

import dk.aau.ida8.data.AddressRepository;
import dk.aau.ida8.data.ClubRepository;
import dk.aau.ida8.model.Address;
import dk.aau.ida8.model.Club;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    private AddressRepository addressRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Iterable<Address> findAll() {
        return addressRepository.findAll();
    }

    public Address findOne(Long id) {
        return addressRepository.findOne(id);
    }

    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }

}
