package com.oliveira.amauri.planner.participant;

import java.util.UUID;

public record ParticipantFindResponseBody(UUID id, String name, String email, Boolean isConfirmed) {
}
