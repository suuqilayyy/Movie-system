# 电影推荐与跟踪系统

## 项目概述
这是一个基于Java的电影推荐与跟踪系统，允许用户登录、管理观看列表、记录观看历史，并根据用户偏好接收电影推荐。

## 功能特性

### 核心功能
- ✅ 用户登录/登出
- ✅ 浏览所有电影
- ✅ 添加/移除电影到观看列表
- ✅ 查看观看列表
- ✅ 标记电影为已观看
- ✅ 查看观看历史
- ✅ 获取电影推荐（Top-N）

### 高级功能（已实现3个）
- ✅ 创建新用户账户
- ✅ 更改用户密码
- ✅ 多种推荐策略（按类型、按评分、按年份）

## 技术实现

### 类结构
- `Main.java` - 主程序，实现命令行菜单界面
- `Movie.java` - 电影类，存储电影信息
- `User.java` - 用户类，管理用户账户信息
- `Watchlist.java` - 观看列表类，管理用户的待观看电影
- `History.java` - 观看历史类，管理用户的观看记录
- `RecommendationEngine.java` - 推荐引擎，实现多种推荐策略
- `FileManager.java` - 文件管理器，处理CSV文件的读写

### 数据结构
- 使用 `HashMap` 存储电影和用户数据
- 使用 `ArrayList` 存储观看列表和观看历史

### 推荐算法
1. **按类型推荐（genre）**：基于用户观看最多的电影类型进行推荐
2. **按评分推荐（rating）**：推荐评分最高的电影
3. **按年份推荐（year）**：基于用户观看电影的平均年份推荐相似年份的电影

## 运行说明

### 编译项目
```bash
javac -d . src/*.java
```

### 运行程序
```bash
java Main
```

### 数据文件
- 电影数据：`CW3_Data_Files/data/movies.csv`
- 用户数据：`CW3_Data_Files/data/users.csv`

### 预置用户账户
- alice / alice123
- bob / bob123
- charlie / charlie123
- diana / diana123
- eric / eric123

## 使用说明

### 未登录菜单
1. **Login** - 使用用户名和密码登录
2. **Exit** - 退出程序

### 登录后菜单
1. **Browse movies** - 浏览所有电影
2. **Add movie to watchlist** - 添加电影到观看列表
3. **Remove movie from watchlist** - 从观看列表移除电影
4. **View watchlist** - 查看观看列表
5. **Mark movie as watched** - 标记电影为已观看
6. **View history** - 查看观看历史
7. **Get recommendations** - 获取推荐电影
8. **Change password** - 更改密码（高级功能）
9. **Create new account** - 创建新账户（高级功能）
10. **Change recommendation strategy** - 更改推荐策略（高级功能）
11. **Logout** - 登出

## 异常处理
- 文件读取/写入异常处理
- 无效输入验证
- 数据格式错误处理
- 空值检查

## 项目要求符合性

### 核心功能要求 ✅
- [x] 登录功能
- [x] 浏览电影
- [x] 观看列表管理
- [x] 观看历史管理
- [x] 电影推荐

### 技术要求 ✅
- [x] 面向对象设计（OOP）
- [x] 使用ArrayList和HashMap
- [x] 文件I/O处理
- [x] 异常处理
- [x] 仅使用课程涵盖的Java库

### 高级功能 ✅
- [x] 创建新用户账户
- [x] 更改用户密码
- [x] 多种推荐策略

## 作者
CPT111 课程作业3

## 日期
2025年

