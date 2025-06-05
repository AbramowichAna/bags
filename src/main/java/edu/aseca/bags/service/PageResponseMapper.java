package edu.aseca.bags.service;

import edu.aseca.bags.application.dto.PageResponse;
import org.springframework.data.domain.Page;

public class PageResponseMapper {

	public static <T> PageResponse<T> fromSpringPage(Page<T> page) {
		return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalPages(),
				page.getTotalElements());
	}
}
