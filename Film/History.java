import java.util.ArrayList;

/**
 * History 类：包装用户的观看历史
 * 内部使用 ArrayList<HistoryItem>
 */
public class History {
    private final ArrayList<HistoryItem> items;

    public History() {
        this.items = new ArrayList<>();
    }

    public ArrayList<HistoryItem> getAll() {
        return items;
    }

    /**
     * 添加一条历史记录
     */
    public void add(String movieId, String date) {
        items.add(new HistoryItem(movieId, date));
    }
}
