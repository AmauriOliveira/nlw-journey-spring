package com.oliveira.amauri.planner.participant;

import com.oliveira.amauri.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    public void registerParticipantsToEvent(List<String> emailList, Trip trip) {
       List<Participant> participants = emailList.stream().map(email -> new Participant(email, trip)).toList();

       this.participantRepository.saveAll(participants);
    }

    public ParticipantCreateResponseBody registerParticipantToEvent(String email, Trip trip) {
        Participant newParticipant = new Participant(email, trip);

        this.participantRepository.save(newParticipant);

        return new ParticipantCreateResponseBody(newParticipant.getId());
    }

    public void triggerConfirmationEmailToParticipants(UUID tripId) {

    }

    public void triggerConfirmationEmailToParticipant(String email) {

    }

    public List<ParticipantFindResponseBody> getAllParticipantsFromEvent(UUID tripId) {
        return this.participantRepository
                .findByTripId(tripId)
                .stream()
                .map(participant -> new ParticipantFindResponseBody(participant.getId(), participant.getName(), participant.getEmail(), participant.getIsConfirmed()))
                .toList();
    }
}
