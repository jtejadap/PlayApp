package com.proyecto.PlayApp.util;

import org.springframework.data.domain.Page;
import java.util.ArrayList;
import java.util.List;

public class PaginationMaker {

    public List<Integer> makePages(Page<?> paginas) {

        int totalPages = paginas.getTotalPages();
        int currentPage = paginas.getNumber() + 1; // Page es 0-based
        int window = 3; // cantidad de páginas a mostrar a cada lado

        List<Integer> pages = new ArrayList<>();

        if (totalPages <= 1) return pages;

        // Siempre mostrar la primera página
        pages.add(1);

        int start = Math.max(2, currentPage - window);
        int end = Math.min(totalPages - 1, currentPage + window);

        // Páginas del centro
        for (int i = start; i <= end; i++) {
            pages.add(i);
        }

        // Siempre mostrar la última página
        if (totalPages > 1) {
            pages.add(totalPages);
        }

        return pages;
    }
}