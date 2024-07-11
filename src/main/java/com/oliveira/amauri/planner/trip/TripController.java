package com.oliveira.amauri.planner.trip;

import com.oliveira.amauri.planner.activity.ActivityCreateRequestBody;
import com.oliveira.amauri.planner.activity.ActivityCreateResponseBody;
import com.oliveira.amauri.planner.activity.ActivitySearchResponseBody;
import com.oliveira.amauri.planner.activity.ActivityService;
import com.oliveira.amauri.planner.link.LinkCreateRequestBody;
import com.oliveira.amauri.planner.link.LinkCreateResponseBody;
import com.oliveira.amauri.planner.link.LinkService;
import com.oliveira.amauri.planner.participant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private LinkService linkService;

    @Autowired
    private ParticipantService participantService;

    // trips

    @PostMapping
    public ResponseEntity<TripCreateResponseBody> createTrip(@RequestBody TripCreateRequestBody tripPayload) {
        Trip newTrip = new Trip(tripPayload);

        this.tripRepository.save(newTrip);

        this.participantService.registerParticipantsToEvent(tripPayload.emails_to_invite(), newTrip);

        return ResponseEntity.ok(new TripCreateResponseBody(newTrip.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id){
        Optional<Trip> trip = this.tripRepository.findById(id);

        return trip.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripCreateRequestBody tripPayload) {
        Optional<Trip> trip = this.tripRepository.findById(id);

        if (trip.isPresent()) {
            Trip rawTrip = trip.get();

            rawTrip.setDestination(tripPayload.destination());
            rawTrip.setEndsAt(LocalDateTime.parse(tripPayload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
            rawTrip.setStartsAt(LocalDateTime.parse(tripPayload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));

            this.tripRepository.save(rawTrip);

            return ResponseEntity.ok(rawTrip);
        }

        return  ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id) {
        Optional<Trip> trip = this.tripRepository.findById(id);

        if (trip.isPresent()) {
            Trip rawTrip = trip.get();
            rawTrip.setIsConfirmed(true);

            this.tripRepository.save(rawTrip);
            this.participantService.triggerConfirmationEmailToParticipants(id);

            return ResponseEntity.ok(rawTrip);
        }

        return  ResponseEntity.notFound().build();
    }

    // activities

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivitySearchResponseBody>> getAllActivities(@PathVariable UUID id) {
        List<ActivitySearchResponseBody> activityList = this.activityService.getAllActivitiesFromId(id);

        return ResponseEntity.ok(activityList);
    }

    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityCreateResponseBody> createActivity(@PathVariable UUID id, @RequestBody ActivityCreateRequestBody activityPayload) {
        Optional<Trip> trip = this.tripRepository.findById(id);

        if (trip.isPresent()) {
            Trip rawTrip = trip.get();

            ActivityCreateResponseBody activityCreateResponseBody = this.activityService.createActivity(activityPayload, rawTrip);

            return ResponseEntity.ok(activityCreateResponseBody);
        }

        return  ResponseEntity.notFound().build();
    }

    // links

    @PostMapping("/{id}/links")
    public ResponseEntity<LinkCreateResponseBody> createLink(@PathVariable UUID id, @RequestBody LinkCreateRequestBody linkPayload) {
        Optional<Trip> trip = this.tripRepository.findById(id);

        if (trip.isPresent()) {
            Trip rawTrip = trip.get();

            LinkCreateResponseBody linkBody = this.linkService.createLink(linkPayload, rawTrip);

            return ResponseEntity.ok(linkBody);
        }

        return  ResponseEntity.notFound().build();
    }

    // participants

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantFindResponseBody>> getAllParticipants(@PathVariable UUID id) {
        List<ParticipantFindResponseBody> participantList = this.participantService.getAllParticipantsFromEvent(id);

        return ResponseEntity.ok(participantList);
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponseBody> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestBody participantPayload) {
        Optional<Trip> trip = this.tripRepository.findById(id);

        if (trip.isPresent()) {
            Trip rawTrip = trip.get();

            ParticipantCreateResponseBody responseBody = this.participantService.registerParticipantToEvent(participantPayload.email(), rawTrip);

            if (rawTrip.getIsConfirmed()) {
                this.participantService.triggerConfirmationEmailToParticipant(participantPayload.email());
            }

            return ResponseEntity.ok(responseBody);
        }

        return  ResponseEntity.notFound().build();
    }

}
