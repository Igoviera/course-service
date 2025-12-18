package com.ead.course.controllers;

import com.ead.course.dtos.LessonDto;
import com.ead.course.models.LessonModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.LessonService;
import com.ead.course.services.ModuleService;
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
@CrossOrigin(origins = "*", maxAge = 3600)
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @Autowired
    private ModuleService moduleService;

    @PostMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<Object> saveLesson(@PathVariable("id") UUID moduleId, @Valid @RequestBody LessonDto lessonDto){
        Optional<ModuleModel> moduleModelOptional = moduleService.finById(moduleId);
        if (!moduleModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module Not Found");
        }

        var lesson = new LessonModel();
        BeanUtils.copyProperties(lessonDto, lesson);
        lesson.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        lesson.setModule(moduleModelOptional.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(lessonService.save(lesson));
    }

    @DeleteMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> deleteLesson(@PathVariable("moduleId") UUID moduleId, @PathVariable("lessonId") UUID lessonId){
        Optional<LessonModel> optionalLessonModel = lessonService.findLessonIntoModule(moduleId, lessonId);
        if (!optionalLessonModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson Not Found");
        }
        lessonService.delete(optionalLessonModel.get());
        return ResponseEntity.status(HttpStatus.OK).body("Lesson deleted sucessfully.");
    }


    @PutMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> updateLesson(
            @PathVariable("moduleId") UUID moduleId,
            @PathVariable("lessonId") UUID lessonId,
            @Valid @RequestBody LessonDto lessonDto)
    {
        Optional<LessonModel> optionalLessonModel = lessonService.findLessonIntoModule(lessonId, moduleId);
        if (!optionalLessonModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson Not Found");
        }
        var lessonModule = optionalLessonModel.get();
        lessonModule.setTitle(lessonDto.getTitle());
        lessonModule.setDescription(lessonDto.getDescription());
        lessonModule.setVideoUrl(lessonDto.getVideoUrl());
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.save(lessonModule));
    }

    @GetMapping("/modules/{moduleId}/lessons")
    public ResponseEntity<List<LessonModel>> getAllLessons(@PathVariable("moduleId") UUID moduleId) {
        return ResponseEntity.status(HttpStatus.OK).body(lessonService.findAllByLesson(moduleId));
    }

    @GetMapping("/modules/{moduleId}/lessons/{lessonId}")
    public ResponseEntity<Object> getOneLesson(
            @PathVariable("moduleId") UUID moduleId,
            @PathVariable("lessonId") UUID lessonId
    ) {
        Optional<LessonModel> optionalLessonModel = lessonService.findLessonIntoModule(moduleId, lessonId);
        if (!optionalLessonModel.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Lesson Not Found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(optionalLessonModel.get());
    }
}
