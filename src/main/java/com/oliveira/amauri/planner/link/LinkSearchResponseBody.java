package com.oliveira.amauri.planner.link;

import java.util.UUID;

public record LinkSearchResponseBody(UUID id, String title, String description, String url) {
}
