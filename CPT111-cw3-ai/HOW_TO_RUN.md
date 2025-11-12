# How to Run the Project

## Method 1: Using Batch Files (Windows)

### Step 1: Compile the Project
Double-click `compile.bat` or run it from command prompt:
```bash
compile.bat
```

This will compile all Java files in the `src` directory and place the compiled `.class` files in the project root directory.  
如果你使用 JDK 11 及以上版本并单独安装了 JavaFX SDK，请把环境变量 `JAVAFX_HOME` 指向 SDK 的根目录（包含 `lib` 子目录），`compile.bat` 会自动为你添加模块路径。

### Step 2: Run the Program
Double-click `run.bat` or run it from command prompt:
```bash
run.bat
```
`run.bat` 将启动 JavaFX 图形界面版本 (`MovieApp`)。  
若 GUI 启动失败或你想继续使用命令行界面，可执行：
```bash
java Main
```

## Method 2: Manual Commands (Windows Command Prompt)

### Step 1: Open Command Prompt
1. Press `Win + R`
2. Type `cmd` and press Enter
3. Navigate to the project directory:
```bash
cd C:\Users\30660\Desktop\CPT111-cw3
```

### Step 2: Compile the Project
```bash
javac -encoding UTF-8 -d . src\*.java
```

If you are using JDK 11+ with a standalone JavaFX SDK, first set `JAVAFX_HOME`, then compile with:
```bash
javac --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls -encoding UTF-8 -d . src\*.java
```

If compilation is successful, you should see no error messages.

### Step 3: Run the Program
```bash
java Main
```
To launch the JavaFX GUI manually (JDK 11+):
```bash
java --module-path "%JAVAFX_HOME%\lib" --add-modules javafx.controls MovieApp
```

## Method 3: Manual Commands (PowerShell)

### Step 1: Open PowerShell
1. Press `Win + X` and select "Windows PowerShell"
2. Navigate to the project directory:
```powershell
cd C:\Users\30660\Desktop\CPT111-cw3
```

### Step 2: Compile the Project
```powershell
javac -encoding UTF-8 -d . src\*.java
```
对于使用独立 JavaFX SDK 的 JDK 11+：
```powershell
javac --module-path "$env:JAVAFX_HOME\lib" --add-modules javafx.controls -encoding UTF-8 -d . src\*.java
```

### Step 3: Run the Program
```powershell
java Main
```
或运行 GUI：
```powershell
java --module-path "$env:JAVAFX_HOME\lib" --add-modules javafx.controls MovieApp
```

## Troubleshooting

### Issue: "javac is not recognized"
**Solution**: Java is not installed or not in PATH. 
- Install Java JDK from https://www.oracle.com/java/technologies/downloads/
- Or add Java to your system PATH

### Issue: "JavaFX runtime components are missing" / "java.lang.NoClassDefFoundError: javafx"
**Solution**: JavaFX 库未加载。请从 https://gluonhq.com/products/javafx/ 下载 JavaFX SDK，将其解压后把环境变量 `JAVAFX_HOME` 指向 SDK 根目录，然后使用提供的 `compile.bat` 与 `run.bat`（或在命令行中手动添加 `--module-path` 与 `--add-modules javafx.controls` 参数）。

### Issue: "Error: Unable to load data files"
**Solution**: Make sure the data files exist at:
- `CW3_Data_Files\data\movies.csv`
- `CW3_Data_Files\data\users.csv`

### Issue: "Could not find or load main class Main"
**Solution**: 
1. Make sure you compiled the project first
2. Make sure you're in the project root directory when running `java Main`
3. Check that `Main.class` exists in the current directory

### Issue: Batch file doesn't work
**Solution**: Use Method 2 or Method 3 (manual commands) instead

### JavaFX SDK Quick Setup (JDK 11+)
1. 从 https://gluonhq.com/products/javafx/ 下载与你 JDK 版本匹配的 JavaFX SDK。
2. 解压至任意目录（例如 `C:\javafx-sdk-22`）。
3. 将环境变量 `JAVAFX_HOME` 设置为该目录。
4. 重新运行 `compile.bat` 和 `run.bat`，或在命令行中使用上文示例中的 `--module-path` 与 `--add-modules javafx.controls` 参数。

## Pre-configured User Accounts

You can login with any of these accounts:
- Username: `alice`, Password: `alice123`
- Username: `bob`, Password: `bob123`
- Username: `charlie`, Password: `charlie123`
- Username: `diana`, Password: `diana123`
- Username: `eric`, Password: `eric123`

## Project Structure

```
CPT111-cw3/
├── src/                    # Source code directory
│   ├── Main.java
│   ├── Movie.java
│   ├── User.java
│   ├── Watchlist.java
│   ├── History.java
│   ├── RecommendationEngine.java
│   ├── FileManager.java
│   └── MovieApp.java            # JavaFX GUI entry point
├── CW3_Data_Files/
│   └── data/
│       ├── movies.csv      # Movie data
│       └── users.csv       # User data
├── compile.bat            # Compilation script
├── run.bat                # Execution script
└── Main.class             # Compiled class (after compilation)
```


