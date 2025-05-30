package edu.aseca.bags.domain.common;

import java.util.List;

public record PagedResult<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isFirst,
        boolean isLast
) {}