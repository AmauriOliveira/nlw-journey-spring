package com.oliveira.amauri.planner.activity;

import java.time.LocalDateTime;
import java.util.UUID;

public record ActivitySearchResponseBody(UUID id, String title, LocalDateTime occurs_at) {
}
