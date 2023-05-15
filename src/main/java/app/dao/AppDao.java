package app.dao;

import app.user.User;

import java.util.List;

public interface AppDao {

    void createUsersTable();

    void dropUsersTable();

    void saveUser(String name, String email, int age);

    void saveUser(User user);

    User findUser(long id);

    void removeUserById(long id);

    List<User> getAllUsers();

    void cleanUsersTable();
}
