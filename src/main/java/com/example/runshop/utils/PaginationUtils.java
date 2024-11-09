package com.example.runshop.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PaginationUtils {
    public static Pageable createPageable(int page, int size) {
        return PageRequest.of(page, size);
    }

}
