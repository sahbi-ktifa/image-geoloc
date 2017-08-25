package fr.efaya.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration
public class GeoLocWebServiceControllerTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    private MockMultipartFile picture = new MockMultipartFile("file", "picture.jpeg", "imagE/jpeg", "JPEG".getBytes());

    @Before
    public void setup() throws Exception {
        mockMvc = webAppContextSetup(wac).build();
    }

    @Test
    public void ensureServiceIsNotReachableWithoutParameters() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.fileUpload("/api/geo/within")
                    .file(picture))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void ensureServiceIsReachable() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.fileUpload("/api/geo/within")
                        .file(picture)
                        .param("latitudeA", "1.48")
                        .param("longitudeA", "1.49")
                        .param("latitudeB", "1.50")
                        .param("longitudeB", "1.51"))
                .andExpect(status().isOk());
    }

    @Configuration
    @EnableWebMvc
    static class TestConfiguration {
        @Bean
        public GeoLocService geoLocService() {
            return mock(GeoLocService.class);
        }
        @Bean
        public GeoLocWebServiceController getController() {
            return new GeoLocWebServiceController(geoLocService());
        }
    }
}