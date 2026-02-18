<!--- @formatter:off --->
# SQLibrary

<div style="text-align: center;">

![Version](https://img.shields.io/github/v/release/huanmeng-qwq/SQLibrary?style=plastic)
![Code-Size](https://img.shields.io/github/languages/code-size/huanmeng-qwq/SQLibrary?style=plastic)
![Repo-Size](https://img.shields.io/github/repo-size/huanmeng-qwq/SQLibrary?style=plastic)
![License](https://img.shields.io/github/license/huanmeng-qwq/SQLibrary?style=plastic)
![Language](https://img.shields.io/github/languages/top/huanmeng-qwq/SQLibrary?style=plastic)
![Last-Commit](https://img.shields.io/github/last-commit/huanmeng-qwq/SQLibrary?style=plastic)
</div>

轻量级`ORM`库，**Java对象到关系数据库数据的映射**，支持Java8及以上版本

## 功能
* 实用性：可以在任何Java项目上运行，不局限于单一框架内
* 灵活性：支持多种数据库，如`MySQL`、`SQLite`、`H2`等**SQL关系型数据库**
* 兼容性：支持`Java8`及以上版本，兼容`Java11`、`Java18`等最新版本
* 便捷性：默认提供CRUD操作，满足大部分场景的需求
* 扩展性：支持自定义`SQL`语句，不局限在默认的CRUD
* 易用性：API简单易用，无需学习复杂的使用方法
* 可读性：支持链式调用，代码简洁易读
* 安全性：支持`SQL`注入防护，防止恶意注入
* 可靠性：支持事务操作，保证数据的完整性
* 轻量性：非常小（包括JavaDoc大约在3k行左右）

## 示例代码

您可以 [点击这里](src/test/java/me/huanmeng/util/sql/SQLibraryTest.java) 查看部分代码演示 。

## 依赖方式

已发布到Maven中心仓库，无需额外添加repository信息
<details>
<summary>Maven依赖</summary>

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.2.3</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <relocations>
                    <relocation>
                        <pattern>me.huanmeng.opensource.bukkit.gui</pattern>
                        <!-- 将 'com.yourpackage' 替换为你的包名 -->
                        <shadedPattern>com.yourpackage.gui</shadedPattern>
                    </relocation>
                </relocations>
            </configuration>
        </plugin>
    </plugins>
</build>

<dependencies>
    <dependency>
        <groupId>com.huanmeng-qwq</groupId>
        <artifactId>SQLibrary</artifactId>
        <version>2.2.10</version>
    </dependency>
</dependencies>
```

</details>

<details>
<summary>Gradle依赖</summary>

```groovy
plugins {
    id 'com.github.johnrengelman.shadow' version '8.1.1'
}

repositories {
    mavenCentral()
}

dependencies {
    api "com.huanmeng-qwq:SQLibrary:2.2.10"
}

shadowJar {
    // 将 'com.yourpackage' 替换为你的包名
    relocate 'me.huanmeng.opensource.bukkit.gui', 'com.yourpackage.huanemng.sqlibrary'
}
```

</details>
