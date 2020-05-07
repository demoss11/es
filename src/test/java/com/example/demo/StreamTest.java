package com.example.demo;

import com.example.demo.domain.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamTest {
    @Test
    public void test001(){
        List<User> users = new ArrayList<>();
        users.add(new User("111","222","333", LocalDate.now()));
        users.add(new User("1112","222","333", LocalDate.now()));
        users.add(new User("1113","222","333", LocalDate.now()));
        users.add(new User("1114","222","333", LocalDate.now().minusDays(1)));
        users.add(new User("1115","222","333", LocalDate.now().minusDays(1)));
        users.add(new User("1118","222","333", LocalDate.now().minusDays(2)));
        users.add(new User("1119","222","333", LocalDate.now().minusDays(2)));
        users.add(new User("11110","222","333", LocalDate.now().minusDays(3)));
        users.add(new User("11114","222","333", LocalDate.now().minusDays(3)));
        users.add(new User("11115","222","333", LocalDate.now().minusDays(4)));
        users.add(new User("11117","222","333", LocalDate.now().minusDays(4)));
        users.add(new User("11121","222","333", LocalDate.now().minusDays(5)));

        Map<LocalDate, List<User>> collect = users.stream()
                .collect(Collectors.groupingBy(User::getRegisterTime));
        List<User> userList = collect.getOrDefault(LocalDate.now(), Collections.emptyList());
        userList.forEach(System.out::println);

    }
}
