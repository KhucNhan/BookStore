package com.example.book_store;

public class Test {
    public static void main(String[] args) {
        System.out.println(validateAddress("1111"));
    }

    public static boolean validateAddress(String address) {
        if (address == null || address.isEmpty()) {
            return false;
        }

        return address.matches(".*[a-zA-Z]+.*");
    }
}
