package com.socrates.app.webflux.chat.infrastructure.config;

import com.socrates.app.webflux.chat.domain.model.voice.AudioFormat;
import com.socrates.app.webflux.chat.domain.model.voice.Voice;
import com.socrates.app.webflux.chat.domain.model.voice.VoiceSettings;
import com.socrates.app.webflux.chat.domain.model.voice.VoiceStyle;
import com.socrates.app.webflux.chat.infrastructure.config.properties.RagDialogueProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DialogueVoiceConfiguration {

	@Bean
	public Voice defaultVoice(RagDialogueProperties properties) {
		var supertone = properties.getSupertone();
		var settings = supertone.getVoiceSettings();

		return Voice.builder().id(supertone.getVoiceId()).name("adam").provider("supertone")
			.settings(new VoiceSettings(settings.getPitchShift(), settings.getPitchVariance(),
				settings.getSpeed()))
			.language(supertone.getLanguage()).style(VoiceStyle.fromString(supertone.getStyle()))
			.outputFormat(AudioFormat.fromString(supertone.getOutputFormat())).build();
	}
}
