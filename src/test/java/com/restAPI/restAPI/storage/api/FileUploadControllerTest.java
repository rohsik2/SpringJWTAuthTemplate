package com.restAPI.restAPI.storage.api;

import com.restAPI.restAPI.storage.StorageService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class FileUploadControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private StorageService storageService;

    @Test
    void listUploadedFiles() throws Exception {
        given(this.storageService.loadAll()).willReturn(Stream.of(Paths.get("first.txt"), Paths.get("second.txt")));
        this.mvc.perform(get("/drive")).andExpect(status().isOk())
                .andExpect(model().attribute("files", Matchers.contains("http://localhost/drive/files/first.txt", "http://localhost/drive/files/second.txt")));

    }

    @Test
    void serveFile() {
    }

    @Test
    void handleFileUpload() {
    }

    @Test
    void handleStorageFileNotFound() {
    }
}