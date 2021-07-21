package com.divitbui.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Pokemon {

    private Integer id;
    private String name;
    private Integer height;
    private Integer weight;
}
