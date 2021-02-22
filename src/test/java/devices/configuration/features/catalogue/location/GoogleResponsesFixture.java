package devices.configuration.features.catalogue.location;

import org.jetbrains.annotations.NotNull;

class GoogleResponsesFixture {

    @NotNull
    static String addressForCoordinates() {
        // https://maps.googleapis.com/maps/api/geocode/json?latlng=52.352206,4.809561&location_type=ROOFTOP&result_type=street_address&key=AIzaSyDwmdKaSAfomiufOdIZ9Fyyd3gZbiFCYCs
        return "{\n" +
                "   \"plus_code\" : {\n" +
                "      \"compound_code\" : \"9R25+VR Amsterdam, Holandia\",\n" +
                "      \"global_code\" : \"9F469R25+VR\"\n" +
                "   },\n" +
                "   \"results\" : [\n" +
                "      {\n" +
                "         \"address_components\" : [\n" +
                "            {\n" +
                "               \"long_name\" : \"34\",\n" +
                "               \"short_name\" : \"34\",\n" +
                "               \"types\" : [ \"street_number\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"Fien de la Mar Straat\",\n" +
                "               \"short_name\" : \"Fien de la Mar Straat\",\n" +
                "               \"types\" : [ \"route\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"Amsterdam Nieuw-West\",\n" +
                "               \"short_name\" : \"Amsterdam Nieuw-West\",\n" +
                "               \"types\" : [ \"political\", \"sublocality\", \"sublocality_level_1\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"Amsterdam\",\n" +
                "               \"short_name\" : \"Amsterdam\",\n" +
                "               \"types\" : [ \"locality\", \"political\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"Amsterdam\",\n" +
                "               \"short_name\" : \"Amsterdam\",\n" +
                "               \"types\" : [ \"administrative_area_level_2\", \"political\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"Noord-Holland\",\n" +
                "               \"short_name\" : \"NH\",\n" +
                "               \"types\" : [ \"administrative_area_level_1\", \"political\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"Holandia\",\n" +
                "               \"short_name\" : \"NL\",\n" +
                "               \"types\" : [ \"country\", \"political\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"1068 SG\",\n" +
                "               \"short_name\" : \"1068 SG\",\n" +
                "               \"types\" : [ \"postal_code\" ]\n" +
                "            }\n" +
                "         ],\n" +
                "         \"formatted_address\" : \"Fien de la Mar Straat 34, 1068 SG Amsterdam, Holandia\",\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 52.35222160000001,\n" +
                "               \"lng\" : 4.8094816\n" +
                "            },\n" +
                "            \"location_type\" : \"ROOFTOP\",\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 52.35357058029151,\n" +
                "                  \"lng\" : 4.810830580291502\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 52.35087261970851,\n" +
                "                  \"lng\" : 4.808132619708497\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"place_id\" : \"ChIJxUit8NTjxUcRvWePmBWfd4Q\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"9R25+VQ Amsterdam, Holandia\",\n" +
                "            \"global_code\" : \"9F469R25+VQ\"\n" +
                "         },\n" +
                "         \"types\" : [ \"street_address\" ]\n" +
                "      }\n" +
                "   ],\n" +
                "   \"status\" : \"OK\"\n" +
                "}";
    }

    @NotNull
    static String streetAddress() {
        // https://maps.googleapis.com/maps/api/geocode/json?address=Wroc%C5%82aw%20%C5%BBwirki%20i%20Wigury%207&key=AIzaSyDwmdKaSAfomiufOdIZ9Fyyd3gZbiFCYCs
        return "{\n" +
                "   \"results\" : [\n" +
                "      {\n" +
                "         \"address_components\" : [\n" +
                "            {\n" +
                "               \"long_name\" : \"7\",\n" +
                "               \"short_name\" : \"7\",\n" +
                "               \"types\" : [ \"street_number\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"Żwirki i Wigury\",\n" +
                "               \"short_name\" : \"Żwirki i Wigury\",\n" +
                "               \"types\" : [ \"route\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"Fabryczna\",\n" +
                "               \"short_name\" : \"Fabryczna\",\n" +
                "               \"types\" : [ \"political\", \"sublocality\", \"sublocality_level_1\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"Wrocław\",\n" +
                "               \"short_name\" : \"Wrocław\",\n" +
                "               \"types\" : [ \"locality\", \"political\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"Wrocław\",\n" +
                "               \"short_name\" : \"Wrocław\",\n" +
                "               \"types\" : [ \"administrative_area_level_2\", \"political\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"Dolnośląskie\",\n" +
                "               \"short_name\" : \"Dolnośląskie\",\n" +
                "               \"types\" : [ \"administrative_area_level_1\", \"political\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"Polska\",\n" +
                "               \"short_name\" : \"PL\",\n" +
                "               \"types\" : [ \"country\", \"political\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"54-620\",\n" +
                "               \"short_name\" : \"54-620\",\n" +
                "               \"types\" : [ \"postal_code\" ]\n" +
                "            }\n" +
                "         ],\n" +
                "         \"formatted_address\" : \"Żwirki i Wigury 7, 54-620 Wrocław, Polska\",\n" +
                "         \"geometry\" : {\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 51.097765,\n" +
                "               \"lng\" : 16.934523\n" +
                "            },\n" +
                "            \"location_type\" : \"ROOFTOP\",\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 51.09911398029149,\n" +
                "                  \"lng\" : 16.9358719802915\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 51.09641601970849,\n" +
                "                  \"lng\" : 16.9331740197085\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"place_id\" : \"ChIJ5xQhbbHBD0cRTAI5RicAdYs\",\n" +
                "         \"plus_code\" : {\n" +
                "            \"compound_code\" : \"3WXM+4R Wrocław, Polska\",\n" +
                "            \"global_code\" : \"9F3R3WXM+4R\"\n" +
                "         },\n" +
                "         \"types\" : [ \"street_address\" ]\n" +
                "      }\n" +
                "   ],\n" +
                "   \"status\" : \"OK\"\n" +
                "}";
    }

    @NotNull
    static String cityLevel() {
        // https://maps.googleapis.com/maps/api/geocode/json?latlng=52.352206,4.809561&location_type=ROOFTOP&result_type=street_address&key=AIzaSyDwmdKaSAfomiufOdIZ9Fyyd3gZbiFCYCs
        return "{\n" +
                "   \"results\" : [\n" +
                "      {\n" +
                "         \"address_components\" : [\n" +
                "            {\n" +
                "               \"long_name\" : \"Wrocław\",\n" +
                "               \"short_name\" : \"Wrocław\",\n" +
                "               \"types\" : [ \"locality\", \"political\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"Wrocław\",\n" +
                "               \"short_name\" : \"Wrocław\",\n" +
                "               \"types\" : [ \"administrative_area_level_2\", \"political\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"Dolnośląskie\",\n" +
                "               \"short_name\" : \"Dolnośląskie\",\n" +
                "               \"types\" : [ \"administrative_area_level_1\", \"political\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"Polska\",\n" +
                "               \"short_name\" : \"PL\",\n" +
                "               \"types\" : [ \"country\", \"political\" ]\n" +
                "            },\n" +
                "            {\n" +
                "               \"long_name\" : \"51\",\n" +
                "               \"short_name\" : \"51\",\n" +
                "               \"types\" : [ \"postal_code\", \"postal_code_prefix\" ]\n" +
                "            }\n" +
                "         ],\n" +
                "         \"formatted_address\" : \"Wrocław, Polska\",\n" +
                "         \"geometry\" : {\n" +
                "            \"bounds\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 51.2114736,\n" +
                "                  \"lng\" : 17.1763478\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 51.04268219999999,\n" +
                "                  \"lng\" : 16.80738\n" +
                "               }\n" +
                "            },\n" +
                "            \"location\" : {\n" +
                "               \"lat\" : 51.1078852,\n" +
                "               \"lng\" : 17.0385376\n" +
                "            },\n" +
                "            \"location_type\" : \"APPROXIMATE\",\n" +
                "            \"viewport\" : {\n" +
                "               \"northeast\" : {\n" +
                "                  \"lat\" : 51.2114736,\n" +
                "                  \"lng\" : 17.1763478\n" +
                "               },\n" +
                "               \"southwest\" : {\n" +
                "                  \"lat\" : 51.04268219999999,\n" +
                "                  \"lng\" : 16.80738\n" +
                "               }\n" +
                "            }\n" +
                "         },\n" +
                "         \"place_id\" : \"ChIJv4q11MLpD0cR9eAFwq5WCbc\",\n" +
                "         \"types\" : [ \"locality\", \"political\" ]\n" +
                "      }\n" +
                "   ],\n" +
                "   \"status\" : \"OK\"\n" +
                "}";
    }

    @NotNull
    static String zeroResults() {
        // https://maps.googleapis.com/maps/api/geocode/json?latlng=51.089644,16.970480&location_type=ROOFTOP&result_type=street_address&key=AIzaSyDwmdKaSAfomiufOdIZ9Fyyd3gZbiFCYCs
        return "{\n" +
                "   \"plus_code\" : {\n" +
                "      \"compound_code\" : \"3XQC+V5 Wrocław, Polska\",\n" +
                "      \"global_code\" : \"9F3R3XQC+V5\"\n" +
                "   },\n" +
                "   \"results\" : [],\n" +
                "   \"status\" : \"ZERO_RESULTS\"\n" +
                "}";
    }
}
