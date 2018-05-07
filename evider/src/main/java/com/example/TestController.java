package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private MySQLConnect db = new MySQLConnect();
    private Connection cxn = null;
    private static final String version = "/api/v1/";

    @RequestMapping(version + "venues")
    public String getVenues2() {
        return "{\"foo\" : \"bar\", \"from\" : \"venues\"}";
    }

    private String executeQueryAndPrintResult(String sql) {
        SimpleModule module = new SimpleModule();
        module.addSerializer(new ResultSetSerializer());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);

        ResultSet result = null;
        Statement statement = null;

        try {
            statement = cxn.createStatement();
            result = statement.executeQuery(sql);
        } catch (SQLException e) {
            return IOHelper.writeException(e);
        }

        ObjectNode objectNode = objectMapper.createObjectNode();
        // put the resultset in a containing structure
        objectNode.putPOJO("results", result);

        // generate all
        StringWriter sw = new StringWriter();
        try {
            objectMapper.writeValue(sw, objectNode);
        } catch (IOException e) {
            return IOHelper.writeException(e);
        }

        return sw.toString();
    }

    @RequestMapping("/")
    public String troll() {
        return "All your scrum are belong to us";
    }

    @RequestMapping("/venues")
    public String getVenues() {
        cxn = db.connect();
        String sql = "SELECT * FROM venues";
        String json = this.executeQueryAndPrintResult(sql);
        db.disconnect();
        return json;
    }

    @RequestMapping("/events")
    public String getEvents() {
        cxn = db.connect();
        String sql = "SELECT events.id, events.name, venues.name FROM events INNER JOIN venues ON events.venue_id = venues.id";
        String json = this.executeQueryAndPrintResult(sql);
        db.disconnect();
        return json;
    }

    @CrossOrigin
    @RequestMapping("/jwtSharp")
    public String jwtTest(@RequestHeader("Authorization") String authHeader) {
        if (authHeader.length() < 7) {
            return "Invalid Authorization header!";
        }
        if (!authHeader.substring(0, 7).equals("Bearer ")) {
            return "Malformed Authorization header";
        }
        JWTDecoder jwtDecoder = new JWTDecoder();
        boolean decoded = jwtDecoder.decode(authHeader.substring(7));
        if (decoded) {
            return "{\"result\" : \"success\"}";
        } else {
            return "{\"result\" : \"fail\"}";
        }
    }

    @RequestMapping("/jwt")
    public String jwtTest() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJpb25pYy1hcHAifQ.6USP3K3hKsmkU17W4u8iCuRHSXmL50P51vgLdDj8sLU";
        JWTDecoder jwtDecoder = new JWTDecoder();
        boolean decoded = jwtDecoder.decode(token);
        if (decoded) {
            return "{\"result\" : \"success\"}";
        } else {
            return "{\"result\" : \"fail\"}";
        }
    }

    @RequestMapping("/test2")
    public String test2() {
        return "This is just another test";
    }

    @RequestMapping("/test3")
    public String jTest() {
        return "This is test";
    }
}
