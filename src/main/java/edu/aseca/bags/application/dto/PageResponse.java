package edu.aseca.bags.application.dto;

import java.util.List;

public record PageResponse<T>(List<T> content, int number, int size, int totalPages, long totalElements) {
}