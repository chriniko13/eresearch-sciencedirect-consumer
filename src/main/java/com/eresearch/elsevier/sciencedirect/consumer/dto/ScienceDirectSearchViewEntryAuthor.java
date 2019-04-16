package com.eresearch.elsevier.sciencedirect.consumer.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScienceDirectSearchViewEntryAuthor {

    @JsonProperty("$")
    private String name;


    /*
        Note, in order to handle the following case:

        1)
        "authors": {
          "author": "Ioannis Voyiatzis"
        }


        2)
        "authors": {
          "author": [
            {
              "$": "Serge Bernard"
            },
            {
              "$": "Mohamed Masmoudi"
            },
            {
              "$": "Ioannis Voyiatzis"
            }
          ]
        }


     */
    @JsonCreator
    public ScienceDirectSearchViewEntryAuthor(String input) {
        if (input.contains("$")) {
            String s = input.split(":")[1];
            this.setName(s);
        } else {
         this.setName(input);
        }
    }
}
