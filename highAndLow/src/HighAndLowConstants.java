package highAndLow.src;

public final class HighAndLowConstants {
    // コンストラクタをprivateにすることで、外部からのインスタンス生成を禁止する
    // 定数のみを保持するため、インスタンス化は不要
    private HighAndLowConstants() {

    }

    // ゲーム設定関連の定数
    /** ゲーム開始時の手持ちコイン */
    public static final int INITIAL_GAME_COIN = 1000;
    /** ヒントを見るのに必要なコイン */
    public static final int HINT_COST = 10;
    /** カードの最小値 */
    public static final int CARD_MIN_VALUE = 1;
    /** カードの最大値 */
    public static final int CARD_MAX_VALUE = 13;
    /** 掛け金の最小値 定数に設定している値より大きい金額を掛け金として設定する */
    public static final int BET_MIN_VALUE = 0;

    // コインの増減倍率関連の定数
    /** 正解時は2倍 */
    public static final int WIN_MULTIPLIER = 2;
    /** 不正解時は1/2 */
    public static final int LOSING_DIVIDER = 2;

    // ボーナス関連の定数
    /** 連続正解ボーナス 1回目 */
    public static final int BONUS_WIN_COUNT_SMALL = 3;
    /** 連続正解ボーナス 2回目 */
    public static final int BONUS_WIN_COUNT_LARGE = 5;
    /** 連続正解ボーナス1回目のボーナスコインのレート */
    public static final int BONUS_RATE_SMALL = 2;
    /** 連続正解ボーナス2回目のボーナスコインのレート */
    public static final int BONUS_RATE_LARGE = 2;

    // ゲームロジック関連の定数
    /** ヒント使用時のCPUのカードの境界値 */
    public static final int CPU_HINT_THRESHOLD = 7;
    /** 運命のダイスで使用するサイコロの最小値 */
    public static final int DICE_MIN_VALUE = 1;
    /** 運命のダイスで使用するサイコロの最大値 */
    public static final int DICE_MAX_VALUE = 6;

    // メッセージ関連の定数（文字列）
    /** 大きい */
    public static final String CHOICE_HIGH = "1";
    /** 小さい */
    public static final String CHOICE_LOW = "2";
    /** はい */
    public static final String CHOICE_YES = "Y";
    /** いいえ */
    public static final String CHOICE_NO = "N";
    /** 緊急回避 */
    public static final String ITEM_EMERGENCY_AVOIDANCE = "緊急回避";
    /** 勝利の確約 */
    public static final String ITEM_GUARANTEED_WIN = "勝利の確約";
    /** 運命のダイス */
    public static final String ITEM_FATE_DICE = "運命のダイス";

    // エラーメッセージ関連の定数
    /** 数字以外が入力された時 */
    public static final String ERR_MSG_INVALID_NUMBER_FORMAT = "エラー: 無効な数値形式です。半角数字で入力してください。";
    /** 強制終了時 ユーザ向け */
    public static final String ERR_MSG_IO_ERROR_FATAL = "入出力にエラーが発生しました。" + "\n" + "ゲームを終了します";
    /** 強制終了時 開発者向け */
    public static final String ERR_MSG_PROGRAM_TERMINATED = "プログラムを終了します。";

}