package com.divitbui.model.client;

import java.util.List;

import lombok.Data;

@Data
public class PokemonsClientResponse {

    private int count;
    private String next;
    private List<NamedAPIResource> results;
    
}
