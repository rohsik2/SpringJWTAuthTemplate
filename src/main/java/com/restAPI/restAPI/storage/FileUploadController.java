package com.restAPI.restAPI.storage.api;

import com.restAPI.restAPI.storage.StorageException;
import com.restAPI.restAPI.storage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.stream.Collectors;

@Controller @RequiredArgsConstructor @RequestMapping("/drive") @Slf4j
public class FileUploadController {
    private final StorageService storageService;

    @GetMapping("/")
    public ResponseEntity<?> listUploadedFiles() throws IOException{
        log.info("List up loaded files called");
        String result = storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(
                                FileUploadController.class, "serveFile",
                                path.getFileName().toString())
                        .build().toUriString()
                ).collect(Collectors.toList()).toString();

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<?> serveFile(@PathVariable String filename) throws StorageException.StorageFileNotFoundException {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(
                HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public ResponseEntity<?> handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes){
        storageService.store(file);
        redirectAttributes.addFlashAttribute("message", "You successfully uploaded " + file.getOriginalFilename() + "!");
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(StorageException.StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageException.StorageFileNotFoundException exc){
        return ResponseEntity.notFound().build();
    }



}
