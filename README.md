

### 🟤 成员 1: 团队负责人 & 架构师 (Team Lead & Architect)

**核心职责：** 搭建项目骨架，定义所有类与方法的接口，负责 UI 流程控制和最终的模块集成。

**💻 开发任务 (Code):**
* **定义类骨架：** 这是最优先的任务。创建所有必需类的 `.java` 文件（`Movie`, `User`, `Watchlist`, `History`, `RecommendationEngine`），并写好所有必需的属性和方法签名（空方法体）。这将作为团队其他成员的工作基础。
* **主程序 `Main.java`：** 编写程序的 `main` 方法入口。
* **UI 菜单控制 (Menu Class)：**
    * 实现“未登录”菜单（选项：登录、退出）。
    * 实现“登录后”菜单（选项：浏览、添加、移除、查看等）。
    * 处理所有菜单的用户输入（使用 `Scanner`）和基本的输入验证。
* **最终集成：** 将所有其他成员完成的功能模块（如登录逻辑、文件读取、列表操作等）“插入”到对应的菜单选项中。

**✍️ 报告任务 (Report):**
* **系统设计和类结构 (System design and class structure)** ：
    * 绘制一张 UML 类图。
    * 详细描述为什么这样设计类（例如，为什么 `User` 类要包含一个 `Watchlist` 对象，而不是一个 `ArrayList`），以及这如何体现了封装性等 OOP 原则 。
* **软件工程团队管理实践 (Software engineering team management practices)** ：
    * 描述你们团队是如何进行分工的（即本计划）。
    * 描述你们的沟通方式（例如，使用微信群、Tencent Docs）和代码合并策略。

---

### 🔵 成员 2: 数据模型 & 持久化 (Data Model & Persistence)

**核心职责：** 负责所有数据的**读取**（I/O），以及核心数据对象（`Movie`）的实现。

**💻 开发任务 (Code):**
* **`Movie.java` 实现：**
    * 根据规范 完整实现 `Movie` 类（属性：ID, Title, Genre, Year, Rating）。
    * 包含构造函数 (constructor)、所有属性的 getters 方法，以及一个用于打印的 `toString()` 方法。
* **数据加载器 (Utility Class)：**
    * `loadMovies(String filePath)`：编写一个方法，用于在程序启动时读取 `movies.csv` ，为每一行创建一个 `Movie` 对象，并返回一个 `ArrayList<Movie>` 。
    * `loadUsers(String filePath)`：编写一个方法，用于读取 `users.csv` ，为每一行创建一个 `User` 对象（这需要依赖成员 3 的 `User` 类和成员 4 的 `Watchlist` / `History` 类），并返回一个 `ArrayList<User>` 或 `HashMap<String, User>` 。
* **实现 "Browse movies" 功能** ：编写一个简单的方法，遍历 `ArrayList<Movie>` 并将其打印到控制台。

**✍️ 报告任务 (Report):**
* **异常处理机制 (Mechanisms for handling exceptions)** ：
    * 这是你的核心报告任务。由于你负责文件 I/O ，你需要详细说明你是如何处理 `FileNotFoundException`, `IOException` 的。
    * 还需说明如何处理数据格式错误（例如 `NumberFormatException`（当 "Year" 不是数字时）或 `ArrayIndexOutOfBoundsException`（当 CSV 某行数据缺失时））。

---

### 🟡 成员 3: 用户 & 认证 (User & Authentication)

**核心职责：** 负责用户身份验证逻辑，以及将更新后的用户数据**写回**（保存）到文件。

**💻 开发任务 (Code):**
* **`User.java` 实现：**
    * 根据规范 完整实现 `User` 类（属性：Username, Password, Watchlist, History）。
    * `Watchlist` 和 `History` 属性应该是成员 4 实现的 `Watchlist` 和 `History` 对象。
    * 包含构造函数和所有必要的方法（如 `checkPassword()`, `getWatchlist()` 等）。
* **认证逻辑：**
    * 实现 "Login" 功能 ：接受用户名和密码，在 `ArrayList<User>`（来自成员 2）中查找匹配项。
    * 实现 "Logout" 功能 ：将当前登录的 `User` 对象设为 `null`。
* **数据保存 (Utility Class)：**
    * `saveUsers(ArrayList<User> users, String filePath)`：编写一个关键方法，在程序退出或用户登出时，将*内存中*所有 `User` 对象的*最新*数据（包括他们修改过的 `Watchlist` 和 `History`）转换成 CSV 格式，并**覆盖**写回到 `users.csv` 文件中 。

**✍️ 报告任务 (Report):**
* **伦理评估 (Ethical assessment)** ：
    * 由于你处理用户密码和个人数据（观看历史），你最适合撰写此部分。
    * 讨论：1. 将密码明文存储在 CSV 中的巨大隐私风险。 2. 观看历史被泄露的潜在问题。 3. 推荐系统可能被滥用（例如产生偏见）。
    * 提出解决方案（例如，使用哈希加密密码——这也是一个高级功能选项 ）。

---

### 🟢 成员 4: 核心功能 (Core Features)

**核心职责：** 实现用户与电影数据交互的所有核心功能（观看列表和历史记录）。

**💻 开发任务 (Code):**
* **`Watchlist.java` 实现：**
    * 完整实现 `Watchlist` 类。其内部很可能包含一个 `ArrayList<Movie>` 。
    * 提供 `addMovie(Movie m)`, `removeMovie(Movie m)`, `getMovies()` 等方法。
* **`History.java` 实现：**
    * 完整实现 `History` 类。内部也包含一个 `ArrayList<Movie>`。
    * 提供 `addWatchedMovie(Movie m)`, `getHistory()` 等方法。
* **实现菜单功能逻辑：**
    * "Add movie to watchlist" 
    * "Remove movie from watchlist" 
    * "View watchlist" 
    * "View history" 
    * **"Mark movie as watched"** ：这个功能最复杂，它必须：1. 将电影添加到 `History` 对象。 2. **同时**从 `Watchlist` 对象中移除该电影（如果存在）。

**✍️ 报告任务 (Report):**
* **测试方法和结果 (Testing methods and results)** ：
    * 你实现了最多可独立测试的单元。你需要描述如何测试你的功能。
    * **单元测试**：例如，"测试 1：向空列表添加电影，列表大小应为 1"。 "测试 2：移除一个不存在的电影，程序不应崩溃"。
    * **集成测试**：例如，"测试 3：测试‘标记已看’功能，验证电影是否已加入 History *并* 已从 Watchlist 移除"。

---

### 🟣 成员 5: 推荐 & 高级功能 (Recommender & Advanced Features)

**核心职责：** 实现项目的“智能”部分（推荐）和必须的扩展功能。

**💻 开发任务 (Code):**
* **`RecommendationEngine.java` 实现** ：
    * 实现 `getRecommendations(User user, ArrayList<Movie> allMovies, int N)` 方法。
    * 实现一个简单的推荐算法 。例如：
        1.  访问 `user.getHistory()`（来自成员 4）。
        2.  统计用户看过电影中，出现次数最多的**类型 (Genre)** 。
        3.  遍历 `allMovies`（来自成员 2），找出该类型下评分最高的 N 部电影。
        4.  确保推荐的电影不在用户的 `History` 或 `Watchlist` 中。
* **实现 "Get recommendations" 功能** ：调用上述方法并打印结果。
* **实现至少一项高级功能** ：
    * 建议选择一个工作量明确的，例如：**"Functionality for creating a new user account"** 。
    * 这需要你：1. 提示输入新用户名/密码。 2. 检查用户名是否已存在（遍历 `ArrayList<User>`）。 3. 创建新的 `User` 对象（使用空列表）。 4. 将新用户添加到 `ArrayList<User>` 中（以便成员 3 的 `saveUsers` 方法能将其存盘）。

**✍️ 报告任务 (Report):**
* **推荐算法描述 (Description of the recommendation algorithm)** ：
    * 用清晰的步骤（如上文 1-4）或流程图来解释你的推荐逻辑是如何工作的 。
* **最终报告排版 & 截图**：
    * 收集所有 4 名成员撰写的报告部分。
    * 统一格式（确保字体、页边距、行距 符合要求）。
    * 运行整个程序，截取所有关键功能的**执行截图 (Example screenshots)** 。
    * 将所有内容合并，并导出为**最终的 PDF** 。
