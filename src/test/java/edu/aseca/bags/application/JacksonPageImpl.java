package edu.aseca.bags.application;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@JsonIgnoreProperties({"pageable"})
public class JacksonPageImpl<T> extends PageImpl<T> {

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public JacksonPageImpl(@JsonProperty("content") List<T> content, @JsonProperty("number") int number,
			@JsonProperty("size") int size, @JsonProperty("totalElements") long totalElements) {
		super(content, PageRequest.of(number, size), totalElements);
	}

	public JacksonPageImpl(List<T> content) {
		super(content);
	}
}