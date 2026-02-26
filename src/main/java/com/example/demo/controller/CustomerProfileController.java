package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.CustomerProfileRequestDTO;
import com.example.demo.entity.CustomerProfile;
import com.example.demo.service.CustomerProfileService;
import com.example.demo.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/profile")
public class CustomerProfileController {

    @Autowired
    private CustomerProfileService profileService;

    @Autowired
    private CustomerService customerService;

    @PostMapping("/create")
    public CustomerProfile createProfile(

            Authentication auth,

            @Valid
            @RequestBody CustomerProfileRequestDTO dto) {
        String username =
                auth.getName();
        Long accountNo =
                customerService
                .getCustomerByUsernameDTO(username)
                .getAccountNo();

        CustomerProfile profile =
                new CustomerProfile();

        profile.setPan(dto.getPan());
        profile.setDob(dto.getDob());
        profile.setAddress(dto.getAddress());
        profile.setIfsc(dto.getIfsc());
        profile.setAnnualIncome(dto.getAnnualIncome());
        profile.setOccupation(dto.getOccupation());


        return profileService
                .createProfile(accountNo, profile);

    }
    @GetMapping("/my")
    public CustomerProfile getProfile(

            Authentication auth) {


        String username =
                auth.getName();


        Long accountNo =
                customerService
                .getCustomerByUsernameDTO(username)
                .getAccountNo();


        return profileService
                .getProfile(accountNo);

    }

    @GetMapping("/pan/{pan}")
    public CustomerProfile getByPan(
            @PathVariable String pan,
            Authentication auth) {
        boolean isAdmin =

                auth.getAuthorities()
                .stream()
                .anyMatch(a ->
                a.getAuthority()
                .equals("ADMIN"));
        if(!isAdmin){

            throw new RuntimeException(
                    "Admin Access Required");
        }
        return profileService
                .getByPan(pan);

    }
}