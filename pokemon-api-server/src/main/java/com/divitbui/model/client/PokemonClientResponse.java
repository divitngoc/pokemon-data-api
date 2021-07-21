package com.divitbui.model.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@lombok.Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PokemonClientResponse {

    private Integer id;
    private String name;
    private Integer height;
    private Integer weight;

}
