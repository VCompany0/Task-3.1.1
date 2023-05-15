package app.user;

import app.dao.AppDao;

import java.util.List;

public class UserServiceImplementation implements UserService {
    private AppDao daoImplementation;

    public UserServiceImplementation(AppDao appDao) {
        daoImplementation = appDao;
    }

    @Override
    public void createUsersTable() {
        daoImplementation.createUsersTable();
    }

    @Override
    public void dropUsersTable() {
        daoImplementation.dropUsersTable();
    }

    @Override
    public void saveUser(String name, String email, int age) {
        daoImplementation.saveUser(name, email, age);
    }

    @Override
    public void saveUser(User user) {
        daoImplementation.saveUser(user);
    }

    @Override
    public User findUser(long id) {
        return daoImplementation.findUser(id);
    }

    @Override
    public void removeUserById(long id) {
        daoImplementation.removeUserById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return daoImplementation.getAllUsers();
    }

    @Override
    public void cleanUsersTable() {
        daoImplementation.cleanUsersTable();
    }
}
