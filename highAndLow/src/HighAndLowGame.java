package highAndLow.src;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * ハイアンドローゲームを行うクラス
 */
public class HighAndLowGame {
    // 入力値
    static BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    // ユーザーが入手したアイテムを管理するリスト
    static List<String> userItemsList = new ArrayList<>();
    // 乱数の生成
    static Random rand = new Random();

    // 以下、アイテム効果を管理するための変数
    // 勝利の確約が使用されたかどうか。初期値は使用されていないのでfalse（アイテム効果：使用されたターンで必ず正解する）
    static boolean isGuaranteedWin = false;
    // 運命のダイスが使用されたかどうか。初期値はnull(アイテム効果：サイコロを振って大小を選択する)
    static String choiceByDice = null;
    // 緊急回避が使用されたかどうか。初期値は使用されていないのでfalse
    static boolean isEmergencyAvoidanceUsed = false;

    public static void main(String[] args) throws Exception {

        // 開始メッセージを表示
        System.out.println("ハイアンドローゲームスタート");

        // ループし、手持ちが0になるまでゲームを遊べるようにする
        Boolean isGameRunning = true;
        int gameCoin = HighAndLowConstants.INITIAL_GAME_COIN;

        // 正解数を記録し、連続正解数に応じてコインの増減を管理する
        int winCount = 0;

        while (isGameRunning) {
            // 現在の手持ちのコインの枚数を表示
            System.out.println("☆------------------★---------------☆");
            System.out.println("現在のゲームコイン：" + gameCoin + " 連続正解数：" + winCount);
            // アイテムを持っていれば表示
            if (!userItemsList.isEmpty()) {
                System.out.println("所持アイテム" + userItemsList);
            }
            System.out.println("☆------------------★---------------☆");

            // アイテム効果をリセット(アイテム効果が永続的に発揮されるのを防ぐ)
            isGuaranteedWin = false;
            choiceByDice = null;
            isEmergencyAvoidanceUsed = false;

            // 乱数を用意し、CPUとプレイヤーのカードを決定し表示
            int cpuCard = rand.nextInt(HighAndLowConstants.CARD_MAX_VALUE - HighAndLowConstants.CARD_MIN_VALUE + 1)
                    + HighAndLowConstants.CARD_MIN_VALUE;
            int playerCard = rand.nextInt(HighAndLowConstants.CARD_MAX_VALUE - HighAndLowConstants.CARD_MIN_VALUE + 1)
                    + HighAndLowConstants.CARD_MIN_VALUE;

            System.out.println("【あなた】" + playerCard + " vs ???? 【コンピュータ】");

            // プレイヤーにコンピュータのカードが大きいか小さいかを予想させる
            System.out.println("あなたのカードはコンピュータのカードより大きい？小さい？");

            // ヒントを見るかどうかの変数を保持する変数を用意
            String hintChoice = "";

            while (true) {
                // ヒントを見るかどうか確認
                System.out.println("ヒントを見ますか？ヒントを見るには、10コイン必要です" + "\n"
                        + "はい→" + HighAndLowConstants.CHOICE_YES + "/いいえ→" + HighAndLowConstants.CHOICE_NO);

                if (gameCoin < HighAndLowConstants.HINT_COST) {
                    System.out.println("コインが足りないためヒントが見れません");
                    // ループを抜ける
                    break;
                }
                try {
                    // 入力値受け取り
                    hintChoice = br.readLine();

                    // ヒントを見る場合
                    if (HighAndLowConstants.CHOICE_YES.equalsIgnoreCase(hintChoice)) {
                        hints(cpuCard);
                        // ゲームコインを10枚消費
                        gameCoin -= HighAndLowConstants.HINT_COST;

                        // 有効な入力のため、ループを抜ける
                        break;
                    } else if (HighAndLowConstants.CHOICE_NO.equalsIgnoreCase(hintChoice)) {

                        // 有効な入力のため、ループを抜ける
                        break;
                    }
                } catch (IOException e) {
                    System.out.println(
                            HighAndLowConstants.CHOICE_YES + "または" + HighAndLowConstants.CHOICE_NO + "を入力してください");
                }
            }

            // 「緊急回避」を除いたリストを作成
            List<String> selectableItemList = userItemsList.stream()
                    .filter(item -> !item.equals(HighAndLowConstants.ITEM_EMERGENCY_AVOIDANCE))
                    .collect(Collectors.toList());

            // 「緊急回避」を除いたリストにアイテムがあれば、アイテム一覧を表示し、使用するかどうか選択
            if (!selectableItemList.isEmpty()) {
                // 手持ちのアイテムを表示する
                System.out.println("選択できるアイテムは以下です");

                // 緊急回避を除いたリストに格納されているアイテムを表示
                for (String items : selectableItemList) {
                    System.out.println(items);
                }

                System.out.println("「緊急回避」は負けが確定した後に選択が可能なため表示されません");

                System.out.println("表示されているアイテムを使用しますか" + "\n"
                        + "はい→" + HighAndLowConstants.CHOICE_YES + "/いいえ→" + HighAndLowConstants.CHOICE_NO);

                if (HighAndLowConstants.CHOICE_YES.equalsIgnoreCase(br.readLine())) {
                    // アイテム使用メソッドへ
                    useItem(selectableItemList);
                }
            } else if (!userItemsList.isEmpty()) {
                // リストの中身が「緊急回避」だけの時のみに表示される
                System.out.println("「緊急回避」は負けが確定した後に選択が可能なため表示されていません");
            }

            // プレイヤーの最終的な選択を持つ変数
            String playerChoice = "";

            // 勝敗を管理するフラグ
            boolean isPlayerWin = false;

            // 勝利の確約使用時
            if (isGuaranteedWin) {
                isPlayerWin = true;
                System.out.println("アイテム「勝利の確約」の効果により、あなたの勝利が確定しました！");
                // 保守性の面から値を代入
                playerChoice = HighAndLowConstants.CHOICE_HIGH;
            }
            // 運命のダイス使用時
            else if (choiceByDice != null) {
                playerChoice = choiceByDice;
                System.out.println("運命のダイスにより、「" + (choiceByDice.equals("1") ? "大きい" : "小さい") + "」 "
                        + "を選択しました");
            }
            // アイテム未使用時
            else {
                while (true) {
                    System.out.println("1：大きい 2：小さい");
                    try {
                        playerChoice = br.readLine();

                        if (HighAndLowConstants.CHOICE_HIGH.equals(playerChoice)
                                || HighAndLowConstants.CHOICE_LOW.equals(playerChoice)) {
                            // 有効な入力のためループを抜ける
                            break;
                        } else {
                            // 入力が「1」または「2」以外の場合
                            System.out.println("1または2を入力してください");
                        }
                    } catch (IOException e) {
                        // ユーザー向けのメッセージ
                        System.err.println(HighAndLowConstants.ERR_MSG_IO_ERROR_FATAL);
                        // 開発、デバッグ用にコンソールに出力
                        e.printStackTrace();
                        // 異常終了
                        System.err.println(HighAndLowConstants.ERR_MSG_PROGRAM_TERMINATED);
                        System.exit(1);
                    }
                }
            }

            // 掛け金を設定する
            int bet = 0;
            while (true) {
                System.out.println("掛け金を設定してください");
                if (!isPlayerWin) {
                    // 「勝利の確約」未使用時のみ表示
                    System.out
                            .println("正解時は掛け金の2倍のコインを取得できます" + "\n" + "不正解時は手持ちのコインが1/2になります");
                }

                System.out.println("現在の手持ちコインは" + gameCoin + "枚です");

                try {

                    String inputBet = br.readLine();
                    bet = Integer.parseInt(inputBet);

                    // 1以上の金額を掛けているか確認
                    if (bet <= HighAndLowConstants.BET_MIN_VALUE) {
                        System.out.println("掛け金は1以上を入力してください");
                        // 掛け金設定のwhile文の頭に戻る
                        continue;
                    }

                    if (checkCoin(bet, gameCoin)) {
                        // 掛け金が有効なのでループを抜ける
                        break;
                    } else {
                        // 掛け金を再度設定するよう促す
                        System.out.println("掛け金が手持ちのコインを超えています");
                    }

                } catch (NumberFormatException e) {
                    System.out.println(HighAndLowConstants.ERR_MSG_INVALID_NUMBER_FORMAT);
                } catch (IOException e) {
                    // ユーザー向けのメッセージ
                    System.err.println(HighAndLowConstants.ERR_MSG_IO_ERROR_FATAL);
                    // 開発、デバッグ用にコンソールに出力
                    e.printStackTrace();
                    // 異常終了
                    System.err.println(HighAndLowConstants.ERR_MSG_PROGRAM_TERMINATED);
                    System.exit(1);
                }
            }

            // 配られたカードを表示
            System.out.println("【あなた】" + playerCard + " vs " + cpuCard + "【コンピュータ】");

            // カードを比較して、正解していたらフラグを勝利にする
            if (playerCard > cpuCard && HighAndLowConstants.CHOICE_HIGH.equals(playerChoice)
                    || playerCard < cpuCard && HighAndLowConstants.CHOICE_LOW.equals(playerChoice)) {

                isPlayerWin = true;
            }

            // 正解、不正解時の処理
            if (isPlayerWin) {
                System.out.println("正解 あなたの勝ち");

                System.out.println("ゲームコイン" + bet * HighAndLowConstants.WIN_MULTIPLIER + "枚を取得しました");

                // 手持ちのコインに掛け金の2倍を追加
                gameCoin = gameCoin + bet * HighAndLowConstants.WIN_MULTIPLIER;

                // 連続正解数を増やす
                winCount++;

                // 連続正解数に応じたボーナスコインを設定
                int bonus = 0;
                switch (winCount) {
                    case HighAndLowConstants.BONUS_WIN_COUNT_SMALL:
                        // 掛け金の1/2のコインをボーナスコインとして付与
                        bonus = bet / HighAndLowConstants.BONUS_RATE_SMALL;
                        System.out.println(
                                HighAndLowConstants.BONUS_WIN_COUNT_SMALL + "回連続して正解したため、掛け金の半分をボーナスコインとして追加します");
                        System.out.println("+" + bonus + "枚");
                        gameCoin = gameCoin + bonus;
                        break;

                    case HighAndLowConstants.BONUS_WIN_COUNT_LARGE:
                        // 掛け金2倍をボーナスコインとして与える
                        bonus = bet * HighAndLowConstants.BONUS_RATE_LARGE;
                        System.out.println(
                                HighAndLowConstants.BONUS_WIN_COUNT_LARGE + "回連続して正解したため、掛け金の2倍をボーナスコインとして追加します");
                        System.out.println("+" + bonus + "枚");
                        gameCoin = gameCoin + bonus;
                        break;

                    default:
                        break;
                }

            } // ユーザのカードとCPUのカードを比較し引き分けか確認
            else if (playerCard == cpuCard) {
                System.out.println("引き分け");

                // 引き分け時は絵柄合わせミニゲームを実施する
                gameCoin = suitMatch(gameCoin);

                // 次のラウンドへ進む
                continue;
            } else {
                System.out.println("残念 あなたの負け");

                // 緊急回避を持っているか確認する
                if (userItemsList.contains("緊急回避")) {
                    System.out.println("アイテム「緊急回避」を使用しますか？" + "\n" + "負けを無効にし、掛け金を返却します" + "\n" + "連続正解数は初期化されません");
                    System.out.println("アイテムを使用しますか" + "はい→" + HighAndLowConstants.CHOICE_YES + "いいえ→"
                            + HighAndLowConstants.CHOICE_NO);

                    String useEmergencyAvoidance = br.readLine();

                    if (HighAndLowConstants.CHOICE_YES.equalsIgnoreCase(useEmergencyAvoidance)) {
                        // 掛け金の操作を行わない
                        System.out.println("アイテム緊急回避を使用したため、掛け金" + bet + "枚を返却しました");
                        System.out.println("現在の所持金は" + gameCoin + "枚です");

                        // 使用済みのアイテムを削除
                        int indexOfEmergencyAvoidance = userItemsList
                                .indexOf(HighAndLowConstants.ITEM_EMERGENCY_AVOIDANCE);
                        if (indexOfEmergencyAvoidance != -1) {
                            userItemsList.remove(indexOfEmergencyAvoidance);
                        }

                        // 緊急回避を使用したので、フラグ書き換え
                        isEmergencyAvoidanceUsed = true;
                    }

                }

                // 緊急回避未使用時
                if (!isEmergencyAvoidanceUsed) {

                    System.out.println("ゲームコイン" + bet / HighAndLowConstants.LOSING_DIVIDER + "枚を失いました");

                    // 手持ちのコインから掛け金を引いた結果を2で割る
                    gameCoin = (gameCoin - bet) / HighAndLowConstants.LOSING_DIVIDER;

                    if (winCount > 0) {
                        System.out.println("連続正解数を初期化します");
                        // 連続正解数を初期化
                        winCount = 0;
                    }
                    // アイテム獲得メソッドへ
                    userItemsList.addAll(getItems());
                }
            }

            // intの正の最大値を超えると負になる特性を利用し、オーバーフロー時はゲームを終了させる
            if (gameCoin < 0) {
                System.out.println("相手が降参しました！ゲームを終了します");
                break;
            } else if (gameCoin == 0) {
                // オバーフローにならなくても、ゲームコインが0になったらゲーム終了
                System.out.println("ゲームコインがなくなりました" + "\n" + "ゲームを終了します");
                break;
            }

        }

    }

    /**
     * ヒントメソッド
     * * @param cpuCard
     */
    public static void hints(int cpuCard) {

        // CPUのカードに合わせてヒントを表示
        if (cpuCard <= HighAndLowConstants.CPU_HINT_THRESHOLD) {
            System.out.println("コンピュータのカードは7以下です");
        } else {
            System.out.println("コンピュータのカードは8以上です");
        }
    }

    // 掛け金が手持ちのコインを超えていないことを確認するメソッド
    public static boolean checkCoin(int bet, int gameCoin) {

        if (gameCoin >= bet) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * アイテム獲得メソッド
     * * @return userList
     */
    public static List<String> getItems() {
        // アイテムリストを用意
        List<String> itemsList = new ArrayList<>();
        // 緊急回避→負けを無効化する
        itemsList.add(HighAndLowConstants.ITEM_EMERGENCY_AVOIDANCE);
        // 勝利の確約→アイテムが使用されたラウンドで必ず勝利する
        itemsList.add(HighAndLowConstants.ITEM_GUARANTEED_WIN);
        // 運命のダイス→大小の選択をサイコロで決める 奇数→小さい 偶数→大きい
        itemsList.add(HighAndLowConstants.ITEM_FATE_DICE);

        // リストの中身が抽選され、どれか一つが手に入る
        int item = new Random().nextInt(itemsList.size());
        String userItem = itemsList.get(item);
        System.out.println("「" + userItem + "」" + "を獲得しました");

        // ユーザーの手持ちアイテムを追加
        List<String> userList = new ArrayList<>();
        userList.add(userItem);
        return userList;

    }

    /**
     * アイテム使用メソッド
     * * @param selectableItemList
     * 
     * @throws IOException
     */
    public static void useItem(List<String> selectableItemList) throws IOException {
        System.out.println("どのアイテムを使用しますか");

        // 緊急回避を除いたリストをループさせ、インデックス番号とアイテム名を表示
        for (int i = 0; i < selectableItemList.size(); i++) {
            System.out.println((i + 1) + "：" + selectableItemList.get(i));
        }

        // 初期化
        int itemNum = -1;
        while (true) {
            System.out.println("使用するアイテムの番号を入力してください");
            try {
                itemNum = Integer.parseInt(br.readLine());

                if (itemNum >= 1 && itemNum <= selectableItemList.size()) {
                    // 有効な番号が選ばれた時
                    String selectedItem = selectableItemList.get(itemNum - 1);

                    // 選ばれたアイテム名に合わせて、フラグなどを書き換え
                    switch (selectedItem) {

                        case HighAndLowConstants.ITEM_GUARANTEED_WIN:
                            System.out.println("勝利の確約が選ばれました");
                            System.out.println("このターンで必ず勝利します" + "\n" + "連続正解数は追加されます");

                            // アイテム使用により、フラグの値を書き換え
                            isGuaranteedWin = true;

                            break;

                        case HighAndLowConstants.ITEM_FATE_DICE:
                            System.out.println("運命のダイスが選ばれました");
                            System.out.println("大小の選択をサイコロで決めます 奇数→小さい 偶数→大きい");

                            // 運命のダイス使用メソッドに移動
                            choiceByDice = FateDice();
                            break;

                        default:
                            break;
                    }

                    // 使用済みのアイテムを削除
                    userItemsList.remove(selectedItem);

                    break;
                } else {
                    System.out.println("無効なアイテム番号です。表示されている番号の中から選択してください。");
                }
            } catch (NumberFormatException e) {
                System.out.println(HighAndLowConstants.ERR_MSG_INVALID_NUMBER_FORMAT);
            } catch (IOException e) {
                // ユーザー向けのメッセージ
                System.err.println(HighAndLowConstants.ERR_MSG_IO_ERROR_FATAL);
                // 開発、デバッグ用にコンソールに出力
                e.printStackTrace();
                // 異常終了
                System.err.println(HighAndLowConstants.ERR_MSG_PROGRAM_TERMINATED);
                System.exit(1);
            }

        }

    }

    /**
     * 運命のダイス使用メソッド
     * * @return chosen
     */
    public static String FateDice() {

        // サイコロを振って、数字を決める
        int diceRoll = rand.nextInt(HighAndLowConstants.DICE_MAX_VALUE - HighAndLowConstants.DICE_MIN_VALUE + 1)
                + HighAndLowConstants.DICE_MIN_VALUE;

        // メインメソッドに渡すサイコロを振った結果の変数
        String chosen = "";

        if (diceRoll % 2 == 1) {
            // 奇数だったら小さい
            chosen = HighAndLowConstants.CHOICE_LOW;
            System.out.println("サイコロの目は" + diceRoll + "です");

        } else {
            // 偶数だったら大きい
            chosen = HighAndLowConstants.CHOICE_HIGH;
            System.out.println("サイコロの目は" + diceRoll + "です");
        }

        // 大小の選択結果をメインメソッドに渡す
        return chosen;
    }

    /**
     * 絵合わせミニゲームメソッド
     * * @param gameCoin
     * 
     * @return gameCoin
     * @throws IOException
     */
    public static int suitMatch(int gameCoin) throws IOException {
        System.out.println("----------" + "\n" + "絵柄合わせミニゲームスタート" + "\n" + "コンピュータの絵柄を当てよう！"
                + "\n" + "絵柄に応じてボーナスコインをゲット！" + "\n" + "----------");

        // 全てのSuitの列挙子を配列として取得
        Suit[] allSuits = Suit.values();

        // CPUのカードの絵柄を決定
        Suit cpuSuit = allSuits[rand.nextInt(allSuits.length)];

        System.out.println("どの絵柄だと思いますか？");
        for (int i = 0; i < allSuits.length; i++) {
            // getDisplayName()を使って表示名を出力
            System.out.println((i + 1) + ":" + allSuits[i].getDisplayName());
        }

        int playerChoiceNum = -1;
        while (true) {
            try {
                System.out.println("番号を入力してください");
                playerChoiceNum = Integer.parseInt(br.readLine());

                // 1から絵柄の総数までの範囲で入力されたかチェック
                if (playerChoiceNum >= 1 && playerChoiceNum <= allSuits.length) {
                    // 有効な入力であればループを抜ける
                    break;
                } else {
                    // 無効な入力の場合は再度入力を求める
                    System.out.println("1から" + allSuits.length + "の番号を入力してください。");
                }
            } catch (NumberFormatException e) {
                System.out.println(HighAndLowConstants.ERR_MSG_INVALID_NUMBER_FORMAT);
            } catch (IOException e) {
                // ユーザー向けのメッセージ
                System.err.println(HighAndLowConstants.ERR_MSG_IO_ERROR_FATAL);
                // 開発、デバッグ用にコンソールに出力
                e.printStackTrace();
                // 異常終了
                System.err.println(HighAndLowConstants.ERR_MSG_PROGRAM_TERMINATED);
                System.exit(1);
            }

        }

        // プレイヤーが選んだ絵柄 (0-indexedに変換)
        Suit playerSuit = allSuits[playerChoiceNum - 1];

        System.out.println("あなたの選択: " + playerSuit.getDisplayName());
        System.out.println("コンピュータの絵柄: " + cpuSuit.getDisplayName());

        // 絵柄の一致をチェック
        if (playerSuit == cpuSuit) {
            // 当たった絵柄からボーナスコインを取得
            int bonus = cpuSuit.getBonusCoin();
            // 現在のコインに追加
            gameCoin += bonus;
            System.out.println("見事、絵柄が一致しました！");
            System.out.println("「" + cpuSuit.getDisplayName() + "」ボーナスとして " + bonus + " コインを獲得！");
        } else {
            System.out.println("残念、絵柄は合いませんでした...");
        }
        System.out.println("現在のゲームコイン: " + gameCoin);

        return gameCoin;

    }
}