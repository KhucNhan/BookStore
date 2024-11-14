package com.example.book_store;

public class Test {
    public static void main(String[] args) {
        System.out.println(validateAddress("1234234"));
    }

    public static boolean validateAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }

        return address.matches(".*[a-zA-Z]+.*");
    }

    public static boolean validateName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        return name.matches("[a-zA-Z\\s]+");
    }
}
