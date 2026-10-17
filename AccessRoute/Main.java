public class Main {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}

import java.util.Scanner;

public class Main {
    // Define a method to greet a user
    public static void greetUser(String name) {
        System.out.println("Hello, " + name + "!");
    }

    // Main program flow
    public static void main(String[] args) {
        Scanner input = new Scanner(System.class.in);
        
        System.out.print("Enter your name: ");
        String userName = input.nextLine();
        
        greetUser(userName);
        
        input.close();
    }
}
