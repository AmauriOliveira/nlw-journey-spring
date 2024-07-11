package com.oliveira.amauri.planner.activity;

import com.oliveira.amauri.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;
    public ActivityCreateResponseBody createActivity(ActivityCreateRequestBody activityPayload, Trip trip) {
        Activity newActivity = new Activity(activityPayload.title(), activityPayload.occurs_at(), trip);

        this.activityRepository.save(newActivity);

        return new ActivityCreateResponseBody(newActivity.getId());
    }
}
