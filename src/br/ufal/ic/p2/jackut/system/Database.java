package br.ufal.ic.p2.jackut.system;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a simple database system for managing user accounts and sessions.
 * It provides methods for creating, finding, and managing user accounts, as well as handling
 * user sessions and data persistence.
 */

public class Database {
    private ArrayList<User> users;
    private ArrayList<Session> sessions;
    private int activeSessions = 0;


    /**
     * Constructs a Database object with initial user data imported from a file.
     */

    public Database() {
        this.users = (ArrayList<User>) importUsers("data.txt");
        this.sessions = new ArrayList<>();
    }

    /**
     * Creates a new user account with the provided login, password, and name.
     *
     * @param login    The login name for the new user.
     * @param password The password for the new user.
     * @param name     The name of the new user.
     * @throws RuntimeException if the login or password is invalid or if an account with the same login already exists.
     */

    public void newUser(String login, String password, String name){
        if(login == null) {
            throw new RuntimeException("Login inv�lido.");
        }

        if(password == null) {
            throw new RuntimeException("Senha inv�lida.");
        }

        for (User user : this.users) {
            if (user.getLogin().equals(login)) {
                throw new RuntimeException("Conta com esse nome j� existe.");
            }
        }

        User user = new User(login, password, name);
        this.users.add(user);
    }

    /**
     * Finds a user by their login.
     *
     * @param login The login of the user to find.
     * @return The User object matching the login.
     * @throws RuntimeException if no user with the given login is found.
     */

    public User findUser(String login) {
        for (User user : this.users) { // for each user in users
            if (user.getLogin().equals(login)) { // if user login is equal to login

                return user;
            }
        }

        throw new RuntimeException("Usu�rio n�o cadastrado."); // if user not found
    }

    /**
     * Starts a new session for a user with the provided login and password.
     *
     * @param login    The login of the user to start a session for.
     * @param password The password of the user.
     * @return The ID of the new session.
     * @throws RuntimeException if the login or password is invalid.
     */

    public int startSession(String login, String password){
        /**
         * Starts a new session for a user.
         * @param login The user's login.
         * @param password The user's password.
         * @return The session id.
         */

        try {
            User user = findUser(login); // find user by login
            if (user.matchPassword(password)) { // if user password is equal to password
                int id = activeSessions; // id is equal to activeSessions
                sessions.add(new Session(user, id)); // add new session to sessions
                activeSessions++;
                return id;
            } else {
                throw new RuntimeException("Login ou senha inv�lidos."); // if user password is not equal to password
            }
        } catch (RuntimeException error) {
            throw new RuntimeException("Login ou senha inv�lidos."); // if user not found
        }
    }


    /**
     * Flushes all user and session data from the database.
     */


    public void flush() {
        this.users = new ArrayList<>(); // flush users
        this.sessions = new ArrayList<>(); // flush sessions
        System.out.println("Flushed data.");
    }

    /**
     * Retrieves a specific attribute of a user.
     *
     * @param login     The login of the user.
     * @param attribute The attribute to retrieve ("nome" for the user's name).
     * @return The value of the requested attribute.
     */

    public String getUserAttribute(String login, String attribute) {
        User user = findUser(login); // find user by login
        if(Objects.equals(attribute, "nome")) return user.getName(); // if attribute is name

        return null;
    }

    /**
     * Exports user data to a file.
     */

    public void shutdown() {
        exportUsers();
    }

    /**
     * Imports user data from a file.
     *
     * @param filePath The path to the file containing user data.
     * @return A list of User objects imported from the file.
     */

    public static List<User> importUsers(String filePath) {
        List<User> data = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    String login = parts[0];
                    String name = parts[1];
                    String password = parts[2];
                    data.add(new User(login, password, name));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (data.isEmpty()) {
            data = new ArrayList<>();
        }

        return data;
    }
    public void exportUsers() {

        try (FileWriter fileWriter = new FileWriter("data.txt")) {
            for (User user : this.users) {
                fileWriter.write(
                        user.getLogin() + ';' +
                                user.getName() + ';' +
                                user.getPassword() +
                                System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
