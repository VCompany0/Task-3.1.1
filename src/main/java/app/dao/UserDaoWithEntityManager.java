package app.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.jpa.boot.spi.PersistenceUnitDescriptor;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import app.user.User;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Repository
public class UserDaoWithEntityManager implements AppDao {
    public static String TABLE_NAME = "users_table";
    private EntityManager manager = createEntityManager("jdbc:postgresql://localhost:5432/postgres", "postgres","649553934");

    @Override
    public void createUsersTable() {
        doInTransaction(() -> {
            String sql = "CREATE TABLE  IF NOT EXISTS " + TABLE_NAME
                    + "(id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name text, email text, age int);";
            manager.createNativeQuery(sql).executeUpdate();
        });
    }

    @Override
    public void dropUsersTable() {
        doInTransaction(() -> {
            String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
            manager.createNativeQuery(sql).executeUpdate();
        });
    }

    @Override
    public void saveUser(String name, String email, int age) {
        doInTransaction(() -> manager.persist(new User(name, email, age)));
    }

    @Override
    public void saveUser(User user) {
        User oldUser = findUser(user.getId());
        doInTransaction(()->{
            if(oldUser != null){
                oldUser.setName(user.getName());
                oldUser.setEmail(user.getEmail());
                oldUser.setAge(user.getAge());
            }
            else {
                manager.persist(user);
            }
        });
    }

    @Override
    public User findUser(long id) {
        return manager.find(User.class, id);
    }

    @Override
    public void removeUserById(long id) {
        doInTransaction(() -> {
            User user = findUser(id);
            manager.remove(user);
        });
    }

    @Override
    public List<User> getAllUsers() {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(root);
        TypedQuery<User> query = manager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    public void cleanUsersTable() {
        doInTransaction(() -> manager.createNativeQuery("TRUNCATE " + TABLE_NAME).executeUpdate());

    }

    private void doInTransaction(Runnable runnable) {
        manager.getTransaction().begin();
        runnable.run();
        manager.getTransaction().commit();
    }

    private EntityManager createEntityManager(String dbUrl, String dbUser, String dbPassword) {

        MutablePersistenceUnitInfo mutablePersistenceUnitInfo = new MutablePersistenceUnitInfo() {
            @Override
            public ClassLoader getNewTempClassLoader() {
                return null;
            }
        };

        mutablePersistenceUnitInfo.setPersistenceUnitName("TestUnit");
        mutablePersistenceUnitInfo.setPersistenceProviderClassName(HibernatePersistenceProvider.class.getName());

        Properties props = new Properties();
        props.put("hibernate.connection.url", dbUrl);
        props.put("hibernate.connection.username", dbUser);

        if (StringUtils.hasText(dbPassword)) {
            props.put("hibernate.connection.password", dbPassword);
        }

        mutablePersistenceUnitInfo.setProperties(props);

        mutablePersistenceUnitInfo.addManagedClassName(User.class.getName());

        PersistenceUnitDescriptor persistenceUnitDescriptor = new PersistenceUnitInfoDescriptor(
                mutablePersistenceUnitInfo);

        EntityManagerFactoryBuilder entityManagerFactoryBuilder = new EntityManagerFactoryBuilderImpl(
                persistenceUnitDescriptor, Collections.EMPTY_MAP);

        EntityManagerFactory entityManagerFactory = entityManagerFactoryBuilder.build();

        return entityManagerFactory.createEntityManager();

    }
}
