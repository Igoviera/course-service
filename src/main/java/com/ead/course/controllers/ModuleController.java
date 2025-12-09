package com.ead.course.controllers;

import com.ead.course.dtos.ModuleDto;
import com.ead.course.models.CourseModel;
import com.ead.course.models.ModuleModel;
import com.ead.course.services.CourserService;
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
public class ModuleController {

    @Autowired
    ModuleService moduleService;

    @Autowired
    CourserService courserService;

    @PostMapping("/courses/{courseId}/modules")
    public ResponseEntity<Object> saveModule(@PathVariable("id") UUID courseId, @Valid @RequestBody ModuleDto moduleDto){
        Optional<CourseModel> courseModelOptional = courserService.finById(courseId);
        if (!courseModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course Not Found");
        }

        var module = new ModuleModel();
        BeanUtils.copyProperties(moduleDto, module);
        module.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        module.setCourse(courseModelOptional.get());
        return ResponseEntity.status(HttpStatus.CREATED).body(moduleService.save(module));
    }

    @DeleteMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> deleteModule(@PathVariable("id") UUID courseId, @PathVariable("id") UUID moduleId){
        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(courseId,moduleId);
        if (!moduleModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module Not Found");
        }
        moduleService.delete(moduleModelOptional.get());
        return ResponseEntity.status(HttpStatus.OK).body("Module deleted sucessfully.");
    }

    @PutMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> updateModule(
            @PathVariable("id") UUID courseId,
            @PathVariable("id") UUID moduleId,
            @Valid @RequestBody ModuleDto moduleDto)
    {
        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(courseId,moduleId);
        if (!moduleModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module Not Found");
        }
        var moduleModel = moduleModelOptional.get();
        moduleModel.setTitle(moduleDto.getTitle());
        moduleModel.setDescription(moduleDto.getDescription());
        return ResponseEntity.status(HttpStatus.OK).body(moduleService.save(moduleModel));
    }

    @GetMapping("/courses/{courseId}/modules")
    public ResponseEntity<List<ModuleModel>> getAllModule(@PathVariable("id") UUID courseId) {
        return ResponseEntity.status(HttpStatus.OK).body(moduleService.findAllByCourse(courseId));
    }

    @GetMapping("/courses/{courseId}/modules/{moduleId}")
    public ResponseEntity<Object> getOneModule(
            @PathVariable("id") UUID moduleId,
            @PathVariable("id") UUID courseId)
    {
        Optional<ModuleModel> moduleModelOptional = moduleService.findModuleIntoCourse(courseId,moduleId);
        if (!moduleModelOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Module Not Found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(moduleModelOptional.get());
    }
}
