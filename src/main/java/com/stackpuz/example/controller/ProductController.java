package com.stackpuz.example.controller;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import com.stackpuz.example.entity.Product;
import com.stackpuz.example.repository.ProductRepository;
import com.stackpuz.example.dto.DataTables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProductController {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private HttpServletRequest request;

    @GetMapping("/api/products")
    public DataTables<Product> getProducts(@RequestParam("page") Optional<Integer> pageParam, @RequestParam("length") Optional<Integer> sizeParam, @RequestParam("order[0][dir]") Optional<String> directionParam, @RequestParam("search[value]") String search, @RequestParam("draw") int draw) {
        int page = pageParam.orElse(1) - 1;
        int size = sizeParam.orElse(10);
        String order = request.getParameter("order[0][column]") != null ? request.getParameter("columns[" + request.getParameter("order[0][column]") + "][data]") : "Id";
        String direction = directionParam.orElse("asc");
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Direction.fromString(direction), order));
        long recordsTotal = repository.count();
        Page<Product> pageProduct = (search.isEmpty() ? repository.findAll(pageRequest) : repository.findByNameContains(pageRequest, search));
        return new DataTables<Product>(draw, recordsTotal, pageProduct.getTotalElements(), pageProduct.getContent());
    }
}