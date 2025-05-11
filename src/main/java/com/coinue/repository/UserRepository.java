package com.coinue.repository;

import com.coinue.model.User;

import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问仓库
 */
public class UserRepository extends BaseRepository<User> {
    
    public UserRepository() {
        super("data/users.json");
    }
    
    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }
    
    /**
     * 保存用户
     * @param user 用户对象
     */
    public void save(User user) {
        // TODO: 实现数据验证
        // TODO: 实现原子性操作
        List<User> users = readAll();
        users.add(user);
        writeAll(users);
    }
    
    // /**
    //  * 更新用户
    //  * @param user 用户对象
    //  */
    // public void update(User user) {
    //     // TODO: 实现数据验证
    //     // TODO: 实现原子性操作
    //     List<User> users = readAll();
    //     for (int i = 0; i < users.size(); i++) {
    //         if (users.get(i).getId().equals(user.getId())) {
    //             users.set(i, user);
    //             writeAll(users);
    //             return;
    //         }
    //     }
    // }
    
    // /**
    //  * 通过ID查找用户
    //  * @param id 用户ID
    //  * @return 用户对象
    //  */
    // public Optional<User> findById(String id) {
    //     return readAll().stream()
    //             .filter(user -> user.getId().equals(id))
    //             .findFirst();
    // }
    
    /**
     * 通过用户名查找用户
     * @param username 用户名
     * @return 用户对象
     */
    public Optional<User> findByUsername(String username) {
        return readAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }
    
    // /**
    //  * 删除用户
    //  * @param id 用户ID
    //  */
    // public void delete(String id) {
    //     // TODO: 实现原子性操作
    //     List<User> users = readAll();
    //     users.removeIf(user -> user.getId().equals(id));
    //     writeAll(users);
    // }
    
    /**
     * 获取所有用户
     * @return 用户列表
     */
    public List<User> findAll() {
        return readAll();
    }
} 