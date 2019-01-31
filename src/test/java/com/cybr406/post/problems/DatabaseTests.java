package com.cybr406.post.problems;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DatabaseTests {
  
  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;
  
  @Test
  public void testPostCreation() throws Exception {
    String content = "{ \"author\": \"test author\", \"content\": \"test content\" }";
    for (int i = 0; i < 2; i++) {
      String response = mockMvc.perform(post("/posts")
          .contentType(MediaType.APPLICATION_JSON)
          .content(content))
          .andExpect(status().isCreated())
          .andReturn()
          .getResponse()
          .getContentAsString();

      TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {};
      Map<String, Object> post = objectMapper.readValue(response, typeReference);

      // The id of the post should be auto-generated, and increment up with each new post.
      assertEquals(i + 1, post.get("id"));
      assertEquals("test author", post.get("author"));
      assertEquals("test content", post.get("content"));
    }
  }


}
