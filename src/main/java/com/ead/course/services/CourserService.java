package com.ead.course.services;

import com.ead.course.models.CourseModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourserService {
    void delete(CourseModel courseModel);

    CourseModel save(CourseModel courseModel);

    Optional<CourseModel> finById(UUID courseId);

    List<CourseModel> findAll();
}

