package org.saharsh.gupchup.server.resources;

import java.util.List;

import javax.validation.Valid;

import org.saharsh.gupchup.server.model.Message;
import org.saharsh.gupchup.server.services.MessagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/messages")
@RestController
public class Messages {

	private final MessagesService service;

	@Autowired
	public Messages(MessagesService service) {
		this.service = service;
	}

	@PostMapping
	public void create(@Valid @RequestBody Message message) {
		service.deliverMessage(message);
	}

	@GetMapping(params = { "userId" })
	public List<Message> getNewMessages(@RequestParam("userId") String userId) {
		return service.getNewMessagesForUser(userId);
	}

}
