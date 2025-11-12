# 电影推荐与追踪系统（命令行版本）

本项目为 Coursework 3：Movie Recommendation & Tracker System。后端逻辑完全基于纯 Java SE（不依赖第三方库），数据使用 CSV 文件；当前仓库仅保留命令行（CLI）版本，GUI 暂不包含。

## 功能概览
- 登录、注册新用户、修改密码
- 浏览电影列表（读取 `movies.csv`）
- 待看清单：添加、移除、查看
- 观影历史：标记已观看（记录当天日期）、查看历史
- 推荐功能（三种策略）：
  - 基于历史的最常观看类型（评分降序，取前 N）
  - 指定年份（含）之后的高分电影（评分降序，取前 N）
  - 按最低评分阈值筛选（评分降序，取前 N）

## 目录结构
- `Main.java`：命令行入口与菜单
- `fx/FxApp.java`：JavaFX GUI 入口与界面（登录页 + 主界面）
- `Movie.java`、`MovieManager.java`：电影实体与 CSV 加载
- `User.java`、`UserManager.java`：用户实体、CSV 读写与认证（保存时进行 SHA‑256 密码哈希）
- `HistoryItem.java`、`History.java`、`Watchlist.java`：历史与待看清单数据结构
- `RecommendationEngine.java`：推荐算法实现
- 数据文件：
  - `CW3_Data_Files/data/movies.csv`
  - `CW3_Data_Files/data/users.csv`

## 运行环境
- JDK 8+（命令行版本）

## 命令行运行
- 在项目根目录（与 `Main.java` 同级）执行：
  - 编译：
    - `javac *.java`
  - 运行：
    - `java Main`
- 程序运行时从 `CW3_Data_Files/data` 读取 CSV；登出或退出时写回 `users.csv`。

提示：IDEA可直接运行。

## CSV 格式
- `users.csv`：`username,password,watchlist,history`
  - `watchlist`：分号分隔电影 ID（如 `M008;M015`）
  - `history`：分号分隔 `movieId@YYYY-MM-DD`（如 `M001@2025-07-12;M011@2025-08-10`）
- `movies.csv`：`id,title,genre,year,rating`

## 常见问题
- 为什么注册后 CSV 没更新？
  - 请确保工作目录是项目根（例如 `d:\Film`），并且不要用 Excel 打开占用 `users.csv`；登出或退出会触发保存。

## 限制与说明
- 仅使用基础 Java 标准库（`java.util`, `java.io`, `java.time`, `java.lang`）与 JavaFX（可选）。
- 不使用任何第三方库、框架或外部 JAR。
