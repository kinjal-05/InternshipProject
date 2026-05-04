package com.einfochips.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class MessageService {

	@Autowired
	private MessageSource messageSource;

	public String getMessage(String key) {
		return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
	}
}