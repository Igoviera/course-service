package com.ead.course.controllers;

import com.ead.course.dtos.CourseDto;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/course")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {

    @Autowired
    CourserService courserService;

    @PostMapping
    public ResponseEntity<Object> saveCourse(@Valid @RequestBody CourseDto courseDto){
        var courseModel = new CourseModel();
        BeanUtils.copyProperties(courseDto, courseModel);
        courseModel.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        courseModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.CREATED).body(courserService.save(courseModel));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Object> deleteCourse(@PathVariable("id") UUID courseId){
       Optional<CourseModel> courseModelOptional = courserService.finById(courseId);
       if (!courseModelOptional.isPresent()){
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Corse Not Found");
       }
       courserService.delete(courseModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Corse deleted sucessfully.");
    }

    @PutMapping("/{courseid}")
    public ResponseEntity<Object> updateCourse(@PathVariable("id") UUID courseId, @Valid @RequestBody CourseDto courseDto){
        Optional<CourseModel> courseModelOptional = courserService.finById(courseId);
        if (!courseModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Corse Not Found");
        }
        var courseModel = courseModelOptional.get();
        courseModel.setName(courseDto.getName());
        courseModel.setDescription(courseDto.getDescription());
        courseModel.setImageUrl(courseDto.getImageUrl());
        courseModel.setCourseStatus(courseDto.getCourseStatus());
        courseModel.setCourseLevel(courseDto.getCourseLevel());
        courseModel.setLastUpdateDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity.status(HttpStatus.OK).body(courserService.save(courseModel));
    }

    @GetMapping
    public ResponseEntity<List<CourseModel>> getAllCourses() {
        return ResponseEntity.status(HttpStatus.OK).body(courserService.findAll());
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Object> getOneCourse(@PathVariable("id") UUID courseId) {
        Optional<CourseModel> courseModelOptional = courserService.finById(courseId);
        if (!courseModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Corse Not Found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(courseModelOptional.get());
    }
}
