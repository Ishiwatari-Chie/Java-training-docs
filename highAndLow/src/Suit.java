package highAndLow.src;

/**
 * 絵柄合わせミニゲームで使用するトランプの絵柄とボーナスコインを管理するenum
 */
public enum Suit {
    // クラブ: +100
    CLUBS("クローバー", 100),
    // ハート: +70
    HEARTS("ハート", 70),
    // ダイヤ: +25
    DIAMONDS("ダイヤ", 25),
    // スペード: +50
    SPADES("スペード", 50);

    // 絵柄名を表示
    private final String displayName;
    // 絵柄ごとのボーナススコアを追加
    private final int bonusCoin;

    Suit(String displayName, int bonusCoin) {
        this.displayName = displayName;
        this.bonusCoin = bonusCoin;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getBonusCoin() {
        return bonusCoin;
    }
}