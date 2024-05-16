package com.pokemonreview.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemonreview.api.controllers.PokemonController;
import com.pokemonreview.api.dto.PokemonDto;
import com.pokemonreview.api.dto.PokemonResponse;
import com.pokemonreview.api.dto.ReviewDto;
import com.pokemonreview.api.models.Pokemon;
import com.pokemonreview.api.models.Review;
import com.pokemonreview.api.service.PokemonService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = PokemonController.class) // to be able to test apis without the server part running. Only list Controllers that are being tested.
@AutoConfigureMockMvc(addFilters = false) // so no need to add tokens 
@ExtendWith(MockitoExtension.class)
public class PokemonControllerTests {
    
    @Autowired
    private MockMvc mockMvc; // to perform get ,post, update and delete actions on APIs

    @MockBean // to bring our pokemon service
    private PokemonService pokemonService;

    @InjectMocks
    private PokemonController pokemonController;

    @Autowired
    private ObjectMapper objectMapper; // map data as json string

    private PokemonDto pokemonDto;

    @BeforeEach
    public void init() {
        pokemonDto = PokemonDto.builder().name("pickachu").type("electric").build();
    }

    @Test
    public void PokemonController_CreatePokemon_ReturnCreated() throws Exception {

        // Mocking service method

        // when thenReturn is same as given willAnswer , and here's example on both but use one only
        // given(pokemonService.createPokemon(ArgumentMatchers.any())).willAnswer((invocation -> invocation.getArgument(0)));
        when(pokemonService.createPokemon(pokemonDto)).thenReturn(pokemonDto);

        ResultActions response = mockMvc.perform(post("/api/pokemon/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pokemonDto)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(pokemonDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", CoreMatchers.is(pokemonDto.getType())))
                .andDo(MockMvcResultHandlers.print()); // to print the result
    }

    @Test
    public void PokemonController_GetAllPokemon_ReturnResponseDto() throws Exception {
        PokemonResponse responseDto = PokemonResponse.builder().pageSize(10).last(true).pageNo(1)
        .content(Arrays.asList(pokemonDto)).build();
        when(pokemonService.getAllPokemon(1,10)).thenReturn(responseDto);

        ResultActions response = mockMvc.perform(get("/api/pokemon")
                .contentType(MediaType.APPLICATION_JSON)
                .param("pageNo","1")
                .param("pageSize", "10"));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.size()", CoreMatchers.is(responseDto.getContent().size())));
    }

    @Test
    public void PokemonController_PokemonDetail_ReturnPokemonDto() throws Exception {
        int pokemonId = 1;
        when(pokemonService.getPokemonById(pokemonId)).thenReturn(pokemonDto);

        ResultActions response = mockMvc.perform(get("/api/pokemon/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pokemonDto)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(pokemonDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", CoreMatchers.is(pokemonDto.getType())));
    }

    @Test
    public void PokemonController_UpdatePokemon_ReturnPokemonDto() throws Exception {
        int pokemonId = 1;
        when(pokemonService.updatePokemon(pokemonDto, pokemonId)).thenReturn(pokemonDto);

        ResultActions response = mockMvc.perform(put("/api/pokemon/1/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pokemonDto)));

        response.andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is(pokemonDto.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.type", CoreMatchers.is(pokemonDto.getType())));
    }

    @Test
    public void PokemonController_DeletePokemon_ReturnString() throws Exception {
        int pokemonId = 1;
        // this is the way to call void function
        doNothing().when(pokemonService).deletePokemonId(pokemonId);

        ResultActions response = mockMvc.perform(delete("/api/pokemon/1/delete")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(MockMvcResultMatchers.status().isOk());

// try to get the deleted object
        // when(pokemonService.getPokemonById(pokemonId)).thenReturn(pokemonDto);
        // ResultActions getApiResponse = mockMvc.perform(get("/api/pokemon/1")
        //         .contentType(MediaType.APPLICATION_JSON)
        //         .content(objectMapper.writeValueAsString(pokemonDto)));

        //         getApiResponse.andExpect(MockMvcResultMatchers.status().isOk())
        //         .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.not(pokemonId)))
        //         .andDo(MockMvcResultHandlers.print());
   

    }



    /* start codium test*/

// @Test
// public void test_getPokemons_invalidParameters() {
//     // Arrange
//     PokemonController pokemonController = new PokemonController(pokemonService);
//     int pageNo = -1;
//     int pageSize = 0;

//     // Act
//     ResponseEntity<PokemonResponse> response = pokemonController.getPokemons(pageNo, pageSize);

//     // Assert
//     assertEquals(HttpStatus.OK, response.getStatusCode());
//     assertNotNull(response.getBody());
// }

 /* end codium test  */

 // another way to test create api
//  @Test
//     public void testCreatePokemon() {
//         // Mocking data
//         PokemonDto pokemonDto = new PokemonDto();
//         pokemonDto.setName("Pikachu");
//         pokemonDto.setType("Electric");

//         // Mocking service method
//         when(pokemonService.createPokemon(any(PokemonDto.class))).thenReturn(pokemonDto);

//         // Call the controller method
//         ResponseEntity<PokemonDto> response = pokemonController.createPokemon(pokemonDto);

//         // Verify that the service method was called with the correct parameter
//         verify(pokemonService, times(1)).createPokemon(pokemonDto);

//         // Verify the response status
//         assertEquals(HttpStatus.CREATED, response.getStatusCode());

//         // Verify the returned PokemonDto
//         PokemonDto returnedPokemon = response.getBody();
//         assertNotNull(response.getBody());
//         assertEquals("Pikachu", returnedPokemon.getName());
//         assertEquals("Electric", returnedPokemon.getType());
//     }
}
