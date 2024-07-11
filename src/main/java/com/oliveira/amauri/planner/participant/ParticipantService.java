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

    public void triggerConfirmationEmailToParticipants(UUID tripId) {

    }
}
