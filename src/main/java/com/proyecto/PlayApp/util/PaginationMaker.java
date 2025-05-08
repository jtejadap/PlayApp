package com.proyecto.PlayApp.util;

import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor
public class PaginationMaker {
    public List<Integer> makePages(Page paginas){
        int totalPages = paginas.getTotalPages();
        if (totalPages > 0) {
            return IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
