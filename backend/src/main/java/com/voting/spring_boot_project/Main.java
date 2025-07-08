package com.voting.spring_boot_project;

import java.util.List;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@RequestMapping("api/v1/customers")
public class Main {

    private final CustomerRepo customerRepo;
    
    public Main(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }
    
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(Main.class, args);
    }
    
    @GetMapping
    public List<Customer> getCustomers() {
        return customerRepo.findAll();
    }
    
    record NewCustomerRequest(
        String name,
        String email,
        Integer age
    ) {

    }

    @PostMapping
    public void addCustomer(@RequestBody NewCustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setAge(request.age());
        customerRepo.save(customer);
    }

    @DeleteMapping("{customerId}")
    public void deleteCustomer(@PathVariable("customerId") Integer id) {
        customerRepo.deleteById(id);
    }
    
    @PutMapping("{customerId}")
    public void updateCustomer(@PathVariable("customerId") Integer id, @RequestBody NewCustomerRequest request) {
        Customer customer = customerRepo.findById(id)
            .orElseThrow(() -> new IllegalStateException("Customer with id " + id + " does not exist"));
        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setAge(request.age());
        customerRepo.save(customer);
    }
}