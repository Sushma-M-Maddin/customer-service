package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dto.CustomerRequestDTO;
import com.example.demo.dto.CustomerResponseDTO;
import com.example.demo.dto.LoginDTO;
import com.example.demo.entity.Customer;
import com.example.demo.jwt.JwtUtil;
import com.example.demo.service.CustomerService;

/*
 * CustomerController
 * ------------------
 * Fully secured REST APIs.
 */

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService service;

    @Autowired
    private JwtUtil jwtUtil;
    
    /* Constructor for Unit Testing */
    public CustomerController(
    CustomerService service,
    JwtUtil jwtUtil){

    this.service = service;
    this.jwtUtil = jwtUtil;
    }


    /*
     * Register Customer
     */
    @PostMapping("/register")
    public CustomerResponseDTO registerCustomer(
            @RequestBody CustomerRequestDTO dto){

        return service.registerCustomer(dto);
    }



    /*
     * Login Customer
     */
    @PostMapping("/login")
    public String loginCustomer(
            @RequestBody LoginDTO dto){

        Customer c = service.loginCustomer(
                dto.getUsername(),
                dto.getPassword());

        return jwtUtil.generateToken(
                c.getUsername(),
                c.getRole());
    }



    /*
     * Get Own Profile
     * Most Secure API
     */
    @GetMapping("/profile")
    public CustomerResponseDTO getProfile(
            Authentication auth){

        String username = auth.getName();

        return service.getCustomerByUsernameDTO(username);
    }



    /*
     * Get Customer By ID
     * Ownership Validation
     */
    @GetMapping("/{id}")
    public CustomerResponseDTO getCustomerById(
            @PathVariable Long id,
            Authentication auth){

        String username = auth.getName();

        CustomerResponseDTO dto =
                service.getCustomerById(id);

        if(!dto.getUsername()
                .equals(username)){

            throw new RuntimeException(
                    "Access Denied");
        }

        return dto;
    }



    /*
     * List Customers
     * Only for Admin Later
     */
    @GetMapping("/list")
    public List<CustomerResponseDTO>
    getAllCustomers(Authentication auth){

    boolean isAdmin=

    auth.getAuthorities()
    .stream()
    .anyMatch(a->a.getAuthority()
    .equals("ADMIN"));

    if(!isAdmin){

    throw new RuntimeException(
    "Admin Access Required");

    }

    return service.getAllCustomers();

    }


    /*
     * Update Own Account
     */
    @PutMapping("/update/{id}")
    public CustomerResponseDTO updateCustomer(

            @RequestBody CustomerRequestDTO dto,

            @PathVariable Long id,

            Authentication auth){

        String username = auth.getName();

        CustomerResponseDTO existing =
                service.getCustomerById(id);

        if(!existing.getUsername()
                .equals(username)){

            throw new RuntimeException(
                    "Access Denied");
        }

        return service.updateCustomer(dto,id);
    }



    /*
     * Delete Own Account
     */
    @DeleteMapping("/delete/{id}")
    public String deleteCustomer(

            @PathVariable Long id,

            Authentication auth){

        String username = auth.getName();

        CustomerResponseDTO existing =
                service.getCustomerById(id);

        if(!existing.getUsername()
                .equals(username)){

            throw new RuntimeException(
                    "Access Denied");
        }

        service.deleteCustomer(id);

        return "Customer Deleted Successfully";
    }

}