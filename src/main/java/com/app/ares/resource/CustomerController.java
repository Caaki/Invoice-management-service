package com.app.ares.resource;

import com.app.ares.domain.Customer;
import com.app.ares.domain.HttpResponse;
import com.app.ares.domain.Invoice;
import com.app.ares.dto.UserDTO;
import com.app.ares.report.CustomerReport;
import com.app.ares.service.CustomerService;
import com.app.ares.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final UserService userService;

    @GetMapping("/list")
    public ResponseEntity<HttpResponse> getCustomers(@AuthenticationPrincipal UserDTO user, @RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", userService.getUserByEmail(user.getEmail()),
                                "page", customerService.getCustomers(page.orElse(0), size.orElse(10)),
                                "statistics", customerService.getStatistics()))
                        .message("Customers retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PostMapping("/create")
    public ResponseEntity<HttpResponse> createCustomer(
            @AuthenticationPrincipal UserDTO user, @RequestBody @Valid Customer customer) throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        return ResponseEntity.created(URI.create(""))
                .body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user,
                                "customers", customerService.createCustomer(customer)))
                        .message("Customers created")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<HttpResponse> getCustomer(
            @AuthenticationPrincipal UserDTO user,
            @RequestParam Optional<Integer> page, @PathVariable("id") Long id){

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user,
                                "customer", customerService.getCustomer(id)))
                        .message("Customer retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/search")
    public ResponseEntity<HttpResponse> searchCustomers(
            @AuthenticationPrincipal UserDTO user,
            Optional<String> name,
            @RequestParam Optional<Integer> page,
            @RequestParam Optional<Integer> size){

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user,
                                "page", customerService.searchCustomers(name.orElse(""),page.orElse(0),size.orElse(10))))
                        .message("Customers retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }
    @PreAuthorize("hasAuthority('UPDATE:CUSTOMER')")
    @PutMapping("/update")
    public ResponseEntity<HttpResponse> updateCustomer(@AuthenticationPrincipal UserDTO user, @RequestBody Customer customer) throws InterruptedException {
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user,
                                "customer", customerService.updateCustomer(customer)))
                        .message("Customer updated")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PostMapping("/invoice/create")
    public ResponseEntity<HttpResponse> createInvoice(@AuthenticationPrincipal UserDTO user, @RequestBody Invoice invoice){

        return ResponseEntity.created(URI.create(""))
                .body(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user,
                                "invoice", customerService.createInvoice(invoice)))
                        .message("Invoice created")
                        .status(HttpStatus.CREATED)
                        .statusCode(HttpStatus.CREATED.value())
                        .build());
    }

    @GetMapping("/invoice/new")
    public ResponseEntity<HttpResponse> newInvoice(@AuthenticationPrincipal UserDTO user){

        return ResponseEntity.ok(
                        HttpResponse.builder()
                                .timeStamp(LocalDateTime.now().toString())
                                .data(Map.of("user", user,
                                        "customers", customerService.getCustomers()))
                                .message("Customers retrieved")
                                .status(OK)
                                .statusCode(OK.value())
                                .build());
    }

    @GetMapping("/invoice/list")
    public ResponseEntity<HttpResponse> getInvoices(
            @AuthenticationPrincipal UserDTO user,
            @RequestParam Optional<Integer> page, @RequestParam Optional<Integer> size){

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user,
                                "page", customerService.getInvoices(page.orElse(0),size.orElse(10))))
                        .message("Invoices retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/invoice/get/{id}")
    public ResponseEntity<HttpResponse> getInvoice(
            @AuthenticationPrincipal UserDTO user,
            @RequestParam Optional<Integer> page, @PathVariable("id") Long id){
        Invoice invoice = customerService.getInvoice(id);

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user,
                                "invoice", invoice,
                                "customer", invoice.getCustomer()))
                        .message("Customer retrieved")
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @PostMapping("/invoice/addtocustomer/{id}")
    public ResponseEntity<HttpResponse> addInvoiceToCustomer(
            @AuthenticationPrincipal UserDTO user,
            @PathVariable("id") Long id,
            @RequestBody Invoice invoice){

        customerService.addInvoiceToCustomer(id,invoice);
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .data(Map.of("user", user,
                                "customers", customerService.getCustomers()))
                        .message(String.format("Invoice added to customer with ID: %s",id))
                        .status(OK)
                        .statusCode(OK.value())
                        .build());
    }

    @GetMapping("/download/report")
    public ResponseEntity<Resource> downloadReport() throws IOException {
        List<Customer> customers = new ArrayList<>();
        customerService.getCustomers().iterator().forEachRemaining(customers::add);

        CustomerReport report = new CustomerReport(customers);
        HttpHeaders headers = new HttpHeaders();
        headers.add("File-Name", "customer-report.xlsx");
        headers.add("Content-Length", String.valueOf(report.export().contentLength()));
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;File-Name=customer-report.xlsx");

        return ResponseEntity.ok().contentType(
                MediaType.parseMediaType("application/vnd.ms-excel"))
                .headers(headers).body(
                        report.export());
    }

}
