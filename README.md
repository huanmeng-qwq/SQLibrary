# SQLibrary

类似MyBatis的一个~~框架~~

但是是个 __简易版__

没有那么多什么事务之类的

实例代码

```java

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserData {
    @SQLField(id = true, isAutoIncrement = true)
    //dbId 主键 自增
    private int id;
    @SQLField(id = true)
    private UUID uuid;
    private List<String> data;

    /**
     * 如果存在setId这个方法则自动调用
     * 如不存在直接setField
     * 不想被调用该方法可以加上{@link me.huanmeng.util.sql.annotation.SQLIgnore}
     * @param id 主键id
     * @see me.huanmeng.util.sql.annotation.SQLIgnore
     */
    public void setId(int id) {
        this.id = id;
    }
}

```

``` java
        final SQLManagerImpl sqlManager = EasySQL.createManager(
                "com.mysql.cj.jdbc.Driver", "jdbc:mysql://localhost:3306/test", "root", "123456");
        sqlManager.setDebugMode(true);
        final SQLEntityInstance<UserData> instance = new SQLEntityInstance<>(UserData.class, sqlManager);
        final SQLEntityManagerImpl<UserData> manager = instance.getSqlEntityManager();
        //返回的是实例dbId会自动填上
        final UserData as = manager.insert(new UserData(-1, UUID.randomUUID(), new ArrayList<>()));
        System.out.println(data);
        manager.update(data);
```