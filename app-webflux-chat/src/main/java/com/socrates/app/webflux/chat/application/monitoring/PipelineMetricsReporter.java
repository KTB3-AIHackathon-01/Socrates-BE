package com.socrates.app.webflux.chat.application.monitoring;

public interface PipelineMetricsReporter {
	void report(DialoguePipelineTracker.PipelineSummary summary);
}
