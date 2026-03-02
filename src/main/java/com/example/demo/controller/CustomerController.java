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

import jakarta.validation.Valid;

@RestController
@RequestMapping("/customer")
@CrossOrigin(origins = "http://localhost:4200")
public class CustomerController {

    @Autowired
    private CustomerService service;

    @Autowired
    private JwtUtil jwtUtil;
    
    public CustomerController(
    CustomerService service,
    JwtUtil jwtUtil){

    this.service = service;
    this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public CustomerResponseDTO registerCustomer(
    		@Valid
            @RequestBody CustomerRequestDTO dto){

        return service.registerCustomer(dto);
    }

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

    @GetMapping("/profile")
    public CustomerResponseDTO getProfile(
            Authentication auth){

        String username = auth.getName();

        return service.getCustomerByUsernameDTO(username);
    }

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
     * Only the logged-in customer can update their own data
     */
    @PutMapping("/update/{id}")
    public CustomerResponseDTO updateCustomer(

            @Valid
            @RequestBody CustomerRequestDTO dto,

            @PathVariable Long id,

            Authentication auth){

        String username = auth.getName();

        CustomerResponseDTO existing =
                service.getCustomerById(id);

        // Ownership validation
        if(!existing.getUsername()
                .equals(username)){

            throw new RuntimeException(
                    "Access Denied");
        }

        return service.updateCustomer(dto,id);
    }
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