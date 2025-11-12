import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;

/**
 * 用户管理类，负责用户数据的加载、保存、登录、注册等
 */
public class UserManager {
    private ArrayList<User> users = new ArrayList<>();
    private HashMap<String, User> userMap = new HashMap<>();

    /**
     * 从CSV文件加载所有用户
     */
    public void loadUsers(String filename) {
        users.clear();
        userMap.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = br.readLine(); // 跳过表头
            while ((line = br.readLine()) != null) {
                String[] arr = line.split(",", -1);
                if (arr.length >= 4) {
                    String username = arr[0].trim();
                    String password = arr[1].trim();
                    User user = new User(username, password);
                    // watchlist
                    if (!arr[2].isEmpty()) {
                        String[] wl = arr[2].split(";");
                        for (String id : wl) {
                            user.addMovieToWatchlist(id.trim());
                        }
                    }
                    // history
                    if (!arr[3].isEmpty()) {
                        String[] his = arr[3].split(";");
                        for (String h : his) {
                            String[] part = h.split("@");
                            if (part.length == 2) {
                                user.getHistory().add(new HistoryItem(part[0].trim(), part[1].trim()));
                            } else if(part.length == 1){
                                user.getHistory().add(new HistoryItem(part[0].trim(), ""));
                            }
                        }
                    }
                    users.add(user);
                    userMap.put(username, user);
                }
            }
        } catch (Exception e) {
            System.out.println("加载用户文件失败: " + e.getMessage());
        }
    }

    /**
     * 保存所有用户信息到CSV
     */
    public void saveUsers(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("username,password,watchlist,history");
            bw.newLine();
            for (User user : users) {
                // watchlist
                StringBuilder sbWatch = new StringBuilder();
                for (String w : user.getWatchlist()) {
                    if (sbWatch.length() > 0) sbWatch.append(";");
                    sbWatch.append(w);
                }
                // history
                StringBuilder sbHis = new StringBuilder();
                for (HistoryItem hi : user.getHistory()) {
                    if (sbHis.length() > 0) sbHis.append(";");
                    sbHis.append(hi.getMovieId() + "@" + hi.getDate());
                }
                bw.write(user.getUsername() + "," + ensureHashed(user.getPassword()) + "," + sbWatch + "," + sbHis);
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.println("保存用户文件失败: " + e.getMessage());
        }
    }

    /**
     * 登录（用户名密码验证），成功返回User对象，否则null
     */
    public User login(String username, String password) {
        User u = userMap.get(username);
        if (u != null && verifyPassword(password, u.getPassword())) {
            return u;
        }
        return null;
    }

    /**
     * 根据用户名查找用户
     */
    public User getUserByName(String username) {
        return userMap.get(username);
    }

    /**
     * 新建用户（注册功能），用户名不可重复，成功返回User对象
     */
    public User register(String username, String password) {
        if (userMap.containsKey(username)) return null;
        User newUser = new User(username, password);
        users.add(newUser);
        userMap.put(username, newUser);
        return newUser;
    }

    /**
     * 修改密码，返回是否成功
     */
    public boolean changePassword(String username, String oldPwd, String newPwd) {
        User u = userMap.get(username);
        if (u != null && verifyPassword(oldPwd, u.getPassword())) {
            // 这里只是简单替换明文，后面可加密
            u.setPassword(newPwd);
            return true;
        }
        return false;
    }

    // ===== 密码哈希与校验辅助 =====
    private String ensureHashed(String current) {
        if (current == null) return "";
        if (current.startsWith("sha256:")) return current; // 已是哈希
        return "sha256:" + sha256Hex(current);
    }

    private boolean verifyPassword(String inputRaw, String stored) {
        if (stored == null) return false;
        if (stored.startsWith("sha256:")) {
            String hex = stored.substring("sha256:".length());
            String calc = sha256Hex(inputRaw == null ? "" : inputRaw);
            return hex.equalsIgnoreCase(calc);
        } else {
            // 兼容旧数据（明文）
            return stored.equals(inputRaw);
        }
    }

    private String sha256Hex(String s) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(s.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
