package com.oliveira.amauri.planner.link;

import com.oliveira.amauri.planner.activity.Activity;
import com.oliveira.amauri.planner.activity.ActivityCreateRequestBody;
import com.oliveira.amauri.planner.activity.ActivityCreateResponseBody;
import com.oliveira.amauri.planner.activity.ActivitySearchResponseBody;
import com.oliveira.amauri.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LinkService {

    @Autowired
    private LinkRepository linkRepository;

    public LinkCreateResponseBody createLink(LinkCreateRequestBody linkPayload, Trip trip) {
        Link newLink = new Link(linkPayload.title(), linkPayload.description(),linkPayload.url(), trip);

        this.linkRepository.save(newLink);

        return new LinkCreateResponseBody(newLink.getId());
    }

    public List<LinkSearchResponseBody> getAllLinksFromId(UUID tripId) {
        return this.linkRepository
                .findByTripId(tripId)
                .stream()
                .map(link -> new LinkSearchResponseBody(link.getId(), link.getTitle(), link.getDescription(), link.getUrl()))
                .toList();
    }
}
