import app.dao.UserDaoWithEntityManager;
import org.junit.Assert;
import org.junit.Test;
import app.user.User;
import app.user.UserService;
import app.user.UserServiceImplementation;

import java.util.List;

public class UserServiceTest {
    private final UserService userService = new UserServiceImplementation(new UserDaoWithEntityManager());
    private final String testName = "Ivan";
    private final String testEmail = "iva@mail.comcom";
    private final byte testAge = 5;

    @Test
    public void dropUsersTable() {
        try {
            userService.dropUsersTable();
        } catch (Exception e) {
            Assert.fail("При тестировании удаления таблицы произошло исключение\n" + e);
        }
    }

    @Test
    public void createUsersTable() {
        try {
            userService.dropUsersTable();
            userService.createUsersTable();
        } catch (Exception e) {
            Assert.fail("При тестировании создания таблицы пользователей произошло исключение\n" + e.getMessage());
        }
    }

    @Test
    public void saveUser() {
        try {
            userService.dropUsersTable();
            userService.createUsersTable();
            userService.saveUser(testName, testEmail, testAge);
            userService.saveUser("Vyacheslav", "Taranov", (byte) 21);
            User user = userService.getAllUsers().get(0);

            if (!testName.equals(user.getName())
                    || !testEmail.equals(user.getEmail())
                    || testAge != user.getAge()
            ) {
                Assert.fail("User был некорректно добавлен в базу данных");
            }

        } catch (Exception e) {
            Assert.fail("Во время тестирования сохранения пользователя произошло исключение\n" + e);
        }
    }

    @Test
    public void updateUser(){
        try {
            userService.dropUsersTable();
            userService.createUsersTable();
            User oldUser = new User("Name", "@mail", 23);

            userService.saveUser(oldUser);

            User newUser = new User( oldUser.getId(), "newName", "@mail", 27);
            userService.saveUser(newUser);

            User actual = userService.findUser(oldUser.getId());
            Assert.assertEquals(newUser, actual);
        } catch (Exception e) {
            Assert.fail("При попытке перезаписать пользователя возникло исключение\n" + e);
        }
    }

    @Test
    public void removeUserById() {
        try {
            userService.dropUsersTable();
            userService.createUsersTable();
            userService.saveUser(testName, testEmail, testAge);
            userService.removeUserById(1L);
        } catch (Exception e) {
            Assert.fail("При тестировании удаления пользователя по id произошло исключение\n" + e);
        }
    }

    @Test
    public void getAllUsers() {
        try {
            userService.dropUsersTable();
            userService.createUsersTable();
            userService.saveUser(testName, testEmail, testAge);
            List<User> userList = userService.getAllUsers();
            if (userList.size() != 1) {
                Assert.fail("Проверьте корректность работы метода сохранения пользователя/удаления или создания таблицы");
            }
        } catch (Exception e) {
            Assert.fail("При попытке достать всех пользователей из базы данных произошло исключение\n" + e);
        }
    }

    @Test
    public void cleanUsersTable() {
        try {
            userService.dropUsersTable();
            userService.createUsersTable();
            userService.saveUser(testName, testEmail, testAge);
            userService.cleanUsersTable();

            if (userService.getAllUsers().size() != 0) {
                Assert.fail("Метод очищения таблицы пользователей реализован не корректно");
            }
        } catch (Exception e) {
            Assert.fail("При тестировании очистки таблицы пользователей произошло исключение\n" + e);
        }
    }

    @Test
    public void attemptToGetNonExistentId(){
        userService.cleanUsersTable();
        User user = new User();
        try{
            user = userService.findUser(10000000000L);
        }catch (Exception e){
            Assert.fail("Getting non-existent user caused an exception: \n" + e);
        }
        System.out.println(user);
    }

}
