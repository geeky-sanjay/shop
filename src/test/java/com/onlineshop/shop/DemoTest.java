package com.onlineshop.shop;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class DemoTest {
    @Test
    public void addition(){
        List<String> expected = Arrays.asList("sanjay", "vinod");
        List<String> actial = Arrays.asList("sanjay", "vinod");
        Assertions.assertLinesMatch(expected,actial);

    }
}
