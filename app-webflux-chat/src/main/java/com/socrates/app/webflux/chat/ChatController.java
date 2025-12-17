package com.socrates.app.webflux.chat;

import java.time.Duration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

	@GetMapping(path = "/stream/ping", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<String> pingStream() {
		return Flux.interval(Duration.ofSeconds(1))
			.map(seq -> "ping " + seq);
	}
}

